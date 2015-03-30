package fr.iut.brainwave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;
/**
 * Permet de cr�er un fichier CSV d'apr�s les donn�es recueillies
 * 
 * @author Robin, Chafik, Lo�c, C�cile
 *
 */
public class csvWriter {

	//D�claration des variables
	String fileName;
	File root;
	File monFichier;
	
	/**
	 * Permet de cr�er le fichier CSV
	 * @param fileName
	 */
	csvWriter(String fileName){
		this.fileName = fileName;
		root=Environment.getExternalStoragePublicDirectory("org.BrainWaves");
		if(!root.exists())
		{
			root.mkdir();
		}
		monFichier = new File(root, fileName);
	}
	
	/**
	 * Permet d'ajouter les donn�es au fichier CSV
	 * @param valeursMeditation
	 * @param valeursAttention
	 * @param entete
	 */
	public void addCSVTwoList(ArrayList<Integer[]> ValeursData, Integer time_record, ArrayList<String> entete){
		try
		{
			FileWriter writer = new FileWriter(monFichier);
			for(String element: entete){
				writer.write(element);
			}
			int i=0;
			int j=0;
			for(i=0;i<time_record;i++){
				writer.write(Integer.toString(ValeursData.get(i)[0]));
				for(j=1;j<ValeursData.get(i).length;j++)
				{
				writer.write(";");
				writer.write(Integer.toString(ValeursData.get(i)[j]));
				}
				writer.append("\n");
			}
		
			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		}
	}
	
	public void createCSV(ArrayList<Integer[]> datas, ArrayList<String> entete) {
		try
		{
			FileWriter writer = new FileWriter(monFichier);
			for(String element: entete){
				writer.write(element);
			}
			for(int i=0; i<datas.size(); i++){
				for(int j=0; j<datas.get(i).length; j++) {
					writer.write(Integer.toString(datas.get(i)[j]) + ";");
				}
				writer.append("\n");
			}
			
			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		}
	}

}
