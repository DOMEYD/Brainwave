package com.jjoe64.graphview;

import com.jjoe64.graphview.GraphViewDataInterface;
/**
 * Permet de créer un graphique en prenant les abscisses et les ordonées x et y respectives
 * 
 * @author Robin, Chafik, Loïc, Cécile
 *
 */
public class GraphViewData implements GraphViewDataInterface {
    private double x,y;
    /**
     * Crée le graphique en prenant en paramètre les coordonées x et y
     * @param x
     * @param y
     */
    public GraphViewData(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Récupère l'abscisse X
     */
    @Override
    public double getX() {
        return this.x;
    }
    /**
     * Récupère l'ordonnée Y
     */
    @Override
    public double getY() {
        return this.y;
    }
}
