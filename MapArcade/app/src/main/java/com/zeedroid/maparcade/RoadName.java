package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 22/11/2017.
 */

public class RoadName {
    private String roadName;
    private String postCode;

    public RoadName(String roadName, String postCode){
        this.roadName = roadName;
        this.postCode = postCode;
    }

    public RoadName(){}

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
