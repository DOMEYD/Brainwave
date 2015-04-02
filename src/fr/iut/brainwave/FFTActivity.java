package fr.iut.brainwave;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class FFTActivity extends Activity {
	
	//Variable de DialogBox permettant le chargement d'un fichier CSV
	private	Dialog AddFileDialog = null;
	
	//Variable permettant de savoir quelle courbe est choisi
	int flagInit = 0;
	
	//Ensemble des variables pour la création de la liste de fichier CSV
	public ArrayList<File> ArrayListCsvFiles = new ArrayList<File>();
	private ListView LvAllCsvFiles;
	public ArrayAdapter<String> AdaptateurFiles;
	
	//Variable stockant les données d'un fichier chargé par l'utilisateur
	public ArrayList<Integer[]> ArrayDataImport = new ArrayList<Integer[]>();
	
	// Courbe de l'attention (Couleur = rouge / Nom = Attention)
    GraphViewSeries seriesRaw = new GraphViewSeries("RawData", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), new GraphViewData[] {});
    // Instanciation du GraphView
    GraphView graphView;
    LinearLayout layout;
    
    //Variable FLAG pour savoir si on affiche le spectre ou la courbe des données des RAWDATA
    boolean flagFFT=false;
	
    // Echelle Y du graph
    double max = 100 ;
	double min = 0;
	
	 //Variable permettant le changement d'icone dans l'actionBar
	MenuItem MenuFFTItem;
	
	//Méthode permettant d'afficher l'ensemble des fichiers de RAWDATA dans une DialogBox
	private void AddFilesBox(){
		AddFileDialog = new Dialog(FFTActivity.this);
		AddFileDialog.setTitle(getString(R.string.paramsFilesDialogTitle));
		AddFileDialog.setContentView(R.layout.dialog_list);
        Button b1 = (Button) AddFileDialog.findViewById(R.id.button_cancel);

		AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		LvAllCsvFiles = (ListView) AddFileDialog.findViewById(R.id.lvCSVFiles);	
		LvAllCsvFiles.setOnItemClickListener(mCSVClickListener);
		    
		AddFileDialog.show();
		getListFiles();

        b1.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        	 Log.d("CLICK", "test2");
	        	 AddFileDialog.dismiss(); // dismiss the dialog
	         }    
        });
        
      
	}
	
	
	//Méthode permettant la création de la liste des fichiers CSV à afficher dans la dialogBox
	private void getListFiles() {
		File parentDir = Environment.getExternalStoragePublicDirectory("org.BrainWaves");
		if(parentDir.exists())
		{
			File[] filesCSV = parentDir.listFiles();
			if(filesCSV.length>0)
			{

				AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
				LvAllCsvFiles.setAdapter(AdaptateurFiles);
		
				SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRANCE);
		
				for (File file : filesCSV) {			
					if(file.getName().startsWith("rawRecord"))
					{
					AdaptateurFiles.add(file.getName()+"\n"+ sdfDate.format(file.lastModified()) );
					ArrayListCsvFiles.add(file);
					}
				}   
				
			}
			else
			{
				Toast.makeText(getApplicationContext(),"Aucun fichier CSV trouvé !",Toast.LENGTH_LONG).show();
				AddFileDialog.dismiss();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(),"Aucun fichier CSV trouvé !",Toast.LENGTH_LONG).show();
			AddFileDialog.dismiss();
		}

	}

	//Méthode permettant de gérer le clique de l'utilisateur sur un fichier qu'il désire chargé
	 public OnItemClickListener mCSVClickListener = new OnItemClickListener() {
		 @Override
			public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
			{			
				String info = ((TextView) v).getText().toString();
				String fileName = info.substring(0, 28);
				Log.v("FILENAME", fileName);
				
				Log.d("Nbr CSV",  ArrayListCsvFiles.size()+"");
				
				for(int i = 0; i < ArrayListCsvFiles.size(); i++) {
					Log.d("TEST", "TURN");
					if(ArrayListCsvFiles.get(i).getName().equals(fileName)) {
						
							ArrayDataImport = new ArrayList<Integer[]>();
							try {
								
								
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
	 
	 //méthode pour lire un fichier CSV 
	 /**** @throws IOException ***/
		 public ArrayList<Integer[]> ReaderCSVFile (File file) throws IOException{
				Log.v("READER", "test1");
				ArrayList<Integer[]> dataValues = new ArrayList<Integer[]>();
				Integer[] tempValues = new Integer[2];
				 int echantillon =0;
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
				            if(echantillon==0)
				            {
					        	   try{
					        		   tempValues[0]=Integer.parseInt(temp[0]);
						        	   tempValues[1]=Integer.parseInt(temp[1]);

						        	   dataValues.add(tempValues);		
							           tempValues = new Integer[2];
					        	   }
					        	   catch(NumberFormatException e){
					        		   
					        	   }
					        	   
				        	 }
				        	   
				            if(echantillon<10)
				        	{
				        	   echantillon++;
				        	}
				        	else
				        	{
				        		echantillon =0;
				        	}
				        
				                     
				           Log.v("READER", echantillon+"");
				         
				           line = br.readLine();
				        }
				       Log.v("READER", "fin de reader");
				        br.close();
				        fr.close();
				        
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return dataValues;
			      
			}
		 
		 //Méthode qui permet de créer un graphique pou la première instanciation de la Class 
		 public void createGraph(){

		    	graphView = new LineGraphView(this, "Courbes EEG");
		    	
				graphView.setManualYAxisBounds((double) max, (double) min);
				graphView.setShowLegend(true);
				graphView.setViewPort(1,19);
				graphView.setScrollable(true);
				graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
				graphView.getGraphViewStyle().setLegendWidth(200);
				graphView.setLegendAlign(LegendAlign.TOP);
				graphView.getGraphViewStyle().setNumVerticalLabels(21);
				graphView.getGraphViewStyle().setNumHorizontalLabels(20);
				graphView.getGraphViewStyle().setTextSize(15);
				layout = (LinearLayout) findViewById(R.id.layout1);
				layout.addView(graphView);
		
		    }	 
		 
		 //Permet de dessiner le graphique en ajoutant les valeurs dans la série
		 public void dessiner_graph()
			{
				int i=0;
				int j=0;
				newGraphFFT();
			    graphView.removeSeries(seriesRaw);
			   	seriesRaw = new GraphViewSeries("RawData", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 2), new GraphViewData[] {});
			   graphView.addSeries(seriesRaw);
			    
			    	for(i=0;i<ArrayDataImport.size();i++)
					{
						seriesRaw.appendData(new GraphViewData( i, ArrayDataImport.get(i)[1]), true);
					}	    
			    	
			}
		 
		 //Permet de récréer le graphique en modifiant son type suivant 
		 //si on souhaite afficher la FFT
		 public void newGraphFFT(){
			 	layout.removeView(graphView);
			 	if(!flagFFT)
			 	{
			 		MenuFFTItem.setIcon(R.drawable.ic_trending_up_128);
			 		graphView = new BarGraphView(this, "Courbes EEG");
			 	}
			 	else
			 	{
			 		MenuFFTItem.setIcon(R.drawable.ic_equalizer_128);
			 		graphView = new LineGraphView(this, "Courbes EEG");
			 	}
			 	int j=0;
			 	int i=0;
				 int tempMax=0;
		    	 int tempMin=0;

				 	for(j=0;j<ArrayDataImport.size();j++)
				 	{
				 			if(ArrayDataImport.get(j)[1]>tempMax)
				       	    {
				 			    tempMax=ArrayDataImport.get(j)[1];
				        	}
				        	if( ArrayDataImport.get(j)[1]< tempMin)
				        	{
				        		  tempMin=ArrayDataImport.get(j)[1];
				        	}
				       
					}
		
		 			String tempString =Integer.toString(tempMax);
			         
			        Double tempInt =tempMax/(Math.pow(10, (tempString.length()-1)));
			        max=Math.ceil(tempInt)*Math.pow(10, (tempString.length()-1));
			           
			        tempMin=tempMin*(-1);
			        tempInt = tempMin/(Math.pow(10, (tempString.length()-1)));
			        min=(-1)*Math.ceil(tempInt)*Math.pow(10, (tempString.length()-1));

				graphView.setManualYAxisBounds((double) max, (double) min);
				graphView.setShowLegend(true);
				graphView.setViewPort(1,499);
				graphView.setScrollable(true);
				graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
				graphView.getGraphViewStyle().setLegendWidth(200);
				graphView.setLegendAlign(LegendAlign.TOP);
				graphView.getGraphViewStyle().setNumVerticalLabels(21);
				graphView.getGraphViewStyle().setNumHorizontalLabels(10);
				graphView.getGraphViewStyle().setTextSize(15);
				layout = (LinearLayout) findViewById(R.id.layout1);
				
				layout.addView(graphView);
		    }	 
		 
		 //DialogBox permettant de gérer le choix de courbes à visualiser
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
			        	 graphView.removeSeries(seriesRaw);
			        	 
			        	 if(RB_meditaton.isChecked()==true){
			 	        	graphView.addSeries(seriesRaw);
			 	        	flagInit = 0;
			 	        }
			        	 else if(RB_attention.isChecked()==true){
			 	        	graphView.addSeries(seriesRaw);
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
		flagFFT=true;
		createGraph();
		
	}
	
    /**
     * MANAGE action bar
     */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fft_menu, menu);
        MenuFFTItem = menu.findItem(R.id.applyFFT);
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addSave:
				AddFilesBox();
				return true;
			case R.id.applyFFT:
				//newGraphFFT();
				dessiner_graph();
				if(flagFFT){
					flagFFT=false;
				}
				else
				{
					flagFFT=true;
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