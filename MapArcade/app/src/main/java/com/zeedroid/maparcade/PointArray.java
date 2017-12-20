package com.zeedroid.maparcade;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve Dixon on 23/06/2017.
 */

public class PointArray extends ArrayList<Point> implements Parcelable {



        private List<List> points;

        public PointArray() {
            super();
        }

        protected PointArray(Parcel parcel) {

            parcel.readTypedList(this, Point.CREATOR);
        }

        public void setPoints(List<List> points){ this.points = points; }

        public List<List> getPoints(){
            return points;
        }



        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i){
            parcel.writeTypedList(this);
        }

        public static final Parcelable.Creator<PointArray> CREATOR = new Parcelable.Creator<PointArray>() {
            public PointArray createFromParcel(Parcel parcel) {
                 return new PointArray(parcel);
            }

            @Override
            public PointArray[] newArray(int size) {
                return new PointArray[size];
            }
        };
}