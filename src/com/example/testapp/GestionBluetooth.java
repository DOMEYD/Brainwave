package com.example.testapp;

//IMPORTS
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	//Déclaration des variables
	private Button listAround ;
	private BluetoothAdapter BA;
	private Set<BluetoothDevice>pairedDevices;
	private ListView lvPaired , lvAround;
	private TextView tvPaired , tvAround , tvRechercheEnCours;
	private ArrayList<String> tab;
	private boolean discoveryFinished  ;

	/**
	 * Permet de trouver les listes des devices appairés et ceux dans la portée du bluetooth
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestionbluetooth);

		//Les listes des devices appairés et ceux dans la portée du bluetooth
		lvPaired = (ListView)findViewById(R.id.lvPairedDevices);
		lvAround = (ListView)findViewById(R.id.lvAroundDevices);

		//Titres des listes
		tvPaired = (TextView)findViewById(R.id.tvListPairedDevices);
		tvAround = (TextView)findViewById(R.id.tvListAroundDevices);



		//Cache les titres, les titres apparaissent lorsque les listes sont demandées
		tvPaired.setVisibility(View.GONE);
		tvAround.setVisibility(View.GONE);

		//Message de charchement, affiché que lorsque la recherche est en cours
		tvRechercheEnCours = (TextView)findViewById(R.id.tvRechercheEnCours);
		tvRechercheEnCours.setVisibility(View.GONE); 

		//Boolean qui déclare si la recherche est fini ou non
		discoveryFinished = false;

		//Déclaration du bluetoothAdapter
		BA = BluetoothAdapter.getDefaultAdapter();

		tab = new ArrayList<String>() ;
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
			startActivityForResult(turnOn, 0); //Active le bluetooth
			Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,Toast.LENGTH_LONG).show();
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

		tvPaired.setVisibility(View.VISIBLE); //Apparition du titre 

		pairedDevices = BA.getBondedDevices(); //Recupération des devices appairés

		ArrayList<String> list = new ArrayList<String>();

		for(BluetoothDevice bt : pairedDevices) //Ajout des devices dans la liste
		{
			list.add(bt.getName());
		}
		Toast.makeText(getApplicationContext(),"Showing the " + list.size() + " paired device", Toast.LENGTH_SHORT).show();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  list);

		lvPaired.setAdapter(adapter); //Affichage de la liste
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


		if (discoveryFinished ) //Si la recherche de device a été effectuée
		{

			if (tab.size() <= 0) //Teste si il y a des appareils a afficher sinon quitte la méthode
			{
				Toast.makeText(getApplicationContext(),"There is not device around, retry later", Toast.LENGTH_LONG).show();
				return; //Sort de la méthode
			}

			tvRechercheEnCours.setVisibility(View.GONE);//Cache le message de chargement

			tvAround.setVisibility(View.VISIBLE); //Affiche le titre de la liste

			ArrayList<String> list = new ArrayList<String>(); //Création de la liste



			for (int i = 0 ;i<tab.size(); i++) //Ajoute des appareils trouvé à la liste
			{
				list.add(tab.get(i));
			}
			Toast.makeText(getApplicationContext(),"Showing the " + list.size() + " device(s) that are around", Toast.LENGTH_SHORT).show();

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  list); //adaptation de la liste pour l'affichage

			lvAround.setAdapter(adapter); //Affichage de la liste

			discoveryFinished = false; //Remet le boolean a son état initial ( false )

		}
		else
		{
			tab.clear(); // Vide le tableau si une recherche a été faite précédement

			//Cache la liste est son titre pour lancer une nouvelle recherche
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); 
			lvAround.setAdapter(adapter); //Affichage de la liste vide 
			tvAround.setVisibility(View.GONE); //Cache le titre de la liste
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 

			// Ajoute des actions de début et de fin à la recherche 
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); 
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

			registerReceiver(bluetoothReceiver, filter);
			BA.startDiscovery(); //Démarre la recherche
			tvRechercheEnCours.setVisibility(View.VISIBLE); //Affiche le message de chargement
		}	
	}
	/**
	 * Recherches les autres appareils bluetooth
	 */
	final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) //Début de la recherche
			{
				Toast.makeText(getApplicationContext(), "La recherche commence" , Toast.LENGTH_SHORT).show();
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action))  //Lorsqu'un appareil est trouvé
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Toast.makeText(getApplicationContext(), "New Device = " + device.getName(), Toast.LENGTH_SHORT).show();
				//Ajoute le nom de l'appareil au tableau
				tab.add(device.getName());
			}

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) //Lorsque la recherche est fini
			{
				discoveryFinished = true;
				listAround(listAround);

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
}
