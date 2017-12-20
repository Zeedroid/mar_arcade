package com.zeedroid.maparcade;

/**
 * Created by Steve Dixon on 03/08/2017.
 */

public class TileNumberPixel {
    private int tileNumber;
    private int tilePixel;

    public TileNumberPixel(int tileNumber, int tilePixel){
        this.tileNumber = tileNumber;
        this.tilePixel = tilePixel;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public void setTileNumber(int tileNumber) {
        this.tileNumber = tileNumber;
    }

    public int getTilePixel() {
        return tilePixel;
    }

    public void setTilePixel(int tilePixel) {
        this.tilePixel = tilePixel;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        TileNumberPixel tnp = (TileNumberPixel) obj;

        return tileNumber == tnp.getTileNumber() &&
               tilePixel  == tnp.getTilePixel();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + tileNumber;
        hash = 31 * hash + tilePixel;

        return hash;
    }
}
