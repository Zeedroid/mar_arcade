package com.zeedroid.maparcade.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.zeedroid.maparcade.entity.PointBackup;

import java.util.List;

/**
 * Created by User on 18/11/2017.
 */

@Dao
public interface PointBackupDAO {

    /**
     * Save the PointDTO to the persistence layer
     * @param pointBackup
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(PointBackup pointBackup);

    /**
     * Save the PointDTO to the persistence layer
     * @param pointBackup
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<PointBackup> pointBackup);

    /*
     * Update a PointBackupDTO in the persistant layer
     * @param id
     * @param latitude
     * @param longitude
     */
/*    @Query("UPDATE " + Point.TABLE_NAME + " SET latitude = :latitude, longitude = :longitude WHERE id = :id")
    int update(Long id, String latitude, String longitude);*/

    @Update
    int update(PointBackup pointBackup);

    /**
     * Delete a pointDTO from the persistant layer
     * @param pointBackup
     */
    @Delete
    void delete(PointBackup pointBackup);


    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @param undoPosition
     * @return
     */
    @Query("SELECT * FROM " + PointBackup.TABLE_NAME + " WHERE " +  PointBackup.UNDO_POSITION  + " = :undoPosition")
    List<PointBackup> searchLastUndo(int undoPosition);

    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @return
     */
    @Query("SELECT COALESCE(MAX(" + PointBackup.UNDO_POSITION + "),0) FROM " + PointBackup.TABLE_NAME)
    int searchLastUndoPosition();


    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @return
     */
    @Query("SELECT COUNT(*) FROM " + PointBackup.TABLE_NAME)
    int searchCount();

}
