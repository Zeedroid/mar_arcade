package com.zeedroid.maparcade.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.IdRes;

import com.zeedroid.maparcade.database.DataConverters;

import java.util.Date;
import java.util.Objects;

import static com.zeedroid.maparcade.entity.MapRoute.TABLE_NAME;
import static com.zeedroid.maparcade.entity.Point.ROUTE_ID;

/**
 * Created by Steve Dixon on 11/07/2017.
 */
@Entity(tableName = MapRoute.TABLE_NAME)
public class MapRoute {

    public  static final String TABLE_NAME         = "map_route";
    public  static final String ROUTE_ID           = "route_id";
    private static final String ROUTE_NAME         = "route_name";
    private static final String ROUTE_TYPE         = "route_type";
    private static final String ROUTE_DESCRIPTION  = "route_description";
    private static final String ROUTE_DIFFICULTY   = "route_difficulty";
    private static final String ROUTE_SCORE        = "route_score";
    public static final String ROUTE_CREATED       = "route_created";
    public static final String ROUTE_STATUS        = "route_status";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ROUTE_ID)
    private Long   routeID;

    @ColumnInfo(name = ROUTE_NAME)
    private String routeName;

    @ColumnInfo(name = ROUTE_TYPE)
    private int    routeType;

    @ColumnInfo(name = ROUTE_DESCRIPTION)
    private String routeDescription;

    @ColumnInfo(name = ROUTE_DIFFICULTY)
    private int    routeDifficulty;

    @ColumnInfo(name = ROUTE_SCORE)
    private float  routeScore;

    @ColumnInfo(name = ROUTE_CREATED)
    @TypeConverters(DataConverters.class)
    private Date routeCreated;

    @ColumnInfo(name = ROUTE_STATUS)
    private int routeStatus;


    public MapRoute(String routeName,
                    int    routeType,
                    String routeDescription,
                    int    routeDifficulty,
                    float  routeScore,
                    Date   routeCreated,
                    int    routeStatus){
        this.routeName        = routeName;
        this.routeType        = routeType;
        this.routeDescription = routeDescription;
        this.routeDifficulty  = routeDifficulty;
        this.routeScore       = routeScore;
        this.routeCreated     = routeCreated;
        this.routeStatus      = routeStatus;
    }

    @Ignore
    public MapRoute(){}

    public Long getRouteID() {
        return routeID;
    }

    public void setRouteID(Long routeID) {
        this.routeID = routeID;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public String getRouteDescription() {
        return routeDescription;
    }

    public void setRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
    }

    public int getRouteDifficulty() {
        return routeDifficulty;
    }

    public void setRouteDifficulty(int routeDifficulty) {
        this.routeDifficulty = routeDifficulty;
    }

    public float getRouteScore() {
        return routeScore;
    }

    public void setRouteScore(float routeScore) {
        this.routeScore = routeScore;
    }

    public Date getRouteCreated() { return routeCreated; }

    public void setRouteCreated(Date routeCreated){ this.routeCreated = routeCreated; }

    public int getRouteStatus() {
        return routeStatus;
    }

    public void setRouteStatus(int routeStatus) {
        this.routeStatus = routeStatus;
    }

    @Override
    public String toString(){
        return ""  + routeID +
               " " + routeName +
               " " + routeType +
               " " + routeDescription +
               " " + routeDifficulty +
               " " + routeScore +
               " " + routeCreated +
               " " + routeStatus;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        MapRoute mr = (MapRoute) obj;
        return (routeID == mr.getRouteID() ||
                (routeID != null && routeID.equals(mr.getRouteID()))) &&
                (routeName == mr.getRouteName() ||
                (routeName != null && routeName.equals(mr.getRouteName()))) &&
                 routeType == mr.getRouteType() &&
                (routeDescription == mr.getRouteDescription() ||
                (routeDescription!= null && routeDescription.equals(mr.getRouteDescription()))) &&
                 routeDifficulty == mr.getRouteDifficulty() &&
                 routeScore == mr.getRouteScore() &&
                (routeCreated == mr.getRouteCreated() ||
                (routeCreated != null && routeCreated.equals(mr.getRouteCreated())) &&
                 routeStatus == mr.getRouteStatus());
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (null == routeID ? 0 : routeID.hashCode());
        hash = 31 * hash + (null == routeName ? 0 : routeName.hashCode());
        hash = 31 * hash + routeType;
        hash = 31 * hash + (null == routeDescription ? 0 : routeDescription.hashCode());
        hash = 31 * hash + routeDifficulty;
        hash = 31 * hash + Float.valueOf(routeScore).hashCode();
        hash = 31 * hash + (null == routeCreated ? 0 : routeCreated.hashCode());
        hash = 31 * hash + routeStatus;
        return hash;
    }
}