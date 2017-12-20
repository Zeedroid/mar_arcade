package com.zeedroid.maparcade.repository;

import android.arch.lifecycle.LiveData;

import com.zeedroid.maparcade.dao.MapAddressDAO;
import com.zeedroid.maparcade.entity.MapAddress;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Steve Dixon on 29/10/2017.
 */

public class MapAddressRepository {
    private final MapAddressDAO mapAddressDao;

    @Inject
    public MapAddressRepository(MapAddressDAO mapAddressDao){
        this.mapAddressDao = mapAddressDao;
    }

    public LiveData<List<MapAddress>> getAllMapAddresses(){
        return mapAddressDao.searchAll();
    }

    public LiveData<List<MapAddress>> getRouteAddresses(int route){
        return mapAddressDao.searchAddress(route);
    }

    public LiveData<List<MapAddress>> getMapAddress(Long roadID){
        return mapAddressDao.searchAddress(roadID);
    }

    public void insertMapAddress(MapAddress mapAddress){
        mapAddressDao.insert(mapAddress);
    }

    public void updateMapAddress(MapAddress mapAddress){
        mapAddressDao.update(mapAddress);
    }

    public void deleteMapAddress(MapAddress mapAddress){
        mapAddressDao.delete(mapAddress);
    }
}
