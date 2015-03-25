package fr.iut.brainwave;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class CompareActivity extends Activity {
	
	
	
	/*************************PARTIE CODE EN TEST********************************/
	
	public ArrayList<File> ArrayListCsvFiles = new ArrayList<File>();
	private ListView LvAllCsvFiles;
	private TextView TvFilesTitle;
	public ArrayAdapter<String> AdaptateurFiles;

	private void getListFiles() {
		File parentDir = Environment.getExternalStoragePublicDirectory("org.BrainWaves");
		File[] filesCSV = parentDir.listFiles();

		AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		LvAllCsvFiles.setAdapter(AdaptateurFiles);

		ArrayList<File> ArrayListCsvFiles = new ArrayList<File>();
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		for (File file : filesCSV) {			
			AdaptateurFiles.add(file.getName()+"\n"+ sdfDate.format(file.lastModified()) );
			ArrayListCsvFiles.add(file);
		}   
	}
	
	 private void FilesBox(){
	    	final Dialog d = new Dialog(CompareActivity.this);
	        d.setTitle(getString(R.string.paramsFilesDialogTitle));
	        d.setContentView(R.layout.dialog_list);
	        Button b1 = (Button) d.findViewById(R.id.button_validation);
	        Button b2 = (Button) d.findViewById(R.id.button_cancel);

			//Titres des listes
			AdaptateurFiles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
			
			LvAllCsvFiles = (ListView) d.findViewById(R.id.lvCSVFiles);	
			        
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
	
	
	
	
	/*****************************PARTIE CODE CORRECTE***************************/
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		
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