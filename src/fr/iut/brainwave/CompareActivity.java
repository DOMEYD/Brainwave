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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CompareActivity extends Activity {

	private	Dialog ad = null;
	private	Dialog rd = null;
	private	Dialog gd = null;
	public ArrayList<File> ArrayListCsvFiles = new ArrayList<File>();
	public ArrayList<File> ArrayFilesCharging = new ArrayList<File>();
	private ListView LvAllCsvFiles;
	private ListView LvFilesCharging;

	public ArrayAdapter<String> AdaptateurFiles;
	public ArrayAdapter<String> AdaptateurFilesCharging;
	
	public ArrayList<ArrayList<Integer[]>> ArrayDataImport = new ArrayList<ArrayList<Integer[]>>();
	
	public ArrayList<Integer[]> ArrayDataImportFile = new ArrayList<Integer[]>();
//	public ArrayList<Integer[]> ArrayDataImportFile2 = new ArrayList<Integer[]>();
	
	public Integer importFiles_max =2;
	
    GraphViewSeries seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 2), new GraphViewData[] {});
    GraphViewSeries seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
   
    GraphViewSeries seriesAttention2 = new GraphViewSeries("Attention2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    GraphViewSeries seriesMeditation2 = new GraphViewSeries("Meditation2", new GraphViewSeriesStyle(Color.rgb(0, 50, 100),2), new GraphViewData[] {});
   
    GraphViewSeries seriesDelta = new GraphViewSeries("Delta", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries seriesDelta2 = new GraphViewSeries("Delta2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
  
    GraphViewSeries seriesTheta = new GraphViewSeries("Theta", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries seriesTheta2 = new GraphViewSeries("Theta2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    
    GraphViewSeries serieslowAlpha = new GraphViewSeries("lowAlpha", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries serieslowAlpha2 = new GraphViewSeries("lowAlpha2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    
    GraphViewSeries serieshighAlpha = new GraphViewSeries("highAlpha", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries serieshighAlpha2 = new GraphViewSeries("highAlpha2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    
    GraphViewSeries seriesLowBeta = new GraphViewSeries("LowBeta", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries serieslowBeta2 = new GraphViewSeries("LowBeta2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    
    GraphViewSeries serieshighBeta = new GraphViewSeries("highBeta", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries serieshighBeta2 = new GraphViewSeries("highBeta2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    
    GraphViewSeries seriesLowGamma = new GraphViewSeries("LowGamma", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries serieslowGamma2 = new GraphViewSeries("LowGamma2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    
    GraphViewSeries serieshighGamma = new GraphViewSeries("highGamma", new GraphViewSeriesStyle(Color.rgb(0, 50, 200), 2), new GraphViewData[] {});
    GraphViewSeries serieshighGamma2 = new GraphViewSeries("highGamma2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
    
    // Instanciation du GraphView
    GraphView graphView;
	double max = 100 ;
	double min = 0;
	
	int flagInit = 0;
	int flagCourbe=0;
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
	
	private void getListFilesCharging() {

		AdaptateurFilesCharging = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		LvFilesCharging.setAdapter(AdaptateurFilesCharging);

		//SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRANCE);

		for (File file :ArrayFilesCharging) {	
			
			AdaptateurFilesCharging.add(file.getName()+"\n"/*+ sdfDate.format(file.lastModified()) */);
		}   

	}
	
	private void RemoveFilesBox(){
		
			rd = new Dialog(CompareActivity.this);
	        rd.setTitle(getString(R.string.paramsFilesDialogTitle));
	        rd.setContentView(R.layout.dialog_remove_list);
	        Button b1 = (Button) rd.findViewById(R.id.button_cancel);

			//Titres des listes
	        AdaptateurFilesCharging = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2);

			LvFilesCharging = (ListView) rd.findViewById(R.id.lvCSVFiles);	
			LvFilesCharging.setOnItemClickListener(mCSVClickRemoveListener);

			getListFilesCharging();
			
	        b1.setOnClickListener(new OnClickListener() {
		         @Override
		         public void onClick(View v) {
		        	 rd.dismiss();  
		          }    
	        });
	        
	        rd.show();
	}
	
	private void AddFilesBox(){
		ad = new Dialog(CompareActivity.this);
        ad.setTitle(getString(R.string.paramsFilesDialogTitle));
        ad.setContentView(R.layout.dialog_list);
        Button b1 = (Button) ad.findViewById(R.id.button_cancel);

		//Titres des listes
		AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		LvAllCsvFiles = (ListView) ad.findViewById(R.id.lvCSVFiles);	
		LvAllCsvFiles.setOnItemClickListener(mCSVClickListener);
		        
		getListFiles();
		
        b1.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	            ad.dismiss(); // dismiss the dialog
	         }    
        });
        
        ad.show();
	}
	
	 private void SelectGraphBox(){    	
	    	final Dialog gd = new Dialog(CompareActivity.this);
	        gd.setTitle(getString(R.string.paramsTimeDialogTitle));
	        gd.setContentView(R.layout.dialog_import);
	        Button b1 = (Button) gd.findViewById(R.id.button_validation);
	        Button b2 = (Button) gd.findViewById(R.id.button_cancel);
	        
	        final RadioButton RB_meditaton = (RadioButton) gd.findViewById(R.id.radioButton_meditation);
	        final RadioButton RB_attention = (RadioButton) gd.findViewById(R.id.radioButton_attention);
	        final RadioButton RB_clindoeil = (RadioButton) gd.findViewById(R.id.radioButton_clindoeil);
	        final RadioButton RB_Delta = (RadioButton) gd.findViewById(R.id.radioButton_delta);
	        final RadioButton RB_Theta = (RadioButton) gd.findViewById(R.id.radioButton_theta);
	        final RadioButton RB_LowAlpha = (RadioButton) gd.findViewById(R.id.radioButton_lowalpha);
	        final RadioButton RB_HighAlpha = (RadioButton) gd.findViewById(R.id.radioButton_highalpha);
	        final RadioButton RB_LowBeta = (RadioButton) gd.findViewById(R.id.radioButton_lowbeta);
	        final RadioButton RB_HighBeta = (RadioButton) gd.findViewById(R.id.radioButton_highbeta);
	        final RadioButton RB_LowGamma = (RadioButton) gd.findViewById(R.id.radioButton_lowgamma);
	        final RadioButton RB_HighGamma = (RadioButton) gd.findViewById(R.id.radioButton_highgamme);

	        
	        RB_meditaton.setChecked(true);
	       
	        
	        
	        
	        RB_meditaton.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(true);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 1;	    		    
	    		}
	    	});
	        RB_attention.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(true);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 2;
	    		}
	    	});
	        RB_clindoeil.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(true);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 3;
	    		}
	    	});
	        RB_Delta.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(true);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 4;
	    		}
	    	});
	        RB_Theta.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(true);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 5;
	    		}
	    	});
	        RB_LowAlpha.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(true);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 6;
	    		}
	    	});
	        RB_HighAlpha.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(true);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 7;
	    		}
	    	});
	        RB_LowBeta.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 8;
	    		}
	    	});
	        RB_HighBeta.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 9;
	    		}
	    	});
	        RB_LowGamma.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(true);
	    	       RB_HighGamma.setChecked(false);
	    		    flagInit = 10;
	    		}
	    	});
	        RB_HighGamma.setOnClickListener( new View.OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    		    RB_meditaton.setChecked(false);
	    		    RB_attention.setChecked(false);
	    		    RB_clindoeil.setChecked(false);
	    		    RB_Delta.setChecked(false);
	    	        RB_Theta.setChecked(false);
	    	        RB_LowAlpha.setChecked(false);
	    	        RB_HighAlpha.setChecked(false);
	    	        RB_LowBeta.setChecked(false);
	    	        RB_HighBeta.setChecked(false);
	    	        RB_LowGamma.setChecked(false);
	    	       RB_HighGamma.setChecked(true);
	    		    flagInit =11;
	    		}
	    	});

	        b1.setOnClickListener(new OnClickListener() {
		         @Override
		         public void onClick(View v) {
		        	 graphView.removeSeries(seriesMeditation);
		     		    graphView.removeSeries(seriesAttention);
		     		    graphView.removeSeries(seriesMeditation2);
		     		    graphView.removeSeries(seriesAttention2);
		        	 
		        	 if(RB_meditaton.isChecked()==true){
		 	        	graphView.addSeries(seriesMeditation);
		     		    graphView.addSeries(seriesMeditation2);
		 	        	flagInit = 0;
		 	        }
		        	 else if(RB_attention.isChecked()==true){
		 	        	graphView.addSeries(seriesAttention);
		     		    graphView.addSeries(seriesAttention2);
		 	        	flagInit = 0;
		 	        }
		        	 else if(RB_clindoeil.isChecked()==true){
		 	        	flagInit = 0;
		 	        }
		        	 gd.dismiss();
		          }    
	        });
	        b2.setOnClickListener(new OnClickListener() {
		         @Override
		         public void onClick(View v) {
		            gd.dismiss(); // dismiss the dialog
		         }    
	        });
	        
	        gd.show();
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
						
						if(!ArrayFilesCharging.contains(ArrayListCsvFiles.get(i)))
						{
							ArrayDataImportFile = new ArrayList<Integer[]>();
							try {
								Log.d("TEST", "try");
								
								ArrayDataImport.add(ReaderCSVFile(ArrayListCsvFiles.get(i)));
								ArrayFilesCharging.add(ArrayListCsvFiles.get(i));
							
								dessiner_graph();
								ad.dismiss();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							Toast.makeText(getApplicationContext(),"Le fichier sélectionné est déjà chargé !",Toast.LENGTH_LONG).show();
							ad.dismiss();
						}
						break;
					
					}
				}
				
			}
	 };

	 
	 public OnItemClickListener mCSVClickRemoveListener = new OnItemClickListener() {
		 @Override
			public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
			{			
				String info = ((TextView) v).getText().toString();
				String fileName = info.substring(0, 29);
				Log.v("FILENAME", fileName);
				
				Log.d("Nbr CSV",  ArrayFilesCharging.size()+"");
				
				for(int i = 0; i < ArrayFilesCharging.size(); i++) {
					
					if(ArrayFilesCharging.get(i).getName().equals(fileName)) {						
					
						ArrayFilesCharging.remove(ArrayFilesCharging.get(i));
						ArrayDataImport.remove(ArrayDataImport.get(i));
						dessiner_graph();
						rd.dismiss();
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
	
	/*****************************PARTIE CODE CORRECTE***************************/
	public void dessiner_graph()
	{
		int i=0;
		int j=0;
	    graphView.removeSeries(seriesMeditation);
	    graphView.removeSeries(seriesAttention);
	   	seriesAttention = new GraphViewSeries("Attention", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 2), new GraphViewData[] {});
	    seriesMeditation = new GraphViewSeries("Meditation", new GraphViewSeriesStyle(Color.rgb(0, 50, 200),2), new GraphViewData[] {});
		graphView.addSeries(seriesMeditation);

	    graphView.removeSeries(seriesMeditation2);
	    graphView.removeSeries(seriesAttention2);
	    seriesAttention2 = new GraphViewSeries("Attention2", new GraphViewSeriesStyle(Color.rgb(100, 50, 00), 2), new GraphViewData[] {});
	    seriesMeditation2 = new GraphViewSeries("Meditation2", new GraphViewSeriesStyle(Color.rgb(0, 50, 100), 2), new GraphViewData[] {});
	    graphView.addSeries(seriesMeditation2);
	    if(ArrayDataImport.size()>0){
	    	for(i=0;i<ArrayDataImport.get(0).size();i++)
			{
				seriesAttention.appendData( new GraphViewData(ArrayDataImport.get(0).get(i)[0], ArrayDataImport.get(0).get(i)[1]), true);
				seriesMeditation.appendData( new GraphViewData(ArrayDataImport.get(0).get(i)[0], ArrayDataImport.get(0).get(i)[2]), true);
			}	    
	    	if(ArrayDataImport.size()>1){
		    	
			    for(i=0;i<ArrayDataImport.get(1).size();i++)
				{
					seriesAttention2.appendData( new GraphViewData(ArrayDataImport.get(1).get(i)[0], ArrayDataImport.get(1).get(i)[1]), true);
					seriesMeditation2.appendData( new GraphViewData(ArrayDataImport.get(1).get(i)[0], ArrayDataImport.get(1).get(i)[2]), true);
				}
	    	}
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
				if(ArrayFilesCharging.size()<importFiles_max)
				{
					AddFilesBox();
				}
				else
				{
					 Toast.makeText(getApplicationContext(),"Trop de fichiers chargés ! Veuillez en supprimer avant !",Toast.LENGTH_LONG).show();
				}
				
				return true;
			case R.id.removeSave:
					if(ArrayFilesCharging.size()>0)
					{
						RemoveFilesBox();
					}
					else
					{
						 Toast.makeText(getApplicationContext(),"Rien à supprimer !",Toast.LENGTH_LONG).show();
					}
					
					return true;
			case R.id.selectCurve:
				SelectGraphBox();
				
				return true;
			default: 
	    		return super.onOptionsItemSelected(item);
		}
	}
	
	
}