package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 06/07/2017.
 */

public class PointFeature {

    private String name;
    private String color;

    public PointFeature(){

    }

    public PointFeature(String name, String color){
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        PointFeature pf = (PointFeature)obj;

        return (name == pf.getName() ||
                (name != null && name.equals(pf.getName()))) &&
                (color  == pf.getColor() ||
                        (color  != null && color.equals(pf.getColor())));
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + (null == name ? 0 : name.hashCode());
        hash = 31 * hash + (null == color ? 0 : color.hashCode());

        return hash;
    }
}
