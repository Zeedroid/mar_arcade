package com.zeedroid.maparcade;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.zeedroid.maparcade.dao.MapRouteDAO;
import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.dao.PointExtraDAO;
import com.zeedroid.maparcade.database.AppDatabase;
import com.zeedroid.maparcade.entity.Point;
import com.zeedroid.maparcade.entity.PointExtra;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.util.Log.d;


/**
 * Created by Steve Dixon on 17/08/2017.
 */

public class PreviewMapModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    private final LiveData<List<Point>> pointLiveData;
    private final LiveData<List<PointExtra>> pointExtraLiveData;

    private PointArray myJourney;
    private PointExtraArray myPoints;
    private ArrayList features = new ArrayList<PointFeature>();
    private String mapType;
    private int zoomLevel;
    private int screenWidth, screenHeight;
    private int actionBarHeight, statusBarHeight;
    private int tileSize;
    private int hypotenuse, squares;
    private boolean creatingMap;
    private SharedPreferences prefs;
    private MapTileBoundingBox mapTileBoundingBox;
    private Geocoder gc;
    private ArrayList<TileCoordinates> tileCoordinates = new ArrayList<TileCoordinates>();
    private List<Address> addressList;
    private static Handler mHandler;

    private long routeID;

    private Path compass;

//    private Application application;

    public PreviewMapModel(Application application) {
        super(application);
//        this.application = application;
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());

        pointLiveData = appDatabase.getPointDAO().searchAll();
        pointExtraLiveData = appDatabase.getPointExtraDAO().searchAll();

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplication());
        tileSize = Integer.parseInt(prefs.getString("map_tile_size", "512"));
//        hypotenuse           = (int)Math.sqrt(Math.pow(screenHeight, 2) + Math.pow(screenWidth,2));
//        squares              = (hypotenuse / tileSize) + 2;
        gc = new Geocoder(application);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        appDatabase.close();
    }

    public LiveData<List<Point>> getPointLiveData() {
        return pointLiveData;
    }

    public LiveData<List<PointExtra>> getPointExtraLiveData() {
        return pointExtraLiveData;
    }

    public void setPoints(PointArray myJourney) {
        this.myJourney = myJourney;
    }

    public PointArray getPoints() {
        return myJourney;
    }

    public void setPointsExtra(PointExtraArray myPoints) {
        this.myPoints = myPoints;
    }

    public PointExtraArray getPointsExtra() {
        return myPoints;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getMapType() {
        return mapType;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setSquares() {
        hypotenuse = (int) Math.sqrt(Math.pow(screenHeight, 2) + Math.pow(screenWidth, 2));
        squares = (hypotenuse / tileSize) + 4;
        if (squares % 2 != 0) squares++;
    }

    public int getSquares() {
        return squares;
    }

    public void setCreatingMap(boolean cr){
        creatingMap =cr;
    }

    public boolean isMapBeingCreated(){
        return creatingMap;
    }

/*        previewMapLayoutView.createCompassCircles();
        previewMapLayoutView.setNumberOfSquares(hypotenuse, squares);
        previewMapLayoutView.setMapTileBoundingBox(mapTileBoundingBox);*/

    public Geocoder getGeocoder(){
        return gc;
    }


    /**
     * Map projection is based on a grid of tiles. This method Calculates the top left MercatorProjection X Tile coordinate
     * and the screen pixel starting point for the top left hand corner of the tile.
     *
     * @return A TileNumberPixel object containing the Mercator projection tile X coordinate, and the x screen pixel starting position for the tile.
     */
    @NonNull
    public TileNumberPixel getStartX(){
        return MercatorProjection.getStartX(myJourney, zoomLevel, tileSize, screenWidth, squares);
    }

    /**
     * Map projection is based on a grid of tiles. This method Calculates the top left MercatorProjection Y Tile coordinate
     * and the screen pixel starting point for the top left hand corner of the tile.
     *
     * @return A TileNumberPixel object containing the Mercator projection tile y coordinate, and the y screen pixel starting position for the tile.
     */
    @NonNull
    public TileNumberPixel getStartY(){
        return MercatorProjection.getStartY(myJourney,  zoomLevel, tileSize, screenHeight, actionBarHeight, squares);
    }

    public void addPointToJourney(double longitude, double latitude, float timepassed) {
        try {
            myJourney.add(new com.zeedroid.maparcade.Point(longitude, latitude, timepassed));
            Log.d("xxx", "PreviewMapModel timepassed = " + timepassed);
//            if (myJourney != null && myJourney.size() > 0) setupMapStats();
        } catch (Exception e) {
            d("sjd", "Error: Adding Point" + e.getMessage());
        }
        setMapTileBoundingBox();
    }

//    public com.zeedroid.maparcade.PointExtra addFeatureToJourney(double longitude, double latitude, String pointText) throws EmptyFeatureException {
    public void addFeatureToJourney(double longitude, double latitude, String pointText) throws EmptyFeatureException {
        String pos = "left";
        String feature = pointText;
        String pointColor;
        int defaultShape = PointShape.circle;


        if (pointText.matches(".*\\bleft\\b.*")) {
            pos = "left";
//            feature = pointText.replaceAll("\\s*\\bleft\\b\\s*", "");
        }
        if (pointText.matches(".*\\bright\\b.*")) {
            pos = "right";
//            feature = pointText.replaceAll("\\s*\\bright\\b\\s*", "");
        }
        if (feature.length() == 0) throw new EmptyFeatureException("No Feature Found");

        pointColor = getPointColor(feature);

//     change PointExtra to Point
        com.zeedroid.maparcade.Point point = new com.zeedroid.maparcade.Point(longitude, latitude, 0.0f);
        com.zeedroid.maparcade.PointExtra pointExtra = new com.zeedroid.maparcade.PointExtra(point, feature, pos, pointColor, defaultShape);
        myPoints.add(pointExtra);

        setMapTileBoundingBox();
//        return pointExtra;
    }

    public void addFeatureToJourney(double longitude, double latitude, String pointText, String pointColor, int pointShape) throws EmptyFeatureException {
        String pos = "left";
        String feature = pointText;

        if (pointText.matches(".*\\bleft\\b.*")) {
            pos = "left";
            feature = pointText.replaceAll("\\s*\\bleft\\b\\s*", "");
        }
        if (pointText.matches(".*\\bright\\b.*")) {
            pos = "right";
            feature = pointText.replaceAll("\\s*\\bright\\b\\s*", "");
        }
        if (feature.length() == 0) throw new EmptyFeatureException("No Feature Found");

//     change PointExtra to Point
        com.zeedroid.maparcade.Point point = new com.zeedroid.maparcade.Point(longitude, latitude, 0.0f);
        com.zeedroid.maparcade.PointExtra pointExtra = new com.zeedroid.maparcade.PointExtra(point, feature, pos, pointColor, pointShape);
        myPoints.add(pointExtra);

        setMapTileBoundingBox();
//        return pointExtra;
    }

    private String getPointColor(String feature) {
        String color = "";
        boolean featureFound = false;
        for (int i = 0; i < features.size(); i++) {
            PointFeature pointFeature = (PointFeature) features.get(i);

            if (pointFeature.getName().equals(feature)) {
                featureFound = true;
                color = pointFeature.getColor();
                break;
            }
        }

        if (!featureFound) {
            color = PointColorTable.getAColor(features.size());
            features.add(new PointFeature(feature, color));
            return color;
        }
        return color;
    }

    public void setScreenSizeInfo(ScreenSizeInfo screenInfo){
        this.screenWidth     = screenInfo.screenWidth;
        this.screenHeight    = screenInfo.screenHeight;
        this.actionBarHeight = screenInfo.actionBarHeight;
        this.statusBarHeight = screenInfo.statusBarHeight;
    }

    public void createCompassCircles(){
//        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());
//        int navigateBarHeight     = Utilities.getNavigationBarHeight((Activity)getContext());
        int width;
        if (screenWidth < screenHeight){
            width = screenWidth;
        }else {
            width = screenHeight;
        }
        compass = new Path();
        compass.addCircle(screenWidth / 2, ((screenHeight + actionBarHeight) / 2) + actionBarHeight, (width / 2) - 30, Path.Direction.CW);
        compass.addCircle(screenWidth / 2, ((screenHeight + actionBarHeight) / 2) + actionBarHeight, (width / 2) - 130, Path.Direction.CW);

        //        (screenHeight / 2) + statusActionBarHeight
        Log.d("sjd5","screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " radius=" + ((width / 2) - 30));
    }

    public Path getCompass(){
        return compass;
    }

    public void setMapTileBoundingBox(){
        mapTileBoundingBox = MercatorProjection.mapTileBoundingBox(myJourney, myPoints, zoomLevel, tileSize);
    }

    public MapTileBoundingBox getMapTileBoundingBox(){
        return mapTileBoundingBox;
    }

    public void recalibratePointsExtra() {
        com.zeedroid.maparcade.Point point;
        TileCoordinates tc;
        double tileStartX, tileStartY;
        double pixelX, pixelY;

        for (int j = 0; j < myPoints.size(); j++) {
            point = myPoints.get(j).getPoint();
            tc = getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
            if (tc.getTileX() != 0 && tc.getTileY() != 0) {
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);

                myPoints.get(j).setCanvasX(pixelX);
                myPoints.get(j).setCanvasY(pixelY);
                myPoints.get(j).setWidthExtra(45);
            }
        }
    }

    public TileCoordinates getTileCoordinate(double tileX, double tileY) {
        for (int i = 0; i < tileCoordinates.size(); i++) {
            if (tileCoordinates.get(i).getTileX() == tileX && tileCoordinates.get(i).getTileY() == tileY) {
                return tileCoordinates.get(i);
            }
        }
        return new TileCoordinates();
    }

    public void setTileCoordinates(TileNumberPixel startTilePixelX, TileNumberPixel startTilePixelY) {
//        TileNumberPixel startTilePixelX = getStartX();
//        TileNumberPixel startTilePixelY = getStartY();
        int drawLineX = startTilePixelX.getTilePixel();
        int drawLineY = startTilePixelY.getTilePixel();
        int tileStartX = startTilePixelX.getTileNumber();
        int tileStartY = startTilePixelY.getTileNumber();

        tileCoordinates.clear();

        for (int x = 0; x < squares; x++) {
            for (int y = 0; y < squares; y++) {
                tileCoordinates.add(new TileCoordinates(tileStartX + x, tileStartY + y, drawLineX + (tileSize * x), drawLineY + (tileSize * y)));
            }
        }
    }

    public ArrayList<TileCoordinates> getTileCoordinates(){
        return tileCoordinates;
    }

    public PointXY getTilePointXY(com.zeedroid.maparcade.Point point) {
        TileCoordinates tc = getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
        double tileStartX = tc.getScreenPixelX();
        double tileStartY = tc.getScreenPixelY();

        double pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
        double pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);

        return new PointXY(pixelX, pixelY);
    }

    public com.zeedroid.maparcade.PointExtra getFeature(float x, float y){
        double xCord = Math.round(x);
//        double yCord = Math.round(y - statusBarHeight);
        double yCord = Math.round(y - (actionBarHeight + statusBarHeight));
        Log.d("sjd5", "actionBarHeight=" + actionBarHeight + " statusBarHeight=" + statusBarHeight);
        for (int i=0; i< myPoints.size(); i++){
            Log.d("sjd5", "canvasX=" + myPoints.get(i).getCanvasX() + " canvasY=" + myPoints.get(i).getCanvasY() + " width=" +
                    myPoints.get(i).getWidthExtra() + " xCord=" + xCord + " yCord=" + yCord + " type=" + myPoints.get(i).getPointType());
            if ((xCord >= (myPoints.get(i).getCanvasX() - myPoints.get(i).getWidthExtra()) &&
                    xCord <= (myPoints.get(i).getCanvasX() + myPoints.get(i).getWidthExtra()))  &&
                    (yCord >= (myPoints.get(i).getCanvasY() - myPoints.get(i).getWidthExtra()) &&
                     yCord <= (myPoints.get(i).getCanvasY() + myPoints.get(i).getWidthExtra()))) {
                return myPoints.get(i);
            }
        }
        return new com.zeedroid.maparcade.PointExtra();
    }

    public void preserveJourney(){
       InsertMapRouteRecord    imrr = new InsertMapRouteRecord();
       InsertPointRecords      ipr  = new InsertPointRecords();
       InsertPointExtraRecords iper = new InsertPointExtraRecords();
       SelectPointCount        spc  = new SelectPointCount();

       imrr.execute();
       ipr.execute();
       iper.execute();
       spc.execute();
    }

    private class SelectPointCount extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            try {
                PointDAO pDAO = appDatabase.getPointDAO();
                Log.d("sjdmod", "After Point Count: Count=" + pDAO.searchCount());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("sjdmod", "" + e.getMessage());
            }
            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class InsertPointRecords extends AsyncTask<Void, Void, String>{

            @Override
            protected String doInBackground (Void...voids){
                String aaa = "OK";
                for (int i = 0; i < myJourney.size(); i++) {
                    try {
                        PointDAO pDAO = appDatabase.getPointDAO();
                        com.zeedroid.maparcade.entity.Point p =
                                new com.zeedroid.maparcade.entity.Point(
                                        1, routeID, i, Double.valueOf(myJourney.get(i).getLongitude()).toString(), Double.valueOf(myJourney.get(i).getLatitude()).toString(), Float.valueOf(myJourney.get(i).getTimepassed()));

                        pDAO.insert(p);
                        Log.d("sjd", "After Point created");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("sjd", "" + e.getMessage());
                    }
                }
                return aaa;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
     }

    private class InsertPointExtraRecords extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            for (int i = 0; i < myPoints.size(); i++) {
                try {
                    PointExtraDAO peDAO = appDatabase.getPointExtraDAO();
                    com.zeedroid.maparcade.entity.PointExtra p =
                            new com.zeedroid.maparcade.entity.PointExtra(
                                    myPoints.get(i).getPoint(), routeID, myPoints.get(i).getPointType(), myPoints.get(i).getPosition(), myPoints.get(i).getPointColor(), myPoints.get(i).getPointShape());

                    peDAO.insert(p);
                    Log.d("sjd", "After PointExtra created");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("sjd", "" + e.getMessage());
                }
            }
            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class InsertMapRouteRecord extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            for (int i = 0; i < myPoints.size(); i++) {
                try {
                    MapRouteDAO mrDAO = appDatabase.getMapRouteDAO();
                    com.zeedroid.maparcade.entity.MapRoute p =
                            new com.zeedroid.maparcade.entity.MapRoute(
                                    "myroute",1,"very easy route",1,3.9f,new Date(), Constants.CREATE_MAP);


                    routeID = mrDAO.insert(p);
                    Log.d("sjd", "After MapRoute created, routeID = " + routeID);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("sjd", "" + e.getMessage());
                }
            }
            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class UpdateMapRouteRecord extends AsyncTask<Void, Void, String>{
        private int status;

        private UpdateMapRouteRecord(int status){
            this.status = status;
        }

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            for (int i = 0; i < myPoints.size(); i++) {
                try {
                    MapRouteDAO mrDAO = appDatabase.getMapRouteDAO();
                    com.zeedroid.maparcade.entity.MapRoute p =
                            new com.zeedroid.maparcade.entity.MapRoute(
                                    "myroute",1,"very easy route",1,3.9f,new Date(), Constants.CREATE_MAP);


                    mrDAO.insert(p);
                    Log.d("sjd", "After MapRoute created");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("sjd", "" + e.getMessage());
                }
            }
            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

/*    public String getPathName(double latitude, double longitude) {
        List<Address> addresses;
        Address address;
        String pathName = "";
        try {
            addresses = gc.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0);
            String aaa = "";
            for (int i = 0; i< address.getMaxAddressLineIndex(); i++){
                aaa = aaa.concat(": " + address.getAddressLine(i));
            }
            pathName = addresses.get(0).getAddressLine(0).replace("^\\s*[0-9]+\\s+", "");
            Log.d("aaa", "Address:" + aaa);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("sjd", e.getMessage());
        }

        return pathName;
    }*/

    public void getRoadNames() {
//        Toast.makeText(application,"AAAAAAAAAAAA",Toast.LENGTH_SHORT).show();
        if (Geocoder.isPresent()) {

            if (addressList != null) addressList.clear();
            Runnable myRunnable = new Runnable(){

               public void run() {
                    Looper.prepare();
//                    Toast.makeText(application, "BBBBBBBBBBBBBBB",Toast.LENGTH_SHORT).show();
                    mHandler = new Handler() {
                        public void handleMessage(Message msg) {
 //                           Toast.makeText(application, "CCCCCCCCCCCCCCCCC",Toast.LENGTH_SHORT).show();
                            Log.d("ROAD", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                            if (myJourney != null && myJourney.size() > 0) {
                                Log.d("ROAD", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
                                for (int i = 0; i < myJourney.size(); i++) {
                                    Log.d("ROAD", "CCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
                                    try {
                                        Log.d("ROAD", "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
                                        addressList.add((gc.getFromLocation(myJourney.get(i).getLatitude(), myJourney.get(i).getLongitude(), 1)).get(0));
 //                                       Toast.makeText(application, addressList.get(0).getPostalCode(), Toast.LENGTH_SHORT);
                                        Log.d("ROAD", "EEEEEEEEEEEEEEEEEEEEEEEEEE");
                                    } catch (IOException e) {
                                        e.printStackTrace();
 //                                       Toast.makeText(application, "Err:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            super.handleMessage(msg);
                        }

                    };
                    Looper.loop();
                }
            };
            Thread thrd = new Thread(myRunnable);
            thrd.start();
        }
    }

/*    public void getRoadNames() {
        Toast.makeText(application,"AAAAAAAAAAAA",Toast.LENGTH_SHORT).show();
        if (Geocoder.isPresent()) {

            if (addressList != null) addressList.clear();
            Runnable myRunnable = new Runnable(){

                public void run() {
                    Looper.prepare();
                    Toast.makeText(application, "BBBBBBBBBBBBBBB",Toast.LENGTH_SHORT).show();
                    mHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            Toast.makeText(application, "CCCCCCCCCCCCCCCCC",Toast.LENGTH_SHORT).show();
                            Log.d("ROAD", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                            if (myJourney != null && myJourney.size() > 0) {
                                Log.d("ROAD", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
                                for (int i = 0; i < myJourney.size(); i++) {
                                    Log.d("ROAD", "CCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
                                    try {
                                        Log.d("ROAD", "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
                                        addressList.add((gc.getFromLocation(myJourney.get(i).getLatitude(), myJourney.get(i).getLongitude(), 1)).get(0));
                                        Toast.makeText(application, addressList.get(0).getPostalCode(), Toast.LENGTH_SHORT);
                                        Log.d("ROAD", "EEEEEEEEEEEEEEEEEEEEEEEEEE");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(application, "Err:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            super.handleMessage(msg);
                        }

                    };
                    Looper.loop();
                }
            };
            Thread thrd = new Thread(myRunnable);
            thrd.start();
        }
    }   */


/*    private void setRoadNames() {
        boolean wasOffScreen = false;
        com.zeedroid.maparcade.Point pointA, pointB;
        com.zeedroid.maparcade.Point point = getPoints().get(0);
        pointA = point;
        TileCoordinates tc = getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), getZoomLevel(), tileSize),
                MercatorProjection.tileCoordinateY(point.getLatitude(), getZoomLevel(), tileSize));
        double tileStartX = tc.getScreenPixelX();
        double tileStartY = tc.getScreenPixelY();

        double pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), getZoomLevel(), tileSize);
        double pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), getZoomLevel(), tileSize);
        Log.d("ROAD", "tileStartX=" + tileStartX + " tileStartY=" + tileStartY + " pixelX=" + pixelX + " pixelY=" + pixelY);

        String currentRoad = getPathName(point.getLatitude(), point.getLongitude());
        Log.d("ROAD", "currentRoad=" + currentRoad);
        for (int i = 1; i < getPoints().size(); i++) {
            point = getPoints().get(i);
            pointB = pointA;
            pointA = point;

            if (currentRoad != "" && currentRoad != getPathName(point.getLatitude(), point.getLongitude())) {
                Log.d("ROAD", "currentRoad=" + currentRoad + " NewRoad=" + getPathName(point.getLatitude(), point.getLongitude()));
                if (currentRoad != "") {
                    canvas.drawPath(routeText, red_paintbrush_stroke);
                    canvas.drawTextOnPath(currentRoad, routeText, 100, 0, compass_text_fill_stroke);
                }
                routeText = new Path();
                routeText.moveTo((float) pixelX, (float) pixelY);
                currentRoad = getPathName(point.getLatitude(), point.getLongitude());
            }
            tc = getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), getZoomLevel(), tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), getZoomLevel(), tileSize));
            if (tc.getTileX() == 0 && tc.getTileY() == 0) {
                Log.d("ROAD", "Off Screen");
                if (!routeText.isEmpty()) {
                    if (currentRoad != "") {
                        Log.d("ROAD","DRAW ROAD NAME ON PATH");
                        canvas.drawPath(routeText, red_paintbrush_stroke);
                        canvas.drawTextOnPath(currentRoad, routeText, 100, 0, compass_text_fill_stroke);
                    }
                    currentRoad = getPathName(point.getLatitude(), point.getLongitude());
                    Log.d("ROAD", "Create New Path");
                    routeText = new Path();
                    routeText.moveTo((float) pixelX, (float) pixelY);
                }
                wasOffScreen = true;
            } else {
                Log.d("ROAD","On Screen");
                if (wasOffScreen) {
                    wasOffScreen = false;
                    routeText.moveTo((float) pixelX, (float) pixelY);
                    continue;
                }
                currentRoad = getPathName(point.getLatitude(), point.getLongitude());
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), getZoomLevel(), tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), getZoomLevel(), tileSize);
                routeText.lineTo((float) pixelX, (float) pixelY);
                routeText.moveTo((float) pixelX, (float) pixelY);
            }
        }
        if (currentRoad != "") {
            Log.d("ROAD", "Drawing Road on Path");
            canvas.drawPath(routeText, red_paintbrush_stroke);
            canvas.drawTextOnPath(currentRoad, routeText, 100, 0, compass_text_fill_stroke);
        }
    }*/
}
