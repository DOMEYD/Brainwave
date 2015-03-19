package fr.iut.brainwave;

//IMPORTS
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


//import com.example.testapp.R;
import com.jjoe64.graphview.GraphViewData;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;


/** Activité de gestion des différentes option du bluetooth :
 * 
 * 		activation, 
 * 		désactivation,
 * 		rendre l'appareil détectable par les autres appareils bluetooth, 
 * 		listage des appareils appairés,
 * 		listage des appareils bluetooth à porté de notre appareil 
 * 
 * @author Chafik, Robin, Loïc, Cécile
 *
 */
public class GestionBluetooth extends Activity
{
	private BluetoothAdapter BA;
	private Set<BluetoothDevice>pairedDevices;
	private ListView lvAround;
	private TextView listDeviceTitle;
	private ProgressBar tvRechercheEnCours;

	public ArrayAdapter<String> mesNewDevicesArrayAdapter;
	public ArrayList<BluetoothDevice> listDevicesArround = new ArrayList<BluetoothDevice>();
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	
	private boolean isRegisterReceiver = false;

	static final int ENABLED_BTH = 1;
	
	private TGDevice tgDevice;
	private String macAdd;

	/**
	 * Permet de trouver les listes des devices appairés et ceux dans la portée du bluetooth
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestionbluetooth);

		//Les listes des devices appairés et ceux dans la portée du bluetooth
		lvAround = (ListView)findViewById(R.id.lvAroundDevices);

		//Titres des listes
		listDeviceTitle = (TextView)findViewById(R.id.listDeviceTitle);

		//Message de charchement, affiché que lorsque la recherche est en cours
		tvRechercheEnCours = (ProgressBar) findViewById(R.id.tvRechercheEnCours);
//		tvRechercheEnCours.setVisibility(View.GONE); 

		//Déclaration du bluetoothAdapter
		BA = BluetoothAdapter.getDefaultAdapter();
		
		// MANAGE list of peripheral
		mesNewDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		lvAround.setOnItemClickListener(mDeviceClickListener);
		
		// SET depending adapter
		lvAround.setAdapter(mesNewDevicesArrayAdapter);
		
		// MANAGE if the Bluetooth adaptor is enable
		if(BA.isEnabled()) ((Switch) findViewById(R.id.togglebth)).setChecked( true );
	}

	
	public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((Switch) view).isChecked();
	    
	    if (on) {
	        // Enable vibrate
	    	
	    	this.on( null );
	    } else {
	        // Disable vibrate
	    	this.off( null );
	    }
	}
	
	/** 
	 * Permet d'activer le bluetooth
	 * Agit si il est déjà activé, non activé et demande l'accord de l'utilisateur
	 */
	public void on(View view)
	{
		if (!BA.isEnabled()) //Si le bluetooth est désactivé
		{
			Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnOn, ENABLED_BTH); //Active le bluetooth
		}
		else
		{
			Toast.makeText(getApplicationContext(),"Bluetooth already on",Toast.LENGTH_LONG).show();
		}
	}

	/** 
	 * Permet de désactiver le bluetooth
	 * Agit si il est déjà activé, non activé.
	 */
	public void off(View view)
	{
		if (BA.isEnabled()) //Si le bluetooth est activé
		{
			BA.disable(); //Désactive le bluetooth
			Toast.makeText(getApplicationContext(),"Bluetooth turned off" ,Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(getApplicationContext(),"Bluetooth already off",Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == ENABLED_BTH) {
	        if (resultCode == RESULT_OK) {}
	        else {
				((Switch) findViewById(R.id.togglebth)).setChecked( false );
	        }
	    }
	}

	/** 
	 * Permet la création et affichage de la liste des devices appairés avec notre appareil
	 */
	public void list(View view)
	{

		if (!BA.isEnabled())  //Test si le bluetooth est activé sinon quitte la méthode
		{
			Toast.makeText(getApplicationContext(),"The bluetooth is not on", Toast.LENGTH_LONG).show();
			return; //Sort de la méthode
		}

		// SET list of device title
		listDeviceTitle.setText(R.string.ListPairedDevices);

		// RETRIEVE all already paired devices
		pairedDevices = BA.getBondedDevices();

		ArrayList<String> list = new ArrayList<String>();

		for(BluetoothDevice bt : pairedDevices) //Ajout des devices dans la liste
		{
			list.add(bt.getName());
		}
		Toast.makeText(getApplicationContext(),"Showing the " + list.size() + " paired device", Toast.LENGTH_SHORT).show();

//		lvPaired.setAdapter(adapter); //Affichage de la liste
	}

	/**
	 * Rend notre device visible par les autres devices bluetooth, va demander l'activation du bluetooth si celui-ci est désactivé
	 */
	public void visible(View view)
	{
		Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(getVisible, 0);

	}

	/** 
	 * Création et affichage de la liste des devices bluetooth dans la portée de notre appareil
	 */
	public void listAround(View view)
	{
		if (!BA.isEnabled())  //Test si le bluetooth est activé sinon quitte la méthode
		{
			Toast.makeText(getApplicationContext(),"The bluetooth is not on", Toast.LENGTH_LONG).show();
			return; //Sort de la méthode
		}
		
		// SET arround list title
		listDeviceTitle.setText(R.string.ListAroundDevices);
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 

		// Ajoute des actions de début et de fin à la recherche 
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); 
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

		registerReceiver(bluetoothReceiver, filter);
		BA.startDiscovery(); //Démarre la recherche
		tvRechercheEnCours.setVisibility(View.VISIBLE); //Affiche le message de chargement
	}
	
	/**
	 * Recherches les autres appareils bluetooth
	 */
	final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			isRegisterReceiver = true;

			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) //Début de la recherche
			{
				Toast.makeText(getApplicationContext(), "La recherche commence" , Toast.LENGTH_SHORT).show();
				mesNewDevicesArrayAdapter.clear();
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action))  //Lorsqu'un appareil est trouvé
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				mesNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				listDevicesArround.add(device);
			}

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) //Lorsque la recherche est fini
			{
				if (mesNewDevicesArrayAdapter.getCount() == 0) {
					mesNewDevicesArrayAdapter.add("Aucun peripherique trouvé");	
				}
				tvRechercheEnCours.setVisibility(View.GONE);
			}
		}
	};
	
	public OnItemClickListener mDeviceClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
		{
			// Disable Bluetooth search device if ingage
			if (BA.isDiscovering()) BA.cancelDiscovery();
			
			// Retrieve TextView content
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17); // Device MAC addresse
			macAdd = address;
			
			BluetoothDevice device = null;
//			BluetoothSocket mmSocket = null;
			for(int i = 0; i < listDevicesArround.size(); i++) {
				if(listDevicesArround.get(i).getAddress().equals(address)) {
					device = listDevicesArround.get(i);
				}
			}
			
//			ConnectThread CT = new ConnectThread(device);
//			CT.run();
			tgDevice = new TGDevice(BA, handler); 
        	tgDevice.connect(device);
		}
	};
	
	private final Handler handler = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    			case TGDevice.MSG_STATE_CHANGE:
    				switch (msg.arg1) {
    					case TGDevice.STATE_IDLE:
    					break;
    					case TGDevice.STATE_CONNECTING:
    						Log.v("Statut", getString(R.string.TRYAppair));
    						Toast.makeText(getApplicationContext(), getString(R.string.TRYAppair), Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_CONNECTED:
							Log.v("Statut", getString(R.string.BTAppair));
							Toast.makeText(getApplicationContext(), getString(R.string.BTAppair), Toast.LENGTH_SHORT).show();
							
							// SAVE mac address in prefs
							SharedPreferences settings = getSharedPreferences("Bluetooth", MODE_PRIVATE);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("MAC-Address", macAdd);
							editor.commit();							
				        	
							// CLOSE device connexion and close prefs
							tgDevice.close();
							finish();
						break;
						case TGDevice.STATE_DISCONNECTED:
//							Toast.makeText(getApplicationContext(), "Systeme déconnecté !", Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_NOT_FOUND:
							Toast.makeText(getApplicationContext(), "Systeme non trouvé !", Toast.LENGTH_SHORT).show();
							finish(); //Nous renvoie sur le menu
						break;
						case TGDevice.STATE_ERR_NO_DEVICE:
							Log.v("Statut", "Not paired");
						default:
						break;
    				}
    			break;
    			default:
    			break;
    		}
    	}
    };

	/**
	 * Ajoute des objets sur la barre d'action
	 */
	public boolean onCreateOptionsMenu(Menu menuu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gestionbluetooth, menuu);
		return true;
	}

	/**
	 * Permet d'afficher les boutons "à propos" et "quitter"
	 * A propos : lance une boîte de dialogue avec les noms des développeurs de l'application
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.propos:
			// Comportement du bouton "A propos"
			new AlertDialog.Builder(this)
			.setTitle("A propos")
			.setMessage("Application réalisé dans le cadre du projet BrainWaves de Licence Pro Dev Web et Mobile d'Orleans.\n" +
					"- Robin Hayart \n" +
					"- Loic Dieudonné \n" +
					"- Chafik Daggag \n" +
					"- Cecile Kergall")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
			return true;
		case R.id.quitter:
			// Comportement du bouton "Quitter"
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BA.isDiscovering()) BA.cancelDiscovery();
		if(isRegisterReceiver) unregisterReceiver(bluetoothReceiver);
	}
}
