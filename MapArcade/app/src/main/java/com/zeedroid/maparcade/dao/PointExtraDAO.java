package com.zeedroid.maparcade.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.zeedroid.maparcade.entity.PointExtra;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static com.zeedroid.maparcade.entity.PointExtra.TABLE_NAME;

/**
 * Created by Steve Dixon on 17/08/2017.
 */

@Dao
public interface PointExtraDAO {

    /**
     * Save the PointExtraDTO to the persistence layer
     * @param pointExtra
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PointExtra pointExtra);

    /**
     * Update a PointExtraDTO in the persistant layer
     * @param pointExtra
     */
    @Update
    void update(PointExtra pointExtra);


    /**
     * Delete a pointExtraDTO from the persistant layer
     * @param pointExtra
     */
    @Delete
    void delete(PointExtra pointExtra);

    /**
     * Return all the points within the bounding box that match the searchString
     * @return
     */
    @Query("SELECT * FROM " + TABLE_NAME)
    LiveData<List<PointExtra>> searchAll();
//    @Query("SELECT * FROM " + PointExtra.TABLE_NAME + " WHERE " +  PointExtra.ROUTE_ID  + " > :route")
//    LiveData<List<PointExtra>> searchAll(long route);

    /**
     * Return PointExtra that matches latitude, longitude
     * @param latitude
     * @param longitude
     * @return
     */
    @Query("SELECT * FROM " + PointExtra.TABLE_NAME + " WHERE latitude = :latitude and longitude = :longitude")
    List<PointExtra> searchPoint(String latitude, String longitude);
}
