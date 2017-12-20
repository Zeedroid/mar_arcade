package com.zeedroid.maparcade;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Steve Dixon on 13/06/2017.
 */

public final class MercatorProjection {
//    static final int TILE_SIZE = 256;
    static final float minPointSeperationMeters = 10;

    /**
     * To find the global X pixel coordinate for the Longitude, zoom and tile size entered.
     * @param longitude From -180.0 to +180.0 degrees along the equator
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize The size of each tile in pixels
     * @return x Pixel coordinate
     */
    public static double pixelCoordinateX(double longitude, int zoom, int tileSize){
        int scale = 1 << zoom;
        double piX  = Math.floor((tileSize * (0.5 + longitude / 360)) * scale);

        return piX;
    }

    /**
     * To find the global Y pixel coordinate for the Latitude, zoom and tile size entered.
     * @param latitude From -90.0 to +90.0 degrees along the prome meridian
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize The size of each tile in pixels
     * @return y Pixel coordinate
     */
    public static double pixelCoordinateY(double latitude, int zoom, int tileSize){
        int scale = 1 << zoom;
        double siny = Math.sin(latitude * Math.PI / 180);
        siny        = Math.min(Math.max(siny, -0.9999), 0.9999);
        double piY  = Math.floor((tileSize * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))) * scale);

        return piY;
    }

    /**
     * To find the global Y tile coordinate for the Longitude, zoom and tile size entered.
     * @param longitude From -180.0 to +180.0 degrees along the equator
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize The size of each tile in pixels
     * @return x tile coordinate
     */
    public static double tileCoordinateX(double longitude, int zoom, int tileSize){
        int scale = 1 << zoom;
        double piX  = Math.floor((tileSize * (0.5 + longitude / 360)) * scale / tileSize);

        return piX;
    }

    /**
     * To find the global X tile coordinate for the Latitude, zoom and tile size entered.
     * @param latitude From -90.0 to +90.0 degrees along the prome meridian
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize The size of each tile in pixels
     * @return y tile coordinate
     */
    public static double tileCoordinateY(double latitude, int zoom, int tileSize){
        int scale = 1 << zoom;
        double siny = Math.sin(latitude * Math.PI / 180);
        siny        = Math.min(Math.max(siny, -0.9999), 0.9999);
        double piY  = Math.floor((tileSize * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))) * scale / tileSize);

        return piY;
    }

    /**
     * To find the x pixel number within a tile that relates to the longitude, zoom and tile size entered.
     * @param longitude From -180.0 to +180.0 degrees along the equator
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize The size of each tile in pixels
     * @return x pixel number within tile
     */
    public static double tilePixelX(double longitude, int zoom, int tileSize){
//        int scale = 1 << zoom;
//        double piX  = (Math.floor((tileSize * (0.5 + longitude / 360)) * scale) -
        double piX  = (pixelCoordinateX(longitude, zoom, tileSize) -
                      (tileCoordinateX(longitude, zoom, tileSize) * tileSize));
        return piX;
    }

    /**
     * To find the y pixel number within a tile that relates to the latitude, zoom and tile size entered.
     * @param latitude latitude From -90.0 to +90.0 degrees along the prome meridian
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize tileSize The size of each tile in pixels
     * @return y pixel number within tile
     */
    public static double tilePixelY(double latitude, int zoom, int tileSize){
//        int scale = 1 << zoom;
//        double siny = Math.sin(latitude * Math.PI / 180);
//        siny        = Math.min(Math.max(siny, -0.9999), 0.9999);
//        double piY  = (Math.floor((tileSize * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))) * scale) -
        double piY  = (pixelCoordinateY(latitude, zoom, tileSize) -
                      (tileCoordinateY(latitude, zoom, tileSize) * tileSize));
        return piY;
    }

    /**
     * To find the longitude for the x pixel, zoom and tile size entered
     * @param pixelX global x pixel coordinate
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize tileSize The size of each tile in pixels
     * @return longitude
     */
    public static double tilePixelXToLongitude(double pixelX, int zoom, int tileSize){
        double lon  = (pixelX / tileSize) / Math.pow(2.0, zoom)* 360.00 - 180;
         return lon;
    }

    /**
     * To find the latitude for the y pixel, zoom and tile size entered
     * @param pixelY global y pixel coordinate
     * @param zoom The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize tileSize The size of each tile in pixels
     * @return latitude
     */
    public static double tilePixelYToLatitude(double pixelY, int zoom, int tileSize){
        double n = Math.PI - (2.0 * Math.PI * (pixelY / tileSize)) / Math.pow(2.0, zoom);
        double lat = Math.toDegrees(Math.atan(Math.sinh(n)));

        return lat;
    }

    /**
     * To find the Map tile bounding box (Minimum and maximum x and y tile numbers) for the set of coordinates, zoom level and tile size given
     * @param points Array of GPS points congtaining latitude and longitude
     * @param pointsExtra Array og GPS points showing location of features containing latitude and longitude
     * @param zoomLevel The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param tileSize tileSize The size of each tile in pixels
     * @return MapTileBoundingBox containing minumum and maximum x and y tile numbers fop Array of points and features entered.
     */
    public static MapTileBoundingBox mapTileBoundingBox(PointArray points, PointExtraArray pointsExtra, int zoomLevel, int tileSize){
        double minLongitude = points.get(0).getLongitude();
        double minLatitude  = points.get(0).getLatitude();
        double maxLongitude = points.get(0).getLongitude();
        double maxLatitude  = points.get(0).getLatitude();
        for (int j = 0; j < points.size(); j++) {
            Point point = points.get(j);
            if (point.getLongitude() < minLongitude) minLongitude = point.getLongitude();
            if (point.getLatitude() < minLatitude) minLatitude = point.getLatitude();
            if (point.getLongitude() > maxLongitude) maxLongitude = point.getLongitude();
            if (point.getLatitude() > maxLatitude) maxLatitude = point.getLatitude();
        }

        for (int j = 0; j < pointsExtra.size(); j++) {
            Point point = pointsExtra.get(j).getPoint();
            if (point.getLongitude() < minLongitude) minLongitude = point.getLongitude();
            if (point.getLatitude() < minLatitude) minLatitude = point.getLatitude();
            if (point.getLongitude() > maxLongitude) maxLongitude = point.getLongitude();
            if (point.getLatitude() > maxLatitude) maxLatitude = point.getLatitude();
        }

        int minTileCoordinateX = (int)MercatorProjection.tileCoordinateX(minLongitude, zoomLevel, tileSize);
        int minTileCoordinateY = (int)MercatorProjection.tileCoordinateY(minLatitude, zoomLevel, tileSize);
        int maxTileCoordinateX = (int)MercatorProjection.tileCoordinateX(maxLongitude, zoomLevel, tileSize);
        int maxTileCoordinateY = (int)MercatorProjection.tileCoordinateY(maxLatitude, zoomLevel, tileSize);

        return new MapTileBoundingBox(minTileCoordinateX,minTileCoordinateY,maxTileCoordinateX,maxTileCoordinateY,
                                      minLatitude,minLongitude,maxLatitude,maxLongitude);
    }

    /**
     * To find the angle between 2 sets of coordinates.
     * @param fromLat From this latitude
     * @param fromLong From this longitude
     * @param toLat To this latitude
     * @param toLong To this longitude
     * @return Angle
     */
    public static double angleFromCoordinate(double fromLat, double fromLong, double toLat, double toLong) {

        double dLon = (toLong - fromLong);

        double y = Math.sin(dLon)    * Math.cos(toLat);
        double x = Math.cos(fromLat) * Math.sin(toLat) - Math.sin(fromLat)
                                     * Math.cos(toLat) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
 //       brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return brng;
    }



    /**
     * To find the distance between two sets of coordinates in kilometers
     * @param fromLatitude From this latitude
     * @param fromLongitude From this longitude
     * @param toLatitude To this latitude
     * @param toLongitude To this longitude
     * @return distange in kilometers
     */
    public static double distanceBetweenTwoCoordinatesKm(double fromLatitude,
                                                          double fromLongitude,
                                                          double toLatitude,
                                                          double toLongitude){
        double earthRadiusKm = 6371;
        double fromLatRad = degreesToRadians(fromLatitude);
        double fromLonRad = degreesToRadians(fromLongitude);
        double toLatRad   = degreesToRadians(toLatitude);
        double toLonRad   = degreesToRadians(toLongitude);

        double degLat     = toLatRad - fromLatRad;
        double degLon     = toLonRad - fromLonRad;

        double calc1      = Math.sin(degLat/2) *
                            Math.sin(degLat/2) +
                            Math.cos(toLatRad) *
                            Math.cos(fromLatRad) *
                            Math.sin(degLon/2) *
                            Math.sin(degLon/2);

        double calc2      = 2 * Math.atan2(Math.sqrt(calc1),Math.sqrt(1-calc1));

        return earthRadiusKm * calc2;

/*        double earthRadiusKm = 6371;
        double dLatRad = degreesToRadians(toLatitude - fromLatitude);
        double dLonRad = degreesToRadians(toLongitude - fromLongitude);
        double fromLatRad = degreesToRadians(fromLatitude);
        double toLatRad   = degreesToRadians(toLatitude);

        double calc1      = Math.sin(dLatRad/2) *
                            Math.sin(dLatRad/2) +
                            Math.sin(dLonRad/2) *
                            Math.sin(dLonRad/2) *
                            Math.cos(fromLatitude) *
                            Math.cos(toLatitude);
        double calc2      = 2 * Math.atan2(Math.sqrt(calc1),Math.sqrt(1 - calc1));
        return earthRadiusKm * calc2;*/
    }

    /**
     * To find the distance in kilometers for the whole journey taken.
     * @param points Array of coordinates of the journey taken
     * @return distance in kilometers for the whole journey
     */
    public static double distanceOfJourneyKm(PointArray points){
        double distance = 0;
        double lastLatitude, lastLongitude;
        lastLatitude = points.get(0).getLatitude();
        lastLongitude = points.get(0).getLongitude();

        for (int i=1; i < points.size(); i++){
            distance = distance + distanceBetweenTwoCoordinatesKm(lastLatitude, lastLongitude,
                                                                  points.get(i).getLatitude(), points.get(i).getLongitude());
            lastLatitude = points.get(i).getLatitude();
            lastLongitude = points.get(i).getLongitude();
        }
        return distance;
    }

    /**
     * To convery degreed into radians
     * @param degrees number of degrees
     * @return radians
     */
    public static double degreesToRadians(double degrees){
        return (degrees * Math.PI) / 180;
    }

    /**
     * To find the scale in meters per pixel for the given zoom level.
     * @param zoomLevel The level of zoom on the map projection, zoom 0 = whole World shown in 1 tile, zoom 24 is 2^24 tiles
     * @param latitude latitude
     * @return
     */
    public static double mapScaleMetersPerPixel(int zoomLevel, double latitude){
        return (156412 * Math.cos(latitude)) / Math.pow(2,zoomLevel);
    }

    /**
     * Map projection is based on a grid of tiles. This method Calculates the top left MercatorProjection X Tile coordinate
     * and the screen pixel starting point for the top left hand corner of the tile.
     *
     * @return A TileNumberPixel object containing the Mercator projection tile X coordinate, and the x screen pixel starting position for the tile.
     */

    public static TileNumberPixel getStartX(PointArray myJourney, int zoomLevel, int tileSize, int screenWidth, int squares){
        double minPixelX = pixelCoordinateX(myJourney.get(myJourney.size() - 1).getLongitude(), zoomLevel, tileSize);
        double maxPixelX = minPixelX;

        double lastXT    = tileCoordinateX(myJourney.get(myJourney.size() - 1).getLongitude(), zoomLevel, tileSize);
        double lastX     = minPixelX;
        com.zeedroid.maparcade.Point minPointX  = myJourney.get(myJourney.size() - 1);
        com.zeedroid.maparcade.Point maxPointX  = minPointX;

        // Starts from the last point stored
        for (int i = myJourney.size() - 2; i >= 0; i--){
            double currentPixel = pixelCoordinateX(myJourney.get(i).getLongitude(), zoomLevel, tileSize);
            if (currentPixel >= minPixelX && currentPixel <= maxPixelX) continue;

            if (currentPixel < minPixelX) {
                if (maxPixelX - currentPixel > ((screenWidth / 100) * 80)) break;
                minPixelX = currentPixel;
                minPointX = myJourney.get(i);
            }
            if (currentPixel > maxPixelX) {
                if (currentPixel - minPixelX > ((screenWidth / 100) * 80)) break;
                maxPixelX = currentPixel;
                maxPointX = myJourney.get(i);
            }
        }

        int screenTileX     = (int)tileCoordinateX(minPointX.getLongitude(),zoomLevel,tileSize);
        double mapPixelsX   = (maxPixelX - minPixelX) + 1;
        double tilePixelX   = tilePixelX(minPointX.getLongitude(),zoomLevel,tileSize);
        int screenPixelXMin = (int)(((screenWidth - mapPixelsX) * 0.5) - tilePixelX);

        int    boundXStart  = (int)(((squares * tileSize) * 0.5) - (screenWidth * 0.5)) * -1;

        while ((screenPixelXMin - tileSize) >= boundXStart){
            screenPixelXMin = screenPixelXMin - tileSize;
            screenTileX = screenTileX - 1;
        }

        return new TileNumberPixel(screenTileX,screenPixelXMin);
    }

    /**
     * Map projection is based on a grid of tiles. This method Calculates the top left MercatorProjection Y Tile coordinate
     * and the screen pixel starting point for the top left hand corner of the tile.
     *
     * @return A TileNumberPixel object containing the Mercator projection tile y coordinate, and the y screen pixel starting position for the tile.
     */

    public static TileNumberPixel getStartY(PointArray myJourney,  int zoomLevel, int tileSize, int screenHeight, int actionBarHeight, int squares){
        // not using number of squares to calculate correct start pixel.
//        int actionBarHeight = Utilities.getActionBarHeight(application.getContext());
        double minPixelY = pixelCoordinateY(myJourney.get(myJourney.size() - 1).getLatitude(), zoomLevel, tileSize);
        double maxPixelY = minPixelY;

        double lastYT    = tileCoordinateY(myJourney.get(myJourney.size() - 1).getLatitude(), zoomLevel, tileSize);
        double lastY     = minPixelY;
        com.zeedroid.maparcade.Point minPointY  = myJourney.get(myJourney.size() - 1);
        com.zeedroid.maparcade.Point maxPointY  = minPointY;

        for (int i = myJourney.size() - 2; i >= 0; i--){
            double currentPixel = pixelCoordinateY(myJourney.get(i).getLatitude(), zoomLevel, tileSize);
            if (currentPixel >= minPixelY && currentPixel <= maxPixelY) continue;

            if (currentPixel < minPixelY) {
                if (maxPixelY - currentPixel > ((screenHeight / 100) * 80)) break;
                minPixelY = currentPixel;
                minPointY = myJourney.get(i);
            }
            if (currentPixel > maxPixelY) {
                if (currentPixel - minPixelY > ((screenHeight / 100) * 80)) break;
                maxPixelY = currentPixel;
                maxPointY = myJourney.get(i);
            }
        }

        int screenTileY     = (int)tileCoordinateY(minPointY.getLatitude(),zoomLevel,tileSize);
        double mapPixelsY   = (maxPixelY - minPixelY) + 1;
        double tilePixelY   = tilePixelY(minPointY.getLatitude(),zoomLevel,tileSize);
        int screenPixelYMin = (int)(((screenHeight - mapPixelsY) * 0.5) - tilePixelY);
        int    boundYStart  = (int)(((squares * tileSize) * 0.5) - (((screenHeight + actionBarHeight) / 2) + actionBarHeight)) * -1;

        while ((screenPixelYMin - tileSize) >= boundYStart){
            screenPixelYMin = screenPixelYMin - tileSize;
            screenTileY = screenTileY - 1;
        }

        return new TileNumberPixel(screenTileY,screenPixelYMin);
    }

    /**
     * To find the Road Name for the latitude and longitude entered.
     * @param gc Geocoder
     * @param latitude latitude
     * @param longitude longitude
     * @return Roadname object containing road name and post code.
     */
    public static RoadName getPathName(Geocoder gc, double latitude, double longitude) {
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
        }

        return new RoadName(pathName, postCode);
    }

}
