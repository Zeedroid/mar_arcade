package com.zeedroid.maparcade.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Steve Dixon on 30/07/2017.
 */

public class Sensor_Service extends Service{

        private SensorEventListener listener;
        private SensorManager       sensorManager;
        private Sensor              ACCELEROMETER, MAGNETIC_FIELD;
    
        private float[] accelerometerReading = new float[3];
        private float[] magnetometerReading  = new float[3];
    
        private float[] rotationMatrix       = new float[9];
        private float[] orientationAngles    = new float[3];

        private boolean accelerometerSet     = false;
        private boolean magnetometerSet      = false;


        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate(){
            sensorManager   = (SensorManager) this.getSystemService(SENSOR_SERVICE);

            ACCELEROMETER   = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            MAGNETIC_FIELD  = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            listener = new SensorEventListener(){
                @Override

                public void onSensorChanged(SensorEvent event){
                    if (event.sensor == ACCELEROMETER){
                        System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
                        accelerometerSet = true;
                    }else if (event.sensor == MAGNETIC_FIELD){
                        System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
                        magnetometerSet = true;
                    }

                    if (accelerometerSet && magnetometerSet) {
                        accelerometerSet = false;
                        magnetometerSet = false;
                        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
                        SensorManager.getOrientation(rotationMatrix, orientationAngles);

                        float azimuthInRadians = orientationAngles[0];
                        float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

                        Intent i = new Intent("sensor_update");
                        i.putExtra("azimuthInDegrees", azimuthInDegrees);

                        sendBroadcast(i);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy){

                }

            };
            sensorManager.registerListener(listener, ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(listener, MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        

        @Override
        public void onDestroy(){
            super.onDestroy();
            sensorManager.unregisterListener(listener,ACCELEROMETER);
            sensorManager.unregisterListener(listener,MAGNETIC_FIELD);
        }
}


