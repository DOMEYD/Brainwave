package com.example.testapp;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

public class Interpolation {
	static DecimalFormat df = new DecimalFormat("0.00");
	static int i=0;
	static int intervalle=2;
	static String retourLigne = "";
	
	public static void main(String[] args) {
		
		//tableau des raw data
		double x0, y0, x1, y1, x2, y2, x3, y3, x4, y4, x5, y5, x6, y6;
		x0 = 0;
		y0 = 0;
		
		x1 = 1;
		y1 = 2;
		
		x2 = 3;
		y2 = 3;
		
		x3 = 6;
		y3 = 7;
		
		x4 = 9;
		y4 = 2;
		
		x5 = 12;
		y5 = 6;
		
		x6 = 2;
		y6 = 5;

		double tableau[][] = new double[][]{
											{x0, y0},
											{x1, y1},
											{x2, y2},
											{x3, y3},
											{x4, y4},
											{x5, y5},
											{x6, y6}
										};
		
		
		
		
		System.out.println("\nAffichage du tableau \n");
		//afficherValeursTableau(tableau);
		
		//Ajouter un point à partir du tableau "tableau", en x=4, à partir du point "tableau[3]"
		System.out.println("\nAjout d'un point \n");
		//double tableau2[][] = ajouterPoint(tableau, 4, 3);
		
		System.out.println("\nAffichage du nouveau tableau \n");
		//afficherValeursTableau(tableau2);
		
		System.out.println("\nAffichage du tableau remplacé\n");
		double nouveauTableau[][] = regulariserPoint(tableau, intervalle);
		afficherValeursTableau(nouveauTableau);
		
	}
	
	public static void afficherValeursTableau(double tableau[][]){
		tableau = trierTableau(tableau);
		
		for(i=0; i < tableau.length; i++){
			//System.out.println("Valeur de x" + i + " : " + tableau[i][0]);
			//System.out.println("Valeur de y" + i + " : " + tableau[i][1]);
			
			//coordonnées du point actuel
			double yB = 0;
			double xB = 0;
			
			xB = tableau[i][0];
			yB = tableau[i][1];
			
			System.out.println("Coordonnées du point actuel : {"+xB+", "+yB+"}");
			
			if(i>0){
				//coordonnées du point précédent
				double xA = 0;
				double yA = 0;
				
				xA = tableau[(i-1)][0];
				yA = tableau[(i-1)][1];
				
				System.out.println("Coordonnées du point précédent : {"+xA+", "+yA+"}");
				
				if(xB!=tableau[(i-1)][0]){
					//coefficient directeur de la droite
					double coeffDirect = (yB-yA)/(xB-xA);
					String coeffDirectString = df.format(coeffDirect);
					
					coeffDirect = Double.parseDouble(coeffDirectString.replaceAll(",", "."));
		
					System.out.println("Coefficient directeur : "+coeffDirect);
					
					double p = yB - (coeffDirect*xB);
					String pStr = df.format(p);
					p = Double.parseDouble(pStr.replaceAll(",", "."));
					System.out.println("Ordonnée à l'origine : "+p);
					
					String equationDroite = "yN = ("+ coeffDirect + " * xN) + (" + p + ")";
					System.out.println(equationDroite);
				}
			}
			System.out.println(retourLigne);
		};
	}
	
	public static double[][] ajouterPoint(double tableauData[][], double xN, double k){
		tableauData = trierTableau(tableauData);
		
		for(i=0; i<tableauData.length;i++){
			if(tableauData[i][0]==xN){
				System.out.println("Le point à cette abcisse existe déja");
				return tableauData;
			}
		}
		
		if(k!=0){	
			int position = (int)k;
			double p, yN = 0;
			double yB, xB, xA, yA = 0;
			int lastData = tableauData.length;
			int lastValueAggrandi = 0;
			
			xB = tableauData[position][0];
			yB = tableauData[position][1];
			
			xA = tableauData[(position-1)][0];
			yA = tableauData[(position-1)][1];
			
			System.out.println("Coordonnées : {"+xA+", "+yA+"}, {"+xB+", "+yB+"}");
			
			//On calcule le coefficient directeur de la droite
			double coeffDirect = (yB-yA)/(xB-xA);
			String coeffDirectString = df.format(coeffDirect);
			coeffDirect = Double.parseDouble(coeffDirectString.replaceAll(",", "."));
			
			System.out.println("Coefficient directeur : "+coeffDirect);
			
			//On calcule l'ordonnée à l'origine de la droite
			p = yA - (coeffDirect*xA);
			String pStr = df.format(p);
			p = Double.parseDouble(pStr.replaceAll(",", "."));
			System.out.println("Ordonnée à l'origine : "+p);
			
			// On calcule le yN d'un point xN à partir de l'équation de la droite obtenue
			yN = (coeffDirect*xN) + p;
			System.out.println("Position yN : "+yN);
			
			
			double tableauAggrandi[][] = new double[lastData+1][2];
			lastValueAggrandi = tableauAggrandi.length;
			
			System.arraycopy(tableauData, 0, tableauAggrandi, 0, lastData);
			
			tableauAggrandi[lastValueAggrandi-1][0] = xN;
			tableauAggrandi[lastValueAggrandi-1][1] = yN;
			
			tableauAggrandi = trierTableau(tableauAggrandi);
			
			return tableauAggrandi;
			
		}else if(k==0){
			System.out.println("Vous devez commencer au 2ème point");
			return tableauData ;
		}
		else{
			System.out.println("Position choisie non valide");
			return tableauData ;
		}
		
	}
	
	public static double[][] regulariserPoint(double tableauData[][], int inter){
		tableauData = trierTableau(tableauData);
		for(i=1; i<tableauData.length;i++){
			double p, xN, yN, yB, xB, xA, yA = 0;	
			
			xB = tableauData[i][0];
			yB = tableauData[i][1];
			
			xA = tableauData[(i-1)][0];
			yA = tableauData[(i-1)][1];
			
			System.out.println("Coordonnées : {"+xA+", "+yA+"}, {"+xB+", "+yB+"}");
			
			xN = (Math.round(xB/inter))*inter;
			
			if(xB == xA){
				yN = (yB+yA)/2;
				
				//tableauData[(i-1)][0] = tableauData[(i)][0];
				//tableauData[(i-1)][1] = tableauData[(i)][1];
				
			}else{
				//On calcule le coefficient directeur de la droite
				double coeffDirect = (yB-yA)/(xB-xA);
				String coeffDirectString = df.format(coeffDirect);
				coeffDirect = Double.parseDouble(coeffDirectString.replaceAll(",", "."));
				
				System.out.println("Coefficient directeur : "+coeffDirect);
				
				//On calcule l'ordonnée à l'origine de la droite
				p = yA - (coeffDirect*xA);
				String pStr = df.format(p);
				p = Double.parseDouble(pStr.replaceAll(",", "."));
				System.out.println("Ordonnée à l'origine : "+p);
				
				// On calcule le yN d'un point xN à partir de l'équation de la droite obtenue
				yN = (coeffDirect*xN) + p;
				String yNStr = df.format(yN);
				yN = Double.parseDouble(yNStr.replaceAll(",", "."));
				
				
				
				System.out.println("Position yN : "+yN+"\n");
			}
			tableauData[i][0] = xN;
			tableauData[i][1] = yN;
			
		}
		return tableauData;
	}
	
	public static double[][] trierTableau(double tableauData[][]){
		
		Arrays.sort(tableauData, new Comparator<double[]>() {
			public int compare(final double[] value1, final double[] value2){
				final double x1= value1[0];
				final double x2= value2[0];;
				return Double.compare(x1, x2);
			}
		});
		return tableauData;
	}
	
}
