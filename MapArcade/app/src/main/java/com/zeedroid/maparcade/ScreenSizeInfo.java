package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 21/08/2017.
 */

public class ScreenSizeInfo {
    int screenWidth;
    int screenHeight;
    int actionBarHeight;
    int statusBarHeight;

    public ScreenSizeInfo(int screenWidth, int screenHeight, int actionBarHeight, int statusBarHeight){
        this.screenWidth     = screenWidth;
        this.screenHeight    = screenHeight;
        this.actionBarHeight = actionBarHeight;
        this.statusBarHeight = statusBarHeight;
    }
}
