package com.zeedroid.maparcade.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.zeedroid.maparcade.dto.PointDTO;
import com.zeedroid.maparcade.entity.Point;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static com.zeedroid.maparcade.entity.Point.TABLE_NAME;

/**
 * Created by Steve Dixon on 10/07/2017.
 */

@Dao
public interface PointDAO {

    /**
     * Save the PointDTO to the persistence layer
     * @param point
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Point point);

    /*
     * Update a PointDTO in the persistant layer
     * @param id
     * @param latitude
     * @param longitude
     */
/*    @Query("UPDATE " + Point.TABLE_NAME + " SET latitude = :latitude, longitude = :longitude WHERE id = :id")
    int update(Long id, String latitude, String longitude);*/

    @Update
    int update(List<Point> point);

     /*
     * Update a PointDTO in the persistant layer
     * @param id
     * @param latitude
     * @param longitude
     */
/*    @Query("UPDATE " + Point.TABLE_NAME + " SET latitude = :latitude, longitude = :longitude WHERE id = :id")
    int update(Long id, String latitude, String longitude);*/

    @Update
    int update(Point point);

    /**
     * Delete a pointDTO from the persistant layer
     * @param point
     */
    @Delete
    void delete(List<Point> point);

    /**
     * Return all the points within the bounding box that match the searchString
     * @return
     */
    @Query("SELECT * FROM " + Point.TABLE_NAME + " ORDER BY " + Point.POINT_POSITION)
    LiveData<List<Point>> searchAll();
//    @Query("SELECT * FROM " + Point.TABLE_NAME + " WHERE " +  Point.ROUTE_ID  + " > :route")
//    LiveData<List<Point>> searchAll(long route);


    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @param pathID
     * @return
     */
    @Query("SELECT * FROM " + Point.TABLE_NAME + " WHERE " +  Point.PATH_ID  + " = :pathID ORDER BY " + Point.POINT_POSITION)
    LiveData<List<Point>> searchPoint(int pathID);

    /**
     * Return Point that matches latitude, longitude
     * @param latitude
     * @param longitude
     * @return
     */
    @Query("SELECT * FROM " + Point.TABLE_NAME + " WHERE latitude = :latitude and longitude = :longitude ORDER BY " + Point.POINT_POSITION)
    List<Point> searchPoint(String latitude, String longitude);

    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @return
     */
    @Query("SELECT COUNT(*) FROM " + Point.TABLE_NAME)
    int searchCount();


    /**
     * Return single Point record for the given route position.
     * @return
     */
    @Query("SELECT * FROM " + Point.TABLE_NAME + " WHERE point_position = :pointPosition")
    Point getSingleRecord(int pointPosition);

}
