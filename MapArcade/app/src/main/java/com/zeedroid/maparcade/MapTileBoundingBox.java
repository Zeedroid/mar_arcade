package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 29/06/2017.
 */

public class MapTileBoundingBox {

    private int minTileCoordinateX, minTileCoordinateY;
    private int maxTileCoordinateX, maxTileCoordinateY;
    private double minLatitude, minLongitude;
    private double maxLatitude, maxLongitude;
    private int tileCountX, tileCountY;

    public MapTileBoundingBox(int minTileCoordinateX, int minTileCoordinateY, int maxTileCoordinateX, int maxTileCoordinateY,
                              double minLatitude, double minLongitude, double maxLatitude, double maxLongitude){
        this.minTileCoordinateX  = minTileCoordinateX;
        this.minTileCoordinateY  = minTileCoordinateY;
        this.maxTileCoordinateX  = maxTileCoordinateX;
        this.maxTileCoordinateY  = maxTileCoordinateY;
        this.minLatitude         = minLatitude;
        this.minLongitude        = minLongitude;
        this.maxLatitude         = maxLatitude;
        this.maxLongitude        = maxLongitude;
        tileCountX = (maxTileCoordinateX - minTileCoordinateX) + 1;
        tileCountY = (maxTileCoordinateY - minTileCoordinateY) + 1;
    }

    public MapTileBoundingBox(){}

    public int getMinTileCoordinateX() {
        return minTileCoordinateX;
    }

    public void setMinTileCoordinateX(int minTileCoordinateX) {
        this.minTileCoordinateX = minTileCoordinateX;
    }

    public int getMinTileCoordinateY() {
        return minTileCoordinateY;
    }

    public void setMinTileCoordinateY(int minTileCoordinateY) {
        this.minTileCoordinateY = minTileCoordinateY;
    }

    public int getMaxTileCoordinateX() {
        return maxTileCoordinateX;
    }

    public void setMaxTileCoordinateX(int maxTileCoordinateX) {
        this.maxTileCoordinateX = maxTileCoordinateX;
    }

    public int getMaxTileCoordinateY() {
        return maxTileCoordinateY;
    }

    public void setMaxTileCoordinateY(int maxTileCoordinateY) {
        this.maxTileCoordinateY = maxTileCoordinateY;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public int getTileCountX() {
        return tileCountX;
    }

    public void setTileCountX(int tileCountX) {
        this.tileCountX = tileCountX;
    }

    public int getTileCountY() {
        return tileCountY;
    }

    public void setTileCountY(int tileCountY) {
        this.tileCountY = tileCountY;
    }

    @Override
    public String toString(){
        return ""  + minTileCoordinateX +
               " " + minTileCoordinateY +
               " " + maxTileCoordinateX +
               " " + maxTileCoordinateY +
               " " + minLatitude +
               " " + minLongitude +
               " " + maxLatitude+
               " " + maxLongitude;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        MapTileBoundingBox mapTileBoundingBox = (MapTileBoundingBox) obj;
        return minTileCoordinateX == mapTileBoundingBox.getMinTileCoordinateX() &&
               minTileCoordinateY == mapTileBoundingBox.getMinTileCoordinateY() &&
               maxTileCoordinateX == mapTileBoundingBox.getMaxTileCoordinateX() &&
               maxTileCoordinateY == mapTileBoundingBox.getMaxTileCoordinateY() &&
               minLatitude        == mapTileBoundingBox.getMinLatitude() &&
               minLongitude       == mapTileBoundingBox.getMinLongitude() &&
               maxLatitude        == mapTileBoundingBox.getMaxLatitude() &&
               maxLongitude       == mapTileBoundingBox.getMaxLongitude();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + minTileCoordinateX;
        hash = 31 * hash + minTileCoordinateY;
        hash = 31 * hash + maxTileCoordinateX;
        hash = 31 * hash + maxTileCoordinateY;
        hash = 31 * hash + Double.valueOf(minLatitude).hashCode();
        hash = 31 * hash + Double.valueOf(minLongitude).hashCode();
        hash = 31 * hash + Double.valueOf(maxLatitude).hashCode();
        hash = 31 * hash + Double.valueOf(maxLongitude).hashCode();

        return hash;
    }

}
