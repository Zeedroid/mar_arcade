package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 02/07/2017.
 */

public class TileCoordinates {

    private int screenPixelX;
    private int screenPixelY;
    private double tileX;
    private double tileY;

    public TileCoordinates(){}

    public TileCoordinates(double tileX, double tileY, int screenPixelX, int screenPixelY){
        this.tileX        = tileX;
        this.tileY        = tileY;
        this.screenPixelX = screenPixelX;
        this.screenPixelY = screenPixelY;
    }

    public int getScreenPixelX() {
        return screenPixelX;
    }

    public void setScreenPixelX(int screenPixelX) {
        this.screenPixelX = screenPixelX;
    }

    public int getScreenPixelY() {
        return screenPixelY;
    }

    public void setScreenPixelY(int screenPixelY) {
        this.screenPixelY = screenPixelY;
    }

    public double getTileX() {
        return tileX;
    }

    public void setTileX(double tileX) {
        this.tileX = tileX;
    }

    public double getTileY() {
        return tileY;
    }

    public void setTileY(double tileY) {
        this.tileY = tileY;
    }

    @Override
    public String toString() {
        return "tileX=" + tileX + " tileY=" + tileY + " pixelX=" + screenPixelX + " pixelY=" + screenPixelY;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        TileCoordinates tc = (TileCoordinates) obj;
        return tileX == tc.getTileX() &&
               tileY == tc.getTileY() &&
               screenPixelX == tc.getScreenPixelX() &&
               screenPixelY == tc.getScreenPixelY() ;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + Double.valueOf(tileX).hashCode();
        hash = 31 * hash + Double.valueOf(tileY).hashCode();
        hash = 31 * hash + Double.valueOf(screenPixelX).hashCode();
        hash = 31 * hash + Double.valueOf(screenPixelY).hashCode();
        return hash;
    }
}
