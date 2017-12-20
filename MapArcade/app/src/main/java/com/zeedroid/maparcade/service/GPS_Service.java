package com.zeedroid.maparcade.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Steve Dixon on 07/06/2017.
 */

public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager  locationManager;
    private BroadcastReceiver receiver;
    private int gpsMinTime, gpsMinDistance;
    private int ignoreLocationCount = 3; // delete first 3 locations received to help ensure good starting point.

    private boolean gpsEnabled         = false;

    private float   elapsedMiliseconds = 0;
    private long    startedMiliseconds = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        startedMiliseconds = System.currentTimeMillis();
        listener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location){
                elapsedMiliseconds = System.currentTimeMillis() - startedMiliseconds;
                if (ignoreLocationCount == 0) {
                    Log.d("xxx", "GPS_Service elapsedMilliseconds = " + elapsedMiliseconds);
                    Intent i = new Intent("location_update");
                    Bundle extras = new Bundle();
                    extras.putString("coordinates", location.getLongitude() + " " + location.getLatitude());
                    extras.putDouble("longitude", location.getLongitude());
                    extras.putDouble("latitude", location.getLatitude());
                    extras.putFloat("timepassed", elapsedMiliseconds);
                    Log.d("xxx","Speed = " + location.getSpeed() + " GPS Accuracy = " + location.getAccuracy() + " time = " + location.getTime() + " realtime nano = " + location.getElapsedRealtimeNanos());
                    i.putExtras(extras);

                    sendBroadcast(i);
                }else ignoreLocationCount--;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle){

            }

            @Override
            public void onProviderEnabled(String s){

            }

            @Override
            public void onProviderDisabled(String s){
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equalsIgnoreCase("gps_settings")){
                    gpsMinTime     = intent.getIntExtra("gpsMinSeconds",2) * 1000;
                    gpsMinDistance = intent.getIntExtra("gpsMinDistance",2);

                }
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,"delete_aiding_data",null);
        locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,"force_xtra_injection",null);
        locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,"force_time_injection",null);

        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,gpsMinTime,gpsMinDistance,listener);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
