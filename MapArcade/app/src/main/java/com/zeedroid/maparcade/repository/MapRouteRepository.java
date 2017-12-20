package com.zeedroid.maparcade.repository;

import android.arch.lifecycle.LiveData;

import com.zeedroid.maparcade.dao.MapRouteDAO;
import com.zeedroid.maparcade.entity.MapRoute;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Steve Dixon on 29/10/2017.
 */

public class MapRouteRepository {
    private final MapRouteDAO mapRouteDao;

    @Inject
    public MapRouteRepository(MapRouteDAO mapRouteDao){
        this.mapRouteDao = mapRouteDao;
    }

    public LiveData<List<MapRoute>> getAllRoutes(){
        return mapRouteDao.searchAll();
    }

    public LiveData<List<MapRoute>> getRoute(Long route){
        return mapRouteDao.searchRoute(route);
    }

    public LiveData<List<MapRoute>> getRoutes(Date fromDate, Date toDate){
        return mapRouteDao.searchRoute(fromDate, toDate);
    }

    public void insertRoute(MapRoute mapRoute){
        mapRouteDao.insert(mapRoute);
    }

    public void updateRoute(MapRoute mapRoute){
        mapRouteDao.update(mapRoute);
    }

    public void deleteRoute(MapRoute mapRoute){
        mapRouteDao.delete(mapRoute);
    }
}
