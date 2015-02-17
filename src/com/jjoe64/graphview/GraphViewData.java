package com.jjoe64.graphview;

import com.jjoe64.graphview.GraphViewDataInterface;
/**
 * Permet de cr�er un graphique en prenant les abscisses et les ordon�es x et y respectives
 * 
 * @author Robin, Chafik, Lo�c, C�cile
 *
 */
public class GraphViewData implements GraphViewDataInterface {
    private double x,y;
    /**
     * Cr�e le graphique en prenant en param�tre les coordon�es x et y
     * @param x
     * @param y
     */
    public GraphViewData(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * R�cup�re l'abscisse X
     */
    @Override
    public double getX() {
        return this.x;
    }
    /**
     * R�cup�re l'ordonn�e Y
     */
    @Override
    public double getY() {
        return this.y;
    }
}
