package com.zeedroid.maparcade.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.zeedroid.maparcade.dto.PointDTO;
import com.zeedroid.maparcade.entity.MapRoute;
import com.zeedroid.maparcade.entity.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by User on 11/08/2017.
 */
@Dao
public interface MapRouteDAO {
    /**
     * Save the PointDTO to the persistence layer
     * @param mapRoute
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MapRoute mapRoute);

    /**
     * Update a PointDTO in the persistant layer
     * @param mapRoute
     */
    @Update
    void update(MapRoute mapRoute);


    /**
     * Delete a pointDTO from the persistant layer
     * @param mapRoute
     */
    @Delete
    void delete(MapRoute mapRoute);

    /**
     * Return all the points within the bounding box that match the searchString
     * @return
     */
    @Query("SELECT * FROM " + MapRoute.TABLE_NAME)
    LiveData<List<MapRoute>> searchAll();

    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @param route
     * @return
     */
    @Query("SELECT * FROM " + MapRoute.TABLE_NAME + " WHERE " +  MapRoute.ROUTE_ID  + " > :route")
    LiveData<List<MapRoute>> searchRoute(Long route);

    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @param from
     *
     * @param to
     * @return
     */
    @Query("SELECT * FROM " + MapRoute.TABLE_NAME + " WHERE " +  MapRoute.ROUTE_CREATED + " BETWEEN :from AND :to")
    LiveData<List<MapRoute>> searchRoute(Date from, Date to);

}
