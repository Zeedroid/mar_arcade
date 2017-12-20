package com.zeedroid.maparcade.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.zeedroid.maparcade.entity.MapAddress;

import java.util.List;

/**
 * Created by User on 09/10/2017.
 */

@Dao
public interface MapAddressDAO {

        /**
         * Save the PointDTO to the persistence layer
         * @param mapAddress
         */
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(MapAddress mapAddress);

        /**
         * Update a PointDTO in the persistant layer
         * @param mapAddress
         */
        @Update
        void update(MapAddress mapAddress);


        /**
         * Delete a pointDTO from the persistant layer
         * @param mapAddress
         */
        @Delete
        void delete(MapAddress mapAddress);

        /**
         * Return all the points within the bounding box that match the searchString
         * @return
         */
        @Query("SELECT * FROM " + MapAddress.TABLE_NAME)
        LiveData<List<MapAddress>> searchAll();

        /**
         * Return aa the points within the bounding box that match latitude, longitude and range
         * @param route
         * @return
         */
        @Query("SELECT * FROM " + MapAddress.TABLE_NAME + " WHERE " +  MapAddress.ROUTE_ID  + " > :route")
        LiveData<List<MapAddress>> searchAddress(int route);

        /**
         * Return aa the points within the bounding box that match latitude, longitude and range
         * @param roadID
         * @return
         */
        @Query("SELECT * FROM " + MapAddress.TABLE_NAME + " WHERE " +  MapAddress.ROAD_ID + " = :roadID")
        LiveData<List<MapAddress>> searchAddress(Long roadID);

        /**
         * Return aa the roadID id one exists for the Road Name and Postcode given
         * @param roadName
         * @param postCode
         * @return
         */
        @Query("SELECT road_id FROM " + MapAddress.TABLE_NAME + " WHERE " +  MapAddress.ROAD_NAME + " = :roadName AND " + MapAddress.POST_CODE + " = :postCode")
        int searchAddress(String roadName, String postCode);

        /**
         * Return the last roadID created one exists

         * @return
         */
        @Query("SELECT MAX(road_id) FROM " + MapAddress.TABLE_NAME)
        int searchLastRoadID();
}
