package com.zeedroid.maparcade;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve Dixon on 05/07/2017.
 */

public class PointExtraArray extends ArrayList<PointExtra> implements Parcelable{

         private List<List> points;

        public PointExtraArray() {
            super();
        }

        protected PointExtraArray(Parcel parcel) {

            parcel.readTypedList(this, PointExtra.CREATOR);
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

        public static final Parcelable.Creator<PointExtraArray> CREATOR = new Parcelable.Creator<PointExtraArray>() {
            public PointExtraArray createFromParcel(Parcel parcel) {
                return new PointExtraArray(parcel);
            }

            @Override
            public PointExtraArray[] newArray(int size) {
                return new PointExtraArray[size];
            }
        };
}