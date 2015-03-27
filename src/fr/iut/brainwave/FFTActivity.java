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

import com.jjoe64.graphview.BarGraphView;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class FFTActivity extends Activity {
	
	private	Dialog AddFileDialog = null;
	private	Dialog SelectGraph = null;
	 int flagInit = 0;
	public ArrayList<File> ArrayListCsvFiles = new ArrayList<File>();
	private ListView LvAllCsvFiles;
	public ArrayAdapter<String> AdaptateurFiles;
	public ArrayList<Integer[]> ArrayDataImport = new ArrayList<Integer[]>();
	
	// Courbe de l'attention (Couleur = rouge / Nom = Attention)
    GraphViewSeries seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 1), new GraphViewData[] {});
    // Courbe de la méditation (Couleur = bleu / Nom = Meditation)
    GraphViewSeries seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 1), new GraphViewData[] {});
    // Instanciation du GraphView
    GraphView graphView;
	double max = 100 ;
	double min = 0;
	
	
	private void AddFilesBox(){
		AddFileDialog = new Dialog(FFTActivity.this);
		AddFileDialog.setTitle(getString(R.string.paramsFilesDialogTitle));
		AddFileDialog.setContentView(R.layout.dialog_list);
        Button b1 = (Button) AddFileDialog.findViewById(R.id.button_cancel);

		//Titres des listes
		AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		LvAllCsvFiles = (ListView) AddFileDialog.findViewById(R.id.lvCSVFiles);	
		LvAllCsvFiles.setOnItemClickListener(mCSVClickListener);
		        
		getListFiles();
		
        b1.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        	 AddFileDialog.dismiss(); // dismiss the dialog
	         }    
        });
        
        AddFileDialog.show();
	}
	
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
						
							ArrayDataImport = new ArrayList<Integer[]>();
							try {
								Log.d("TEST", "try");
								
								ArrayDataImport=ReaderCSVFile(ArrayListCsvFiles.get(i));
													
								dessiner_graph();
								AddFileDialog.dismiss();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							break;
					}
				}
			}
	 };
	 
	 
	 /***lecture du fichier CSV
		 * @throws IOException ***/
		 public ArrayList<Integer[]> ReaderCSVFile (File file) throws IOException{
				Log.v("READER", "test1");
				ArrayList<Integer[]> dataValues = new ArrayList<Integer[]>();
				Integer[] tempValues = new Integer[3];
			
				String[] temp;
				
				try {
					
					FileReader fr = new FileReader(file);
					 BufferedReader br = new BufferedReader(fr);
					 String line = br.readLine();
					 line = br.readLine();
					 line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					 line = br.readLine();
					 line = br.readLine();
					
				       while( line != null) {
				    	  
				           temp= line.split(";");
				           for(int i=0;i<temp.length;i++)
				           {
				        	   try{
					        	   tempValues[i]=Integer.parseInt(temp[i]);
				        	   }catch(NumberFormatException e){
				        		   
				        	   }

				           }
				                  
				           dataValues.add(tempValues);		
				         
				           tempValues = new Integer[3];
				           line = br.readLine();
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

		    	graphView = new LineGraphView(this, "Courbes EEG");
		    	
				graphView.setManualYAxisBounds((double) max, (double) min);
				graphView.setShowLegend(true);
				graphView.setViewPort(1,19);
				graphView.setScrollable(true);
				graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
				graphView.getGraphViewStyle().setLegendWidth(200);
				graphView.setLegendAlign(LegendAlign.TOP);
				graphView.getGraphViewStyle().setNumVerticalLabels(5);
				graphView.getGraphViewStyle().setNumHorizontalLabels(20);
				graphView.getGraphViewStyle().setTextSize(15);
				LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
				layout.addView(graphView);
		    }	 
	
		 
		 public void dessiner_graph()
			{
				int i=0;
				int j=0;
			    graphView.removeSeries(seriesMeditation);
			    graphView.removeSeries(seriesAttention);
			   	seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 2), new GraphViewData[] {});
			    seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200),2), new GraphViewData[] {});
				graphView.addSeries(seriesMeditation);
			    
			    	for(i=0;i<ArrayDataImport.size();i++)
					{
						seriesAttention.appendData( new GraphViewData(ArrayDataImport.get(i)[0], ArrayDataImport.get(i)[1]), true);
						seriesMeditation.appendData( new GraphViewData(ArrayDataImport.get(i)[0], ArrayDataImport.get(i)[2]), true);
					}	    
			    	
			}
		 
		 public void newGraphFFT(){

		    	graphView = new BarGraphView(this, "Courbes EEG");
		    	
				graphView.setManualYAxisBounds((double) max, (double) min);
				graphView.setShowLegend(true);
				graphView.setViewPort(1,19);
				graphView.setScrollable(true);
				graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
				graphView.getGraphViewStyle().setLegendWidth(200);
				graphView.setLegendAlign(LegendAlign.TOP);
				graphView.getGraphViewStyle().setNumVerticalLabels(5);
				graphView.getGraphViewStyle().setNumHorizontalLabels(20);
				graphView.getGraphViewStyle().setTextSize(15);
				LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
				layout.addView(graphView);
		    }	 
		 
		 private void SelectGraphBox(){    	
		    	final Dialog SelectGraph = new Dialog(FFTActivity.this);
		    	SelectGraph.setTitle(getString(R.string.paramsTimeDialogTitle));
		    	SelectGraph.setContentView(R.layout.dialog_import);
		        Button b1 = (Button) SelectGraph.findViewById(R.id.button_validation);
		        Button b2 = (Button) SelectGraph.findViewById(R.id.button_cancel);
		        
		        final RadioButton RB_meditaton = (RadioButton) SelectGraph.findViewById(R.id.radioButton_meditation);
		        final RadioButton RB_attention = (RadioButton) SelectGraph.findViewById(R.id.radioButton_attention);
		        final RadioButton RB_clindoeil = (RadioButton) SelectGraph.findViewById(R.id.radioButton_clindoeil);

		        RB_meditaton.setChecked(true);
		             
		        
		        RB_meditaton.setOnClickListener( new View.OnClickListener() {
		    		@Override
		    		public void onClick(View v) {
		    		    RB_meditaton.setChecked(true);
		    		    RB_attention.setChecked(false);
		    		    RB_clindoeil.setChecked(false);
		    		    flagInit = 1;	    		    
		    		}
		    	});
		        RB_attention.setOnClickListener( new View.OnClickListener() {
		    		@Override
		    		public void onClick(View v) {
		    		    RB_meditaton.setChecked(false);
		    		    RB_attention.setChecked(true);
		    		    RB_clindoeil.setChecked(false);
		    		    flagInit = 2;
		    		}
		    	});
		        RB_clindoeil.setOnClickListener( new View.OnClickListener() {
		    		@Override
		    		public void onClick(View v) {
		    		    RB_meditaton.setChecked(false);
		    		    RB_attention.setChecked(false);
		    		    RB_clindoeil.setChecked(true);
		    		    flagInit = 3;
		    		}
		    	});
		        b1.setOnClickListener(new OnClickListener() {
			         @Override
			         public void onClick(View v) {
			        	 graphView.removeSeries(seriesMeditation);
			     		    graphView.removeSeries(seriesAttention);
			        	 
			        	 if(RB_meditaton.isChecked()==true){
			 	        	graphView.addSeries(seriesMeditation);
			 	        	flagInit = 0;
			 	        }
			        	 else if(RB_attention.isChecked()==true){
			 	        	graphView.addSeries(seriesAttention);
			 	        	flagInit = 0;
			 	        }
			        	 else if(RB_clindoeil.isChecked()==true){
			 	        	flagInit = 0;
			 	        }
			        	 SelectGraph.dismiss();
			          }    
		        });
		        b2.setOnClickListener(new OnClickListener() {
			         @Override
			         public void onClick(View v) {
			        	 SelectGraph.dismiss(); // dismiss the dialog
			         }    
		        });
		        
		        SelectGraph.show();
		    }
		 
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fft);
		
		createGraph();
	}
	
    /**
     * MANAGE action bar
     */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fft_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addSave:
				AddFilesBox();
				return true;
			case R.id.applyFFT:
				newGraphFFT();
				dessiner_graph();
					return true;
			case R.id.selectCurve:
				SelectGraphBox();
				
				return true;
			default: 
	    		return super.onOptionsItemSelected(item);
		}
	}
}