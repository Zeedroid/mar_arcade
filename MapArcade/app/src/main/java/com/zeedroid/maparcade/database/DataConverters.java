package com.zeedroid.maparcade.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Steve Dixon on 17/08/2017.
 */

public class DataConverters {
    @TypeConverter
    public static Long dateToLong(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date longToDate(Long time){
        return time == null ? null : new Date(time);
    }
}
