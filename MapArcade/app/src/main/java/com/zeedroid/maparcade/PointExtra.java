package com.zeedroid.maparcade;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by Steve Dixon on 18/06/2017.
 */

public class PointExtra implements Parcelable{

    private Point  point;
    private String pointType;
    private String position;
    private String pointColor;
    private int    pointShape;
    private double  canvasX;
    private double  canvasY;
    private int    widthExtra;

     public PointExtra(){
    }

    public PointExtra(Point point, String pointType, String position, String pointColor, int pointShape){
        this.point      = point;
        this.pointType = pointType;
        this.position  = position;
        this.pointColor = pointColor;
        this.pointShape = pointShape;
    }

    public PointExtra(Parcel parcel){
        this.point = parcel.readParcelable(Point.class.getClassLoader());
        this.pointType = parcel.readString();
        this.position  = parcel.readString();
        this.pointColor = parcel.readString();
        this.pointShape = parcel.readInt();
     }

    public Point getPoint() { return point; }

    public void setPoint(Point point) { this.point = point; }

    public String getPointType() { return pointType; }

    public void setPointType(String pointType){ this.pointType = pointType; }

    public String getPosition() { return position; }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPointColor() {
        return pointColor;
    }

    public void setPointColour(String pointColour) {
        this.pointColor = pointColour;
    }

    public int getPointShape() {
        return pointShape;
    }

    public void setPointShape(int pointShape) {
        this.pointShape = pointShape;
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

    public int getWidthExtra() {
        return widthExtra;
    }

    public void setWidthExtra(int widthExtra) {
        this.widthExtra = widthExtra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.point, flags);
        parcel.writeString(this.pointType);
        parcel.writeString(this.position);
        parcel.writeString(this.pointColor);
        parcel.writeInt(this.pointShape);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PointExtra createFromParcel(Parcel parcel) {
            return new PointExtra(parcel);
        }

        @Override
        public PointExtra[] newArray(int size) {
            return new PointExtra[size];
        }
    };

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        PointExtra pointExtra = (PointExtra)obj;
        return (point == pointExtra.getPoint() ||
               (point != null && point.equals(pointExtra.getPoint()))) &&
               (pointType  == pointExtra.getPointType() ||
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
        hash = 31 * hash + (null == point ? 0 : point.hashCode());
        hash = 31 * hash + (null == pointType ? 0 : pointType.hashCode());
        hash = 31 * hash + (null == position ? 0 : position.hashCode());
        hash = 31 * hash + (null == pointColor ? 0 : pointColor.hashCode());
        hash = 31 * hash + pointShape;
        return hash;
    }
}
