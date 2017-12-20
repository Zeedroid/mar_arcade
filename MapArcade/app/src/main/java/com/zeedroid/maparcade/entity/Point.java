package com.zeedroid.maparcade.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.dto.PointDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.zeedroid.maparcade.entity.Point.ROUTE_ID;
import static com.zeedroid.maparcade.entity.Point.TABLE_NAME;

/**
 * Created by User on 11/08/2017.
 */
@Entity(tableName = Point.TABLE_NAME,
        foreignKeys = @ForeignKey(
                entity = MapRoute.class,
                parentColumns = "route_id",
                childColumns = "route_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"route_id"})} //Error:(37, 8) error: pointRoute referenced in the index does not exists in the Entity. Available column names:id, path_id, route_id, point_position, longitude, latitude
)
public class Point{
    public static final String TABLE_NAME      = "points";

    private static final String ID             = "id";
    public  static final String PATH_ID        = "path_id";
    public  static final String ROUTE_ID       = "route_id";
    public static final String POINT_POSITION = "point_position";
    private static final String LONGITUDE      = "longitude";
    private static final String LATITUDE       = "latitude";
    private static final String TIME_PASSED     = "time_passed";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    private Long   id;

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
    public Point(int pathID, Long routeID, int pointPosition, String longitude, String latitude, float timePassed){
        this.pathID        = pathID;
        this.routeID       = routeID;
        this.pointPosition = pointPosition;
        this.longitude     = longitude;
        this.latitude      = latitude;
        this.timePassed    = timePassed;
    }

    public Point(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        Point point = (Point)obj;
        return (id == point.getId() ||
               (id != null && id.equals(point.getId()))) &&
                routeID == point.getRouteID() &&
                pathID == point.getPathID() &&
                pointPosition == point.getPointPosition() &&
                longitude == point.getLongitude() &&
                latitude == point.getLatitude() &
                timePassed == point.getTimePassed();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (null == id ? 0 : id.hashCode());
        hash = 31 * hash + pathID;
        hash = 31 * hash + (null == routeID ? 0 : routeID.hashCode());
        hash = 31 * hash + pointPosition;
        hash = 31 * hash + Double.valueOf(longitude).hashCode();
        hash = 31 * hash + Double.valueOf(latitude).hashCode();
        hash = 31 * hash + Float.valueOf(timePassed).hashCode();

        return hash;
    }
}
