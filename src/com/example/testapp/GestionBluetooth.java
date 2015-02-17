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

/** Activit� de gestion des diff�rentes option du bluetooth :
 * 
 * 		activation, 
 * 		d�sactivation,
 * 		rendre l'appareil d�tectable par les autres appareils bluetooth, 
 * 		listage des appareils appair�s,
 * 		listage des appareils bluetooth � port� de notre appareil 
 * 
 * @author Chafik, Robin, Lo�c, C�cile
 *
 */
public class GestionBluetooth extends Activity
{
	//D�claration des variables
	private Button listAround ;
	private BluetoothAdapter BA;
	private Set<BluetoothDevice>pairedDevices;
	private ListView lvPaired , lvAround;
	private TextView tvPaired , tvAround , tvRechercheEnCours;
	private ArrayList<String> tab;
	private boolean discoveryFinished  ;

	/**
	 * Permet de trouver les listes des devices appair�s et ceux dans la port�e du bluetooth
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestionbluetooth);

		//Les listes des devices appair�s et ceux dans la port�e du bluetooth
		lvPaired = (ListView)findViewById(R.id.lvPairedDevices);
		lvAround = (ListView)findViewById(R.id.lvAroundDevices);

		//Titres des listes
		tvPaired = (TextView)findViewById(R.id.tvListPairedDevices);
		tvAround = (TextView)findViewById(R.id.tvListAroundDevices);



		//Cache les titres, les titres apparaissent lorsque les listes sont demand�es
		tvPaired.setVisibility(View.GONE);
		tvAround.setVisibility(View.GONE);

		//Message de charchement, affich� que lorsque la recherche est en cours
		tvRechercheEnCours = (TextView)findViewById(R.id.tvRechercheEnCours);
		tvRechercheEnCours.setVisibility(View.GONE); 

		//Boolean qui d�clare si la recherche est fini ou non
		discoveryFinished = false;

		//D�claration du bluetoothAdapter
		BA = BluetoothAdapter.getDefaultAdapter();

		tab = new ArrayList<String>() ;
	}

	/** 
	 * Permet d'activer le bluetooth
	 * Agit si il est d�j� activ�, non activ� et demande l'accord de l'utilisateur
	 */
	public void on(View view)
	{
		if (!BA.isEnabled()) //Si le bluetooth est d�sactiv�
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
	 * Permet de d�sactiver le bluetooth
	 * Agit si il est d�j� activ�, non activ�.
	 */
	public void off(View view)
	{
		if (BA.isEnabled()) //Si le bluetooth est activ�
		{
			BA.disable(); //D�sactive le bluetooth
			Toast.makeText(getApplicationContext(),"Bluetooth turned off" ,Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(getApplicationContext(),"Bluetooth already off",Toast.LENGTH_LONG).show();
		}
	}

	/** 
	 * Permet la cr�ation et affichage de la liste des devices appair�s avec notre appareil
	 */
	public void list(View view)
	{

		if (!BA.isEnabled())  //Test si le bluetooth est activ� sinon quitte la m�thode
		{
			Toast.makeText(getApplicationContext(),"The bluetooth is not on", Toast.LENGTH_LONG).show();
			return; //Sort de la m�thode
		}

		tvPaired.setVisibility(View.VISIBLE); //Apparition du titre 

		pairedDevices = BA.getBondedDevices(); //Recup�ration des devices appair�s

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
	 * Rend notre device visible par les autres devices bluetooth, va demander l'activation du bluetooth si celui-ci est d�sactiv�
	 */
	public void visible(View view)
	{
		Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(getVisible, 0);

	}



	/** 
	 * Cr�ation et affichage de la liste des devices bluetooth dans la port�e de notre appareil
	 */
	public void listAround(View view)
	{

		if (!BA.isEnabled())  //Test si le bluetooth est activ� sinon quitte la m�thode
		{
			Toast.makeText(getApplicationContext(),"The bluetooth is not on", Toast.LENGTH_LONG).show();
			return; //Sort de la m�thode
		}


		if (discoveryFinished ) //Si la recherche de device a �t� effectu�e
		{

			if (tab.size() <= 0) //Teste si il y a des appareils a afficher sinon quitte la m�thode
			{
				Toast.makeText(getApplicationContext(),"There is not device around, retry later", Toast.LENGTH_LONG).show();
				return; //Sort de la m�thode
			}

			tvRechercheEnCours.setVisibility(View.GONE);//Cache le message de chargement

			tvAround.setVisibility(View.VISIBLE); //Affiche le titre de la liste

			ArrayList<String> list = new ArrayList<String>(); //Cr�ation de la liste



			for (int i = 0 ;i<tab.size(); i++) //Ajoute des appareils trouv� � la liste
			{
				list.add(tab.get(i));
			}
			Toast.makeText(getApplicationContext(),"Showing the " + list.size() + " device(s) that are around", Toast.LENGTH_SHORT).show();

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  list); //adaptation de la liste pour l'affichage

			lvAround.setAdapter(adapter); //Affichage de la liste

			discoveryFinished = false; //Remet le boolean a son �tat initial ( false )

		}
		else
		{
			tab.clear(); // Vide le tableau si une recherche a �t� faite pr�c�dement

			//Cache la liste est son titre pour lancer une nouvelle recherche
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); 
			lvAround.setAdapter(adapter); //Affichage de la liste vide 
			tvAround.setVisibility(View.GONE); //Cache le titre de la liste
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 

			// Ajoute des actions de d�but et de fin � la recherche 
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); 
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

			registerReceiver(bluetoothReceiver, filter);
			BA.startDiscovery(); //D�marre la recherche
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

			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) //D�but de la recherche
			{
				Toast.makeText(getApplicationContext(), "La recherche commence" , Toast.LENGTH_SHORT).show();
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action))  //Lorsqu'un appareil est trouv�
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
	 * Permet d'afficher les boutons "� propos" et "quitter"
	 * A propos : lance une bo�te de dialogue avec les noms des d�veloppeurs de l'application
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.propos:
			// Comportement du bouton "A propos"
			new AlertDialog.Builder(this)
			.setTitle("A propos")
			.setMessage("Application r�alis� dans le cadre du projet BrainWaves de Licence Pro Dev Web et Mobile d'Orleans.\n" +
					"- Robin Hayart \n" +
					"- Loic Dieudonn� \n" +
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
