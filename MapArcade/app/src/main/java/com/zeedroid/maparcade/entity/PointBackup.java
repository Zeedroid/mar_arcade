package com.zeedroid.maparcade.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by User on 18/11/2017.
 */

@Entity(tableName = PointBackup.TABLE_NAME,
        indices = {@Index(value = {"id"})}
)
public class PointBackup {
    public static final String TABLE_NAME     = "points_backup";

    private static final String ID             = "id";
    public  static final String UNDO_POSITION  = "undo_position";
    private static final String UNDO_TYPE      = "undo_type";
    public  static final String POINT_ID       = "point_id";
    public  static final String PATH_ID        = "path_id";
    public  static final String ROUTE_ID       = "route_id";
    private static final String POINT_POSITION = "point_position";
    private static final String LONGITUDE      = "longitude";
    private static final String LATITUDE       = "latitude";
    private static final String TIME_PASSED    = "time_passed";

    public static final int   MOVE_POINT           = 1;
    public static final int   JOIN_POINT           = 2;
    public static final int   DELETE_POINT         = 3;
    public static final int   INSERT_POINT         = 4;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    private Long   id;

    @ColumnInfo(name = UNDO_POSITION)
    private int    undoPosition;

    @ColumnInfo(name = UNDO_TYPE)
    private int   undoType;

    @ColumnInfo(name = POINT_ID)
    private Long pointID;

    @ColumnInfo(name = PATH_ID)
    private int    pathID;

    @ColumnInfo(name = ROUTE_ID)
    private Long    routeID;

    @ColumnInfo(name = POINT_POSITION)
    private int    pointPosition;

    @ColumnInfo(name = LONGITUDE)
    private String longitude;

    @ColumnInfo(name = LATITUDE)
    private String latitude;

    @ColumnInfo(name = TIME_PASSED)
    private float timePassed;

    @Ignore
    public PointBackup(int undoPosition, int undoType, Long pointID, int pathID, Long routeID, int pointPosition, String longitude, String latitude, float timePassed){
         this.undoPosition  = undoPosition;
        this.undoType      = undoType;
        this.pointID       = pointID;
        this.pathID        = pathID;
        this.routeID       = routeID;
        this.pointPosition = pointPosition;
        this.longitude     = longitude;
        this.latitude      = latitude;
        this.timePassed   = timePassed;
    }

    public PointBackup(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUndoPosition() {
        return undoPosition;
    }

    public void setUndoPosition(int undoPosition) { this.undoPosition = undoPosition; }

    public int getUndoType() {
        return undoType;
    }

    public void setUndoType(int undoType) { this.undoType = undoType; }

    public Long getPointID() {
        return pointID;
    }

    public void setPointID(Long pointID) { this.pointID = pointID; }

    public int getPathID() {
        return pathID;
    }

    public void setPathID(int pathID) {
        this.pathID = pathID;
    }

    public Long getRouteID() {
        return routeID;
    }

    public void setRouteID(Long routeID) {
        this.routeID = routeID;
    }

    public int getPointPosition() {
        return pointPosition;
    }

    public void setPointPosition(int pointPosition) { this.pointPosition = pointPosition; }

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

    public float getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(float timePassed) {
        this.timePassed = timePassed;
    }

    @Override
    public String toString(){
        return ""  + id +
               " " + undoPosition +
               " " + undoType +
               " " + pointID +
               " " + pathID +
               " " + routeID +
               " " + pointPosition +
               " " + longitude +
               " " + latitude +
               " " + timePassed;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        PointBackup pointBackup = (PointBackup)obj;
        return (id.equals(pointBackup.getId()) ||
                (id != null && id.equals(pointBackup.getId()))) &&
                undoPosition == pointBackup.getUndoPosition() &&
                undoType     == pointBackup.getUndoType() &&
                pointID.equals(pointBackup.getPointID()) &&
                routeID.equals(pointBackup.getRouteID()) &&
                pathID       == pointBackup.getPathID() &&
                pointPosition == pointBackup.getPointPosition() &&
                longitude.equals(pointBackup.getLongitude()) &&
                latitude.equals(pointBackup.getLatitude()) &&
                timePassed == pointBackup.getTimePassed();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (null == id ? 0 : id.hashCode());
        hash = 31 * hash + undoPosition;
        hash = 31 * hash + undoType;
        hash = 31 * hash + (null == pointID ? 0 : pointID.hashCode());
        hash = 31 * hash + pathID;
        hash = 31 * hash + (null == routeID ? 0 : routeID.hashCode());
        hash = 31 * hash + pointPosition;
        hash = 31 * hash + Double.valueOf(longitude).hashCode();
        hash = 31 * hash + Double.valueOf(latitude).hashCode();
        hash = 31 * hash + Float.valueOf(timePassed).hashCode();

        return hash;
    }
}
