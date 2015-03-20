package fr.iut.brainwave;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;








//import com.example.testapp.R;
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

    ArrayList<Integer[]> dataValues = new ArrayList<Integer[]>();
    Integer[] tempValues ={0,0,0};
   //tableau contenant l'ensemble des valeurs 
    ArrayList<Integer> meditationValues = new ArrayList<Integer>();
    ArrayList<Integer> attentionValues = new ArrayList<Integer>();
	
    // Courbe de l'attention (Couleur = rouge / Nom = Attention)
    GraphViewSeries seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), new GraphViewData[] {});
    // Courbe de la méditation (Couleur = bleu / Nom = Meditation)
    GraphViewSeries seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 3), new GraphViewData[] {});
    // Courbe des clins d'oeil (Couleur = vert / Nom = clins d'oeil)
    GraphViewSeries seriesBlink = new GraphViewSeries("Clins d'oeil", new GraphViewSeriesStyle(Color.rgb(0, 200, 50), 3), new GraphViewData[] {});
    
    // Instanciation du GraphView
    GraphView graphView;
	
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
	    	
	    	boolean oldCourbeAttention = courbeAttention;
	    	boolean oldCourbeMeditation = courbeMeditation;
	    	boolean oldCourbeBlink = courbeBlink;
	    	boolean oldValuesRecord = valuesRecord;
	    	courbeAttention = prefs.getBoolean("graph_attention", true);
	    	courbeMeditation = prefs.getBoolean("graph_meditation", true);
	    	courbeBlink = prefs.getBoolean("graph_blink", true);
	    	valuesRecord = prefs.getBoolean("values_record", false);
	    	//timeRecord = Integer.parseInt(prefs.getString("time_record", "30"));

	    	if(!courbeAttention){
	    		graphView.removeSeries(seriesAttention);
	    	}
	    	else if(courbeAttention != oldCourbeAttention){
	    		graphView.addSeries(seriesAttention);
	    	}
	    	
	    	if(!courbeMeditation){
	    		graphView.removeSeries(seriesMeditation);
	    	}
	    	else if(courbeMeditation != oldCourbeMeditation){
	    		graphView.addSeries(seriesMeditation);
	    	}
	    	
	    	if(!courbeBlink){
	    		graphView.removeSeries(seriesBlink);
	    	}
	    	else if(courbeBlink != oldCourbeBlink){
	    		graphView.addSeries(seriesBlink);
	    	}
	    	
	    	if(!courbeAttention && !courbeBlink && !courbeMeditation){
	    		Toast.makeText(getApplicationContext(), "Aucune courbe sélectionné, sélectionnez en une dans les paramètres de l\'application", Toast.LENGTH_LONG).show();
	    	}
	    	
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
    
    /*TimePickerDialog tpd = new TimePickerDialog(this,
            new TimePickerDialog.OnTimeSetListener() {
     
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay,
                        int minute) {
                    txtTime.setText(hourOfDay + ":" + minute);
                }
            }, mHour, mMinute, false);
    tpd.show();*/
    
    private void TimeBox(){
    	// Changer le type ALertDialog en TimePickerDialog
    	
    	AlertDialog.Builder timeBox;
    	timeBox = new AlertDialog.Builder(this);
    	timeBox.setTitle("timebox");
    	timeBox.setIcon(R.drawable.ic_action_time);
    	timeBox.setMessage("Durée de l'enregistrement");
    	timeBox.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
    	});
    	timeBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
    		@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
    	});
    	timeBox.show();
    }

	/**
     * Permet d'enregistrer les données recueillies dans un laps de temps
     */
    private void startRecord() {
		
    	Log.v("MsgRecordStart", "Passage");
    	getAttention = true;
    	getMeditation = true;
    	
    	dataValues = new ArrayList<Integer[]>();
        meditationValues = new ArrayList<Integer>();
        attentionValues = new ArrayList<Integer>();
    	
    	Timer timer = new Timer();
    	
    	timer.schedule(new TimerTask() {
    		  @Override
    		  public void run() {
    			  Log.v("MsgRecordRun", "Passage run");
    			  Log.v("MsgRecordRun", "ALM : "+meditationValues);
    		    //Code pour insertion dans le csv puis arret
    			  valuesRecord = false;
    			  getAttention = false;
    			  getMeditation = false;
    			  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    			  SharedPreferences.Editor editor = prefs.edit();
    			  editor.putBoolean("values_record", false);
    			  editor.commit();
    			  Log.v("MsgRecordRun", "test1");
    			  ArrayList<String> entete = new ArrayList<String>();
    			  Date d = new Date();
    			  SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy'-'HH:mm:ss");
    			  String s = f.format(d);
    			  entete.add("Enregistrement BrainWaves du "+ s);
    			  entete.add("\n");
    			  entete.add("Durée de l'enregistrement : "+ timeRecord +" secondes");
    			  entete.add("\n");
    			  entete.add("\n");
    			  entete.add("Valeurs des courbes");
    			  entete.add("\n");
    			  entete.add("Time");
    			  entete.add(";");
    			  entete.add("Attention");
    			  entete.add(";");
    			  entete.add("Meditation");
    			  entete.add("\n");
    			  Log.v("MsgRecordRun", "test2");
    			
    			
    			  f = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    			  s = f.format(d);
    			  csvWriter csvFile = new csvWriter("recordFile"+ s +".csv");
    			  
    			  Log.v("MsgRecordRun", "test5");
    			  csvFile.addCSVTwoList(dataValues, meditationValues, attentionValues, entete);
    			  Log.v("MsgRecordRun", "test6");
    			 // Toast.makeText(getApplicationContext(),"Fichier CSV Sauvegardé",Toast.LENGTH_LONG).show();
    		  }
    		}, timeRecord*1000);
	}
    
    /**
     * Handler du ThinkGear Device (thread qui traite constamment les données reçus)
     */
    private final Handler handler = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    			case TGDevice.MSG_STATE_CHANGE:
    				switch (msg.arg1) {
    					case TGDevice.STATE_IDLE:
							Log.d("DEBUG", "STATE_IDLE");
    					break;
    					case TGDevice.STATE_CONNECTING:
    						Toast.makeText(getApplicationContext(), "Connection en cours ...", Toast.LENGTH_SHORT).show();
    						Log.v("Statut", "Connection en cours ...");
						break;
						case TGDevice.STATE_CONNECTED:
							Log.v("Statut", "Connecté");
							Toast.makeText(getApplicationContext(), "Connecté !", Toast.LENGTH_SHORT).show();
							tgDevice.start();
						break;
						case TGDevice.STATE_DISCONNECTED:
							Log.d("DEBUG", "STATE_DISCONNECTED");
							Toast.makeText(getApplicationContext(), "Systeme déconnecté !", Toast.LENGTH_SHORT).show();					
						break;
						case TGDevice.STATE_NOT_FOUND:
							Log.d("DEBUG", "STATE_NOT_FOUND");
							Toast.makeText(getApplicationContext(), "Systeme non trouvé !", Toast.LENGTH_SHORT).show();
							finish(); //Nous renvoie sur le menu
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
    					  attentionValues.add(msg.arg1);
    					  Date d = new Date();
    	    			  SimpleDateFormat f = new SimpleDateFormat("HHmmss");
    	    			  String s = f.format(d);
    	    			  tempValues[0]=Integer.parseInt(s);
    					  tempValues[1]=msg.arg1;
    				  }
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
  				    if(getMeditation){
					    meditationValues.add(msg.arg1);
					    tempValues[2]=msg.arg1;
				    }
  				  
  				  dataValues.add(tempValues);
  				  
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

    /**
     * Ajoute des objets dans la barre d'action
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
	 * Permet de gerer le bluetooth
	 * Lance un intent de GestionBluetooth
	 * @param view
	 */
	public void csvWriter(View view)
	{
		Intent intent = new Intent();
		intent.setClass(this, csvWriter.class);
		startActivity(intent);
	}
	
// AJout DIana
	
	private TextView tvDisplayTime;
	private TimePicker timePicker1;
 
	private int hour;
	private int minute;
 
	static final int TIME_DIALOG_ID = 999;
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, 
                                        timePickerListener, hour, minute,false);
 
		}
		return null;
	}
 
	private TimePickerDialog.OnTimeSetListener timePickerListener = 
            new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
 
			// set current time into textview
			tvDisplayTime.setText(new StringBuilder().append(pad(hour))
					.append(":").append(pad(minute)));
 
			// set current time into timepicker
			timePicker1.setCurrentHour(hour);
			timePicker1.setCurrentMinute(minute);
 
		}
	};
 
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
 // Fin Ajout Diana   
    
    /**
     * Ajoute des objets dans la barre d'action :
     * - Paramètres : lance un nouvel intent de SettingsActivity
     * - A propos : lance une boîte de dialogue avec les noms des développeurs de l'application
     * - Aide : lance un nouvel intent avec une page de memo
     * - quitter
     */
    @SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.saveCSV:
    		startRecord();
    		return true;
    	case R.id.time:
    		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    		//timeRecord = Integer.parseInt(prefs.getString("time_record", "30"));
    		//Toast.makeText(getApplicationContext(),"TEST TIME",Toast.LENGTH_LONG).show();
    		
    		// A voir pour le time picker : http://www.mkyong.com/android/android-time-picker-example/
    		showDialog(TIME_DIALOG_ID);
    		//TimeBox();
    		return true;
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
    	    		"- Cecile Kergall")
    	    .setIcon(android.R.drawable.ic_dialog_alert)
    	     .show();
    		return true;
    	case R.id.aide:
    		// Comportement du bouton "Aide"
    		Intent aideIntent = new Intent(this,AideActivity.class);
    		startActivity(aideIntent);
    		return true;
    	case R.id.quitter:
    		tgDevice.close();
    		// Comportement du bouton "Quitter"
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
