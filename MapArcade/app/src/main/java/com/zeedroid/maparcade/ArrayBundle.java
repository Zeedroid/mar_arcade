package com.zeedroid.maparcade;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steve Dixon on 18/06/2017.
 */

public class ArrayBundle implements Parcelable {

//    private List<List> points;
    private PointArray      points;
    private PointExtraArray features;
    private String          mapType;
    private int             zoomLevel;

    public ArrayBundle(){
        super();
    }

    public ArrayBundle(Parcel parcel){
        this.points = parcel.readParcelable(PointArray.class.getClassLoader());
        this.features = parcel.readParcelable(PointExtraArray.class.getClassLoader());
        this.mapType = parcel.readString();
        this.zoomLevel = parcel.readInt();
    }

    public void setPoints(PointArray points){
        this.points = points;
    }

    public PointArray getPoints(){
        return points;
    }

    public PointExtraArray getPointsExtra() { return features; }

    public void setPointsExtra(PointExtraArray features) { this.features = features; }

    public void setMapType(String mapType){ this.mapType = mapType;}

    public String getMapType(){ return mapType; }

    public void setZoomLevel(int zoomLevel){ this.zoomLevel = zoomLevel; }

    public int getZoomLevel(){ return zoomLevel; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags){
        parcel.writeParcelable(this.points, flags);
        parcel.writeParcelable(this.features, flags);
        parcel.writeString(this.mapType);
        parcel.writeInt(this.zoomLevel);
    }

    public static final Parcelable.Creator<ArrayBundle> CREATOR = new Parcelable.Creator<ArrayBundle>() {
        public ArrayBundle createFromParcel(Parcel parcel) {
             return new ArrayBundle(parcel);
        }

        @Override
        public ArrayBundle[] newArray(int size) {
            return new ArrayBundle[size];
        }
    };
}
