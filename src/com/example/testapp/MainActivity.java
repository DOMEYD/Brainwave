package com.example.testapp;

// IMPORTS
// Imports package android (divers)
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
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
	
    // Courbe de l'attention (Couleur = rouge / Nom = Attention)
    GraphViewSeries seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), new GraphViewData[] {

      });
    // Courbe de la méditation (Couleur = bleu / Nom = Meditation)
    GraphViewSeries seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 3), new GraphViewData[] {

    });
	
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
    						Log.v("Test1", "Connecting");
    						Toast.makeText(getApplicationContext(), "Connection en cours ...", Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_CONNECTED:
							Log.v("Test1", "Connected");
							Toast.makeText(getApplicationContext(), "Connecté !", Toast.LENGTH_SHORT).show();
							tgDevice.start();
						break;
						case TGDevice.STATE_DISCONNECTED:
							Toast.makeText(getApplicationContext(), "Systeme déconnecté !", Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_NOT_FOUND:
							Toast.makeText(getApplicationContext(), "Systeme non trouvé !", Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_NOT_PAIRED:
							Log.v("Test1", "Not paired");
							tgDevice.connect(true);
						default:
						break;
    				}
    			break;
    			case TGDevice.MSG_POOR_SIGNAL: // Indique la qualité du signal (0 indique un bon fonctionnement)
    				Log.v("HelloEEG", "PoorSignal: " + msg.arg1);
    			break;
    			case TGDevice.MSG_ATTENTION:
    				/*
    				 * Renvoie une valeur entre 0 et 100 sur la capacité d'attention
    				 * 0 : incapacité à calculé une valeur d'attention
    				 * 1 - 20 : faible attention
    				 * 20 - 40 : peu d'attention
    				 * 40 - 50 : valeur d'attention normal
    				 * 50 - 80 : attention relativement élevé
    				 * 80 - 100 : attention élevé
    				 */
    				Log.v("HelloEEG", "Attention: " + msg.arg1);
    				  seriesAttention.appendData( new GraphViewData(passage, msg.arg1), true); 
    				  passage++;
    			break;
    			case TGDevice.MSG_MEDITATION:
    				Log.v("HelloEEG","Meditation: " +msg.arg1);
    				seriesMeditation.appendData( new GraphViewData(passage, msg.arg1), true); 
    			break;
    			case TGDevice.MSG_RAW_DATA:
    				int rawValue = msg.arg1;
    				Log.v("HelloRawData", "Raw Data : " +rawValue);
    			break;
    			case TGDevice.MSG_HEART_RATE:
    				Log.v("HelloEEG","Heart Rate " +msg.arg1);
    			break;
    			case TGDevice.MSG_BLINK:
    				Log.v("HelloEEG","Blink : " +msg.arg1);
    			break;
    			case TGDevice.MSG_SLEEP_STAGE:
    				Log.v("HelloEEG", "Sleep stage : " +msg.arg1);
    			break;
    			case TGDevice.MSG_RAW_MULTI:
                	TGRawMulti rawM = (TGRawMulti)msg.obj;
                	Log.v("HelloRawMulti","Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
    			break;
                case TGDevice.MSG_LOW_BATTERY:
                	Toast.makeText(getApplicationContext(), "Batterie faible !", Toast.LENGTH_SHORT).show();
                break;
    			case TGDevice.MSG_EEG_POWER:
    				TGEegPower ep = (TGEegPower)msg.obj;
    				Log.v("HelloEEGD", "Delta: " + ep.delta);
    				Log.v("HelloEEGGL","Gamma Low : " + ep.lowGamma);
    				Log.v("HelloEEGGM","Gamma Mid : " + ep.midGamma);
    			default:
    			break;
    		}
    	}
    };

    // Création du graphique attention / meditation
    public void createGraph(){
    	
        // Instanciation du GraphView
        GraphView graphView = new LineGraphView( 
                this, 
                "Activité"
        );

		graphView.setManualYAxisBounds((double) max, (double) min);
		graphView.addSeries(seriesAttention);
		graphView.addSeries(seriesMeditation);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}