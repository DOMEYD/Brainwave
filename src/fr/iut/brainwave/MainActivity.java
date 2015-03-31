package fr.iut.brainwave;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;

/**
 * Permet l'affichage du graphique avec les différentes données recueillies en temps réel :
 * - Attention
 * - Méditation
 * - Clins d'oeil
 * @author Robin, Chafik, Loïc, Cécile
 *
 */

public class MainActivity extends Activity {

	//Déclaration des variables
	TGDevice tgDevice;
	BluetoothAdapter btAdapter;
	
	// Echelle Y du graph pour Meditation et Attention
    double max = 100 ;
    double min = 0;
    
    int passage = 1;
    
    //variables par défaut du menu de paramètres
    boolean courbeAttention = true;
    boolean courbeMeditation = true;
    boolean courbeBlink = true;
    boolean valuesRecord = false;
    int timeRecord = 30;
    
    //getter de valeurs
    boolean getAttention = false;
    boolean getMeditation = false;
    boolean getBlink = false;
    boolean getRawData = false;
    int flag_courbe = 0;
    ArrayList<Integer[]> dataValues = new ArrayList<Integer[]>();
    ArrayList<Integer[]> rawDataValues = new ArrayList<Integer[]>();
    Integer[] tempValues = new Integer[11];
    Integer time_record=0;

	/*
	 * Attention : red with name 'Attention'
	 * Meditation : blue with name 'Meditation'
	 * Blink : green with name 'Clin d'oeil'
	 */
    GraphViewSeries seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), new GraphViewData[] {});
    GraphViewSeries seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 3), new GraphViewData[] {});
    GraphViewSeries seriesBlink = new GraphViewSeries("Clins d'oeil", new GraphViewSeriesStyle(Color.rgb(0, 200, 50), 3), new GraphViewData[] {});
    
    // Instanciation du GraphView
    GraphView graphView;
    
    MenuItem MenuBatteryItem;
    MenuItem MenuSaveItem;
	
    /**
	 * Permet de trouver les listes des devices appairés et ceux dans la portée du bluetooth
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) { 
        	finish();
        }
        
        // RETRIEVE mac address
		SharedPreferences settings = getSharedPreferences("Bluetooth", MODE_PRIVATE);
		String macAdd = settings.getString("MAC-Address", "NO_MACADDRESS");
		
    	tgDevice = new TGDevice(btAdapter, handler);
		
		if(macAdd.equals("NO_MACADDRESS")) {
			tgDevice.connect( true );
		}
		else {
			BluetoothDevice device = null;
			
	        for(BluetoothDevice bt : btAdapter.getBondedDevices()) {
	        	if(bt.getAddress().equals(macAdd)) {
	        		device = bt;
	        	}
			}
	                
	        try {
	        	Log.d("Device", device.getName());
	            
	            tgDevice.connect(device, true);
	        } catch(NullPointerException e) {
	        	Toast.makeText(getApplicationContext(), getString(R.string.NoBTAppair), Toast.LENGTH_LONG).show();
	        	finish();
	        }
		}
        createGraph();
        
	}
    
    /**
     * Herite de la methode onResume() et lance la méthode gestionParametre()
     */
    protected void onResume(){
    	super.onResume();
    	
    	gestionParametre();
    }
    
    /**
     * Permet de gérer les paramètres
     */
    protected void gestionParametre(){
    	try{
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	
	
	    	boolean oldValuesRecord = valuesRecord;
	    
	    	valuesRecord = prefs.getBoolean("values_record", false);
	    	timeRecord = Integer.parseInt(prefs.getString("time_record", "30"));

	    		    	
	    	Log.v("MsgRecordParam", "ValuesRecord : "+valuesRecord);
	    	
	    	if(valuesRecord){
	    		startRecord();
	    	}
	    	else if(!valuesRecord && oldValuesRecord){
	    		
	    	}
    	}catch(Exception exc){
    		Log.e("errorResume", "Erreur : " +exc);
    	}
    }
    
    private void TimeBox(){    	
    	final Dialog d = new Dialog(MainActivity.this);
        d.setTitle(getString(R.string.paramsTimeDialogTitle));
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final int step = 5;
        np.setMaxValue(180/step);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false); // prevent 360 selector
        Log.d("TIMERECORD", ""+timeRecord);
        np.setValue(timeRecord / step);
        np.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
            	return String.valueOf(i*step);
            }
        });
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // DISABLE keyboard
        np.setFocusable(false);

        b1.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        	 // SET new value
	        	 timeRecord = np.getValue() * step;
	        	 
	        	 // SETting default value
	        	 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        	 SharedPreferences.Editor editor = prefs.edit();
	        	 editor.putString("time_record", "" +timeRecord);
	        	 editor.commit();
	        	 
	        	 // CLOSE dialog box
	             d.dismiss();
	          }    
        });
        b2.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	            d.dismiss(); // dismiss the dialog
	         }    
        });
        
        d.show();
    }
    
	// TODO : Change name + manage
    private void selectBox(){    	
    	final Dialog d = new Dialog(MainActivity.this);
        d.setTitle(getString(R.string.paramsTimeDialogTitle));
        d.setContentView(R.layout.dialog_select);
        Button b1 = (Button) d.findViewById(R.id.button_validation);
        Button b2 = (Button) d.findViewById(R.id.button_cancel);
        
        final CheckBox CB_meditation = ( CheckBox) d.findViewById(R.id.CB_meditation);
        final CheckBox CB_attention = ( CheckBox) d.findViewById(R.id.CB_attention);
        final CheckBox CB_clindoeil = ( CheckBox) d.findViewById(R.id.CB_clindoeil);
        
        switch (flag_courbe){
	        case 1: CB_meditation.setChecked(true);
	            break;
	        case 2: CB_attention.setChecked(true);
	            break;
	        case 3: CB_attention.setChecked(true);
	        		CB_meditation.setChecked(true);
	            break;
	        case 4: CB_clindoeil.setChecked(true);
	            break;
	        case 5: CB_clindoeil.setChecked(true);
	        		CB_meditation.setChecked(true);
	            break;
	        case 6: CB_attention.setChecked(true);
	        		CB_clindoeil.setChecked(true);
	            break;
	        case 7:  CB_meditation.setChecked(true);
	        		CB_attention.setChecked(true);
	        		CB_clindoeil.setChecked(true);
	            break;
            
        }
        
        
        b1.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        	 flag_courbe=0;
	        	 graphView.removeSeries(seriesMeditation);
	        	 graphView.removeSeries(seriesAttention);
	        	 graphView.removeSeries(seriesBlink);
	        	 if(CB_meditation.isChecked())
	        	 {
	        		 graphView.addSeries(seriesMeditation);
	        		 flag_courbe= flag_courbe+1;
	        	 }
	        	 if(CB_attention.isChecked())
	        	 {
	        		 graphView.addSeries(seriesAttention);
	        		 flag_courbe= flag_courbe+2;
	        	 }
	        	 if(CB_clindoeil.isChecked())
	        	 {
	        		 graphView.addSeries(seriesBlink);
	        		 flag_courbe= flag_courbe+4;
	        	 }
	        	 
	             d.dismiss();
	          }    
        });
        b2.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	            d.dismiss(); // dismiss the dialog
	         }    
        });
        
        d.show();
    }

	/**
     * Permet d'enregistrer les données recueillies dans un laps de temps
     */
    private void startRecord() {
		
    	Log.v("Statut", "Lancement Record");
    	
    	getAttention = true;
    	getMeditation = true;
    	time_record=0;
    	
    	dataValues = new ArrayList<Integer[]>();
    	rawDataValues = new ArrayList<Integer[]>();
       
        Toast.makeText(getApplicationContext(), "Début de l'enregistrement", Toast.LENGTH_LONG).show();
        MenuSaveItem.setIcon(R.drawable.ic_action_save_on);
    	Timer timer = new Timer();
    	
    	timer.schedule(new TimerTask() {
    		  @Override
    		  public void run() {
    			  Log.v("Statut", "Fin du timer");

    			  valuesRecord = false;
    			  getAttention = false;
    			  getMeditation = false;

    			  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    			  SharedPreferences.Editor editor = prefs.edit();
    			  editor.putBoolean("values_record", false);
    			  editor.commit();
    			  
    			  ArrayList<String> entete = new ArrayList<String>();
    			  ArrayList<String> enteteRaw = new ArrayList<String>();
    			  
    			  Date d = new Date();
    			  SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy'-'HH:mm:ss", Locale.FRANCE);
    			  String s = f.format(d);
    			  entete.add("Enregistrement BrainWaves du "+ s+"\n");
    			  entete.add("Durée de l'enregistrement : "+ time_record +" secondes\n\n");
    			  entete.add("Valeurs des courbes\n");
    			  entete.add("Time;Attention;Meditation;Delta;Theta;Alpha Low;Alpha High;Beta Low;Beta High;Gamma Low;Gamma High\n");
    			  
    			  enteteRaw.add("Enregistrement BrainWaves du "+ s+"\n");
    			  enteteRaw.add("Durée de l'enregistrement : "+ time_record +" secondes\n\n");
    			  enteteRaw.add("Valeurs du Raw DATA\n");
    			  enteteRaw.add("Time;RawData\n");
    			  
    			  // OLD file		
    			  f = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.FRANCE);
    			  s = f.format(d);
    			  csvWriter csvFile = new csvWriter("recordFile"+ s +".csv");
    			  csvFile.addCSVTwoList(dataValues, time_record, entete);
    			  
    			  // NEW FILE with raw data
    			  csvWriter rawCSVFile = new csvWriter("rawRecord" + s + ".csv");
    			  rawCSVFile.createCSV(rawDataValues, enteteRaw);
    		  }
    	}, (timeRecord+1)*1000);
	}
    
    /**
     * Handler du ThinkGear Device (thread qui traite constamment les données reçus)
     */
    private final Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
    	public void handleMessage(Message msg) {
			Integer[] tempRawArray;
			switch (msg.what) {
    			case TGDevice.MSG_STATE_CHANGE:
    				switch (msg.arg1) {
    					case TGDevice.STATE_IDLE:
							Log.d("DEBUG", "STATE_IDLE");
    					break;
    					case TGDevice.STATE_CONNECTING:
    						Toast.makeText(getApplicationContext(), getString(R.string.connecting), Toast.LENGTH_SHORT).show();
    						Log.v("Statut", getString(R.string.connecting));
						break;
						case TGDevice.STATE_CONNECTED:
							Log.v("Statut", "Connecté");
							Toast.makeText(getApplicationContext(), getString(R.string.connected), Toast.LENGTH_SHORT).show();
							tgDevice.start();
						break;
						case TGDevice.STATE_DISCONNECTED:
							Log.d("DEBUG", "STATE_DISCONNECTED");
							Toast.makeText(getApplicationContext(), getString(R.string.disconnect), Toast.LENGTH_SHORT).show();					
						break;
						case TGDevice.STATE_NOT_FOUND:
							Log.d("DEBUG", "STATE_NOT_FOUND");
							Toast.makeText(getApplicationContext(), getString(R.string.sysNotFound), Toast.LENGTH_SHORT).show();
							finish();
						break;
						case TGDevice.STATE_ERR_NO_DEVICE:
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
    				  
    				  if(getAttention){
    					 
    					  tempValues[1]=msg.arg1;
    				  }
    				  
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
  				    if(getMeditation){
  				    	
  				    	tempValues[0]=time_record;
					    tempValues[2]=msg.arg1;
					    dataValues.add(tempValues);
					    tempValues = new Integer[11];
					    time_record ++;
					   
					    if(time_record>timeRecord)
					    {
					    	 Toast.makeText(getApplicationContext(),"CSV enregistré",Toast.LENGTH_LONG).show();
					         MenuSaveItem.setIcon(R.drawable.ic_action_save);
					    }
				    }
  				  passage++;
    			break;
    			case TGDevice.MSG_RAW_DATA:
//    				int rawValue = msg.arg1;
//    				int test = msg.arg2;
//    				Log.v("MsgRawData", "Raw Data : " +rawValue + " / " +test);
    				tempRawArray = new Integer[2];
    				tempRawArray[0] = (int) System.nanoTime();
    				tempRawArray[1] = msg.arg1;
    				rawDataValues.add(tempRawArray);
    				
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
                	MenuBatteryItem.setIcon(R.drawable.ic_battery_low);
                break;
    			case TGDevice.MSG_EEG_POWER:
    				TGEegPower ep = (TGEegPower)msg.obj;
    				Log.v("MsgEEGD", "Delta: " + ep.delta);
    				tempValues[3]=ep.delta;
    				Log.v("MsgEEGGM","Theta : " + ep.theta);
    				tempValues[4]=ep.theta;
    				
    				Log.v("MsgEEGD", "Alpha low : " + ep.lowAlpha);
    				tempValues[5]=ep.lowAlpha;
    				Log.v("MsgEEGD", "Alpha high : " + ep.highAlpha);
    				tempValues[6]=ep.highAlpha;

    				Log.v("MsgEEGD", "Beta low : " + ep.lowBeta);
    				tempValues[7]= ep.lowBeta;
    				Log.v("MsgEEGD", "Beta High : " + ep.highBeta);
    				tempValues[8]= ep.highBeta;
    				
    				Log.v("MsgEEGGL","Gamma Low : " + ep.lowGamma);
    				tempValues[9]=ep.lowGamma;
    				Log.v("MsgEEGGM","Gamma Mid : " + ep.midGamma);
    				tempValues[10]=ep.midGamma;
    			default:
    			break;
    		}
    	}
    };

    /**
     * Création du graphique attention / meditation
     */
    public void createGraph(){

    	graphView = new LineGraphView(this, "Courbes EEG");
    	
    	graphView.addSeries(seriesMeditation);
    	graphView.addSeries(seriesAttention);
    	graphView.addSeries(seriesBlink);
    	
		graphView.setManualYAxisBounds((double) max, (double) min);
		graphView.setShowLegend(true);
		graphView.setViewPort(1,19);
		graphView.setScrollable(true);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setLegendWidth(200);
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.getGraphViewStyle().setNumVerticalLabels(21);
		graphView.getGraphViewStyle().setNumHorizontalLabels(20);
		graphView.getGraphViewStyle().setTextSize(14);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
		layout.addView(graphView);
    }

    /**
     * Ajoute des objets dans la barre d'action
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuBatteryItem = menu.findItem(R.id.battery);
        MenuSaveItem = menu.findItem(R.id.saveCSV);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Ajoute des objets dans la barre d'action :
     * - Paramètres : lance un nouvel intent de SettingsActivity
     * - A propos : lance une boîte de dialogue avec les noms des développeurs de l'application
     * - Aide : lance un nouvel intent avec une page de memo
     * - quitter
     */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.selectCurve:
    		selectBox();
    		return true;
    	case R.id.saveCSV: 
    		startRecord();
    		return true;  
    	case R.id.time:	
    		TimeBox();
    		return true;
    	case R.id.settings:	
    		Intent settingsIntent = new Intent(this,SettingsActivity.class);
    		startActivity(settingsIntent);
    		return true;			
    	case R.id.propos:
    		new AlertDialog.Builder(this)
	    		.setTitle("A propos")
	    		.setMessage("Application réalisé dans le cadre du projet BrainWaves de Licence Pro Dev Web et Mobile d'Orleans.\n" +
	    				"- Dimitri DOMEY \n" +
    	    		    "- Diana GRATADE \n" +
	    				"- Benjamin TUILARD ")
    	    	.setIcon(android.R.drawable.ic_dialog_alert)
    	    	.show();
    			return true;  
    	case R.id.aide:	
    		Intent aideIntent = new Intent(this,AideActivity.class);
    		startActivity(aideIntent);
    		return true;
    	case R.id.quitter:	
    		tgDevice.close();
    		finish();
    		return true;
    	default: 
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d("DEBUG", "onDestroy");
    	try {
    		tgDevice.close();
    	} catch(RuntimeException e) {
    		Log.e("ERROR", "Device close connection");
    	}
    }
}
