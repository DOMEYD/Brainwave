package com.example.testapp;

// IMPORTS
// Imports package android (divers)
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
// Imports api GraphView
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
// Imports api ThinkGear
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;


public class MainActivity extends Activity {

	TGDevice tgDevice;
	BluetoothAdapter btAdapter;
	
	// Echelle Y du graph pour Meditation et Attention
    double max = 100 ;
    double min = 0;
    
    int passage = 1;
    
    boolean courbeAttention = true;
    boolean courbeMeditation = true;
    boolean courbeBlink = true;
	
    // Courbe de l'attention (Couleur = rouge / Nom = Attention)
    GraphViewSeries seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), new GraphViewData[] {

      });
    // Courbe de la méditation (Couleur = bleu / Nom = Meditation)
    GraphViewSeries seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 3), new GraphViewData[] {

    });
    
    // Courbe de la méditation (Couleur = bleu / Nom = Meditation)
    GraphViewSeries seriesBlink = new GraphViewSeries("Clins d'oeil", new GraphViewSeriesStyle(Color.rgb(0, 200, 50), 3), new GraphViewData[] {

    });
    
    // Instanciation du GraphView
    GraphView graphView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter != null) 
        { 
        	tgDevice = new TGDevice(btAdapter, handler); 
        }
        
        
        tgDevice.connect(true);
        createGraph();
	}
    
    protected void onResume(){
    	super.onResume();
    	
    	gestionParametre();
    }
    
    protected void gestionParametre(){
    	try{
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	
	    	boolean oldCourbeAttention = courbeAttention;
	    	boolean oldCourbeMeditation = courbeMeditation;
	    	boolean oldCourbeBlink = courbeBlink;
	    	courbeAttention = prefs.getBoolean("graph_attention", true);
	    	courbeMeditation = prefs.getBoolean("graph_meditation", true);
	    	courbeBlink = prefs.getBoolean("graph_blink", true);
    	
	    	if(!courbeAttention){
	    		graphView.removeSeries(seriesAttention);
	    	}else if(courbeAttention != oldCourbeAttention){
	    		graphView.addSeries(seriesAttention);
	    	}
	    	
	    	if(!courbeMeditation){
	    		graphView.removeSeries(seriesMeditation);
	    	}else if(courbeMeditation != oldCourbeMeditation){
	    		graphView.addSeries(seriesMeditation);
	    	}
	    	
	    	if(!courbeBlink){
	    		graphView.removeSeries(seriesBlink);
	    	}else if(courbeBlink != oldCourbeBlink){
	    		graphView.addSeries(seriesBlink);
	    	}
	    	
	    	if(!courbeAttention && !courbeBlink && !courbeMeditation){
	    		Toast.makeText(getApplicationContext(), "Aucune courbe sélectionné, sélectionnez en une dans les paramètres de l\'application", Toast.LENGTH_LONG).show();
	    	}
    	}catch(Exception exc){
    		Log.e("errorResume", "Erreur : " +exc);
    	}
    }
        
    // Handler du ThinkGear Device (thread qui traite constamment les données reçus)
    @SuppressLint("HandlerLeak") private final Handler handler = new Handler() {
    	@SuppressWarnings("deprecation")
		@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    			case TGDevice.MSG_STATE_CHANGE:
    				switch (msg.arg1) {
    					case TGDevice.STATE_IDLE:
    					break;
    					case TGDevice.STATE_CONNECTING:
    						Log.v("Statut", "Connection en cours ...");
    						Toast.makeText(getApplicationContext(), "Connection en cours ...", Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_CONNECTED:
							Log.v("Statut", "Connecté");
							Toast.makeText(getApplicationContext(), "Connecté !", Toast.LENGTH_SHORT).show();
							tgDevice.start();
						break;
						case TGDevice.STATE_DISCONNECTED:
							Toast.makeText(getApplicationContext(), "Systeme déconnecté !", Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_NOT_FOUND:
							Toast.makeText(getApplicationContext(), "Systeme non trouvé !", Toast.LENGTH_SHORT).show();
							finish(); //Nous renvoie sur le menu
						break;
						case TGDevice.STATE_NOT_PAIRED:
							Log.v("Statut", "Not paired");
							tgDevice.connect(true);
						default:
						break;
    				}
    			break;
    			case TGDevice.MSG_POOR_SIGNAL: // Indique la qualité du signal (0 indique un bon fonctionnement)
    				Log.v("MsgEEG", "PoorSignal: " + msg.arg1);
    			break;
    			case TGDevice.MSG_ATTENTION:
    				/*
    				 * Renvoie une valeur entre 0 et 100 sur la capacité d'attention
    				 * 0 : incapacité à calculé une valeur d'attention
    				 * 1 - 20 : tres faible attention
    				 * 20 - 40 : faible attention
    				 * 40 - 60 : valeur d'attention normal
    				 * 60 - 80 : attention relativement élevé
    				 * 80 - 100 : attention élevé
    				 */
    				Log.v("MsgEEG", "Attention: " + msg.arg1);
    				  seriesAttention.appendData( new GraphViewData(passage, msg.arg1), true); 
    				  passage++;
    			break;
    			case TGDevice.MSG_MEDITATION:
    				/*
    				 * Renvoie une valeur entre 0 et 100 sur la capacité de méditation
    				 * 0 : incapacité à calculé une valeur de meditation
    				 * 1 - 20 : tres faible meditation
    				 * 20 - 40 : faible meditation
    				 * 40 - 60 : valeur de meditation normal
    				 * 60 - 80 : meditation relativement élevé
    				 * 80 - 100 : meditation élevé
    				 */
    				Log.v("MsgEEG","Meditation: " +msg.arg1);
    				seriesMeditation.appendData( new GraphViewData(passage, msg.arg1), true); 
    			break;
    			case TGDevice.MSG_RAW_DATA:
    				int rawValue = msg.arg1;
    				int test = msg.arg2;
    				Log.v("MsgRawData", "Raw Data : " +rawValue + " / " +test);
    			break;
    			case TGDevice.MSG_HEART_RATE:
    				Log.v("MsgEEG","Heart Rate " +msg.arg1);
    			break;
    			case TGDevice.MSG_BLINK:
    				seriesBlink.appendData( new GraphViewData(passage, msg.arg1), true);
    				Log.v("MsgEEG","Blink : " +msg.arg1);
    			break;
    			case TGDevice.MSG_SLEEP_STAGE:
    				Log.v("MsgEEG", "Sleep stage : " +msg.arg1);
    			break;
    			case TGDevice.MSG_RAW_MULTI:
                	TGRawMulti rawM = (TGRawMulti)msg.obj;
                	Log.v("MsgRawMulti","Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
    			break;
                case TGDevice.MSG_LOW_BATTERY:
                	Toast.makeText(getApplicationContext(), "Batterie faible !", Toast.LENGTH_SHORT).show();
                break;
    			case TGDevice.MSG_EEG_POWER:
    				TGEegPower ep = (TGEegPower)msg.obj;
    				Log.v("MsgEEGD", "Delta: " + ep.delta);
    				Log.v("MsgEEGGL","Gamma Low : " + ep.lowGamma);
    				Log.v("MsgEEGGM","Gamma Mid : " + ep.midGamma);
    			default:
    			break;
    		}
    	}
    };

    // Création du graphique attention / meditation
    public void createGraph(){

    	graphView = new LineGraphView(this, "Courbes EEG");
    	
    	graphView.addSeries(seriesMeditation);
    	graphView.addSeries(seriesAttention);
    	graphView.addSeries(seriesBlink);
    	
		graphView.setManualYAxisBounds((double) max, (double) min);
		graphView.setShowLegend(true);
		graphView.setViewPort(1,25);
		graphView.setScrollable(true);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
		graphView.getGraphViewStyle().setLegendWidth(200);
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.getGraphViewStyle().setNumVerticalLabels(5);
		graphView.getGraphViewStyle().setNumHorizontalLabels(25);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
		layout.addView(graphView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.settings:
    		// Comportement du bouton "Paramètres"
    		Intent settingsIntent = new Intent(this,SettingsActivity.class);
    		startActivity(settingsIntent);
    		return true;
    	case R.id.propos:
    		// Comportement du bouton "A propos"
    		new AlertDialog.Builder(this)
    	    .setTitle("A propos")
    	    .setMessage("Application réalisé dans le cadre du projet BrainWaves de Licence Pro Dev Web et Mobile d'Orleans.\n" +
    	    		"- Robin Hayart \n" +
    	    		"- Loic Dieudonné \n" +
    	    		"- Chafik Daggag \n" +
    	    		"- Cecile Kergal")
    	    .setIcon(android.R.drawable.ic_dialog_alert)
    	     .show();
    		return true;
    	case R.id.aide:
    		// Comportement du bouton "Aide"
    		Intent aideIntent = new Intent(this,AideActivity.class);
    		startActivity(aideIntent);
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
