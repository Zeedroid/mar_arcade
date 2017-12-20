package com.zeedroid.maparcade;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve Dixon on 26/06/2017.
 */

public class PreviewMapLayoutKeep extends View {

    private Paint feature_paintbrush_fill, red_paintbrush_fill, blue_paintbrush_fill, green_paintbrush_fill;
    private Paint feature_paintbrush_stroke, red_paintbrush_stroke, blue_paintbrush_stroke, green_paintbrush_stroke;
    private Path route, routeText;
    private android.graphics.Point feature;
    private Point pointA, pointB;

    private int screenWidth, screenHeight;
    private int tileCountX, tileCountY;
    private int tileSize;
    private int hypotenuse, squares;
    private PointArray points;
    private PointExtraArray pointsExtra;
    private int zoomLevel;
    private String mapType;
    private boolean creatingMap = true;
    private MapTileBoundingBox mapTileBoundingBox;
    private ArrayList<TileCoordinates> tileCoordinates = new ArrayList<TileCoordinates>();
    private Geocoder gc = new Geocoder(this.getContext());
    private Context context;
    private PreviewMapModel viewModel;
    private SharedPreferences prefs;
    private TileNumberPixel startTilePixelX, startTilePixelY;
    private Path compass;
    private Paint compass_paintbrush_stoke, compass_text_fill_stroke;
    private String[] compassPoints;

    public PreviewMapLayoutKeep(Context context, PreviewMapModel viewModel) {
        super(context);
        this.context = context;
        this.viewModel = viewModel;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tileSize = Integer.parseInt(prefs.getString("map_tile_size", "400"));

        compassPoints = getResources().getStringArray(R.array.compass_points);
        setUpPaintBrushes();
    }

    public PreviewMapLayoutKeep(Context context, AttributeSet attrs, PreviewMapModel viewModel){
        super(context,attrs);
        this.context = context;
        this.viewModel = viewModel;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tileSize = Integer.parseInt(prefs.getString("map_tile_size", "400"));

        compassPoints = getResources().getStringArray(R.array.compass_points);
        setUpPaintBrushes();
    }

    public PreviewMapLayoutKeep(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
 //       this.viewModel = viewModel;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tileSize = Integer.parseInt(prefs.getString("map_tile_size", "400"));

        compassPoints = getResources().getStringArray(R.array.compass_points);
        setUpPaintBrushes();
    }

    public void setViewModel(PreviewMapModel viewModel){
        this.viewModel = viewModel;
    }

    private Camera camera = new Camera();
    private Matrix matrix = new Matrix();

    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setPoints(PointArray points) {
        this.points = points;
    }

    public void setPointsExtra(PointExtraArray pointsExtra) {
        this.pointsExtra = pointsExtra;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
        switch (mapType) {
            case "Walk":
                setBackgroundColor(Color.parseColor(prefs.getString("map_walk_colour", "#CEFF9D")));
                break;
            case "Ride":
                setBackgroundColor(Color.parseColor(prefs.getString("map_ride_colour", "#AB995B")));
                break;
            case "Cycle":
                setBackgroundColor(Color.parseColor(prefs.getString("map_cycle_colour", "#B0A07F")));
                break;
            case "Drive":
                setBackgroundColor(Color.parseColor(prefs.getString("map_drive_colour", "#ACB4BA")));
                break;
        }
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public void setMapTileBoundingBox(MapTileBoundingBox mapTileBoundingBox) {
        this.mapTileBoundingBox = mapTileBoundingBox;
    }

    public void setNumberOfSquares(int hypotenuse, int squares){
        this.hypotenuse = hypotenuse;
        this.squares = squares;
    }

    public void setCreatingMap(boolean creatingMap){
        this.creatingMap = creatingMap;
    }

 /*   @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("sjd","Pointer Count=" + event.getPointerCount());
        Log.d("sjd","x=" + getX() + " y=" + getY());

           return super.onTouchEvent(event);
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        tileCountX = (screenWidth / tileSize) + 2;
        tileCountY = (screenHeight / tileSize) + 2;
        int[] tileNumbers = new int[tileCountX * tileCountY];
        int[][] mapTiles = new int[tileCountX][tileCountY];

//      camera.save();
        canvas.save();
        if (creatingMap) changeCameraAngle(canvas);

        if (prefs.getBoolean("map_show_zoom_level", false)) {

//            canvas.save();
            drawZoomLevel(canvas);
//            canvas.restore();
        }

        if (prefs.getBoolean("map_show_tile_grid", false)) {
//            canvas.save();
            drawTileLines(canvas);
//            canvas.restore();
        }

//        canvas.save();
        drawTileNumbers(canvas);
//        canvas.restore();

        drawCompassRose(canvas);

//        canvas.save();
        drawPoints(canvas);
//        canvas.restore();
//        drawPointsText(canvas);

//        canvas.save();
        drawPointsExtra(canvas);
//        canvas.restore();

 //       changeCameraAngle(canvas);
//        camera.restore();

        canvas.restore();
//        matrix.preTranslate(-screenWidth/2,-screenHeight/2);
//        matrix.postTranslate(screenWidth/2, screenHeight/2);

    }

    public void createCompassCircles(){
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());
        int navigateBarHeight     = Utilities.getNavigationBarHeight((Activity)getContext());
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

    private void drawCompassRose(Canvas canvas) {
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());
        int centerX;
        int centerY;

        int width;
        int height;

        if (points.size() < 2) return;

        if (screenWidth < screenHeight){
            width = screenWidth;
            height = screenHeight;
        }else {
            width = screenHeight;
            height = screenWidth;
        }

        centerX = screenWidth / 2;
        centerY = ((screenHeight + actionBarHeight) / 2) + actionBarHeight;
//        centerY = screenHeight / 2;

//        centerX = width / 2;
//        centerY = height / 2;


        canvas.drawPath(compass,compass_paintbrush_stoke);

        for (int i=0; i < 24; i++){
/*            canvas.drawLine(centerX, centerY - (centerX - 30), centerX, centerY - (centerX - 130),compass_paintbrush_stoke);
            canvas.rotate(15,centerX, centerY);
            if (i % 6 == 0){
                if (i != 0) canvas.drawText(compassPoints[(i / 6) -1], (centerX) - 30, centerY - (centerX - 130), compass_text_fill_stroke);
                else canvas.drawText(compassPoints[0], (centerX) - 30, centerY - (centerX - 130), compass_text_fill_stroke);
            }else if (i % 3 == 0){
                String angle = String.valueOf(i * 15);
                float  angleLength = compass_text_fill_stroke.measureText(angle);
                int    angleX      = (int)(370 - angleLength / 2);
                canvas.drawText(angle, centerX - (angleLength / 2), centerY - (centerX - 130),compass_text_fill_stroke);
            }*/
            canvas.drawLine(centerX, centerY - ((width / 2) - 30), centerX, centerY - ((width / 2) - 70),compass_paintbrush_stoke);

            if (i % 6 == 0){
/*                switch (i){
                    case 0:  canvas.drawText(compassPoints[0], centerX - 30, centerY - ((width / 2) - 130), compass_text_fill_stroke);
                             break;
                    case 6:  canvas.drawText(compassPoints[1], centerX - 30, centerY - ((width / 2) - 130), compass_text_fill_stroke);
                             break;
                    case 12: canvas.drawText(compassPoints[2], centerX - 30, centerY - ((width / 2) - 130), compass_text_fill_stroke);
                             break;
                    case 18: canvas.drawText(compassPoints[3], centerX - 30, centerY - ((width / 2) - 130), compass_text_fill_stroke);
                             break;
                }*/
                float  angleLength = compass_text_fill_stroke.measureText(compassPoints[(i / 6)]);
                canvas.drawText(compassPoints[(i / 6)], centerX - (angleLength / 2), centerY - ((width / 2) - 130), compass_text_fill_stroke);
//                if (i != 0) canvas.drawText(compassPoints[(i / 6) -1], centerX - 30, centerY - ((width / 2) - 130), compass_text_fill_stroke);
//                else canvas.drawText(compassPoints[0], centerX - 30, centerY - ((width / 2) - 130), compass_text_fill_stroke);
            }else if (i % 3 == 0){
                String angle = String.valueOf(i * 15);
                float  angleLength = compass_text_fill_stroke.measureText(angle);
                canvas.drawText(angle, centerX - (angleLength / 2), centerY - ((width / 2) - 130),compass_text_fill_stroke);
            }
            canvas.rotate(15,centerX, centerY);
        }
        Log.d("sjd4","AAAscreenWidth=" + screenWidth + " screenHeight=" + screenHeight + " canvasWidth=" + canvas.getWidth() + " canvasHeight=" + canvas.getHeight());

    }

    private void changeCameraAngle(Canvas canvas) {
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());
        if (points.size() < 2) return;
        Point pointFrom = points.get(points.size() - 2);
        Point pointTo = points.get(points.size() - 1);

        double dLon = (pointTo.getLongitude() - pointFrom.getLongitude());
        double y = Math.sin(dLon) * Math.cos(pointTo.getLatitude());
        double x = Math.cos(pointFrom.getLatitude()) *
                Math.sin(pointTo.getLatitude()) -
                Math.sin(pointFrom.getLatitude()) *
                        Math.cos(pointTo.getLatitude()) *
                        Math.cos(dLon);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

/*        matrix.preTranslate(-screenWidth / 2, -screenHeight / 2);
        matrix.postTranslate(screenWidth / 2, screenHeight / 2);
        matrix.setRotate((float) brng, screenWidth / 2, screenHeight / 2);*/
        matrix.preTranslate(screenWidth / 2, ((screenHeight + actionBarHeight) / 2) + actionBarHeight);
        matrix.postTranslate(-(screenWidth / 2), -((screenHeight + actionBarHeight) / 2) + actionBarHeight);
        matrix.setRotate((float) brng, screenWidth / 2, ((screenHeight + actionBarHeight) / 2) + actionBarHeight);
//        matrix.setRotate((float) brng, screenWidth / 2, (screenHeight / 2) + (canvas.getHeight() - screenHeight));
//        matrix.setRotate((float) brng, screenWidth / 2, (screenHeight / 2) + (canvas.getHeight() - screenHeight));
        canvas.setMatrix(matrix);

  //      ViewPropertyAnimator aaa = animate();
  //      aaa.setDuration(2000);
    }

/*        float angleTravel = (float) Math.toDegrees(Math.atan2(getTilePointXY(pointTo).getyCoord() -
                                                              getTilePointXY(pointFrom).getyCoord(),
                                                              getTilePointXY(pointTo).getxCoord() -
                                                              getTilePointXY(pointFrom).getxCoord()));
        Toast.makeText(this.context, "Camera Angle Changed by " + angleTravel + " Deg", Toast.LENGTH_SHORT).show();*/
    /*    float angleAhead  = (float) Math.toDegrees(Math.atan2(0 -
                                                              getTilePointXY(pointFrom).getyCoord(),
                                                              getTilePointXY(pointFrom).getxCoord() -
                                                              getTilePointXY(pointFrom).getxCoord()));

        if (angleTravel < 0){
            angleTravel += 360;
        }*/
//        Camera a = new Camera();

//        camera.setLocation(screenWidth / 2, screenHeight / 2, -8);

//        camera.rotate((float)(0 - brng),0,0);
//        camera.getMatrix(matrix);
//        matrix.setTranslate(screenWidth / 2, screenHeight / 2);
//        matrix.setRotate((float)(90 * Math.PI / 180),screenWidth/2, screenHeight / 2);
/*        matrix.preTranslate(-screenWidth/2,-screenHeight/2);
        matrix.postTranslate(screenWidth/2, screenHeight/2);
        matrix.setRotate((float)brng,screenWidth/2, screenHeight / 2);

//        matrix.preRotate((float)(0 - brng),screenWidth/2, screenHeight / 2);


        canvas.setMatrix(matrix);

        ViewPropertyAnimator aaa = animate();
        aaa.setDuration(2000);



//        camera.getMatrix(matrix);
//        Toast.makeText(this.context,"screenWidth=" + screenWidth + " screenHeight=" + screenHeight,Toast.LENGTH_SHORT).show();
//        canvas.concat(matrix);
//        camera.rotateX((float)(0 - brng));
//        camera.applyToCanvas(canvas);
    }*/

    private PointXY getTilePointXY(Point point) {
        TileCoordinates tc = getTileCoordinates(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
        double tileStartX = tc.getScreenPixelX();
        double tileStartY = tc.getScreenPixelY();

        double pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
        double pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);

        return new PointXY(pixelX, pixelY);
    }

    private void drawPoints(Canvas canvas) {
        boolean wasOffScreen = false;
        Point point = points.get(0);
//        Point point = points.get(points.size() - 1);
        pointA = point;
        TileCoordinates tc = getTileCoordinates(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
        double tileStartX = tc.getScreenPixelX();
        double tileStartY = tc.getScreenPixelY();

        double pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
        double pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);
        route = new Path();
        route.moveTo((float) pixelX, (float) pixelY);
        setUpPaintBrushes();
        red_paintbrush_stroke.setStrokeWidth(20f);
        for (int i = 1; i < points.size(); i++) {
//        for (int i = points.size() - 1; i >= 0; i--) {
            point = points.get(i);
            pointB = pointA;
            pointA = point;
            tc = getTileCoordinates(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
            if (tc.getTileX() == 0 && tc.getTileY() == 0) {
                if (!route.isEmpty()) {
                    canvas.drawPath(route, red_paintbrush_stroke);
                    route = new Path();
                }
                wasOffScreen = true;
                continue;
            } else {
                if (wasOffScreen) {
                    wasOffScreen = false;
                    route.moveTo((float) pixelX, (float) pixelY);
                    continue;
                }
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);
                route.lineTo((float) pixelX, (float) pixelY);
                route.moveTo((float) pixelX, (float) pixelY);
            }
        }
        canvas.drawPath(route, red_paintbrush_stroke);
    }

    private void drawPointsText(Canvas canvas) {
        boolean wasOffScreen = false;
        Point point = points.get(0);
        pointA = point;
        TileCoordinates tc = getTileCoordinates(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
        double tileStartX = tc.getScreenPixelX();
        double tileStartY = tc.getScreenPixelY();

        double pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
        double pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);

        routeText = new Path();
        routeText.moveTo((float) pixelX, (float) pixelY);
        red_paintbrush_stroke.setStrokeWidth(20f);

        String currentRoad = getPathName(point.getLatitude(), point.getLongitude());
        for (int i = 1; i < points.size(); i++) {
            point = points.get(i);
            pointB = pointA;
            pointA = point;

            if (currentRoad != "" && currentRoad != getPathName(point.getLatitude(), point.getLongitude())) {
                Toast.makeText(this.context, "currentRoad=" + currentRoad + " NewRoad=" + getPathName(point.getLatitude(), point.getLongitude()), Toast.LENGTH_SHORT).show();
                if (currentRoad != "") {
                    canvas.drawPath(routeText, red_paintbrush_stroke);
                    canvas.drawTextOnPath(currentRoad, routeText, 100, 0, green_paintbrush_stroke);
                }
                routeText = new Path();
                routeText.moveTo((float) pixelX, (float) pixelY);
                currentRoad = getPathName(point.getLatitude(), point.getLongitude());
            }
            tc = getTileCoordinates(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
            if (tc.getTileX() == 0 && tc.getTileY() == 0) {
                Toast.makeText(this.context, "tc empty", Toast.LENGTH_SHORT).show();
                if (!routeText.isEmpty()) {
                    if (currentRoad != "") {
                        canvas.drawPath(routeText, red_paintbrush_stroke);
                        canvas.drawTextOnPath(currentRoad, routeText, 100, 0, green_paintbrush_stroke);
                    }
                    currentRoad = getPathName(point.getLatitude(), point.getLongitude());
                    routeText = new Path();
                    routeText.moveTo((float) pixelX, (float) pixelY);
                }
                wasOffScreen = true;
                continue;
            } else {
                if (wasOffScreen) {
                    wasOffScreen = false;
                    routeText.moveTo((float) pixelX, (float) pixelY);
                    continue;
                }
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);
                routeText.lineTo((float) pixelX, (float) pixelY);
                routeText.moveTo((float) pixelX, (float) pixelY);
            }
        }
        if (currentRoad != "") {
            canvas.drawPath(routeText, red_paintbrush_stroke);
            canvas.drawTextOnPath(currentRoad, routeText, 100, 0, green_paintbrush_stroke);
        }
    }

    private String getPathName(double latitude, double longitude) {
        List<Address> addresses;
        String pathName = "";
        try {
            addresses = gc.getFromLocation(latitude, longitude, 1);
            pathName = addresses.get(0).getAddressLine(0).replace("^\\s*[0-9]+\\s+", "");
        } catch (Exception e) {
            Log.d("sjd", e.getMessage());
            Log.d("sjd", e.getStackTrace().toString());
        }
        return pathName;
    }

    private void drawPointsExtra(Canvas canvas) {
        Point point;
        TileCoordinates tc;
        double tileStartX, tileStartY;
        double pixelX, pixelY;

        for (int j = 0; j < pointsExtra.size(); j++) {
            point = pointsExtra.get(j).getPoint();
            String pointColor = pointsExtra.get(j).getPointColor();
            tc = getTileCoordinates(MercatorProjection.tileCoordinateX(point.getLongitude(), zoomLevel, tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), zoomLevel, tileSize));
            if (tc.getTileX() != 0 && tc.getTileY() != 0) {
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), zoomLevel, tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), zoomLevel, tileSize);
                feature = new android.graphics.Point((int) pixelX, (int) pixelY);

             /* need to calculate point to left or right of path depending on direction from pointA to pointB  */

                canvas.drawCircle((float) pixelX, (float) pixelY, 50, feature_paintbrush_stroke);
                feature_paintbrush_fill.setColor((Color.parseColor(pointColor) & 0xfefefe) >> 1);
                canvas.drawCircle((float) pixelX, (float) pixelY, 49, feature_paintbrush_fill);
                feature_paintbrush_fill.setColor(Color.parseColor(pointColor));
                canvas.drawCircle((float) pixelX, (float) pixelY, 45, feature_paintbrush_fill);
                pointsExtra.get(j).setCanvasX(pixelX);
                pointsExtra.get(j).setCanvasY(pixelY);
                pointsExtra.get(j).setWidthExtra(45);
            }
        }
    }

    private TileCoordinates getTileCoordinates(double tileX, double tileY) {
        for (int i = 0; i < tileCoordinates.size(); i++) {
            if (tileCoordinates.get(i).getTileX() == tileX && tileCoordinates.get(i).getTileY() == tileY) {
                return tileCoordinates.get(i);
            }
        }
        return new TileCoordinates();
    }

    private void drawZoomLevel(Canvas canvas) {
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());

        blue_paintbrush_fill.setTextSize(800f);
        blue_paintbrush_fill.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + zoomLevel, screenWidth / 2, ((screenHeight + actionBarHeight) / 2) + actionBarHeight, blue_paintbrush_fill);
//        canvas.drawText("" + zoomLevel, screenWidth / 2, screenHeight / 2, blue_paintbrush_fill);
    }

    private void drawTileNumbers(Canvas canvas) {
        if (zoomLevel < 18) red_paintbrush_fill.setTextSize(60f);
        else red_paintbrush_fill.setTextSize(50f);

        red_paintbrush_fill.setTextAlign(Paint.Align.LEFT);

        int minTileCoordinateX = mapTileBoundingBox.getMinTileCoordinateX();
        int minTileCoordinateY = mapTileBoundingBox.getMinTileCoordinateY();
        int maxTileCoordinateX = mapTileBoundingBox.getMaxTileCoordinateX();
        int maxTileCoordinateY = mapTileBoundingBox.getMaxTileCoordinateY();
        int routeTilesX = mapTileBoundingBox.getTileCountX();
        int routeTilesY = mapTileBoundingBox.getTileCountY();

        startTilePixelX = viewModel.getStartX();
        startTilePixelY = viewModel.getStartY();
        int drawLineX = startTilePixelX.getTilePixel();
        int drawLineY = startTilePixelY.getTilePixel();


        int tileStartX = startTilePixelX.getTileNumber();
        int tileStartY = startTilePixelY.getTileNumber();
 //       int tileStartX = minTileCoordinateX - (((tileCountX + 1) - routeTilesX) / 2 - 1);
   //     int tileStartY = minTileCoordinateY - (((tileCountY + 1) - routeTilesY) / 2 - 1);


//        int drawLineY = (tileSize - (((screenHeight % tileSize) + 1) / 2)) * -1;   // Starting position off canvas
//        int drawLineX = (tileSize - (((screenWidth % tileSize) + 1) / 2)) * -1;
//        int drawLineY = ((hypotenuse % 2) - (screenHeight % 2)) * -1;
//        int drawLineX = ((hypotenuse % 2) - (screenWidth % 2) * -1);


//        tileCoordinates = new ArrayList<TileCoordinates>();
        tileCoordinates.clear();


//        for (int x = 0; x <= tileCountX; x++) {
//            for (int y = 0; y <= tileCountY; y++) {
        for (int x = 0; x <= (squares + 1); x++) {
            for (int y = 0; y <= (squares + 1); y++) {
                tileCoordinates.add(new TileCoordinates(tileStartX + x, tileStartY + y, drawLineX + (tileSize * x), drawLineY + (tileSize * y)));
                if (prefs.getBoolean("map_show_tile_grid", false)) {
                    canvas.drawText("" + (tileStartX + x) + ",", drawLineX + 30 + (tileSize * x), drawLineY + 70 + (tileSize * y), red_paintbrush_fill);
                    canvas.drawText("" + (tileStartY + y), drawLineX + 30 + (tileSize * x), drawLineY + 140 + (tileSize * y), red_paintbrush_fill);
                }
            }
        }

    }

/*    @NonNull
    private TileNumberPixel getStartX(){
        double minPixelX = MercatorProjection.pixelCoordinateX(points.get(points.size() - 1).getLongitude(), zoomLevel, tileSize);
        double maxPixelX = minPixelX;

        double lastXT    = MercatorProjection.tileCoordinateX(points.get(points.size() - 1).getLongitude(), zoomLevel, tileSize);
        double lastX     = minPixelX;
        Point minPointX  = points.get(points.size() - 1);
        Point maxPointX  = minPointX;

        for (int i = points.size() - 2; i >= 0; i--){
            double currentPixel = MercatorProjection.pixelCoordinateX(points.get(i).getLongitude(), zoomLevel, tileSize);
            if (currentPixel >= minPixelX && currentPixel <= maxPixelX) continue;

            if (currentPixel < minPixelX) {
                if (maxPixelX - currentPixel > ((screenWidth / 100) * 80)) break;
                minPixelX = currentPixel;
                minPointX = points.get(i);
            }
            if (currentPixel > maxPixelX) {
                if (currentPixel - minPixelX > ((screenWidth / 100) * 80)) break;
                maxPixelX = currentPixel;
                maxPointX = points.get(i);
            }
        }

        int screenTileX     = (int)MercatorProjection.tileCoordinateX(minPointX.getLongitude(),zoomLevel,tileSize);
        double mapPixelsX   = maxPixelX - minPixelX + 1;
        double tilePixelX   = MercatorProjection.tilePixelX(minPointX.getLongitude(),zoomLevel,tileSize);
        int screenPixelXMin = (int)(((screenWidth - mapPixelsX) * 0.5) - tilePixelX);

        int    boundXStart  = (int)(((squares * tileSize) * 0.5) - (screenWidth * 0.5)) * -1;

        while ((screenPixelXMin - tileSize) > boundXStart){
            screenPixelXMin = screenPixelXMin - tileSize;
            screenTileX = screenTileX - 1;
        }

        return new TileNumberPixel(screenTileX,screenPixelXMin);
    }*/

/*    @NonNull
    private TileNumberPixel getStartY(){
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());
        double minPixelY = MercatorProjection.pixelCoordinateY(points.get(points.size() - 1).getLatitude(), zoomLevel, tileSize);
        double maxPixelY = minPixelY;

        double lastYT    = MercatorProjection.tileCoordinateY(points.get(points.size() - 1).getLatitude(), zoomLevel, tileSize);
        double lastY     = minPixelY;
        Point minPointY  = points.get(points.size() - 1);
        Point maxPointY  = minPointY;

        for (int i = points.size() - 2; i >= 0; i--){
            double currentPixel = MercatorProjection.pixelCoordinateY(points.get(i).getLatitude(), zoomLevel, tileSize);
            if (currentPixel >= minPixelY && currentPixel <= maxPixelY) continue;

            if (currentPixel < minPixelY) {
                if (maxPixelY - currentPixel > ((screenHeight / 100) * 80)) break;
                minPixelY = currentPixel;
                minPointY = points.get(i);
            }
            if (currentPixel > maxPixelY) {
                if (currentPixel - minPixelY > ((screenHeight / 100) * 80)) break;
                maxPixelY = currentPixel;
                maxPointY = points.get(i);
            }
        }

        int screenTileY     = (int)MercatorProjection.tileCoordinateY(minPointY.getLatitude(),zoomLevel,tileSize);
        double mapPixelsY   = maxPixelY - minPixelY + 1;
        double tilePixelY   = MercatorProjection.tilePixelY(minPointY.getLatitude(),zoomLevel,tileSize);
        int screenPixelYMin = (int)(((screenHeight - mapPixelsY) * 0.5) - tilePixelY);
        int    boundYStart  = (int)(((squares * tileSize) * 0.5) - (((screenHeight + actionBarHeight) / 2) + actionBarHeight)) * -1;

        while ((screenPixelYMin - tileSize) > boundYStart){
            screenPixelYMin = screenPixelYMin - tileSize;
            screenTileY = screenTileY - 1;
        }

        return new TileNumberPixel(screenTileY,screenPixelYMin);


    }*/

    private void drawTileLines(Canvas canvas) {
//        int routeTilesX = mapTileBoundingBox.getTileCountX() + 1;
//        int routeTilesY = mapTileBoundingBox.getTileCountY() + 1;
        startTilePixelX = viewModel.getStartX();
        startTilePixelY = viewModel.getStartY();
        int drawLineX = startTilePixelX.getTilePixel();
        int drawLineY = startTilePixelY.getTilePixel();

//        int drawLineY = ((hypotenuse % 2) - (screenHeight % 2)) * -1;
//        int drawLineX = ((hypotenuse % 2) - (screenWidth % 2) * -1);
//        int drawLineY = (tileSize - (((screenHeight % tileSize) + 1) / 2)) * -1;   // Starting position off canvas
//        int drawLineX = (tileSize - (((screenWidth % tileSize) + 1) / 2)) * -1;
         Path boxLine;
//        for (int y = 0; y <= (tileCountY + 1); y++) {
        for (int y = 0; y <= squares; y++) {
            boxLine = new Path();
            boxLine.moveTo(drawLineX, drawLineY);
            boxLine.lineTo(drawLineX, drawLineY + (squares * tileSize));
//            boxLine.lineTo(drawLineX, screenHeight + tileSize - (((screenHeight % tileSize) + 1) / 2));
            canvas.drawPath(boxLine, red_paintbrush_stroke);
            drawLineX = drawLineX + tileSize;
        }


           drawLineX = startTilePixelX.getTilePixel();

  //      drawLineX = ((hypotenuse / 2) - (screenWidth / 2) * -1);
//        drawLineX = (tileSize - (((screenWidth % tileSize) + 1) / 2)) * -1;
//        for (int x = 0; x <= (tileCountX + 1); x++) {
        for (int x = 0; x <= squares; x++) {
            boxLine = new Path();
            boxLine.moveTo(drawLineX, drawLineY);
            boxLine.lineTo(drawLineX + (squares * tileSize), drawLineY);
//            boxLine.lineTo(screenWidth + tileSize - (((screenWidth % tileSize) + 1) / 2), drawLineY);
            canvas.drawPath(boxLine, red_paintbrush_stroke);
            drawLineY = drawLineY + tileSize;
        }
        Log.d("sjd4","screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " canvasWidth=" + canvas.getWidth() + " canvasHeight=" + canvas.getHeight());
    }

    private void setUpPaintBrushes(){
        red_paintbrush_fill = new Paint();
        red_paintbrush_fill.setColor(Color.RED);

        red_paintbrush_fill.setStyle(Paint.Style.FILL);

        blue_paintbrush_fill = new Paint();
        blue_paintbrush_fill.setColor(Color.BLUE);
        blue_paintbrush_fill.setStyle(Paint.Style.FILL);

        green_paintbrush_fill = new Paint();
        green_paintbrush_fill.setColor(Color.GREEN);
        green_paintbrush_fill.setStyle(Paint.Style.FILL);

        red_paintbrush_stroke = new Paint();
        red_paintbrush_stroke.setColor(Color.RED);
        red_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        red_paintbrush_stroke.setAlpha(50);
        red_paintbrush_stroke.setStrokeWidth(10);

        blue_paintbrush_stroke = new Paint();
        blue_paintbrush_stroke.setColor(Color.BLUE);
        blue_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        blue_paintbrush_stroke.setAlpha(50);
        blue_paintbrush_stroke.setStrokeWidth(10);

        green_paintbrush_stroke = new Paint();
        green_paintbrush_stroke.setColor(Color.GREEN);
        green_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        green_paintbrush_stroke.setStrokeWidth(10);

        feature_paintbrush_fill = new Paint();
        feature_paintbrush_fill.setStyle(Paint.Style.FILL);
        feature_paintbrush_stroke = new Paint();
        feature_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        feature_paintbrush_stroke.setColor(Color.BLACK);
        feature_paintbrush_stroke.setStrokeWidth(10f);

        compass_paintbrush_stoke = new Paint(Paint.ANTI_ALIAS_FLAG);
        compass_paintbrush_stoke.setColor(Color.RED);
        compass_paintbrush_stoke.setStyle(Paint.Style.STROKE);
        compass_paintbrush_stoke.setStrokeWidth(3);

        compass_text_fill_stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        compass_text_fill_stroke.setColor(Color.BLACK);
        compass_text_fill_stroke.setStyle(Paint.Style.FILL_AND_STROKE);
        float scale = getResources().getDisplayMetrics().scaledDensity;
        compass_text_fill_stroke.setTextSize(17 * scale);
    }
}
