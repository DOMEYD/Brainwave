package fr.iut.brainwave;

//import com.example.testapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * 
 * @author Robin, Chafik, Lo�c, C�cile
 *
 */
public class MenuApp extends Activity 
{
	
	//D�claration des variables
	private BluetoothAdapter BA;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu); //Attribution du layout (de la vue � utilser)
		
		//D�claration du bluetoothAdapter
		BA = BluetoothAdapter.getDefaultAdapter();
	}
	
	/**
	 * Permet de tester si le bluetooth est activ�
	 * Lance un intent de MainActivity si il est actif, sinon il quitte l'activit� du graph
	 * @param view
	 */
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
	
	/**
	 * Permet de gerer le bluetooth
	 * Lance un intent de GestionBluetooth
	 * @param view
	 */
	public void GestionBluetooth(View view)
	{
		Intent intent = new Intent();
		intent.setClass(this, GestionBluetooth.class);
		startActivity(intent);
	}
	
	/**
	 * Permet de gerer le bluetooth
	 * Lance un intent de GestionBluetooth
	 * @param view
	 */
	public void CompareGraph(View view)
	{
		Intent intent = new Intent();
		intent.setClass(this, CompareActivity.class);
		startActivity(intent);
	}
	
	public void FFTGraph(View view)
	{
		Intent intent = new Intent();
		intent.setClass(this, FFTActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Menu de la barre d'action
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menuapp, menu);
        return super.onCreateOptionsMenu(menu);
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
    	case R.id.action_params:
    		// Comportement du bouton "Quitter"
    		this.GestionBluetooth(null);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
}
