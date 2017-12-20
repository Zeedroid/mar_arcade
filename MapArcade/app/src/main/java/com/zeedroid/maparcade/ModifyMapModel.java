package com.zeedroid.maparcade;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zeedroid.maparcade.dao.MapAddressDAO;
import com.zeedroid.maparcade.dao.MapRouteDAO;
import com.zeedroid.maparcade.dao.PointBackupDAO;
import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.dao.PointExtraBackupDAO;
import com.zeedroid.maparcade.dao.PointExtraDAO;
import com.zeedroid.maparcade.database.AppDatabase;
import com.zeedroid.maparcade.entity.MapAddress;
import com.zeedroid.maparcade.entity.PointBackup;
import com.zeedroid.maparcade.entity.PointExtraBackup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.util.Log.d;
import static java.lang.Double.valueOf;

/**
 * Created by User on 22/10/2017.
 */

public class ModifyMapModel extends AndroidViewModel {
    private AppDatabase appDatabase;

    private LiveData<List<com.zeedroid.maparcade.entity.Point>> pointLiveData;
    private LiveData<List<com.zeedroid.maparcade.entity.PointExtra>> pointExtraLiveData;
    private LiveData<List<com.zeedroid.maparcade.entity.MapAddress>> mapAddressLiveData;

    private HashMap myRoads;
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

    private double lastLatitude, lastLongitude;
    private double newLongitude, newLatitude;

    private boolean isOnClick = false;

    private boolean canUndo      = false;
    private boolean canDelete    = false;
    private boolean canJoin      = false;
    private boolean canShowRoads = false;

    private int minPointSeperationMeters = 10;

    private Path compass;

//    private Application application;

    public ModifyMapModel(Application application) {
        super(application);

        appDatabase        = AppDatabase.getAppDatabase(application);
        pointLiveData      = new MutableLiveData<>();
        pointExtraLiveData = new MutableLiveData<>();
        mapAddressLiveData = new MutableLiveData<>();

        pointLiveData      = getPointLiveData();
        pointExtraLiveData = getPointExtraLiveData();
        mapAddressLiveData = getMapAddressLiveData();


        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplication());
        tileSize = Integer.parseInt(prefs.getString("map_tile_size", "512"));
//        hypotenuse           = (int)Math.sqrt(Math.pow(screenHeight, 2) + Math.pow(screenWidth,2));
//        squares              = (hypotenuse / tileSize) + 2;
        gc = new Geocoder(application);
        myRoads = new HashMap();

    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        appDatabase.close();
    }



    public LiveData<List<com.zeedroid.maparcade.entity.Point>> getPointLiveData() {
            pointLiveData = appDatabase.getPointDAO().searchAll();
            try {
                Log.d("livedata", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXpointLiveData count records = " + pointLiveData.getValue().size());
            }
            catch (NullPointerException e){
                Log.d("livedata", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX    NullPointerException");
            }
        return pointLiveData;
    }

    /**
     *
     * @return
     */
    public LiveData<List<com.zeedroid.maparcade.entity.PointExtra>> getPointExtraLiveData() {
        pointExtraLiveData = appDatabase.getPointExtraDAO().searchAll();
        return pointExtraLiveData;
    }

    /**
     *
     * @return
     */
    public LiveData<List<com.zeedroid.maparcade.entity.MapAddress>> getMapAddressLiveData() {
        mapAddressLiveData = appDatabase.getMapAddressDAO().searchAll();
        return mapAddressLiveData;
    }

 /*   public void setLiveDataJourney(PointArray myJourney) {
        this.myJourney = myJourney;
    }*/

    public PointArray getJourney() {
        return myJourney;
    }

 /*   public void setLiveDataFeatures(PointExtraArray myPoints) {
        this.myPoints = myPoints;
    }*/

    public PointExtraArray getFeatures() {
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
        creatingMap = cr;
    }

    public boolean isMapBeingCreated(){
        return creatingMap;
    }

/*        previewMapLayoutView.createCompassCircles();
        previewMapLayoutView.setNumberOfSquares(hypotenuse, squares);
        previewMapLayoutView.setMapTileBoundingBox(mapTileBoundingBox);*/

    public boolean foundJourneyAndFeatures(){
        if ((myJourney != null && myJourney.size() > 0) && (myPoints != null && myPoints.size() > 0)) return true;
        else return false;
    }

    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public double getLastLatitude(){
        return lastLatitude;
    }

    public void setLastLongitude(double lastLongitude){
        this.lastLongitude = lastLongitude;
    }

    public double getLastLongitude(){
        return lastLongitude;
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
//            if (myJourney != null && myJourney.size() > 0) setupMapStats();
        } catch (Exception e) {
            d("mod", "Error: Adding Point" + e.getMessage());
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
            feature = pointText.replaceAll("\\s*\\bleft\\b\\s*", "");
        }
        if (pointText.matches(".*\\bright\\b.*")) {
            pos = "right";
            feature = pointText.replaceAll("\\s*\\bright\\b\\s*", "");
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

    public void recalibrateNodePoints() {
        com.zeedroid.maparcade.Point point;
        TileCoordinates tc;
        double tileStartX, tileStartY;
        double pixelX, pixelY;

        for (int j = 0; j < myJourney.size(); j++) {
            point = myJourney.get(j);
            tc = getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
            if (tc.getTileX() != 0 && tc.getTileY() != 0) {
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);

                myJourney.get(j).setCanvasX(pixelX);
                myJourney.get(j).setCanvasY(pixelY);
                myJourney.get(j).setWidthNode(30);
            }
        }
    }

    public void setupJourney(List<com.zeedroid.maparcade.entity.Point> points){
        com.zeedroid.maparcade.entity.Point point;
        myJourney = new PointArray();
        Iterator it = points.iterator();
        while (it.hasNext()){
            point = (com.zeedroid.maparcade.entity.Point)it.next();
            myJourney.add(new Point(Double.parseDouble(point.getLongitude()), Double.parseDouble(point.getLatitude()), point.getPathID(), point.getTimePassed()));
        }
        recalibrateNodePoints();
    }

    public void setupFeatures(List<com.zeedroid.maparcade.entity.PointExtra> features){
        com.zeedroid.maparcade.entity.PointExtra pointExtra;
        myPoints = new PointExtraArray();
        Iterator it = features.iterator();
        while (it.hasNext()){
            pointExtra = (com.zeedroid.maparcade.entity.PointExtra)it.next();
            myPoints.add(new PointExtra(pointExtra.getPoint(), pointExtra.getPointType(), pointExtra.getPosition(), pointExtra.getPointColor(), pointExtra.getPointShape()));
        }
    }

    public void setupRoadNames(List<com.zeedroid.maparcade.entity.MapAddress> roads){
        com.zeedroid.maparcade.entity.MapAddress road;
        myRoads = new HashMap();
        Iterator it = roads.iterator();

        while (it.hasNext()){
            road = (com.zeedroid.maparcade.entity.MapAddress)it.next();
            myRoads.put(road.getRoadID(), road.getRoadName());
        }
    }

    public String getRoadName(int pathID){
        String roadName = "";
        try{
            roadName = (String)myRoads.get(pathID);
        }catch (Exception e){

        }
        return roadName;
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

 /*   public void undoModification(int undoCount){

    }*/

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
//        Log.d("mod", "actionBarHeight=" + actionBarHeight + " statusBarHeight=" + statusBarHeight);
        for (int i=0; i< myPoints.size(); i++){
            if ((xCord >= (myPoints.get(i).getCanvasX() - myPoints.get(i).getWidthExtra()) &&
                    xCord <= (myPoints.get(i).getCanvasX() + myPoints.get(i).getWidthExtra()))  &&
                    (yCord >= (myPoints.get(i).getCanvasY() - myPoints.get(i).getWidthExtra()) &&
                            yCord <= (myPoints.get(i).getCanvasY() + myPoints.get(i).getWidthExtra()))) {
                return myPoints.get(i);
            }
        }
        return new com.zeedroid.maparcade.PointExtra();
    }


    public void setLiveDataObservers(){
        ObservePointRecords       opr = new ObservePointRecords();
        ObservePointExtraRecords oper = new ObservePointExtraRecords();
        ObserveMapAddressRecords  oma = new ObserveMapAddressRecords();

        opr.execute();
        oper.execute();
        oma.execute();
    }

    public void getPointCount(){
        PointCount pc = new PointCount();
        pc.execute();
    }

    private class PointCount extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            try {

                PointDAO pDAO = appDatabase.getPointDAO();
                int bbb = pDAO.searchCount();

            } catch (Exception e) {
                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();
            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Log.d("mod", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXAfter Point Observed: " + pointLiveData.getValue().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }


    public void setCanJoin(boolean canJoin){
        this.canJoin = canJoin;
    }

    public boolean isJoinable(){
        return canJoin;
    }

    public void setCanDelete(boolean canDelete){
        this.canDelete = canDelete;
    }

    public boolean isDeletable(){
        return canDelete;
    }

    public void setCanUndo(boolean canUndo){
        this.canUndo = canUndo;
    }

    public boolean isUndoable(){
        return canUndo;
    }

    public void setCanShowRoads(boolean canShowRoads){
        this.canShowRoads = canShowRoads;
    }

    public boolean isShowingRoads(){
        if (myRoads.isEmpty()) return false;
        else return true;
    }

    public void checkPointsChanged(){
        PointBackupCount pbc = new PointBackupCount();
        pbc.execute();
    }

    private class PointBackupCount extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground (Void...voids){
            int bbb = 0;
            try {

                PointBackupDAO pbDAO = appDatabase.getPointBackupDAO();
                bbb = pbDAO.searchCount();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Integer.valueOf(bbb);
        }

        @Override
        protected void onPostExecute(Integer i) {
            try {
                if (i.intValue() > 0) canUndo = true;
                else canUndo = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(i);
        }
    }

    private class ObservePointRecords extends AsyncTask<Void, Void, String> implements LifecycleOwner{

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            try {

                    PointDAO pDAO = appDatabase.getPointDAO();
                    pointLiveData = pDAO.searchAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }

             return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
//                Log.d("mod", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXAfter Point Observed: " + pointLiveData.getValue().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }

        @Override
        public Lifecycle getLifecycle(){
            return this.getLifecycle();
        }
    }

    private class ObservePointExtraRecords extends AsyncTask<Void, Void, String> implements LifecycleOwner{

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
             try {
                 PointExtraDAO peDAO = appDatabase.getPointExtraDAO();
                 pointExtraLiveData = peDAO.searchAll();
             } catch (Exception e) {
                 e.printStackTrace();
             }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
//                Log.d("mod", "After PointExtra Observed count: " + pointExtraLiveData.getValue().size());
            } catch (Exception e) {
                Log.d("mod", "" + e.getMessage());
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }

        @Override
        public Lifecycle getLifecycle(){
            return this.getLifecycle();
        }
    }

    private class ObserveMapAddressRecords extends AsyncTask<Void, Void, String> implements LifecycleOwner{

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            try {

                MapAddressDAO maDAO = appDatabase.getMapAddressDAO();
                mapAddressLiveData = maDAO.searchAll();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
//                Log.d("mod", "After Point Observed: " + pointLiveData.getValue().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }

        @Override
        public Lifecycle getLifecycle(){
            return this.getLifecycle();
        }
    }

    public com.zeedroid.maparcade.Point getPoint(float x, float y){
        double xCord = (double)x;                                        //Math.round(x);
//        double yCord = Math.round(y - statusBarHeight);
        double yCord = (double)y - (actionBarHeight + statusBarHeight);                                        //Math.round(y - (actionBarHeight + statusBarHeight));
//        for (int i=0; i< myJourney.size(); i++){
        for (int i=myJourney.size() - 1; i >= 0; i--){
            if ((xCord >= (myJourney.get(i).getCanvasX() - myJourney.get(i).getWidthNode()) &&
                    xCord <= (myJourney.get(i).getCanvasX() + myJourney.get(i).getWidthNode()))  &&
                    (yCord >= (myJourney.get(i).getCanvasY() - myJourney.get(i).getWidthNode()) &&
                            yCord <= (myJourney.get(i).getCanvasY() + myJourney.get(i).getWidthNode()))) {
                return myJourney.get(i);
            }
        }
        return new com.zeedroid.maparcade.Point();
    }

/*    public boolean isTheStartPoint(float lastXPosition, float lastYPosition){
        if (myPoints.get(0).getPoint().getCanvasX() == lastXPosition && myPoints.get(0).getPoint().getCanvasY() == lastYPosition) return true;
        else return false;
    }

    public boolean isTheEndPoint(float lastXPosition, float lastYPosition){
        if (myPoints.get(myPoints.size() -1).getPoint().getCanvasX() == lastXPosition && myPoints.get(myPoints.size() -1).getPoint().getCanvasY() == lastYPosition) return true;
        else return false;
    }*/

    public boolean isTheStartPoint(float lastXPosition, float lastYPosition){
        for (int i= myPoints.size() -1; i >= 0; i--) {
            if (myPoints.get(i).getPoint().getCanvasX() == lastXPosition && myPoints.get(i).getPoint().getCanvasY() == lastYPosition) {
                if (i == 0) return true;
                else return false;
            }
        }
        return false;
    }

    public boolean isTheEndPoint(float lastXPosition, float lastYPosition){
        for (int i= myPoints.size() -1; i >= 0; i--) {
            if (myPoints.get(i).getPoint().getCanvasX() == lastXPosition && myPoints.get(i).getPoint().getCanvasY() == lastYPosition)
                if (i == myPoints.size() - 1) return true;
                else return false;
        }
        return false;
    }

    public PointExtra getStartEndPoint(String startEnd){
        if (startEnd == "Start") return myPoints.get(0);
        else return myPoints.get(myPoints.size() -1);
    }

    public void movePointExtra(PointExtra pointExtra, float movementX, float movementY){
        double pixelX = MercatorProjection.pixelCoordinateX(pointExtra.getPoint().getLongitude(), zoomLevel,tileSize) + movementX;
        double pixelY = MercatorProjection.pixelCoordinateY(pointExtra.getPoint().getLatitude(),  zoomLevel, tileSize) + movementY;
        newLongitude = MercatorProjection.tilePixelXToLongitude(pixelX, zoomLevel, tileSize);
        newLatitude = MercatorProjection.tilePixelYToLatitude(pixelY, zoomLevel, tileSize);

        pointExtra.getPoint().setCanvasX(pointExtra.getCanvasX() + (double)movementX);
        pointExtra.getPoint().setCanvasY(pointExtra.getCanvasY() + (double)movementY);
        pointExtra.getPoint().setLatitude(newLatitude);
        pointExtra.getPoint().setLongitude(newLongitude);
    }

    public void updatePointExtra(){
        UpdatePointExtraRecord opr = new UpdatePointExtraRecord(appDatabase);
        opr.execute(Double.toString(lastLatitude), Double.toString(lastLongitude), Double.toString(newLatitude), Double.toString(newLongitude));
    }

    private class UpdatePointExtraRecord extends AsyncTask<String, Void, String>{

        private AppDatabase db;

        UpdatePointExtraRecord(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected String doInBackground (String... params){
            String aaa = "OK";
            try {
                PointExtraDAO peDAO = db.getPointExtraDAO();
                PointExtraBackupDAO pebDAO = db.getPointExtraBackupDAO();
                List<com.zeedroid.maparcade.entity.PointExtra> pe = peDAO.searchPoint(params[0], params[1]);

                int lastUndoPosition = pebDAO.searchLastUndoPosition() + 1;

                for (int i=0; i < pe.size(); i++) {
                    PointExtraBackup peb = new PointExtraBackup(lastUndoPosition, PointExtraBackup.MOVE_POINT, pe.get(i).getId(), pe.get(i).getPoint(), pe.get(i).getPointType(), pe.get(i).getPosition(), pe.get(i).getPointColor(), pe.get(i).getPointShape());
                    pebDAO.insert(peb);
                    pe.get(i).getPoint().setLatitude(valueOf(params[2]).doubleValue());
                    pe.get(i).getPoint().setLongitude(valueOf(params[3]).doubleValue());

                    peDAO.update(pe.get(i));
                }
                recalibratePointsExtra();

            } catch (Exception e) {

                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();

            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void movePoint(com.zeedroid.maparcade.Point point, float movementX, float movementY){
        double pixelX = MercatorProjection.pixelCoordinateX(point.getLongitude(), zoomLevel,tileSize) + movementX;
        double pixelY = MercatorProjection.pixelCoordinateY(point.getLatitude(),  zoomLevel, tileSize) + movementY;
        newLongitude = MercatorProjection.tilePixelXToLongitude(pixelX, zoomLevel, tileSize);
        newLatitude = MercatorProjection.tilePixelYToLatitude(pixelY, zoomLevel, tileSize);

        point.setCanvasX(point.getCanvasX() + (double)movementX);
        point.setCanvasY(point.getCanvasY() + (double)movementY);
        point.setLatitude(newLatitude);
        point.setLongitude(newLongitude);
    }

    public void updatePoint(){
        UpdatePointRecord opr = new UpdatePointRecord(appDatabase);
        opr.execute(Double.toString(lastLatitude), Double.toString(lastLongitude), Double.toString(newLatitude), Double.toString(newLongitude));
    }

    private class UpdatePointRecord extends AsyncTask<String, Void, String>{

        private AppDatabase db;

        UpdatePointRecord(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected String doInBackground (String... params){
            String aaa = "OK";
            try {
                PointDAO pDAO = db.getPointDAO();
                PointBackupDAO pbDAO = db.getPointBackupDAO();
                List<com.zeedroid.maparcade.entity.Point> p = pDAO.searchPoint(params[0], params[1]);

                int lastUndoPosition = pbDAO.searchLastUndoPosition() + 1;
                int i = p.size() -1;
//                for (int i=0; i < p.size(); i++) {
                    PointBackup pb = new PointBackup(lastUndoPosition, PointBackup.MOVE_POINT, p.get(i).getId(), p.get(i).getPathID(), p.get(i).getRouteID(), p.get(i).getPointPosition(), p.get(i).getLongitude(), p.get(i).getLatitude(), p.get(i).getTimePassed());
                    Long pnIns = pbDAO.insert(pb);
                    p.get(i).setLatitude(params[2]);
                    p.get(i).setLongitude(params[3]);

                    int ans = pDAO.update(p.get(i));
//                }

                recalibrateNodePoints();

            } catch (Exception e) {

                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();

            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public int recalibratePoints() {
        int pointsCalibrated = 0;
        float distanceTraveled = 0;

        double distanceAccum = 0;
        double lastLatitude, lastLongitude;
        lastLatitude = myJourney.get(0).getLatitude();
        lastLongitude = myJourney.get(0).getLongitude();

        for (int i=1; i < myJourney.size() - 1; i++){
            double distanceBetweenPoints = MercatorProjection.distanceBetweenTwoCoordinatesKm(lastLatitude, lastLongitude, myJourney.get(i).getLatitude(), myJourney.get(i).getLongitude()) * 1000;
            Log.d("sjd","distance between points = " + distanceBetweenPoints);
            if (distanceAccum + distanceBetweenPoints < minPointSeperationMeters){
                myJourney.get(i).setSelected(true);
                distanceAccum = distanceAccum + distanceBetweenPoints;
                pointsCalibrated++;
                continue;
            }
            lastLatitude = myJourney.get(i).getLatitude();
            lastLongitude = myJourney.get(i).getLongitude();
            distanceAccum = 0;
        }

        return pointsCalibrated;
    }

    public int errorGpsPoints() {
        int gpsErrorCount = 0;
        float distanceTraveled = 0;

        double lastLatitude = myJourney.get(0).getLatitude();
        double lastLongitude = myJourney.get(0).getLongitude();
        float lastTimePassed = 0;

        for (int i=1; i < myJourney.size() - 1; i++){
            double distanceBetweenPoints = MercatorProjection.distanceBetweenTwoCoordinatesKm(lastLatitude, lastLongitude, myJourney.get(i).getLatitude(), myJourney.get(i).getLongitude()) * 1000;
            Log.d("xxx", "erreoGpsPoints timePassed = " + myJourney.get(i).getTimepassed() );
            float timeTaken             = (myJourney.get(i).getTimepassed() - lastTimePassed) / 1000.000f;
            int speed                    = (int)(((distanceBetweenPoints / timeTaken) * 3600) / 1000);
            Log.d("sjd","distance = " + distanceBetweenPoints + " timetaken = " + timeTaken + " speed between points = " + speed + "Kph");
            if (speed > 5){
                myJourney.get(i).setSelected(true);
                gpsErrorCount++;
            }
            lastLatitude = myJourney.get(i).getLatitude();
            lastLongitude = myJourney.get(i).getLongitude();
            lastTimePassed = myJourney.get(i).getTimepassed();
        }

        return gpsErrorCount;
    }

    public void deletePoints(){
        DeletePointRecord opr = new DeletePointRecord(appDatabase);
        opr.execute();
    }

    private class DeletePointRecord extends AsyncTask<Void, Void, String>{

        private AppDatabase db;

        DeletePointRecord(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected String doInBackground (Void... params){
            String aaa = "OK";
            Iterator journey = myJourney.iterator();
            List<com.zeedroid.maparcade.entity.Point> deleteList = new ArrayList<>();
            List<com.zeedroid.maparcade.entity.PointBackup> backupList = new ArrayList<>();

            try {
                PointDAO pDAO = db.getPointDAO();
                PointBackupDAO pbDAO = db.getPointBackupDAO();

                int nextUndo = pbDAO.searchLastUndoPosition() + 1;

                while (journey.hasNext()) {
                    Point q = (Point) journey.next();
                    if (!q.isSelected()) continue;

                    List<com.zeedroid.maparcade.entity.Point> pointToDel = pDAO.searchPoint(Double.toString(q.getLatitude()), Double.toString(q.getLongitude()));

                    for (int i = 0; i < pointToDel.size(); i++) {
                        com.zeedroid.maparcade.entity.Point pd = pointToDel.get(i);
                        PointBackup pb = new PointBackup(nextUndo, PointBackup.DELETE_POINT, pd.getId(), pd.getPathID(), pd.getRouteID(), pd.getPointPosition(), pd.getLongitude(), pd.getLatitude(), pd.getTimePassed());
                        Long ins = pbDAO.insert(pb);
                    }
                    pDAO.delete(pointToDel);
                }
                recalibrateNodePoints();

            } catch (Exception e) {

                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();

            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

/*    private class DeletePointRecord extends AsyncTask<Void, Void, String>{

        private AppDatabase db;

        DeletePointRecord(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected String doInBackground (Void... params){
            String aaa = "OK";
            Iterator journey = myJourney.iterator();
            List<com.zeedroid.maparcade.entity.Point> deleteList = new ArrayList<>();
            List<com.zeedroid.maparcade.entity.PointBackup> backupList = new ArrayList<>();

            try {
                PointDAO pDAO = db.getPointDAO();
                PointBackupDAO pbDAO = db.getPointBackupDAO();

                int nextUndo = pbDAO.searchLastUndoPosition() + 1;

                while (journey.hasNext()) {
                    Point q = (Point) journey.next();
                    if (!q.isSelected()) continue;

                    List<com.zeedroid.maparcade.entity.Point> pointToDel = pDAO.searchPoint(Double.toString(q.getLatitude()), Double.toString(q.getLongitude()));

                    for (int i = 0; i < pointToDel.size(); i++) {
                        com.zeedroid.maparcade.entity.Point pd = pointToDel.get(i);
                        PointBackup pb = new PointBackup(nextUndo, PointBackup.DELETE_POINT, pd.getId(), pd.getPathID(), pd.getRouteID(), pd.getPointPosition(), pd.getLongitude(), pd.getLatitude(), pd.getTimePassed());
                        Long ins = pbDAO.insert(pb);
                    }
                    pDAO.delete(pointToDel);
                }
                recalibrateNodePoints();

            } catch (Exception e) {

                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();

            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }*/

    public void undoPoint(){
        UndoPointRecord upr = new UndoPointRecord(appDatabase);
        upr.execute();
    }

    private class UndoPointRecord extends AsyncTask<Void, Void, String>{

        private AppDatabase db;

        UndoPointRecord(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected String doInBackground (Void... params){
            String aaa = "OK";
            try {

                PointDAO pDAO = db.getPointDAO();
                PointBackupDAO pbDAO = db.getPointBackupDAO();

                int undoNumber = pbDAO.searchLastUndoPosition();
                List<PointBackup> undoList = pbDAO.searchLastUndo(undoNumber);
                for (int i=0; i < undoList.size(); i++){
                    PointBackup pb = undoList.get(i);
                    switch (pb.getUndoType()){
                        case PointBackup.MOVE_POINT:
                            com.zeedroid.maparcade.entity.Point mp = pDAO.getSingleRecord(pb.getPointPosition());
                            if (mp != null){
                                mp.setLatitude(pb.getLatitude());
                                mp.setLongitude(pb.getLongitude());
                                pDAO.update(mp);
                            }
                            pbDAO.delete(pb);
                            break;
                        case PointBackup.JOIN_POINT:
                            break;
                        case PointBackup.DELETE_POINT:
                            com.zeedroid.maparcade.entity.Point dp = new com.zeedroid.maparcade.entity.Point(pb.getPathID(),pb.getRouteID(),pb.getPointPosition(),pb.getLongitude(),pb.getLatitude(),pb.getTimePassed());
                            pDAO.insert(dp);
                            pbDAO.delete(pb);
                            break;
                        case PointBackup.INSERT_POINT:
                            break;
                        default: Log.d("sjd", "Undo Type does not exist");
                    }
                }

                recalibrateNodePoints();

            } catch (Exception e) {

                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();

            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

 /*   public void preserveJourney(){
        ModifyMapModel.InsertMapRouteRecord imrr = new ModifyMapModel.InsertMapRouteRecord();
        ModifyMapModel.InsertPointRecords ipr = new ModifyMapModel.InsertPointRecords();
        ModifyMapModel.InsertPointExtraRecords iper = new ModifyMapModel.InsertPointExtraRecords();

        imrr.execute();
        ipr.execute();
        iper.execute();
    }

    private class InsertPointRecords extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground (Void...voids){
            String aaa = "OK";
            for (int i = 0; i < myJourney.size(); i++) {
                try {
                    PointDAO pDAO = appDatabase.getPointDAO();
                    com.zeedroid.maparcade.entity.Point p =
                            new com.zeedroid.maparcade.entity.Point(
                                    1, 1, i, Double.valueOf(myJourney.get(i).getLongitude()).toString(), Double.valueOf(myJourney.get(i).getLatitude()).toString());

                    pDAO.insert(p);
                    Log.d("mod", "After Point created");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("mod", "" + e.getMessage());
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
                                    myPoints.get(i).getPoint(), 1, myPoints.get(i).getPointType(), myPoints.get(i).getPosition(), myPoints.get(i).getPointColor(), myPoints.get(i).getPointShape());

                    peDAO.insert(p);
                    Log.d("mod", "After PointExtra created");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("mod", "" + e.getMessage());
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


                    mrDAO.insert(p);
                    Log.d("mod", "After MapRoute created");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("mod", "" + e.getMessage());
                }
            }
            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }*/

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
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("mod", "" + e.getMessage());
                }
            }
            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

/*    public RoadName getPathName(double latitude, double longitude) {
        List<Address> addresses;
        Address address;
        String pathName = "";
        String postCode = "";

        try {
            addresses = gc.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0);

            String aaa = "";
            for (int i = 0; i< address.getMaxAddressLineIndex(); i++){
                aaa = aaa.concat(": " + address.getAddressLine(i));
            }
            pathName = addresses.get(0).getAddressLine(0).replaceAll("^\\s*[0-9]+[A-Za-z]*\\ +", "");
            postCode = address.getPostalCode();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("mod", e.getMessage());
        }

        return new RoadName(pathName, postCode);
    }*/


    public int getSelectedRoadId(RoadName roadName, MapAddressDAO maDAO){
        int roadID = 0;
        try {
            roadID = maDAO.searchAddress(roadName.getRoadName(), roadName.getPostCode());
        } catch (Exception e) {

            Log.d("mod", "AAAAAAAAAA" + e.getMessage());
            e.printStackTrace();
        }
        return roadID;
    }

    public void assignRoadNames(){
        UpdateMapAddressRecords uma = new UpdateMapAddressRecords(appDatabase);
        uma.execute(myJourney);
    }

    private class UpdateMapAddressRecords extends AsyncTask<PointArray, Void, String>{

        private AppDatabase db;

        UpdateMapAddressRecords(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected String doInBackground (PointArray... params){
            String aaa = "OK";
            try {

                PointDAO pDAO = db.getPointDAO();
                MapAddressDAO maDAO = db.getMapAddressDAO();

                int pathID = 0;
                String lastPostcode = "";
                String lastRoadName = "";
                PointArray points = params[0];
                List<com.zeedroid.maparcade.entity.Point> updatedPoints = new ArrayList<>();
                com.zeedroid.maparcade.entity.Point p = new com.zeedroid.maparcade.entity.Point();
                for (int i = 0; i < (points.size()); i++) {
                    List pList = pDAO.searchPoint(Double.toString(points.get(i).getLatitude()), Double.toString(points.get(i).getLongitude()));
                    for (int j=0; j < pList.size(); j++) {
                        p = (com.zeedroid.maparcade.entity.Point)pList.get(j);
//                        RoadName ma = getPathName(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()));
                        RoadName ma = MercatorProjection.getPathName(gc, Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()));
                        if (!ma.getRoadName().equals(lastRoadName) || !ma.getPostCode().equals(lastPostcode)) {
                            pathID = getSelectedRoadId(ma, maDAO);
                            if (pathID == 0) {
                                pathID = maDAO.searchLastRoadID() + 1;
                                maDAO.insert(new MapAddress(p.getRouteID(), pathID, ma.getRoadName(), ma.getPostCode()));
                            }
                            lastPostcode = ma.getPostCode();
                            lastRoadName = ma.getRoadName();
                        }
                        p.setPathID(pathID);
                        updatedPoints.add(p);
//                        pDAO.update(p);
                    }
                }

                pDAO.update(updatedPoints);

            } catch (Exception e) {

                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();

            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

 /*   private class UpdateMapAddressRecords extends AsyncTask<Void, Void, String>{

        private AppDatabase db;

        UpdateMapAddressRecords(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected String doInBackground (Void... params){
            String aaa = "OK";
            Log.d("sjd", "UpdateMapAddressRecords.doInBackground");
            try {

                PointDAO pDAO = db.getPointDAO();
                MapAddressDAO maDAO = db.getMapAddressDAO();
                Log.d("sjd","AAAAA ");

                int pathID = 0;
                String lastPostcode = "";
                String lastRoadName = "";


                if (pointLiveData.getValue() != null) {
                    for (int i = 0; i < pointLiveData.getValue().size(); i++) {
                        com.zeedroid.maparcade.entity.Point p = pointLiveData.getValue().get(i);
                        Log.d("sjd", "UpdateMapAddressRecords Longitude = " + p.getLongitude());
                        RoadName ma = getPathName(Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongitude()));
                        Log.d("sjd", "UpdateMapAddressRecords RoadName = " + ma.getRoadName());
                        if (!ma.getRoadName().equals(lastRoadName) || !ma.getPostCode().equals(lastPostcode)) {
                            pathID = getSelectedRoadId(ma, maDAO);
                            if (pathID == 0) {
                                pathID++;
                                maDAO.insert(new MapAddress(p.getRouteID(), pathID, ma.getRoadName(), ma.getPostCode()));
                            }
                        }
                        p.setPathID(pathID);
                        pDAO.update(p);
                    }
                }else{
                    Log.d("sjd", "UpdateMapAddressRecords pointLiveData is null");
                }

                Log.d("sjd", "UpdateMapAddressRecords.doInBackground Ends");

            } catch (Exception e) {

                Log.d("mod", "AAAAAAAAAA" + e.getMessage());
                e.printStackTrace();

            }

            return aaa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
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
//                                        Toast.makeText(application, "Err:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
