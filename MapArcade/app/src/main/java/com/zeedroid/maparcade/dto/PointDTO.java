package com.zeedroid.maparcade.dto;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.RoomMasterTable.TABLE_NAME;

/**
 * Created by Steve Dixon on 10/07/2017.
 */
@Entity(tableName = TABLE_NAME)
public class PointDTO {

    public static final String TABLE_NAME = "points";
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int    pathID;
    private int    pointPosition;
    private String longitude;
    private String latitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPathID() {
        return pathID;
    }

    public void setPathID(int pathID) {
        this.pathID = pathID;
    }

    public int getPointPosition() {
        return pointPosition;
    }

    public void setPointPosition(int pointPosition) {
        this.pointPosition = pointPosition;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString(){
        return pathID + " " + pointPosition + " " + longitude + " " + latitude;
    }
}
