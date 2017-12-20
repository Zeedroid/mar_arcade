package com.zeedroid.maparcade;

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

import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.database.AppDatabase;
import com.zeedroid.maparcade.entity.*;
import com.zeedroid.maparcade.entity.Point;
import com.zeedroid.maparcade.entity.PointExtra;
import com.zeedroid.maparcade.service.FetchAddressIntentService;
import com.zeedroid.maparcade.service.GPS_Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Steve Dixon on 17/10/2017.
 */

public class ModifyMapActivity extends AppCompatActivity   implements TextToSpeech.OnInitListener{
    //    public class PreviewMapActivity1 extends AppCompatActivity {
//    private TextureView mapTexture;
    private BroadcastReceiver locationBroadcastReceiver, sensorBroadcastReceiver;
    private ModifyMapLayout modifyMapLayoutView;

    private ModifyMapModel viewModel;
    private Button deleteButton, undoButton, joinButton, saveButton;
//    private TextView mapStats, mapStatsExtend;

    private double currentLongitude, currentLatitude, previousLongitude, previousLatitude;
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
//    private boolean isOnClick = false;

    private SharedPreferences prefs;

    private float azimuthInDegrees;

    private boolean creatingMap = false;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    protected Location mLastLocation;
    private ModifyMapActivity.AddressResultReceiver mResultReceiver;
    private String mAddressOutput;

    private double scrollX = 0;
    private double scrollY = 0;

    private float lastXPosition = 0;
    private float lastYPosition = 0;

    private TextToSpeech tts;

    private boolean isOnLongClick = false;
    private boolean isMovingPoint = false;
    private int undoCount = 0;

    private boolean hasRunOnce = false;

    private List<com.zeedroid.maparcade.entity.Point> pointLiveData;
    private List<com.zeedroid.maparcade.entity.PointExtra> pointExtraLiveData;
    private List<com.zeedroid.maparcade.entity.MapAddress> mapAddressLiveData;

    private Observer<List<Point>> pointObserver;
    private Observer<List<PointExtra>> pointExtraObserver;
    private Observer<List<MapAddress>> mapAddressObserver;

    @Override
    protected void onResume() {
        super.onResume();

/*        if (locationBroadcastReceiver == null) {
            locationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle gpsBundle = intent.getExtras();

                    previousLatitude = currentLatitude;
                    previousLongitude = currentLongitude;
                    currentLatitude = gpsBundle.getDouble("latitude");
                    currentLongitude = gpsBundle.getDouble("longitude");

                    viewModel.addPointToJourney(currentLongitude, currentLatitude);
//                    setupMapStats();
                    modifyMapLayoutView.invalidate();
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
*/
//        if (myJourney != null && myJourney.size() > 0) setupMapStats();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        tts.shutdown();
/*        if (locationBroadcastReceiver != null) {
            unregisterReceiver(locationBroadcastReceiver);
        }
        if (sensorBroadcastReceiver != null) {
            unregisterReceiver(sensorBroadcastReceiver);
        }
*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_map);
        modifyMapLayoutView = findViewById(R.id.mapLayout);

        deleteButton   = findViewById(R.id.deleteButton);
        undoButton     = findViewById(R.id.undoButton);
        joinButton     = findViewById(R.id.joinButton);
        saveButton     = findViewById(R.id.saveButton);

//        mapStats             = findViewById(R.id.mapStats);
//        mapStatsExtend       = findViewById(R.id.mapStatsExtend);
//        mapStatsExtend.setVisibility(View.GONE);


        ArrayBundle ab = getIntent().getParcelableExtra("points"); // (ArrayBundle)
//        myJourney            = ab.getPoints();
//        myPoints             = ab.getPointsExtra();
        mapType        = ab.getMapType();
        zoomLevel      = ab.getZoomLevel();

        prefs                = PreferenceManager.getDefaultSharedPreferences(this);
        tileSize             = Integer.parseInt(prefs.getString("map_tile_size","400"));

        viewModel = ViewModelProviders.of(this).get(ModifyMapModel.class);

        viewModel.setMapType(mapType);
        viewModel.setZoomLevel(zoomLevel);
//        viewModel.setCreatingMap(true);
        getScreenSize();
        viewModel.createCompassCircles();
        viewModel.setSquares();
        if (viewModel.isUndoable()) undoButton.setVisibility(Button.VISIBLE);


        pointObserver = new Observer<List<Point>>() {
            @Override
            public void onChanged(@Nullable final List<Point> newPoint){
                viewModel.setupJourney(newPoint);
                runOnce();
                viewModel.checkPointsChanged();
                modifyMapLayoutView.invalidate();
            }
        };
        viewModel.getPointLiveData().observe(this, pointObserver);

        pointExtraObserver = new Observer<List<com.zeedroid.maparcade.entity.PointExtra>>() {
            @Override
            public void onChanged(@Nullable final List<com.zeedroid.maparcade.entity.PointExtra> newPointExtra){
                viewModel.setupFeatures(newPointExtra);
                runOnce();
                modifyMapLayoutView.invalidate();

            }
        };
        viewModel.getPointExtraLiveData().observe(this, pointExtraObserver);

        mapAddressObserver = new Observer<List<com.zeedroid.maparcade.entity.MapAddress>>() {
            @Override
            public void onChanged(@Nullable final List<com.zeedroid.maparcade.entity.MapAddress> newMapAddress){
                viewModel.setupRoadNames(newMapAddress);
                runOnce();
                modifyMapLayoutView.invalidate();

            }
        };
        viewModel.getMapAddressLiveData().observe(this, mapAddressObserver);

        viewModel.setLiveDataObservers();


/*        Log.d("sjd","Before setMapType");
        viewModel.setMapType(mapType);
        Log.d("sjd", "Before setZoomLevel");
        viewModel.setZoomLevel(zoomLevel);
//        viewModel.setCreatingMap(true);
        Log.d("sjd", "Before getScreenSize");
        getScreenSize();
        Log.d("sjd", "Before createCompassCircles");
        viewModel.createCompassCircles();
        Log.d("sjd", "Before setSquares");
        viewModel.setSquares();
        Log.d("sjd", "Before setTileCoordinates");
        viewModel.setTileCoordinates(viewModel.getStartX(),viewModel.getStartY());   //  java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Object com.zeedroid.maparcade.PointArray.get(int)' on a null object reference
        Log.d("sjd", "Before setMapTileBoundingBox");
        viewModel.setMapTileBoundingBox();                                           // java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Object com.zeedroid.maparcade.PointArray.get(int)' on a null object reference
*/
        modifyMapLayoutView.setViewModel(viewModel);
        mResultReceiver = new ModifyMapActivity.AddressResultReceiver(new Handler());

        tts = new TextToSpeech(this, this);

        Geocoder aaa = new Geocoder(modifyMapLayoutView.getContext());
        List<Address> addresses;
        try {
            addresses = aaa.getFromLocation(50.876508, 0.019885, 1);
            Toast.makeText(modifyMapLayoutView.getContext(),addresses.get(0).getAddressLine(0),Toast.LENGTH_SHORT).show();
            Toast.makeText(modifyMapLayoutView.getContext(),addresses.get(0).getAdminArea(),Toast.LENGTH_SHORT).show();
            Toast.makeText(modifyMapLayoutView.getContext(),addresses.get(0).getPostalCode(),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(modifyMapLayoutView.getContext(),"No Address",Toast.LENGTH_SHORT).show();
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (undoCount != 0) viewModel.undoModification(undoCount);
                viewModel.checkPointsChanged();
                joinButton.setVisibility(Button.INVISIBLE);
                viewModel.setCanJoin(false);
                if (viewModel.isUndoable()) undoButton.setVisibility(Button.VISIBLE);
                modifyMapLayoutView.invalidate();
            }
        });


        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.undoPoint();
//                if (undoCount != 0) viewModel.undoModification(undoCount);
                viewModel.checkPointsChanged();
                if (!viewModel.isUndoable()) undoButton.setVisibility(Button.INVISIBLE);
                modifyMapLayoutView.invalidate();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deletePoints();                                                       // STILL NEEDS SOME WORK
                deleteButton.setVisibility(Button.INVISIBLE);
                viewModel.setCanDelete(false);
                viewModel.checkPointsChanged();
                undoButton.setVisibility(Button.VISIBLE);
                modifyMapLayoutView.invalidate();
//                if (undoCount != 0) viewModel.undoModification(undoCount);
//                viewModel.checkPointsChanged();
//                if (viewModel.isUndoable()) undoButton.setVisibility(Button.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
                viewModel.setCreatingMap(false);

                try {
                    viewModel.addFeatureToJourney(currentLongitude, currentLatitude, getString(R.string.end_point), PointColorTable.endPoint, PointShape.circle);
                }catch (EmptyFeatureException e){
                    Log.d("sjd", "Empty Feature");
                }
                viewModel.recalibratePointsExtra();
                modifyMapLayoutView.invalidate();
                if (!Geocoder.isPresent()) {
                    Toast.makeText(ModifyMapActivity.this,
                            R.string.no_geocoder_available,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Start service and update UI to reflect new location
                startIntentService(); */

                int pointsCalibrated = viewModel.recalibratePoints();

                Toast.makeText(modifyMapLayoutView.getContext(),"points calibrated = " + pointsCalibrated,Toast.LENGTH_LONG).show();
//                int pointErrors      = viewModel.errorGpsPoints();
//                Toast.makeText(modifyMapLayoutView.getContext(),"gps errors = " + pointErrors,Toast.LENGTH_LONG).show();

                viewModel.setCanDelete(true);
                deleteButton.setVisibility(Button.VISIBLE);

                viewModel.assignRoadNames();
//                viewModel.setCanShowRoads(true);
                saveButton.setVisibility(View.INVISIBLE);

//              viewModel.getRoadNames();
            }
        });

        gd  = new GestureDetector(this,new ModifyMapActivity.Gesture());
        sgd = new ScaleGestureDetector(this, new ModifyMapActivity.ScaleListener());


        gl = new PreviewMapLayout.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (sgd.onTouchEvent(event)) {
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mIsScrolling ) {
                        mIsScrolling  = false;
//                        handleScrollFinished();
                    };
                }

                return false;
            }
        };



    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setVoice((Voice) tts.getVoices().toArray()[0]);
            tts.setPitch(1);
//            tts.setSpeechRate(0.7f);
            int language = tts.setLanguage(Locale.getDefault());

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
        viewModel.recalibrateNodePoints();
        viewModel.recalibratePointsExtra();

        gl = new PreviewMapLayout.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (sgd.onTouchEvent(event)) {
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mIsScrolling ) {
                        mIsScrolling  = false;
//                        handleScrollFinished();
                        Toast.makeText(ModifyMapActivity.this, "ACTION_UP",Toast.LENGTH_SHORT).show();
                    };
                }

                return false;
            }
        };



//        previewMapLayoutView.setCreatingMap(creatingMap);
        modifyMapLayoutView.invalidate();
    }

    private void runOnce(){
/*        Log.d("sjd","Before setMapType");
        viewModel.setMapType(mapType);
        Log.d("sjd", "Before setZoomLevel");
        viewModel.setZoomLevel(zoomLevel);
//        viewModel.setCreatingMap(true);
        Log.d("sjd", "Before getScreenSize");
        getScreenSize();
        Log.d("sjd", "Before createCompassCircles");
        viewModel.createCompassCircles();
        Log.d("sjd", "Before setSquares");
        viewModel.setSquares();*/
        if (viewModel.foundJourneyAndFeatures()) {
            if (!hasRunOnce) {
                viewModel.setTileCoordinates(viewModel.getStartX(), viewModel.getStartY());   //  java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Object com.zeedroid.maparcade.PointArray.get(int)' on a null object reference
                viewModel.setMapTileBoundingBox();                                           // java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Object com.zeedroid.maparcade.PointArray.get(int)' on a null object reference
                hasRunOnce = true;
            }
        }
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

/*    public void toggleStats(View view){
        if (mapStatsExtend.isShown()) {
            Utilities.slideUp(this, mapStatsExtend);
            mapStatsExtend.setVisibility(View.GONE);
        }else {
            mapStatsExtend.setVisibility(View.VISIBLE);
            Utilities.slideDown(this, mapStatsExtend);
        }
    }*/

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

                    Toast.makeText(ModifyMapActivity.this, result.get(0), Toast.LENGTH_SHORT).show();

                    try {
                        viewModel.addFeatureToJourney(currentLongitude, currentLatitude, result.get(0));
                        finishActivity(REQ_CODE_SPEECH_INPUT);
                    } catch (EmptyFeatureException e) {
                        Toast.makeText(ModifyMapActivity.this, getString(R.string.say_feature), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Toast.makeText(ModifyMapActivity.this, getString(R.string.voice_exception), Toast.LENGTH_SHORT).show();
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

/*    private void setupMapStats(){
        mapStats.setText(getString(R.string.zoom_scale, zoomLevel,(int)(MercatorProjection.mapScaleMetersPerPixel(zoomLevel, currentLatitude) * tileSize )));
        mapStatsExtend.setText(getString(R.string.distance_traveled, ((double)((int)(MercatorProjection.distanceOfJourneyKm(viewModel.getPoints()) * 1000)) / 1000)));
    }*/

    // A method to speak something
    @SuppressWarnings("deprecation") // Support older API levels too.
    public void speak(String text, Boolean override) {
        if (tts == null) Log.d("sjd", "TTS IS NULL");
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

    public void setIsOnLongClick(boolean isOnLongClick){
        this.isOnLongClick = isOnLongClick;
    }

    public boolean isOnLongClick(){
        return isOnLongClick;
    }

    public void setIsMovingPoint(boolean isMovingPoint){
        this.isMovingPoint = isMovingPoint;
    }

    public boolean onTouchEvent(MotionEvent ev){
        if (gd.onTouchEvent(ev)){
            Log.d("sjd", "ModifyMapActivity onTouchEvent GGGDDD");
        }else {
            switch (ev.getAction()){
                case MotionEvent.ACTION_MOVE :
                    if (isOnLongClick()) {
                        setIsMovingPoint(true);
                        movePoint(ev, true);
                        viewModel.setCanDelete(false);
                        deleteButton.setVisibility(Button.INVISIBLE);
                        modifyMapLayoutView.invalidate();
                    }

                    break;
                case MotionEvent.ACTION_UP :
                    if (isOnLongClick()){
                        if (isMovingPoint) {
                            movePoint(ev, false);
                            undoButton.setVisibility(Button.VISIBLE);
                            modifyMapLayoutView.invalidate();
                            setIsMovingPoint(false);
                        }
//                        modifyMapLayoutView.invalidate();
                        setIsOnLongClick(false);
                    }

                    break;
                default:
                    Log.d("sjd", "aaa);");
            }
        }
        sgd.onTouchEvent(ev);

        return true;
    }

    public void movePoint(MotionEvent ev, boolean moving){
        PointArray aaa = viewModel.getJourney();

        com.zeedroid.maparcade.Point point = viewModel.getPoint(lastXPosition, lastYPosition);

        for (int i=0; i < ev.getPointerCount(); i++) {
            if (point.getCanvasX() != 0.0d) {


                if (viewModel.isTheStartPoint(lastXPosition, lastYPosition)){
                    Log.d("sjd", "start point");
                    viewModel.movePointExtra(viewModel.getStartEndPoint("Start"), ev.getX(i) - lastXPosition, ev.getY(i) - lastYPosition);
                }else if (viewModel.isTheEndPoint(lastXPosition, lastYPosition)){
                    Log.d("sjd","end point");
                    viewModel.movePointExtra(viewModel.getStartEndPoint("End"), ev.getX(i) - lastXPosition, ev.getY(i) - lastYPosition);
                }

                viewModel.movePoint(point, ev.getX(i) - lastXPosition, ev.getY(i) - lastYPosition);

                if (!moving) {
                    viewModel.updatePoint();
                    viewModel.updatePointExtra();
                    viewModel.checkPointsChanged();
                    if (!viewModel.isUndoable()) undoButton.setVisibility(Button.INVISIBLE);
                    if (isMovingPoint) {
                        point.setSelected(false);
                    }
                }
                lastXPosition = ev.getX(i);
                lastYPosition = ev.getY(i);
                modifyMapLayoutView.invalidate();
            }
        }
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
            viewModel.recalibrateNodePoints();
            viewModel.recalibratePointsExtra();

//            setupMapStats();

            modifyMapLayoutView.invalidate();
            return true;
        }
    }


    private class Gesture extends GestureDetector.SimpleOnGestureListener {

        public boolean onSingleTapUp(MotionEvent ev){
            com.zeedroid.maparcade.PointExtra feature = viewModel.getFeature(ev.getX(), ev.getY());

            if (feature.getPoint() != null){
                Toast.makeText(ModifyMapActivity.this, getString(R.string.feature) + feature.getPointType(), Toast.LENGTH_SHORT).show();
                speak(feature.getPointType(), true);
            }else {
                Toast.makeText(ModifyMapActivity.this, getString(R.string.no_feature), Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        public void onLongPress(MotionEvent ev){
            com.zeedroid.maparcade.Point point = viewModel.getPoint(ev.getX(0), ev.getY(0));
            if (point.getCanvasX() != 0.0d){
                setIsOnLongClick(true);
                viewModel.setLastLatitude(point.getLatitude());
                viewModel.setLastLongitude(point.getLongitude());
                lastXPosition = ev.getX(0);
                lastYPosition = ev.getY(0);
                point.toggleSelected();
                viewModel.setCanDelete(true);
                deleteButton.setVisibility(Button.VISIBLE);
                modifyMapLayoutView.invalidate();
            }else {
                Toast.makeText(ModifyMapActivity.this, getString(R.string.no_feature), Toast.LENGTH_SHORT).show();
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

            tilePixelX = new TileNumberPixel((int)tc.getTileX() + moveTileX,
                    (int)(tc.getScreenPixelX() + distanceX + (moveTileX * (tileSize - Math.abs(distanceX)))));
            tilePixelY = new TileNumberPixel((int)tc.getTileY() + moveTileY,
                    (int)(tc.getScreenPixelY() + distanceY + (moveTileY * (tileSize - Math.abs(distanceY)))));

            viewModel.setTileCoordinates(tilePixelX, tilePixelY);

            modifyMapLayoutView.invalidate();

            return true;
        }


        public boolean onFling(MotionEvent ev1, MotionEvent ev2, float velocityX, float velocityY){

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
            Toast.makeText(ModifyMapActivity.this,msg,Toast.LENGTH_SHORT).show();
        }
    }
}
