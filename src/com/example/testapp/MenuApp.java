package com.example.testapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

/** To do :
 * 
 *  Cr�er des menus propres
 *   
 *  */

public class MenuApp extends Activity 
{
	
	//D�claration des variables
	private BluetoothAdapter BA;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu); //Attribution du layout ( de la vue � utilserv)
		
		//D�claration du bluetoothAdapter
		BA = BluetoothAdapter.getDefaultAdapter();
	}

	public void Graph(View view)
	{
		if (!BA.isEnabled())  //Test si le bluetooth est activ� sinon quitte la m�thode
		{
			Toast.makeText(getApplicationContext(),"The bluetooth is not on", Toast.LENGTH_LONG).show();
			return; //Sort de la m�thode, ne lance pas l'activit� Graph
		}
		
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void GestionBluetooth(View view)
	{
		Intent intent = new Intent();
		intent.setClass(this, GestionBluetooth.class);
		startActivity(intent);
	}
	
	public boolean onCreateOptionsMenu(Menu menuu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menuu);
		return true;
	}
}
