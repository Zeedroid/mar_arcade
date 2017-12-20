package com.zeedroid.maparcade.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.zeedroid.maparcade.database.DataConverters;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.zeedroid.maparcade.entity.MapRoute.TABLE_NAME;

/**
 * Created by User on 09/10/2017.
 */

@Entity(tableName = MapAddress.TABLE_NAME,
        foreignKeys = @ForeignKey(
            entity = MapRoute.class,
            parentColumns = "route_id",
            childColumns = "route_id",
            onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"route_id"})} //Error:(37, 8) error: pointRoute referenced in the index does not exists in the Entity. Available column names:id, path_id, route_id, point_position, longitude, latitude
)
public class MapAddress {

    public  static final String TABLE_NAME        = "route_address";
    private static final String ID                = "id";
    public  static final String ROUTE_ID          = "route_id";
    public  static final String ROAD_ID           = "road_id";
    public static final String ROAD_NAME          = "road_name";
    public static final String POST_CODE          = "post_code";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    private Long   id;

    @ColumnInfo(name = ROUTE_ID)
    private Long   routeID;

    @ColumnInfo(name = ROAD_ID)
    private int roadID;

    @ColumnInfo(name = ROAD_NAME)
    private String    roadName;

    @ColumnInfo(name = POST_CODE)
    private String    postCode;

    public MapAddress(Long routeID, int roadID, String roadName, String postCode){
        this.routeID  = routeID;
        this.roadID   = roadID;
        this.roadName = roadName;
        this.postCode = postCode;

    }

    @Ignore
    public MapAddress(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteID() {
        return routeID;
    }

    public void setRouteID(Long routeID) {
        this.routeID = routeID;
    }

    public int getRoadID() {
        return roadID;
    }

    public void setRoadID(int roadID) {
        this.roadID = roadID;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.roadName = postCode;
    }


    @Override
    public String toString(){
        return ""  + id +
               ""  + routeID +
               " " + roadID +
               " " + roadName +
               " " + postCode;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        com.zeedroid.maparcade.entity.MapAddress ma = (com.zeedroid.maparcade.entity.MapAddress) obj;
        return (id == ma.getId() ||
                (id != null && id.equals(ma.getId()))) &&
                (routeID == ma.getRouteID() ||
                (routeID != null && routeID.equals(ma.getRouteID()))) &&
                roadID == ma.getRoadID() &&
                (roadName == ma.getRoadName() ||
                        (roadName != null && roadName.equals(ma.getRoadName()))) &&
                (postCode == ma.getPostCode() ||
                        (postCode != null && postCode.equals(ma.getPostCode())));
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (null == id ? 0 : id.hashCode());
        hash = 31 * hash + (null == routeID ? 0 : routeID.hashCode());
        hash = 31 * hash + roadID;
        hash = 31 * hash + (null == roadName ? 0 : roadName.hashCode());
        hash = 31 * hash + (null == postCode ? 0 : postCode.hashCode());
        return hash;
    }
}