package com.zeedroid.maparcade.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.zeedroid.maparcade.Point;

/**
 * Created by User on 18/11/2017.
 */

@Entity(tableName = PointExtraBackup.TABLE_NAME,
        indices = {@Index(value = {"id"})}
)
public class PointExtraBackup {
    public  static final String TABLE_NAME     = "points_extra_backup";

    private static final String ID             = "id";
    public  static final String UNDO_POSITION  = "undo_position";
    private static final String UNDO_TYPE      = "undo_type";
    private static final String POINT_ID       = "point_id";
    private static final String POINT          = "point";
    private static final String POINT_TYPE     = "point_type";
    private static final String POSITION       = "position";
    private static final String POINT_COLOR    = "point_color";
    private static final String POINT_SHAPE    = "point_shape";

    public static final int   MOVE_POINT           = 1;
    public static final int   JOIN_POINT           = 2;
    public static final int   DELETE_POINT         = 3;
    public static final int   INSERT_POINT         = 4;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    private Long   id;

    @ColumnInfo(name = UNDO_POSITION)
    private int undoPosition;

    @ColumnInfo(name = UNDO_TYPE)
    private int undoType;

    @ColumnInfo(name = POINT_ID)
    private Long pointID;

    //    @ColumnInfo(name = POINT)
    @Embedded
    Point  point;

    @ColumnInfo(name = POINT_TYPE)
    private String    pointType;

    @ColumnInfo(name = POSITION)
    private String position;

    @ColumnInfo(name = POINT_COLOR)
    private String pointColor;

    @ColumnInfo(name = POINT_SHAPE)
    private int pointShape;

    public PointExtraBackup(int undoPosition, int undoType, Long pointID, Point point, String pointType, String position, String pointColor, int pointShape){
        this.undoPosition = undoPosition;
        this.undoType     = undoType;
        this.pointID      = pointID;
        this.point        = point;
        this.pointType    = pointType;
        this.position     = position;
        this.pointColor   = pointColor;
        this.pointShape   = pointShape;
    }

    @Ignore
    public PointExtraBackup(){}

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

    public void setPointID(Long pointID) {
        this.pointID = pointID;
    }

    public Point getPoint() { return point; }

    public void setPoint(Point point){ this.point = point; }

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
                " " + undoPosition +
                " " + undoType +
                " " + pointID +
                " " + point.toString() +
                " " + pointType +
                " " + position +
                " " + pointColor +
                " " + pointShape;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        PointExtraBackup pointExtraBackup = (PointExtraBackup)obj;
        return (id.equals(pointExtraBackup.getId()) ||
                (id != null && id.equals(pointExtraBackup.getId()))) &&
                undoPosition == pointExtraBackup.getUndoPosition() &&
                undoType     == pointExtraBackup.getUndoType() &&
                pointID.equals(pointExtraBackup.getPointID()) &&
                (point == pointExtraBackup.getPoint() ||
                        (point != null && point.equals(pointExtraBackup.getPoint()))) &&
                (pointType.equals(pointExtraBackup.getPointType()) ||
                        (pointType  != null && pointType.equals(pointExtraBackup.getPointType()))) &&
                (position.equals(pointExtraBackup.getPosition()) ||
                        (position   != null && position.equals(pointExtraBackup.getPosition()))) &&
                (pointColor.equals(pointExtraBackup.getPointColor()) ||
                        (pointColor != null && pointColor.equals(pointExtraBackup.getPointColor()))) &&
                pointShape == pointExtraBackup.getPointShape();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (null == id ? 0 : id.hashCode());
        hash = 31 * hash + undoPosition;
        hash = 31 * hash + undoType;
        hash = 31 * hash + (null == pointID ? 0 : pointID.hashCode());
        hash = 31 * hash + (null == point ? 0 : point.hashCode());
        hash = 31 * hash + (null == pointType  ? 0 : pointType.hashCode());
        hash = 31 * hash + (null == position   ? 0 : position.hashCode());
        hash = 31 * hash + (null == pointColor ? 0 : pointColor.hashCode());
        hash = 31 * hash + pointShape;
        return hash;
    }
}
