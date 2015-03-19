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
 * Permet de créer un fichier CSV d'après les données recueillies
 * 
 * @author Robin, Chafik, Loïc, Cécile
 *
 */
public class csvWriter {

	//Déclaration des variables
	String fileName;
	File root;
	File monFichier;
	
	/**
	 * Permet de créer le fichier CSV
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
	 * Permet d'ajouter les données au fichier CSV
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
				writer.write(";");
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
	
	

	public void ListerCSVFile (File file) throws IOException{
		ArrayList<Integer> mediation = new ArrayList<Integer>();
		ArrayList<Integer> attention = new ArrayList<Integer>();
		String[] temp;
		
		try {
			FileReader fr = new FileReader(file);
			 BufferedReader br = new BufferedReader(fr);
			 String line = br.readLine();
		       while( line != null) {
		           temp= line.split(";");
		           mediation.add(Integer.parseInt(temp[1]));
		           attention.add(Integer.parseInt(temp[0]));
		        		   
		        }

		        br.close();
		        fr.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
		
	}
}
