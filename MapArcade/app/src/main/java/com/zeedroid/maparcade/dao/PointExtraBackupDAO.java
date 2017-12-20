package com.zeedroid.maparcade.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.zeedroid.maparcade.entity.PointExtraBackup;

import java.util.List;

/**
 * Created by User on 18/11/2017.
 */

@Dao
public interface PointExtraBackupDAO {

    /**
     * Save the PointExtraDTO to the persistence layer
     * @param pointExtraBackup
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PointExtraBackup pointExtraBackup);

    /**
     * Update a PointExtraDTO in the persistant layer
     * @param pointExtraBackup
     */
    @Update
    void update(PointExtraBackup pointExtraBackup);


    /**
     * Delete a pointExtraDTO from the persistant layer
     * @param pointExtraBackup
     */
    @Delete
    void delete(PointExtraBackup pointExtraBackup);

    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @param undoPosition
     * @return
     */
    @Query("SELECT * FROM " + PointExtraBackup.TABLE_NAME + " WHERE " +  PointExtraBackup.UNDO_POSITION  + " > :undoPosition")
    List<PointExtraBackup> searchLastUndo(int undoPosition);

    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @return
     */
    @Query("SELECT MAX(" + PointExtraBackup.UNDO_POSITION + ") FROM " + PointExtraBackup.TABLE_NAME)
    int searchLastUndoPosition();


    /**
     * Return aa the points within the bounding box that match latitude, longitude and range
     * @return
     */
    @Query("SELECT COUNT(*) FROM " + PointExtraBackup.TABLE_NAME)
    int searchCount();

}
