package com.zeedroid.maparcade;

import android.arch.persistence.room.Room;
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
import android.widget.Toast;

import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.database.AppDatabase;
import com.zeedroid.maparcade.service.GPS_Service;
import com.zeedroid.maparcade.service.Sensor_Service;

import java.util.List;

public class CreateMapActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private TextView locationText;
    private Button startButton, addButton, stopButton;
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
                    myPoints.add(new PointExtra(myJourney.get(0), getString(R.string.start_point),"",PointColorTable.startPoint,PointShape.circle));
                    Log.d("sjd", "CreateMapActivity timepassed = " + gpsBundle.getFloat("timepassed"));
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
        locationText.append("\n" + getString(R.string.setup_map_env));
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
        setContentView(R.layout.activity_create_map);
        mapType = getIntent().getStringExtra("mapType");
        zoomLevel = getIntent().getIntExtra("zoomLevel", 18);

        locationText = (TextView)findViewById(R.id.locationText);
        locationText.setTextSize(20);
        startButton = (Button)findViewById(R.id.startButton);
        addButton = (Button)findViewById(R.id.addButton);
        stopButton = (Button)findViewById(R.id.stopButton);

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
        startButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            locationText.append("\n" + getString(R.string.connect_gps));

            Intent i = new Intent(getApplicationContext(), GPS_Service.class);

            myJourney = new PointArray();
            myPoints = new PointExtraArray();
            startService(i);
            locationText.append("\n" + getString(R.string.waiting_gps));

            Intent j = new Intent(getApplicationContext(), Sensor_Service.class);

            startService(j);
        }
        });

        Log.d("sjd","Before Point Inserted");
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                    Log.d("sjd", "After database created");
                    PointDAO pDAO = db.getPointDAO();
                    com.zeedroid.maparcade.entity.Point p =
                            new com.zeedroid.maparcade.entity.Point(
                                    1, (long)1, 1, "50.2754", "-50.3456", 5.27f);
                    Log.d("sjd", "After Point created");
                    pDAO.insert(p);

//                    List<com.zeedroid.maparcade.entity.Point> a = db.getPointDAO().searchPoint(1);
  //                  Log.d("sjd", "database count =" + a.size());
//                    db.close();

                }
                catch(Exception e){
                    Log.d("sjd", "" + e.getStackTrace());
                }
            }
        });
        a.start();
    }
}
