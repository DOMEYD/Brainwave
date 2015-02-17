package com.example.testapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;
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
		root = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DOWNLOADS);
		monFichier = new File(root, fileName);
	}
	
	/**
	 * Permet d'ajouter les donn�es au fichier CSV
	 * @param valeursMeditation
	 * @param valeursAttention
	 * @param entete
	 */
	public void addCSVTwoList(ArrayList<Integer> valeursMeditation, ArrayList<Integer> valeursAttention, ArrayList<String> entete){
		try
		{
			int listMeditationSize = valeursMeditation.size();
			FileWriter writer = new FileWriter(monFichier);
			for(String element: entete){
				writer.write(element);
			}
			for(int i = 0; i < valeursAttention.size(); i++){
				writer.write(Integer.toString(valeursAttention.get(i)));
				writer.write(",");
				if(i < listMeditationSize){
					writer.write(Integer.toString(valeursMeditation.get(i)));
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
