package com.zeedroid.maparcade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.database.AppDatabase;
import com.zeedroid.maparcade.service.GPS_Service;
import com.zeedroid.maparcade.service.Sensor_Service;

public class MapCreateActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private TextView recordText, modifyText, publishText;
    private Button recordButton, modifyButton, publishButton;
    private PointArray myJourney;
    private PointExtraArray myPoints;
    private String mapType;
    private int zoomLevel;

    @Override
    protected void onResume() {
        super.onResume();

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle gpsBundle = intent.getExtras();

                    myJourney.add(new Point(gpsBundle.getDouble("longitude"), gpsBundle.getDouble("latitude"), gpsBundle.getFloat("timepassed")));
                    Log.d("xxx", "MapCreateActivity myJournet.add timepassed = " + gpsBundle.getFloat("timepassed"));
                    myPoints.add(new PointExtra(myJourney.get(0),getString(R.string.start_point),"",PointColorTable.startPoint,PointShape.circle));

                    ArrayBundle ab = new ArrayBundle();
                    ab.setMapType(mapType);
                    ab.setZoomLevel(zoomLevel);
                    ab.setPoints(myJourney);
                    ab.setPointsExtra(myPoints);

                    unregisterReceiver(broadcastReceiver);
                    Intent mapActivity = new Intent(getApplicationContext(), PreviewMapActivity1.class);
                    mapActivity.putExtra("points", ab);

                    startActivity(mapActivity);
                }
            };
        }
        recordText.setText(R.string.map_env);
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_creation);
        mapType = getIntent().getStringExtra("mapType");
        zoomLevel = getIntent().getIntExtra("zoomLevel", 18);

        recordText = (TextView)findViewById(R.id.recordText);
        recordText.setTextSize(20);
        modifyText = (TextView)findViewById(R.id.modifyText);
        modifyText.setTextSize(20);
        publishText = (TextView)findViewById(R.id.publishText);
        publishText.setTextSize(20);
        recordButton = (Button)findViewById(R.id.recordButton);
        modifyButton = (Button)findViewById(R.id.modifyButton);
        publishButton = (Button)findViewById(R.id.publishButton);

        Intent gpsBroadcast = new Intent("gps_settings");
        gpsBroadcast.putExtra("gpsMinSeconds",2);
        gpsBroadcast.putExtra("gpsMinDistance",2);

        Log.d("sjd","Before create database");
//        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "map_arcade_db").build();
/*        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
        Log.d("sjd","After database created");
        PointDAO pDAO = db.getPointDAO();
        com.zeedroid.maparcade.entity.Point p =
                new com.zeedroid.maparcade.entity.Point(
                        1,1, 1, "50.2754", "-50.3456");*/
//        Log.d("sjd","After Point created");
//        p.setLatitude("50.2567");
//        p.setLongitude("-50.345678");
//        p.setPathID(1);
//        p.setPointPosition(1);
//        p.setRouteID(1);




//        pDAO.insert(p);
//        db.pointDAO().insert(p);
/*        Log.d("sjd","After Point Inserted");

        List<com.zeedroid.maparcade.entity.Point> a = db.pointDAO().searchPoint(1);
        Toast.makeText(this.getApplicationContext(),"database count =" + a.size(),Toast.LENGTH_SHORT).show();
*/
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordText.setText(R.string.connecting_gps);

                Intent i = new Intent(getApplicationContext(), GPS_Service.class);

                myJourney = new PointArray();
                myPoints = new PointExtraArray();
                startService(i);
                recordText.setText(R.string.gps_signal);

                Intent j = new Intent(getApplicationContext(), Sensor_Service.class);

                startService(j);

/*            Log.d("sjd","Before Point Inserted");
                Thread a = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                            Log.d("sjd", "After database created");
                            PointDAO pDAO = db.getPointDAO();
                            com.zeedroid.maparcade.entity.Point p =
                                    new com.zeedroid.maparcade.entity.Point(
                                            1, 1, 1, "50.2754", "-50.3456");
                            Log.d("sjd", "After Point created");
                            pDAO.insert(p);

                        } catch (Exception e) {
                            Log.d("sjd", "" + e.getMessage());
                        }
                    }
                });
                a.start();*/
            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyText.setText(R.string.loading_map);

                Bundle gpsBundle = getIntent().getExtras();

                ArrayBundle ab = new ArrayBundle();
                ab.setMapType(mapType);
                ab.setZoomLevel(zoomLevel);
                ab.setPoints(myJourney);
                ab.setPointsExtra(myPoints);

                Intent mapActivity = new Intent(getApplicationContext(), ModifyMapActivity.class);
                mapActivity.putExtra("points", ab);

                startActivity(mapActivity);

            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishText.setText(R.string.connecting_gps);

                Bundle gpsBundle = getIntent().getExtras();

                ArrayBundle ab = new ArrayBundle();
                ab.setMapType(mapType);
                ab.setZoomLevel(zoomLevel);
                ab.setPoints(myJourney);
                ab.setPointsExtra(myPoints);

                Intent mapActivity = new Intent(getApplicationContext(), PublishMapActivity.class);
                mapActivity.putExtra("points", ab);

                startActivity(mapActivity);
            }
        });
    }
}
