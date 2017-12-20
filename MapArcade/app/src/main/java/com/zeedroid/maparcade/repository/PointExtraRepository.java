package com.zeedroid.maparcade.repository;

import android.arch.lifecycle.LiveData;

import com.zeedroid.maparcade.dao.PointExtraDAO;
import com.zeedroid.maparcade.entity.PointExtra;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Steve Dixon on 29/10/2017.
 */

public class PointExtraRepository {
    private final PointExtraDAO pointExtraDao;

    @Inject
    public PointExtraRepository(PointExtraDAO pointExtraDao){
        this.pointExtraDao = pointExtraDao;
    }

    public LiveData<List<PointExtra>> getAllPointExtras(){
        return pointExtraDao.searchAll();
    }

    public void insertPointExtra(PointExtra pointExtra){
        pointExtraDao.insert(pointExtra);
    }

    public void updatePointExtra(PointExtra pointExtra){
        pointExtraDao.update(pointExtra);
    }

    public void deletePointExtra(PointExtra pointExtra){
        pointExtraDao.delete(pointExtra);
    }
}
