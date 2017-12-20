package com.zeedroid.maparcade.repository;

import android.arch.lifecycle.LiveData;

import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.entity.Point;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Steve Dixon on 29/10/2017.
 */

public class PointRepository {
    private final PointDAO pointDao;

    @Inject
    public PointRepository (PointDAO pointDao){
        this.pointDao = pointDao;
    }

    public LiveData<List<Point>> getAllPoints(){
        return pointDao.searchAll();
    }

    public LiveData<List<Point>> getPoint(int pathID){
        return pointDao.searchPoint(pathID);
    }

    public int getCount(){
        return pointDao.searchCount();
    }

    public void insertPoint(Point point){
        pointDao.insert(point);
    }

 /*   public void updatePoint(Long id, String latitude, String longitude){
        pointDao.update(id, latitude, longitude);
    }*/

    public void updatePoint(Point point){
        pointDao.update(point);
    }

    public void deletePoint(List<Point> point){
        pointDao.delete(point);
    }

}
