package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 09/07/2017.
 */

public class PointXY {

    private double xCoord;
    private double yCoord;

    public PointXY(double xCoord, double yCoord){}

    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        PointXY pxy = (PointXY)obj;
        return xCoord == pxy.getxCoord() &&
               yCoord == pxy.getyCoord();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + Double.valueOf(xCoord).hashCode();
        hash = 31 * hash + Double.valueOf(yCoord).hashCode();

        return hash;
    }
}
