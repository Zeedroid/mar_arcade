package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 30/08/2017.
 * Contains a list of system wide variables.
 */

public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.zeedroid.maparcade";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final int WALK  = 1;
    public static final int RIDE  = 2;
    public static final int CYCLE = 3;
    public static final int DRIVE = 4;

    public static final int CREATE_MAP  = 0;
    public static final int MODIFY_MAP  = 1;
    public static final int PUBLISH_MAP = 2;
}
