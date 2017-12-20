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
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by User on 18/10/2017.
 */

public class ModifyMapLayout extends View{
    private Paint feature_paintbrush_fill, red_paintbrush_fill, blue_paintbrush_fill, green_paintbrush_fill, black_paintbrush_fill;
    private Paint feature_paintbrush_stroke, red_paintbrush_stroke, blue_paintbrush_stroke, green_paintbrush_stroke, black_paintbrush_stroke;
    private Path route, routeText;
    private android.graphics.Point feature;
    private Point pointA, pointB;

    private int tileCountX, tileCountY;
    private int tileSize;
    private String mapType;
//    private boolean creatingMap = true;

    private Context context;
    private ModifyMapModel viewModel;
    private SharedPreferences prefs;
    private TileNumberPixel startTilePixelX, startTilePixelY;
    private int drawLineX, drawLineY;
    private int tileStartX, tileStartY;
    private TileCoordinates tc;

    private Paint compass_paintbrush_stoke, compass_text_fill_stroke;
    private String[] compassPoints;

//    private Button deleteButton, undoButton, joinButton;

    public ModifyMapLayout(Context context) {
        super(context);
        this.context = context;
        Log.d("sjd","ModifyMapLayout");
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tileSize = Integer.parseInt(prefs.getString("map_tile_size", "512"));

        compassPoints = getResources().getStringArray(R.array.compass_points);
        setUpPaintBrushes();
//        deleteButton   = ModifyMapLayout.this.findViewById(R.id.deleteButton);
//        undoButton     = ModifyMapLayout.this.findViewById(R.id.undoButton);
//        joinButton     = ModifyMapLayout.this.findViewById(R.id.joinButton);


    }

    public ModifyMapLayout (Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        Log.d("sjd","ModifyMapLayout");
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tileSize = Integer.parseInt(prefs.getString("map_tile_size", "512"));

        compassPoints = getResources().getStringArray(R.array.compass_points);
        setUpPaintBrushes();
//        deleteButton   = ModifyMapLayout.this.findViewById(R.id.deleteButton);
//        undoButton     = ModifyMapLayout.this.findViewById(R.id.undoButton);
//        joinButton     = ModifyMapLayout.this.findViewById(R.id.joinButton);
    }

    public void setViewModel(ModifyMapModel viewModel){
        this.viewModel = viewModel;
    }

    private Camera camera = new Camera();
    private Matrix matrix = new Matrix();

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


/*    public void setCreatingMap(boolean creatingMap){
        this.creatingMap = creatingMap;
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        tileCountX = (viewModel.getScreenWidth() / tileSize) + 2;
        tileCountY = (viewModel.getScreenHeight() / tileSize) + 2;
        int[] tileNumbers = new int[tileCountX * tileCountY];
        int[][] mapTiles = new int[tileCountX][tileCountY];

//      camera.save();
        canvas.save();
//        if (viewModel.isMapBeingCreated()) changeCameraAngle(canvas);

        if (prefs.getBoolean("map_show_zoom_level", false)) {

//            canvas.save();
            drawZoomLevel(canvas);
//            canvas.restore();
        }
        if (viewModel.foundJourneyAndFeatures()) {

            if (prefs.getBoolean("map_show_tile_grid", false)) {
//             canvas.save();
                drawTileLines(canvas);
//                canvas.restore();
            }

//        canvas.save();
            drawTileNumbers(canvas);
//        canvas.restore();

//        drawCompassRose(canvas);

//        canvas.save();
            drawPoints(canvas);
//        canvas.restore();

            if (viewModel.isShowingRoads()) {
                drawRoadNames(canvas);
            }

//        canvas.save();
            drawPointsExtra(canvas);
//        canvas.restore();

//        changeCameraAngle(canvas);
//        camera.restore();

//
            drawNodePoints(canvas);

        }
        canvas.restore();
//        matrix.preTranslate(-screenWidth/2,-screenHeight/2);
//        matrix.postTranslate(screenWidth/2, screenHeight/2);

    }

/*    private void drawCompassRose(Canvas canvas) {
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());
        int centerX;
        int centerY;

        int width;
        int height;

        if (viewModel.getPoints().size() < 2) return;

        if (viewModel.getScreenWidth() < viewModel.getScreenHeight()){
            width = viewModel.getScreenWidth();
            height = viewModel.getScreenHeight();
        }else {
            width = viewModel.getScreenHeight();
            height = viewModel.getScreenWidth();
        }

        centerX = viewModel.getScreenWidth() / 2;
        centerY = ((viewModel.getScreenHeight() + actionBarHeight) / 2) + actionBarHeight;

        canvas.drawPath(viewModel.getCompass(),compass_paintbrush_stoke);

        for (int i=0; i < 24; i++){
            canvas.drawLine(centerX, centerY - ((width / 2) - 30), centerX, centerY - ((width / 2) - 70),compass_paintbrush_stoke);

            if (i % 6 == 0){
                float  angleLength = compass_text_fill_stroke.measureText(compassPoints[(i / 6)]);
                canvas.drawText(compassPoints[(i / 6)], centerX - (angleLength / 2), centerY - ((width / 2) - 130), compass_text_fill_stroke);
            }else if (i % 3 == 0){
                String angle = String.valueOf(i * 15);
                float  angleLength = compass_text_fill_stroke.measureText(angle);
                canvas.drawText(angle, centerX - (angleLength / 2), centerY - ((width / 2) - 130),compass_text_fill_stroke);
            }
            canvas.rotate(15,centerX, centerY);
        }
        Log.d("sjd4","AAAscreenWidth=" + viewModel.getScreenWidth() + " screenHeight=" + viewModel.getScreenHeight() + " canvasWidth=" + canvas.getWidth() + " canvasHeight=" + canvas.getHeight());
    }*/

 /*   private void changeCameraAngle(Canvas canvas) {
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());
        if (viewModel.getPoints().size() < 2) return;
        Point pointFrom = viewModel.getPoints().get(viewModel.getPoints().size() - 2);
        Point pointTo = viewModel.getPoints().get(viewModel.getPoints().size() - 1);

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

        matrix.preTranslate(viewModel.getScreenWidth() / 2, ((viewModel.getScreenHeight() + actionBarHeight) / 2) + actionBarHeight);
        matrix.postTranslate(-(viewModel.getScreenWidth() / 2), -((viewModel.getScreenHeight() + actionBarHeight) / 2) + actionBarHeight);
        matrix.setRotate((float) brng, viewModel.getScreenWidth() / 2, ((viewModel.getScreenHeight() + actionBarHeight) / 2) + actionBarHeight);

        canvas.setMatrix(matrix);
    }*/

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

    private void drawPoints(Canvas canvas) {
        boolean wasOffScreen = false;
        Point point = viewModel.getJourney().get(0);

        pointA = point;
        TileCoordinates tc = viewModel.getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), viewModel.getZoomLevel(), tileSize),
                MercatorProjection.tileCoordinateY(point.getLatitude(), viewModel.getZoomLevel(), tileSize));
        double tileStartX = tc.getScreenPixelX();
        double tileStartY = tc.getScreenPixelY();

        double pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), viewModel.getZoomLevel(), tileSize);
        double pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), viewModel.getZoomLevel(), tileSize);
        route = new Path();
        route.moveTo((float) pixelX, (float) pixelY);
        setUpPaintBrushes();
        red_paintbrush_stroke.setStrokeWidth(20f);
        for (int i = 1; i < viewModel.getJourney().size(); i++) {
            point = viewModel.getJourney().get(i);
            pointB = pointA;
            pointA = point;
            tc = viewModel.getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), viewModel.getZoomLevel(), tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), viewModel.getZoomLevel(), tileSize));
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
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), viewModel.getZoomLevel(), tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), viewModel.getZoomLevel(), tileSize);
                route.lineTo((float) pixelX, (float) pixelY);
//                route.moveTo((float) pixelX, (float) pixelY);
            }
        }
        canvas.drawPath(route, red_paintbrush_stroke);
        route.moveTo((float) pixelX, (float) pixelY);
    }

    private void drawPointsExtra(Canvas canvas) {
        Point point;
        TileCoordinates tc;
        double tileStartX, tileStartY;
        double pixelX, pixelY;
        viewModel.recalibratePointsExtra();
        for (int j = 0; j < viewModel.getFeatures().size(); j++) {
            point = viewModel.getFeatures().get(j).getPoint();
            String pointColor = viewModel.getFeatures().get(j).getPointColor();
            tc = viewModel.getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), viewModel.getZoomLevel(), tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), viewModel.getZoomLevel(), tileSize));
            if (tc.getTileX() != 0 && tc.getTileY() != 0) {
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), viewModel.getZoomLevel(), tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), viewModel.getZoomLevel(), tileSize);
                feature = new android.graphics.Point((int) pixelX, (int) pixelY);

             /* need to calculate point to left or right of path depending on direction from pointA to pointB  */

                canvas.drawCircle((float) pixelX, (float) pixelY, 50, feature_paintbrush_stroke);
                feature_paintbrush_fill.setColor((Color.parseColor(pointColor) & 0xfefefe) >> 1);
                canvas.drawCircle((float) pixelX, (float) pixelY, 49, feature_paintbrush_fill);
                feature_paintbrush_fill.setColor(Color.parseColor(pointColor));
                canvas.drawCircle((float) pixelX, (float) pixelY, 45, feature_paintbrush_fill);
            }
        }
    }

    private void drawNodePoints(Canvas canvas) {
        Point point;
        TileCoordinates tc;
        double tileStartX, tileStartY;
        double pixelX, pixelY;
        String pointColor;
        viewModel.recalibrateNodePoints();
        for (int j = 0; j < viewModel.getJourney().size(); j++) {
            point = viewModel.getJourney().get(j);
            if (point.isSelected()) pointColor = "#ffff00";
            else pointColor = "#0000ff";
            tc = viewModel.getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), viewModel.getZoomLevel(), tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), viewModel.getZoomLevel(), tileSize));
            if (tc.getTileX() != 0 && tc.getTileY() != 0) {
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), viewModel.getZoomLevel(), tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), viewModel.getZoomLevel(), tileSize);
//                feature = new android.graphics.Point((int) pixelX, (int) pixelY);

             /* need to calculate point to left or right of path depending on direction from pointA to pointB  */

                canvas.drawCircle((float) pixelX, (float) pixelY, 20, feature_paintbrush_stroke);
                feature_paintbrush_fill.setColor((Color.parseColor(pointColor) & 0xfefefe) >> 1);
                canvas.drawCircle((float) pixelX, (float) pixelY, 19, feature_paintbrush_fill);
                feature_paintbrush_fill.setColor(Color.parseColor(pointColor));
                canvas.drawCircle((float) pixelX, (float) pixelY, 15, feature_paintbrush_fill);
            }
        }
    }

    private void drawZoomLevel(Canvas canvas) {
        int actionBarHeight = Utilities.getActionBarHeight((Activity)getContext());

        blue_paintbrush_fill.setTextSize(800f);
        blue_paintbrush_fill.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + viewModel.getZoomLevel(), viewModel.getScreenWidth() / 2, ((viewModel.getScreenHeight() + actionBarHeight) / 2) + actionBarHeight, blue_paintbrush_fill);
    }

    private void drawTileNumbers(Canvas canvas) {
         int margin = 0;
        int gap1   = 0;
        int gap2   = 0;
//        if (viewModel.getZoomLevel() < 18) red_paintbrush_fill.setTextSize(60f);
//        else red_paintbrush_fill.setTextSize(50f);

        switch (tileSize){
            case 128:
                      black_paintbrush_fill.setTextSize(30f);
                      margin = 10;
                      gap1   = 30;
                      gap2   = 80;
                      break;
            case 256:
                      black_paintbrush_fill.setTextSize(60f);
                      margin = 20;
                      gap1   = 50;
                      gap2   = 140;
                      break;
            case 512:
                      black_paintbrush_fill.setTextSize(70f);
                      margin = 40;
                      gap1   = 75;
                      gap2   = 240;
                      break;
            default:
                black_paintbrush_fill.setTextSize(70f);
                margin = 40;
                gap1   = 75;
                gap2   = 240;
                break;
        }

        black_paintbrush_fill.setTextAlign(Paint.Align.LEFT);

/*        if (viewModel.isMapBeingCreated()) {
            startTilePixelX = viewModel.getStartX();
            startTilePixelY = viewModel.getStartY();
            drawLineX       = startTilePixelX.getTilePixel();
            drawLineY       = startTilePixelY.getTilePixel();
            tileStartX      = startTilePixelX.getTileNumber();
            tileStartY      = startTilePixelY.getTileNumber();
            viewModel.setTileCoordinates(viewModel.getStartX(), viewModel.getStartY());
          }else{*/
            tc                 = viewModel.getTileCoordinates().get(0);
            drawLineX          = (int)tc.getScreenPixelX();
            drawLineY          = (int)tc.getScreenPixelY();
            tileStartX         = (int)tc.getTileX();
            tileStartY         = (int)tc.getTileY();
//        }

        for (int x = 0; x < viewModel.getSquares(); x++) {
            for (int y = 0; y < viewModel.getSquares(); y++) {
                if (prefs.getBoolean("map_show_tile_grid", false)) {
                    canvas.drawText("" + (tileStartX + x) + ",", drawLineX + margin + (tileSize * x), drawLineY + gap1 + (tileSize * y), black_paintbrush_fill);
                    canvas.drawText("" + (tileStartY + y), drawLineX + margin + (tileSize * x), drawLineY + gap2 + (tileSize * y), black_paintbrush_fill);
                }
            }
        }
    }

    private void drawTileLines(Canvas canvas) {
/*        if (viewModel.isMapBeingCreated()) {
            startTilePixelX = viewModel.getStartX();
            startTilePixelY = viewModel.getStartY();
            drawLineX       = startTilePixelX.getTilePixel();
            drawLineY       = startTilePixelY.getTilePixel();
          }else{*/
            tc              = viewModel.getTileCoordinates().get(0);
            drawLineX       = (int)tc.getScreenPixelX();
            drawLineY       = (int)tc.getScreenPixelY();
 //       }

        Path boxLine;

        for (int y = 0; y <= viewModel.getSquares(); y++) {
            boxLine = new Path();
            boxLine.moveTo(drawLineX, drawLineY);
            boxLine.lineTo(drawLineX, drawLineY + (viewModel.getSquares() * tileSize));
            canvas.drawPath(boxLine, black_paintbrush_stroke);
            drawLineX = drawLineX + tileSize;
        }

/*        if (viewModel.isMapBeingCreated()) {
            drawLineX = startTilePixelX.getTilePixel();
          }
        else {*/
            drawLineX = (int)tc.getScreenPixelX();
//        }

        for (int x = 0; x <= viewModel.getSquares(); x++) {
            boxLine = new Path();
            boxLine.moveTo(drawLineX, drawLineY);
            boxLine.lineTo(drawLineX + (viewModel.getSquares() * tileSize), drawLineY);
            canvas.drawPath(boxLine, black_paintbrush_stroke);
            drawLineY = drawLineY + tileSize;
        }
    }

    private void drawRoadNames(Canvas canvas) {
        boolean wasOffScreen = false;
        Point point = viewModel.getJourney().get(0);
        pointA = point;
        TileCoordinates tc = viewModel.getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), viewModel.getZoomLevel(), tileSize),
                MercatorProjection.tileCoordinateY(point.getLatitude(), viewModel.getZoomLevel(), tileSize));
        double tileStartX = tc.getScreenPixelX();
        double tileStartY = tc.getScreenPixelY();

        double pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), viewModel.getZoomLevel(), tileSize);
        double pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), viewModel.getZoomLevel(), tileSize);

        routeText = new Path();
        routeText.moveTo((float) pixelX, (float) pixelY);
        red_paintbrush_stroke.setStrokeWidth(20f);

        int currentPathID = point.getPathID();
        String currentRoad   = viewModel.getRoadName(point.getPathID());
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        for (int i = 1; i < viewModel.getJourney().size(); i++) {
            point = viewModel.getJourney().get(i);
            pointB = pointA;
            pointA = point;
            if (currentPathID != 0 && currentPathID != point.getPathID()) {
                if (currentRoad != "") {
//                  routeText.close();
                    canvas.drawPath(routeText, red_paintbrush_stroke);
                    canvas.drawTextOnPath(viewModel.getRoadName(currentPathID), routeText, 50, -20, compass_text_fill_stroke);
                    routeText.moveTo((float) pixelX, (float) pixelY);
//                  setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                routeText = new Path();
                routeText.moveTo((float) pixelX, (float) pixelY);

                currentPathID = point.getPathID();
                currentRoad   = viewModel.getRoadName(point.getPathID());
            }
            tc = viewModel.getTileCoordinate(MercatorProjection.tileCoordinateX(point.getLongitude(), viewModel.getZoomLevel(), tileSize),
                    MercatorProjection.tileCoordinateY(point.getLatitude(), viewModel.getZoomLevel(), tileSize));
            if (tc.getTileX() == 0 && tc.getTileY() == 0) {
                if (!routeText.isEmpty()) {
                    if (currentPathID != 0) {
//                      routeText.close();
                        canvas.drawPath(routeText, red_paintbrush_stroke);
                        canvas.drawTextOnPath(viewModel.getRoadName(currentPathID), routeText, 50, -20, compass_text_fill_stroke);
                        routeText.moveTo((float) pixelX, (float) pixelY);
                    }
                    currentPathID = point.getPathID();
                    routeText = new Path();
                    routeText.moveTo((float) pixelX, (float) pixelY);
                    currentRoad   = viewModel.getRoadName(point.getPathID());
                }
                wasOffScreen = true;
                continue;
            } else {
                if (wasOffScreen) {
                    wasOffScreen = false;
                    routeText.moveTo((float) pixelX, (float) pixelY);
                    continue;
                }
                currentPathID = point.getPathID();
                tileStartX = tc.getScreenPixelX();
                tileStartY = tc.getScreenPixelY();
                pixelX = tileStartX + MercatorProjection.tilePixelX(point.getLongitude(), viewModel.getZoomLevel(), tileSize);
                pixelY = tileStartY + MercatorProjection.tilePixelY(point.getLatitude(), viewModel.getZoomLevel(), tileSize);
//              routeText.moveTo((float) pixelX, (float) pixelY);
                routeText.lineTo((float) pixelX, (float) pixelY);

            }
        }
        if (currentPathID != 0) {
//          routeText.close();
            canvas.drawPath(routeText, red_paintbrush_stroke);
            canvas.drawTextOnPath(viewModel.getRoadName(currentPathID), routeText, 50, -20, compass_text_fill_stroke);
        }
        setLayerType(View.LAYER_TYPE_NONE, null);
    }

    private void setUpPaintBrushes(){
        black_paintbrush_fill = new Paint();
        black_paintbrush_fill.setColor(Color.BLACK);
        black_paintbrush_fill.setStyle(Paint.Style.FILL);
        black_paintbrush_fill.setAlpha(60);

        red_paintbrush_fill = new Paint();
        red_paintbrush_fill.setColor(Color.RED);
        red_paintbrush_fill.setStyle(Paint.Style.FILL);

        blue_paintbrush_fill = new Paint();
        blue_paintbrush_fill.setColor(Color.BLUE);
        blue_paintbrush_fill.setStyle(Paint.Style.FILL);

        green_paintbrush_fill = new Paint();
        green_paintbrush_fill.setColor(Color.GREEN);
        green_paintbrush_fill.setStyle(Paint.Style.FILL);

        black_paintbrush_stroke = new Paint();
        black_paintbrush_stroke.setColor(Color.BLACK);
        black_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        black_paintbrush_stroke.setAlpha(20);
        black_paintbrush_stroke.setStrokeWidth(5);

        red_paintbrush_stroke = new Paint();
        red_paintbrush_stroke.setColor(Color.RED);
        red_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        red_paintbrush_stroke.setAlpha(80);
        red_paintbrush_stroke.setStrokeWidth(5);

        blue_paintbrush_stroke = new Paint();
        blue_paintbrush_stroke.setColor(Color.BLUE);
        blue_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        blue_paintbrush_stroke.setAlpha(50);
        blue_paintbrush_stroke.setStrokeWidth(10);

        green_paintbrush_stroke = new Paint();
        green_paintbrush_stroke.setColor(Color.GREEN);
        green_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        green_paintbrush_stroke.setStrokeWidth(20);

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
