package com.zeedroid.maparcade;

import android.app.Activity;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.zeedroid.maparcade.dao.MapRouteDAO;
import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.database.AppDatabase;
import com.zeedroid.maparcade.entity.MapRoute;
import com.zeedroid.maparcade.service.FetchAddressIntentService;
import com.zeedroid.maparcade.service.GPS_Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class PreviewMapActivity1 extends AppCompatActivity  implements TextToSpeech.OnInitListener{
//    public class PreviewMapActivity1 extends AppCompatActivity {
//    private TextureView mapTexture;
    private BroadcastReceiver locationBroadcastReceiver, sensorBroadcastReceiver;
    private PreviewMapLayout previewMapLayoutView;

    private PreviewMapModel viewModel;
    private Button startButton, stopButton, addButton;
    private TextView mapStats, mapStatsExtend;

    private double currentLongitude, currentLatitude, previousLongitude, previousLatitude;
    private float  timepassed;
    private PointArray myJourney;
    private PointExtraArray myPoints;

    private String mapType;
    private int zoomLevel;
    private int tileSize;

    private final int minZoom = 0;
    private final int maxZoom = 22;

    private GestureDetector gd;
    private View.OnTouchListener gl;
    private boolean mIsScrolling = false;
    private ScaleGestureDetector sgd;

    private SharedPreferences prefs;

    private float azimuthInDegrees;

//    private boolean creatingMap = true;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput;

    private double scrollX = 0;
    private double scrollY = 0;

//    private FeatureTextToSpeech tts;
    private TextToSpeech tts;

    @Override
    protected void onResume() {
        super.onResume();

        if (locationBroadcastReceiver == null) {
            locationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle gpsBundle = intent.getExtras();

                    previousLatitude = currentLatitude;
                    previousLongitude = currentLongitude;
                    currentLatitude = gpsBundle.getDouble("latitude");
                    currentLongitude = gpsBundle.getDouble("longitude");
                    timepassed        = gpsBundle.getFloat("timepassed");
                    Log.d("xxx", "PreviewMapActivity1 timepassed = " + timepassed);

                    viewModel.addPointToJourney(currentLongitude, currentLatitude, timepassed);
                    setupMapStats();
                    previewMapLayoutView.invalidate();
                }
            };
        }
        registerReceiver(locationBroadcastReceiver, new IntentFilter("location_update"));

        if (sensorBroadcastReceiver == null) {
            sensorBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    azimuthInDegrees = intent.getFloatExtra("azimuthInDegrees",0);
                }
            };
        }
        registerReceiver(sensorBroadcastReceiver, new IntentFilter("sensor_update"));

        if (myJourney != null && myJourney.size() > 0) setupMapStats();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationBroadcastReceiver != null) {
            unregisterReceiver(locationBroadcastReceiver);
        }
        if (sensorBroadcastReceiver != null) {
            unregisterReceiver(sensorBroadcastReceiver);
        }
        tts.shutdown();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_map);
        previewMapLayoutView = findViewById(R.id.mapLayout);

        startButton          = findViewById(R.id.startButton);
        addButton            = findViewById(R.id.addButton);
        stopButton           = findViewById(R.id.stopButton);

        mapStats             = findViewById(R.id.mapStats);
        mapStatsExtend       = findViewById(R.id.mapStatsExtend);
        mapStatsExtend.setVisibility(View.GONE);


        ArrayBundle ab       = getIntent().getParcelableExtra("points"); // (ArrayBundle)
        myJourney            = ab.getPoints();
        myPoints             = ab.getPointsExtra();
        mapType              = ab.getMapType();
        zoomLevel            = ab.getZoomLevel();

        prefs                = PreferenceManager.getDefaultSharedPreferences(this);
        tileSize             = Integer.parseInt(prefs.getString("map_tile_size","512"));

        viewModel = ViewModelProviders.of(this).get(PreviewMapModel.class);
        viewModel.setPoints(myJourney);
        viewModel.setPointsExtra(myPoints);
        viewModel.setMapType(mapType);
        viewModel.setZoomLevel(zoomLevel);
        viewModel.setCreatingMap(true);

        getScreenSize();
        viewModel.createCompassCircles();
        viewModel.setSquares();
        viewModel.setTileCoordinates(viewModel.getStartX(),viewModel.getStartY());
        viewModel.setMapTileBoundingBox();

        previewMapLayoutView.setViewModel(viewModel);
        mResultReceiver = new AddressResultReceiver(new Handler());

        tts = new TextToSpeech(this, this);

        Geocoder aaa = new Geocoder(previewMapLayoutView.getContext());
        List<Address> addresses;
        try {
            addresses = aaa.getFromLocation(50.876508, 0.019885, 1);
            Toast.makeText(previewMapLayoutView.getContext(),addresses.get(0).getAddressLine(0),Toast.LENGTH_SHORT).show();
            Toast.makeText(previewMapLayoutView.getContext(),addresses.get(0).getAdminArea(),Toast.LENGTH_SHORT).show();
            Toast.makeText(previewMapLayoutView.getContext(),addresses.get(0).getPostalCode(),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.d("sjd",e.getMessage());
            Log.d("sjd",e.getStackTrace().toString());
            Toast.makeText(previewMapLayoutView.getContext(),"No Address",Toast.LENGTH_SHORT).show();
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.isMapBeingCreated()) promptSpeechInput();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
                viewModel.setCreatingMap(false);

                try {
                    viewModel.addFeatureToJourney(currentLongitude, currentLatitude, getString(R.string.end_point), PointColorTable.endPoint, PointShape.circle);
                }catch (EmptyFeatureException e){
                    Log.d("sjd", "Empty Feature");
                }
                viewModel.recalibratePointsExtra();
//                previewMapLayoutView.setCreatingMap(creatingMap);
                previewMapLayoutView.invalidate();
                viewModel.preserveJourney();
                Toast.makeText(PreviewMapActivity1.this, "Map Created!", Toast.LENGTH_SHORT).show();
                if (!Geocoder.isPresent()) {
                    Toast.makeText(PreviewMapActivity1.this,
                            R.string.no_geocoder_available,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Start service and update UI to reflect new location
                startIntentService();
                addButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
//                viewModel.updateMapRouteRecord(Constants.MODIFY_MAP);
//                updateUI();

//                viewModel.getRoadNames();
             }
        });

        gd  = new GestureDetector(this,new Gesture());
        sgd = new ScaleGestureDetector(this, new ScaleListener());


        gl = new PreviewMapLayout.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (sgd.onTouchEvent(event)) {
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mIsScrolling ) {
                        Log.d("sjd","UP");
                        mIsScrolling  = false;
//                        handleScrollFinished();
                        Toast.makeText(PreviewMapActivity1.this, "ACTION_UP",Toast.LENGTH_SHORT).show();
                    };
                }

                return false;
            }
        };



    }

    @Override
    public void onInit(int status) {
        Log.d("sjdmod","inInit");
        Log.d("sjdmod","status=" + status);
        Log.d("sjdfmod","SUCCESS=" + TextToSpeech.SUCCESS);
        if (status == TextToSpeech.SUCCESS) {
            tts.setVoice((Voice) tts.getVoices().toArray()[0]);
            tts.setPitch(1);
//            tts.setSpeechRate(0.7f);
            int language = tts.setLanguage(Locale.getDefault());

            Log.d("sjdmod", "setLanguage:" + language);
            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {

                Log.d("sjdmod", "language missing or not supported");
            }

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    Log.d("sjdmod", "Started speaking");
                }

                @Override
                public void onError(String utteranceId) {
                    Log.d("sjdmod", "Error in processing Text to speech");
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.d("sjdmod", "Text to speech finished previewing");
                    tts.shutdown();
                }

            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        getScreenSize();
        viewModel.createCompassCircles();
        viewModel.recalibratePointsExtra();

        gl = new PreviewMapLayout.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (sgd.onTouchEvent(event)) {
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mIsScrolling ) {
                        Log.d("sjd","UP");
                        mIsScrolling  = false;
//                        handleScrollFinished();
                        Toast.makeText(PreviewMapActivity1.this, "ACTION_UP",Toast.LENGTH_SHORT).show();
                    };
                }

                return false;
            }
        };



//        previewMapLayoutView.setCreatingMap(creatingMap);
        previewMapLayoutView.invalidate();
    }

    // A method to speak something
    @SuppressWarnings("deprecation") // Support older API levels too.
    public void speak(String text, Boolean override) {
        Log.d("sjdmod","speak");
        Log.d("sjdmod","ttsOK");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (override) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
            }
        }else {
            if (override) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null);
            }
        }
        Log.d("sjdmod","endSpeak");
    }

    protected void startIntentService() {
        mLastLocation = new Location(LocationManager.GPS_PROVIDER);
        mLastLocation.setLatitude(myJourney.get(myJourney.size() -1).getLatitude());
        mLastLocation.setLongitude(myJourney.get(myJourney.size() -1).getLongitude());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

   public void toggleStats(View view){
       if (mapStatsExtend.isShown()) {
           Utilities.slideUp(this, mapStatsExtend);
           mapStatsExtend.setVisibility(View.GONE);
       }else {
           mapStatsExtend.setVisibility(View.VISIBLE);
           Utilities.slideDown(this, mapStatsExtend);
       }
   }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "3000");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Toast.makeText(PreviewMapActivity1.this, result.get(0), Toast.LENGTH_SHORT).show();

                    try {
                        viewModel.addFeatureToJourney(currentLongitude, currentLatitude, result.get(0));
                        finishActivity(REQ_CODE_SPEECH_INPUT);
                    } catch (EmptyFeatureException e) {
                        Toast.makeText(PreviewMapActivity1.this, getString(R.string.say_feature), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Toast.makeText(PreviewMapActivity1.this, getString(R.string.voice_exception), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    private void getScreenSize(){
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        int statusHeight = Utilities.getActionBarHeight(this) +
                           Utilities.getStatusBarHeight(this) +
                           Utilities.getNavigationBarHeight(this);
        viewModel.setScreenSizeInfo(new ScreenSizeInfo(size.x, size.y, Utilities.getActionBarHeight(this),
                Utilities.getStatusBarHeight(this)));
 //       viewModel.setScreenSizeInfo(new ScreenSizeInfo(size.x, size.y - statusHeight, Utilities.getActionBarHeight(this),
 //               Utilities.getStatusBarHeight(this)));
        Toast.makeText(this,"Width=" + size.x + " Height=" + (size.y - statusHeight),Toast.LENGTH_SHORT).show();
    }

    private void setupMapStats(){
        mapStats.setText(getString(R.string.zoom_scale, zoomLevel,(int)(MercatorProjection.mapScaleMetersPerPixel(zoomLevel, currentLatitude) * tileSize )));
        mapStatsExtend.setText(getString(R.string.distance_traveled, ((double)((int)(MercatorProjection.distanceOfJourneyKm(viewModel.getPoints()) * 1000)) / 1000)));
    }

    public boolean onTouchEvent(MotionEvent ev){
//        Toast.makeText(PreviewMapActivity1.this,"AAAAAAAAAAAAAAAAAAA",Toast.LENGTH_SHORT).show();
        if (gd.onTouchEvent(ev)){
//            Toast.makeText(PreviewMapActivity1.this, "Type=" + ev.getAction(), Toast.LENGTH_SHORT).show();
        };
        sgd.onTouchEvent(ev);

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector){
            super.onScale(detector);

            float scale = detector.getScaleFactor();

            if (scale <= 1.0){
                if (scale <= 0.8) {
                    if (zoomLevel > minZoom) zoomLevel--;
                }
            }else {
                if (scale > 1.2) {
                    if (zoomLevel < maxZoom) zoomLevel++;
                }
            }

            viewModel.setZoomLevel(zoomLevel);
            viewModel.setMapTileBoundingBox();
            viewModel.setTileCoordinates(viewModel.getStartX(), viewModel.getStartY());
            viewModel.recalibratePointsExtra();

            setupMapStats();

            previewMapLayoutView.invalidate();
            return true;
        }
    }


    private class Gesture extends GestureDetector.SimpleOnGestureListener {

        public boolean onSingleTapUp(MotionEvent ev){
            PointExtra feature = viewModel.getFeature(ev.getX(), ev.getY());

            if (feature.getPoint() != null){
                Toast.makeText(PreviewMapActivity1.this, getString(R.string.feature) + feature.getPointType(), Toast.LENGTH_SHORT).show();
//                tts = new FeatureTextToSpeech(PreviewMapActivity1.this);

                speak(feature.getPointType(), true);
            }else {
                Toast.makeText(PreviewMapActivity1.this, getString(R.string.no_feature), Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        public void onLongPress(MotionEvent ev){
            PointExtra feature = viewModel.getFeature(ev.getX(), ev.getY());

            if (feature.getPoint() != null){
                Toast.makeText(PreviewMapActivity1.this, getString(R.string.feature) + feature.getPointType(), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(PreviewMapActivity1.this, getString(R.string.no_feature), Toast.LENGTH_SHORT).show();
            }
        }

        float X = 0;
        public boolean onScroll(MotionEvent ev1, MotionEvent ev2, float distanceX, float distanceY){
            double screenPixelX, screenPixelY;
            double tileX, tileY;
            int moveTileX = 0;
            int moveTileY = 0;
            mIsScrolling = true;
            TileNumberPixel tilePixelX,tilePixelY;
            TileCoordinates tc;

/*          if(ev1.getAction() == MotionEvent.ACTION_UP) {
                if(mIsScrolling ) {
                    Log.d("sjd","UP");
                    mIsScrolling  = false;
//                        handleScrollFinished();
                    Toast.makeText(PreviewMapActivity1.this, "ACTION_UP",Toast.LENGTH_SHORT).show();
                };
            }*/

 //           Toast.makeText(PreviewMapActivity1.this,"tileArray=" + viewModel.getTileCoordinates().size(),Toast.LENGTH_SHORT).show();
            Log.d("QQQ","ev1X=" + ev1.getX() + " ev2X=" + ev2.getX() + " distance=" + distanceX);
/*            if (distanceX > 0) {
                if (((Math.abs(ev1.getX() - (ev2.getX() - distanceX))) % tileSize) + distanceX >= tileSize) {
                    moveTileX = -1;
                    Log.d("PPP", "abs1=" +Math.abs(ev1.getX() - (ev2.getX() - distanceX)) + " % " + tileSize + " + " + distanceX + " >= " + tileSize );
                    Log.d("QQQ","MOVE TILE X=" + moveTileX + " pixels=" + ((Math.abs(ev1.getX() - (ev2.getX() - distanceX))) % tileSize) + distanceX);
                }
            }
            else if (distanceX < 0){
                if (((Math.abs(ev1.getX() - (ev2.getX() + (distanceX * -1)))) % tileSize) + distanceX <= 0){
                    moveTileX = 1;
                    Log.d("PPP", "abs2=" + Math.abs(ev1.getX() - (ev2.getX() + (distanceX * -1))) + " % " + tileSize + " + " + distanceX + " <= 0");
                    Log.d("QQQ","MOVE TILE X=" + moveTileX + " pixels=" + ((Math.abs(ev1.getX() - (ev2.getX() + (distanceX * -1)))) % tileSize) + distanceX);
                }
            }

            Log.d("QQQ","ev1Y=" + ev1.getY() + " ev2Y=" + ev2.getY() + " distance=" + distanceY);
            if (distanceY > 0) {
                if (((Math.abs(ev1.getY() - (ev2.getY() - distanceY))) % tileSize) + distanceY >= tileSize) {
                    moveTileY = -1;
                    Log.d("PPP", "abs3=" +Math.abs(ev1.getY() - (ev2.getY() - distanceY)) + " % " + tileSize + " + " + distanceY + " >= " + tileSize );
                    Log.d("QQQ","MOVE TILE Y=" + moveTileY + " pixels=" + ((Math.abs(ev1.getY() - (ev2.getY() - distanceY))) % tileSize) + distanceY );
                }
            }
            else if (distanceY < 0){
                if (((Math.abs(ev1.getY() - (ev2.getY() + (distanceY * -1)))) % tileSize) + distanceY <= 0){
                    moveTileY = 1;
                    Log.d("PPP", "abs4=" + Math.abs(ev1.getY() - (ev2.getY() + (distanceY * -1))) + " % " + tileSize + " + " + distanceY + " <= 0");
                    Log.d("QQQ","MOVE TILE Y=" + moveTileY + " pixels=" + ((Math.abs(ev1.getY() - (ev2.getY() + (distanceY * -1)))) % tileSize) + distanceY);
                }
            }*/



            tc = viewModel.getTileCoordinates().get(0);

            if (distanceX > 0){
                if (  (tc.getScreenPixelX() + distanceX) >= ((viewModel.getScreenWidth() * 0.5) - ((viewModel.getSquares() * tileSize) * 0.5)) + tileSize ){
                        moveTileX = -1;
                }
            }
            else if (distanceX < 0){
                if (  (tc.getScreenPixelX() + distanceX) <= (viewModel.getScreenWidth() * 0.5) - ((viewModel.getSquares() * tileSize) * 0.5) - tileSize ){
                    moveTileX = 1;
                }
            }
            if (distanceY > 0){
                if (  (tc.getScreenPixelY() + distanceY) >= ((viewModel.getScreenHeight() * 0.5) - ((viewModel.getSquares() * tileSize) * 0.5)) + tileSize ){
                    moveTileY = -1;
                }
            }
            else if (distanceY < 0){
                if (  (tc.getScreenPixelY() + distanceY) <= (viewModel.getScreenHeight() * 0.5) - ((viewModel.getSquares() * tileSize) * 0.5) - tileSize ){
                    moveTileY = 1;
                }
            }

//            Log.d("RRR","screenPixelX=" + tc.getScreenPixelX() + " distanceX=" + distanceX + " moveTileX=" + moveTileX);
//            Log.d("RRR","screenPixelY=" + tc.getScreenPixelY() + " distanceY=" + distanceY + " moveTileY=" + moveTileY);
/*            tilePixelX = new TileNumberPixel((int)tc.getTileX() + moveTileX,
                    (int)(tc.getScreenPixelX() + distanceX + (moveTileX * (tileSize - distanceX))));
            tilePixelY = new TileNumberPixel((int)tc.getTileY() + moveTileY,
                    (int)(tc.getScreenPixelY() + distanceY + (moveTileY * (tileSize - distanceY))));*/

            tilePixelX = new TileNumberPixel((int)tc.getTileX() + moveTileX,
                    (int)(tc.getScreenPixelX() + distanceX + (moveTileX * (tileSize - Math.abs(distanceX)))));
            tilePixelY = new TileNumberPixel((int)tc.getTileY() + moveTileY,
                    (int)(tc.getScreenPixelY() + distanceY + (moveTileY * (tileSize - Math.abs(distanceY)))));

            viewModel.setTileCoordinates(tilePixelX, tilePixelY);
 //           previewMapLayoutView.scrollBy((int)distanceX, (int)distanceY);

            previewMapLayoutView.invalidate();



      /*      if (ev1.getX() >= ev2.getX()) {
                if ((ev2.getX() - ev1.getX()) >= tileSize && (ev2.getX() + distanceX) < tileSize) {
                    moveTileX = 1;
                    Toast.makeText(PreviewMapActivity1.this, "moveTileX=" + moveTileX + " moveTileY=" + moveTileY, Toast.LENGTH_SHORT).show();
                }
            }else{
                if ((ev2.getX() - ev1.getX()) >= (tileSize * -1) && (ev2.getX() + distanceX) < tileSize) {
                    moveTileX = -1;
                    Toast.makeText(PreviewMapActivity1.this, "moveTileX=" + moveTileX + " moveTileY=" + moveTileY, Toast.LENGTH_SHORT).show();
                }
            }*/

/*            if (ev1.getY() >= ev2.getY()) {
                if ((ev2.getY() - ev1.getY()) >= tileSize && (ev2.getY() + distanceY) < tileSize) {
                    moveTileY = 1;
                    Toast.makeText(PreviewMapActivity1.this, "moveTileX=" + moveTileX + " moveTileY=" + moveTileY, Toast.LENGTH_SHORT).show();
                }
            }else{
                if ((ev2.getY() - ev1.getY()) >= tileSize && (ev2.getY() + distanceY) < tileSize) {
                    moveTileY = -1;
                    Toast.makeText(PreviewMapActivity1.this, "moveTileX=" + moveTileX + " moveTileY=" + moveTileY, Toast.LENGTH_SHORT).show();
                }
            }*/

//            scrollX = scrollX + distanceX;
//
//            scrollY = scrollY + distanceY;

//            viewModel.recalibratePointsExtra();    Not required now recalibrtates automatically with setTileCoordinates script
//            Toast.makeText(PreviewMapActivity1.this,"startX=" + ev1.getX() + " StartY=" + ev1.getY() + " EndX=" + ev2.getX() + " EndY=" + ev2.getY(),Toast.LENGTH_LONG).show();
//            Toast.makeText(PreviewMapActivity1.this,"onScroll distanceX=" + distanceX + " distanceY=" + distanceY,Toast.LENGTH_SHORT).show();
            return true;
        }


        public boolean onFling(MotionEvent ev1, MotionEvent ev2, float velocityX, float velocityY){
 //           Toast.makeText(PreviewMapActivity1.this,"startX=" + ev1.getX() + " StartY=" + ev1.getY() + " EndX=" + ev2.getX() + " EndY=" + ev2.getY(),Toast.LENGTH_LONG).show();
//            Toast.makeText(PreviewMapActivity1.this,"onFling",Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
//            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast("address=" + mAddressOutput);
                 showToast(getString(R.string.address_found));
            }

        }

        public void showToast(String msg){
            Toast.makeText(PreviewMapActivity1.this,msg,Toast.LENGTH_SHORT).show();
        }
    }
 }