package com.zeedroid.maparcade;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.zeedroid.maparcade.dao.PointExtraDAO;
import com.zeedroid.maparcade.database.AppDatabase;
import com.zeedroid.maparcade.entity.*;
import com.zeedroid.maparcade.entity.PointExtra;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Created by Steve Dixon on 17/08/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MapRouteDaoTest {
    AppDatabase testDatabase;


//    @Rule
//    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init(){
        testDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).build();
    }

    @After
    public void close(){
        testDatabase.close();
    }

    @Test
    public void writeAndReadMapRoute() throws InterruptedException{

 /*       Observer<List<com.zeedroid.maparcade.entity.MapRoute>> mapRouteObserver =
                new Observer<List<com.zeedroid.maparcade.entity.MapRoute>>() {
                    @Override
                    public void onChanged(@Nullable final List<com.zeedroid.maparcade.entity.MapRoute> newPoint){
                        Log.d("sjd", "count of point records = " + newPoint.size());
                    }
                };
        LiveData<List<MapRoute>> result = testDatabase.getMapRouteDAO().searchRoute(12345L);
        result.observe(this, mapRouteObserver);*/
        MapRoute mr = getValue(testDatabase.getMapRouteDAO().searchRoute(12345L));

        MapRoute mapRoute = new MapRoute();
        mapRoute.setRouteID(12345L);
        mapRoute.setRouteType(1);
        mapRoute.setRouteName("Test Walk");
        mapRoute.setRouteDescription("Test Walk Description");
        mapRoute.setRouteDifficulty(5);
        mapRoute.setRouteScore(3.7f);
        testDatabase.getMapRouteDAO().insert(mapRoute);


//        MapRoute mr = mapRoutes.get(0);
        if (BuildConfig.DEBUG) {
            if (mr.getRouteID() != 12345L ||
                    mr.getRouteType() != 1 ||
                    mr.getRouteName() != "Test Walk" ||
                    mr.getRouteDescription() != "Test Walk Description" ||
                    mr.getRouteDifficulty() != 5 ||
                    mr.getRouteScore() != 3.7f) {
                throw new AssertionError("RouteMapDaoTest Failed during writeAndRead Test");
            }
       }
    }

    @Test
    public void writeAndReadPoint(){
        MapRoute mapRoute = new MapRoute();
        mapRoute.setRouteID(23456L);
        mapRoute.setRouteType(1);
        mapRoute.setRouteName("Test Walk");
        mapRoute.setRouteDescription("Test Walk Description");
        mapRoute.setRouteDifficulty(5);
        mapRoute.setRouteScore(3.7f);
        testDatabase.getMapRouteDAO().insert(mapRoute);

        com.zeedroid.maparcade.entity.Point point = new com.zeedroid.maparcade.entity.Point();
        point.setRouteID(23456L);
        point.setPointPosition(1);
        point.setPathID(6);
        point.setLongitude("-0.087890625");
        point.setLatitude("50.86577800109838");
        testDatabase.getPointDAO().insert(point);

        LiveData<List<com.zeedroid.maparcade.entity.Point>> result = testDatabase.getPointDAO().searchAll();
        com.zeedroid.maparcade.entity.Point p = result.getValue().get(0);
        if (BuildConfig.DEBUG) {
            if (p.getRouteID() != 23456L ||
                    p.getPathID() != 6 ||
                    p.getPointPosition() != 1 ||
                    p.getLongitude() != "-0.087890625" ||
                    p.getLatitude() != "50.86577800109838") {
                throw new AssertionError("PointDaoTest Failed during writeAndRead Test");
            }
        }
    }

    @Test
    public void writeAndReadPointExtra(){
//        MutableLiveData<List<PointExtra>> result = new MutableLiveData<>();
//        when(testDatabase.getPointExtraDAO().searchAll()).thenReturn(result);
        MapRoute mapRoute = new MapRoute();
        mapRoute.setRouteID(34567L);
        mapRoute.setRouteType(1);
        mapRoute.setRouteName("Test Walk");
        mapRoute.setRouteDescription("Test Walk Description");
        mapRoute.setRouteDifficulty(5);
        mapRoute.setRouteScore(3.7f);
        testDatabase.getMapRouteDAO().insert(mapRoute);

        LiveData<List<com.zeedroid.maparcade.entity.PointExtra>> result = testDatabase.getPointExtraDAO().searchAll();

        com.zeedroid.maparcade.entity.PointExtra pointExtra = new com.zeedroid.maparcade.entity.PointExtra();
        pointExtra.setRouteID(34567L);
        pointExtra.setPoint(new Point(-0.087890625, 50.86577800109838));
        pointExtra.setPosition("left");
        pointExtra.setPointType("Test Point");
        pointExtra.setPointShape(1);
        pointExtra.setPointColor("#CEFF9D");
        testDatabase.getPointExtraDAO().insert(pointExtra);

        try {
            PointExtra pe = result.getValue().get(0);

            if (BuildConfig.DEBUG) {
                if (pe.getRouteID() != 34567L ||
                        pe.getPoint().getLongitude() != -0.087890625 ||
                        pe.getPoint().getLatitude() != 50.86577800109838 ||
                        pe.getPosition() != "left" ||
                        pe.getPointType() != "Test Point" ||
                        pe.getPointShape() != 1 ||
                        pe.getPointColor() != "#CEFF9D") {
                    throw new AssertionError("PointExtraDaoTest Failed during writeAndReadPointExtra Test");
                }
            }
        }catch (NullPointerException e){
            throw new AssertionError("PointExtraDaoTest NullPointerException Failed during writeAndReadPointExtra Test");
        }
    }

    @Test
    public void writeAndReadMapAddress(){
        MapRoute mapRoute = new MapRoute();
        mapRoute.setRouteID(45678L);
        mapRoute.setRouteType(1);
        mapRoute.setRouteName("Test Walk");
        mapRoute.setRouteDescription("Test Walk Description");
        mapRoute.setRouteDifficulty(5);
        mapRoute.setRouteScore(3.7f);
        testDatabase.getMapRouteDAO().insert(mapRoute);

        MapAddress mapAddress = new MapAddress();
        mapAddress.setRouteID(45678L);
        mapAddress.setRoadID(123);
        mapAddress.setRoadName("Oxford Street");
        mapAddress.setPostCode("W1D 1BS");
        testDatabase.getMapAddressDAO().insert(mapAddress);

        LiveData<List<MapAddress>> result = testDatabase.getMapAddressDAO().searchAll();
        try {
            MapAddress ma = result.getValue().get(0);
            if (BuildConfig.DEBUG) {
                if (!ma.getRoadName().equals("Oxford Street") || !ma.getPostCode().equals("W1D 1BS")){
                    throw new AssertionError("MapAddressDaoTest Failed during writeAndRead Test");
                }
            }
        }catch (NullPointerException e){
            throw new AssertionError("MapAddressDaoTest Failed during writeAndReadMapAddress Test");
        }
    }

    /**
     * This is used to make sure the method waits till data is available from the observer.
     */
    public static <T>  T getValue(LiveData<List<T>> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        final LiveData<List<T>> aaa = liveData;
        Observer<List<T>> observer = new Observer<List<T>>() {
            @Override
            public  void onChanged(@Nullable List<T> o) {
                Log.d("sjdtest", "AAAAAAAAAAAAAAAAAA");
                Log.d("sjdtest", "livedata = " + o.size());
                data[0] = o;
                latch.countDown();
                aaa.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        //noinspection unchecked
        Log.d("sjdtest", "data count = " + data.length);
        Log.d("sjdtest", "data " + data[0].toString());
        return (T) aaa.getValue().get(0);
//        return (T) data[0];
    }
}
