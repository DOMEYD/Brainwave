package fr.iut.brainwave;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.neurosky.thinkgear.TGDevice;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class CompareActivity extends Activity {

	private	Dialog d = null;
	public ArrayList<File> ArrayListCsvFiles = new ArrayList<File>();
	private ListView LvAllCsvFiles;
	private TextView TvFilesTitle;
	public ArrayAdapter<String> AdaptateurFiles;
	public ArrayList<Integer[]> ArrayDataImportFile = new ArrayList<Integer[]>();
	
    // Courbe de l'attention (Couleur = rouge / Nom = Attention)
    GraphViewSeries seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), new GraphViewData[] {});
    // Courbe de la méditation (Couleur = bleu / Nom = Meditation)
    GraphViewSeries seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 3), new GraphViewData[] {});
   
    // Instanciation du GraphView
    GraphView graphView;
	

	private void getListFiles() {
		File parentDir = Environment.getExternalStoragePublicDirectory("org.BrainWaves");
		File[] filesCSV = parentDir.listFiles();

		AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		LvAllCsvFiles.setAdapter(AdaptateurFiles);
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRANCE);
		
		for (File file : filesCSV) {			
			AdaptateurFiles.add(file.getName()+"\n"+ sdfDate.format(file.lastModified()) );
			ArrayListCsvFiles.add(file);
		}   
	}
	

	
	private void FilesBox(){
			d = new Dialog(CompareActivity.this);
	        d.setTitle(getString(R.string.paramsFilesDialogTitle));
	        d.setContentView(R.layout.dialog_list);
	        Button b1 = (Button) d.findViewById(R.id.button_validation);
	        Button b2 = (Button) d.findViewById(R.id.button_cancel);

			//Titres des listes
			AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
			
			LvAllCsvFiles = (ListView) d.findViewById(R.id.lvCSVFiles);	
			LvAllCsvFiles.setOnItemClickListener(mCSVClickListener);
			        
			getListFiles();
			
	        b1.setOnClickListener(new OnClickListener() {
		         @Override
		         public void onClick(View v) {
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
	
	 public OnItemClickListener mCSVClickListener = new OnItemClickListener() {
		 @Override
			public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
			{			
				String info = ((TextView) v).getText().toString();
				String fileName = info.substring(0, 29);
				Log.v("FILENAME", fileName);
				
				Log.d("Nbr CSV",  ArrayListCsvFiles.size()+"");
				
				for(int i = 0; i < ArrayListCsvFiles.size(); i++) {
					Log.d("TEST", "TURN");
					if(ArrayListCsvFiles.get(i).getName().equals(fileName)) {
						
						ArrayDataImportFile = new ArrayList<Integer[]>();
						try {
							ArrayDataImportFile = ReaderCSVFile(ArrayListCsvFiles.get(i));
							dessiner_graph();
							d.dismiss();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}
				}
				
			}
	 };
	
	 /***lecture du fichier CSV
	 * @throws IOException ***/
	 public ArrayList<Integer[]> ReaderCSVFile (File file) throws IOException{
			
			ArrayList<Integer[]> dataValues = new ArrayList<Integer[]>();
			Integer[] tempValues={0,0,0};
		
			String[] temp;
			
			try {
				FileReader fr = new FileReader(file);
				 BufferedReader br = new BufferedReader(fr);
				 String line = br.readLine();
			       while( line != null) {
			           temp= line.split(";");
			           tempValues[0]=Integer.parseInt(temp[0]);
			           tempValues[1]=Integer.parseInt(temp[1]);
			           tempValues[2]= Integer.parseInt(temp[2]);
			           dataValues.add(tempValues);		        		   
			        }

			        br.close();
			        fr.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return dataValues;
		      
		}
	
	    public void createGraph(){
	    	double max = 100 ;
	    	double min = 0;
	    	graphView = new LineGraphView(this, "Courbes EEG");
	    	
	    	graphView.addSeries(seriesMeditation);
	    	graphView.addSeries(seriesAttention);
	    	
			graphView.setManualYAxisBounds((double) max, (double) min);
			graphView.setShowLegend(true);
			graphView.setViewPort(1,19);
			graphView.setScrollable(true);
			graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
			graphView.getGraphViewStyle().setLegendWidth(200);
			graphView.setLegendAlign(LegendAlign.TOP);
			graphView.getGraphViewStyle().setNumVerticalLabels(5);
			graphView.getGraphViewStyle().setNumHorizontalLabels(20);
			graphView.getGraphViewStyle().setTextSize(14);
			LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
			layout.addView(graphView);
	    }
	
	/*****************************PARTIE CODE CORRECTE***************************/
	public void dessiner_graph()
	{
		int i=0;
		
		for(i=0;i<=ArrayDataImportFile.size();i++)
		{
			seriesAttention.appendData( new GraphViewData(ArrayDataImportFile.get(i)[0], ArrayDataImportFile.get(i)[1]), true);
			seriesMeditation.appendData( new GraphViewData(ArrayDataImportFile.get(i)[0], ArrayDataImportFile.get(i)[2]), true);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		
		createGraph();
	}

	
    /**
     * MANAGE action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compare_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addSave:
				FilesBox();
				return true;
			default: 
	    		return super.onOptionsItemSelected(item);
		}
	}
	
	
}