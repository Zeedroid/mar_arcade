package com.zeedroid.maparcade.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.zeedroid.maparcade.Point;

import java.util.Objects;

import static com.zeedroid.maparcade.entity.PointExtra.TABLE_NAME;

/**
 * Created by User on 17/08/2017.
 */

@Entity(tableName = PointExtra.TABLE_NAME,
        foreignKeys = @ForeignKey(
                entity = MapRoute.class,
                parentColumns = "route_id",
                childColumns = "route_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"route_id"})} //Error:(37, 8) error: pointRoute referenced in the index does not exists in the Entity. Available column names:id, path_id, route_id, point_position, longitude, latitude
)
public class PointExtra {
    public  static final String TABLE_NAME     = "points_extra";

    private static final String ID             = "id";
    private static final String POINT          = "point";
    public  static final String ROUTE_ID       = "route_id";
    private static final String POINT_TYPE     = "point_type";
    private static final String POSITION       = "position";
    private static final String POINT_COLOR    = "point_color";
    private static final String POINT_SHAPE    = "point_shape";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    private Long   id;

//    @ColumnInfo(name = POINT)
    @Embedded
    Point  point;

    @ColumnInfo(name = ROUTE_ID)
    private Long    routeID;

    @ColumnInfo(name = POINT_TYPE)
    private String    pointType;

    @ColumnInfo(name = POSITION)
    private String position;

    @ColumnInfo(name = POINT_COLOR)
    private String pointColor;

    @ColumnInfo(name = POINT_SHAPE)
    private int pointShape;

    public PointExtra(Point point, Long routeID, String pointType, String position, String pointColor, int pointShape){
        this.point      = point;
        this.routeID    = routeID;
        this.pointType  = pointType;
        this.position   = position;
        this.pointColor = pointColor;
        this.pointShape = pointShape;
    }

    @Ignore
    public PointExtra(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Point getPoint() { return point; }

    public void setPoint(Point point){ this.point = point; }

    public Long getRouteID() {
        return routeID;
    }

    public void setRouteID(Long routeID) {
        this.routeID = routeID;
    }

    public String getPointType() { return pointType; }

    public void setPointType(String pointType) { this.pointType = pointType; }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) { this.position = position; }

    public String getPointColor() {
        return pointColor;
    }

    public void setPointColor(String pointColor) {
        this.pointColor = pointColor;
    }

    public int getPointShape() {
        return pointShape;
    }

    public void setPointShape(int pointShape) {
        this.pointShape = pointShape;
    }

    @Override
    public String toString(){
        return ""  + id +
               " " + point.toString() +
               " " + routeID +
               " " + pointType +
               " " + position +
               " " + pointColor +
               " " + pointShape;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        PointExtra pointExtra = (PointExtra)obj;
        return (id == pointExtra.getId() ||
               (id != null && id.equals(pointExtra.getId()))) &&
               (point == pointExtra.getPoint() ||
               (point != null && point.equals(pointExtra.getPoint()))) &&
                routeID == pointExtra.getRouteID() &&
               (pointType  == (pointExtra.getPointType()) ||
               (pointType  != null && pointType.equals(pointExtra.getPointType()))) &&
               (position   == (pointExtra.getPosition()) ||
               (position   != null && position.equals(pointExtra.getPosition()))) &&
               (pointColor == (pointExtra.getPointColor()) ||
               (pointColor != null && pointColor.equals(pointExtra.getPointColor()))) &&
                pointShape == pointExtra.getPointShape();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (null == id ? 0 : id.hashCode());
        hash = 31 * hash + (null == point ? 0 : point.hashCode());
        hash = 31 * hash + (null == routeID ? 0 : routeID.hashCode());
        hash = 31 * hash + (null == pointType  ? 0 : pointType.hashCode());
        hash = 31 * hash + (null == position   ? 0 : position.hashCode());
        hash = 31 * hash + (null == pointColor ? 0 : pointColor.hashCode());
        hash = 31 * hash + pointShape;
        return hash;
    }
}
