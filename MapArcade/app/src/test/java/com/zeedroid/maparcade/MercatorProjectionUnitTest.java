package com.zeedroid.maparcade;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by User on 02/12/2017.
 */

public class MercatorProjectionUnitTest {

    @Test
    public void pixelCoordinateXTest() throws Exception{
        double[][]data = new double[][]{
                {-0.087890625, 18},
                {-0.086517333984375, 18},
                {-0.087890625, 17},
                {-0.086517333984375, 17}};

        double[] results = new double[]{33538048, 33538304, 16769024, 16769152};

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.pixelCoordinateX(data[i][0], (int) data[i][1], 256);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void pixelCoordinateYTest() throws Exception{
        double[][]data = new double[][]{
                {50.86491125522503, 18},
                {50.86577800109838, 18},
                {50.86491125522503, 17},
                {50.86577800109838, 17}};



        double[] results = new double[]{22506496, 22506240, 11253248, 11253120};

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.pixelCoordinateY(data[i][0], (int) data[i][1], 256);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void tileCoordinateXTest() throws Exception{
        double[][]data = new double[][]{
                {-0.087890625, 18},
                {-0.086517333984375, 18},
                {-0.087890625, 17},
                {-0.086517333984375, 17}};

        double[] results = new double[]{131008, 131009, 65504, 65504};

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.tileCoordinateX(data[i][0], (int) data[i][1], 256);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void tileCoordinateYTest() throws Exception{
        double[][]data = new double[][]{
                {50.86491125522503, 18},
                {50.86577800109838, 18},
                {50.86491125522503, 17},
                {50.86577800109838, 17}};


        double[] results = new double[]{87916, 87915, 43958, 43957};

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.tileCoordinateY(data[i][0], (int) data[i][1], 256);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void tilePixelXTest() throws Exception{
        double[][]data = new double[][]{
                {-0.087890625, 18},
                {-0.086517333984375, 18},
                {-0.087890625, 17},
                {-0.086517333984375, 17}};


        double[] results = new double[]{0, 0, 0, 128};

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.tilePixelX(data[i][0], (int) data[i][1], 256);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void tilePixelYTest() throws Exception{
        double[][]data = new double[][]{
                {50.86491125522503, 18},
                {50.86577800109838, 18},
                {50.86491125522503, 17},
                {50.86577800109838, 17}};


        double[] results = new double[]{0, 0, 0, 128};

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.tilePixelY(data[i][0], (int) data[i][1], 256);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void tilePixelXToLongitudeTest() throws Exception{
         double[][]data = new double[][]{
                 {33538048, 18},
                 {33538304, 18},
                 {16769024, 17},
                 {16769280, 17}};


        double[] results = new double[]{-0.087890625, -0.086517333984375, -0.087890625, -0.08514404296875};

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.tilePixelXToLongitude(data[i][0], (int) data[i][1], 256);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void tilePixelYToLatitudeTest() throws Exception{
        double[][]data = new double[][]{
                {22506240, 18},
                {22506496, 18},
                {11252992, 17},
                {11253248, 17}};

        int tileSize = 256;

        double[] results = new double[]{50.86577800109838, 50.86491125522503, 50.86664473085768, 50.86491125522503 };

        for ( int i=0; i < data.length; i++) {
            double piX = MercatorProjection.tilePixelYToLatitude(data[i][0], (int) data[i][1], tileSize);
            assertEquals(results[i], piX, 0.9);
        }
    }

    @Test
    public void mapTileBoundingBox() throws Exception{
        PointArray points = new PointArray();
        PointExtraArray pointsExtra = new PointExtraArray();

        int[] zoomLevel = new int[]{18,17};
        int tileSize = 256;

        points.add(new com.zeedroid.maparcade.Point(-0.087890625,50.86491125522503));
        points.add(new com.zeedroid.maparcade.Point(-0.086517333984375,50.86577800109838));
        points.add(new com.zeedroid.maparcade.Point(-0.089263916015625,50.86577800109838));
        points.add(new com.zeedroid.maparcade.Point(-0.087890625,50.8664473085768));
        points.add(new com.zeedroid.maparcade.Point(-0.087890625,50.86577880109838));
        points.add(new com.zeedroid.maparcade.Point(-0.086517333984375,50.8664473085768));

        pointsExtra.add(new PointExtra(new com.zeedroid.maparcade.Point(-0.089263916015625,50.86577800109838), "Test Description", "left", "#000000", 0));

        MapTileBoundingBox box18 = new MapTileBoundingBox(131007, 87916, 131009, 87914, 50.86491125522503, -0.089263916015625, 50.8664473085768 ,-0.086517333984375);
        MapTileBoundingBox box17 = new MapTileBoundingBox(65503, 43958, 65504, 43957,50.86491125522503,-0.089263916015625,50.8664473085768,  -0.086517333984375);

        MapTileBoundingBox[] boxes = new MapTileBoundingBox[]{box18, box17};

        for (int i = 0; i < zoomLevel.length; i++) {
            MapTileBoundingBox testBox = MercatorProjection.mapTileBoundingBox(points, pointsExtra, zoomLevel[i], tileSize);

            assertEquals(boxes[i], testBox);
        }
    }

    @Test
    public void angleFromCoordinate() throws Exception{
        double[][]data = new double[][]{
                {50.86491125522503, -0.087890625, 50.86577800109838, -0.086517333984375},
                {50.8664473085768, -0.087890625, 50.86577880109838, -0.087890625}};
        double[] angles = new double[]{307.425, 180};

        for (int i=0; i < data.length; i++){
            double angleTest = MercatorProjection.angleFromCoordinate(data[i][0], data[i][1], data[i][2], data[i][3]);
            assertEquals(angles[i], angleTest, 0.9);
        }
    }

    @Test
    public void distanceBetweenTwoCoordinatesKm() throws Exception{
        double[][]data = new double[][]{
                {50.86491125522503, -0.087890625, 50.86577800109838, -0.086517333984375},
                {50.8664473085768, -0.087890625, 50.86577880109838, -0.087890625}};
        double[] distance = new double[]{0.18057, 0.001};

        for (int i=0; i < data.length; i++){
            double distanceTest = MercatorProjection.distanceBetweenTwoCoordinatesKm(data[i][0], data[i][1], data[i][2], data[i][3]);
            assertEquals(distance[i], distanceTest, 0.1);
        }
    }

    @Test
    public void distanceOfJourneyKm() throws Exception{
        PointArray points = new PointArray();

        points.add(new com.zeedroid.maparcade.Point(-0.087890625,50.86491125522503));
        points.add(new com.zeedroid.maparcade.Point(-0.086517333984375,50.86577800109838));
        points.add(new com.zeedroid.maparcade.Point(-0.089263916015625,50.86577800109838));
        points.add(new com.zeedroid.maparcade.Point(-0.087890625,50.8664473085768));
        points.add(new com.zeedroid.maparcade.Point(-0.087890625,50.86577880109838));
        points.add(new com.zeedroid.maparcade.Point(-0.086517333984375,50.8664473085768));

        double distance = MercatorProjection.distanceOfJourneyKm(points);

        assertEquals(0.646867, distance, 0.001);
    }

    @Test
    public void degreesToRadians() throws Exception{
        double[][] data = new double[][]{
                {0, 0},
                {45, 0.7853981634},
                {90, 1.5707963268},
                {180, 3.1415926536},
                {360, 6.2831853072}};

        for (int i=0; i < data.length; i++){
            double rad = MercatorProjection.degreesToRadians(data[i][0]);
            assertEquals(data[i][1], rad, 0.1);
        }
    }

    @Test
    public void mapScaleMetersPerPixel() throws Exception{
        int[] zoomLevel = new int[]{10, 14, 18};
        double[] result = new double[]{126.116, 7.88225, 0.49264};


        for (int i=0; i < zoomLevel.length; i++){
            double rad = MercatorProjection.mapScaleMetersPerPixel(zoomLevel[i], 50.86491125522503);
            assertEquals(result[i], rad, 0.1);
        }
    }
}
