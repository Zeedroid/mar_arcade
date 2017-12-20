package com.zeedroid.maparcade;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.zeedroid.maparcade.entity.*;

import java.util.Objects;

/**
 * Created by Steve Dixon on 17/06/2017.
 */

public class Point  implements Parcelable {
    private double  longitude;
    private double  latitude;
    private float   timepassed;
    private double  canvasX;
    private double  canvasY;
    private int     widthNode;
    private boolean selected = false;
    private int     pathID;



    @Ignore
    public Point(){

    }

    /**
     * Create a Point coordinate object
     * @param longitude longitude in DD format
     * @param latitude latitude in DD format
     */
    public Point(double longitude, double latitude, float timepassed){
        this.longitude  = longitude;
        this.latitude   = latitude;
        this.timepassed = timepassed;
    }

    /**
     * Create a Point coordinate object
     * @param longitude longitude in D format
     * @param latitude latitude in DD format
     * @param pathID initially set to 1, this will point to the road name for this coordinate.
     */
    @Ignore
    public Point(double longitude, double latitude, int pathID, float timepassed){
        this.longitude  = longitude;
        this.latitude   = latitude;
        this.pathID     = pathID;
        this.timepassed = timepassed;
    }

    /**
     * Create a Point coordinate object from a parcel.
     * @param parcel
     */
    @Ignore
    public Point(Parcel parcel){
        this.longitude  = parcel.readDouble();
        this.latitude   = parcel.readDouble();
        this.timepassed = parcel.readFloat();
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public float getTimepassed() {
        return timepassed;
    }

    public void setTimepassed(float timepassed) {
        this.timepassed = timepassed;
    }

    public double getCanvasX() {
        return canvasX;
    }

    public void setCanvasX(double canvasX) {
        this.canvasX = canvasX;
    }

    public double getCanvasY() {
        return canvasY;
    }

    public void setCanvasY(double canvasY) {
        this.canvasY = canvasY;
    }

    public int getWidthNode() {
        return widthNode;
    }

    public void setWidthNode(int widthNode) {
        this.widthNode = widthNode;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public int getPathID() {
        return pathID;
    }

    public void setPathID(int pathID) {
        this.pathID = pathID;
    }

    public void toggleSelected() {
        if (selected) selected = false;
        else selected = true;
    }

    public boolean isSelected(){ return selected; }

    @Override
    public String toString(){
        return ""  + longitude  +
               " " + latitude   +
               " " + timepassed +
               " " + canvasX    +
               " " + canvasY    +
               " " + widthNode  +
               " " + pathID     +
               " " + Boolean.toString(selected);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.longitude);
        parcel.writeDouble(this.latitude);
        parcel.writeFloat(this.timepassed);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Point createFromParcel(Parcel parcel) {
            return new Point(parcel);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        Point point = (Point)obj;
        return longitude == point.getLongitude() &&
                latitude == point.getLatitude()  &&
              timepassed == point.getTimepassed();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + Double.valueOf(longitude).hashCode();
        hash = 31 * hash + Double.valueOf(latitude).hashCode();
        hash = 31 * hash + Float.valueOf(timepassed).hashCode();

        return hash;
    }
}
