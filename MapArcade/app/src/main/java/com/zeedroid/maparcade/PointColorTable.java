package com.zeedroid.maparcade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Steve Dixon on 01/07/2017.
 */

public final class PointColorTable {

    public static final String startPoint = "#00ff00";    // green
    public static final String endPoint   = "#ff0000";    // red

    static String[] colors = {
                    "#00ffff",          //aqua:
                    "#f0ffff",          //azure:
                    "#f5f5dc",          //beige:
                    "#000000",          //black:
                    "#0000ff",          //blue:
                    "#a52a2a",          //brown:
                    "#00ffff",          //cyan:
                    "#00008b",          //darkblue:
                    "#008b8b",          //darkcyan:
                    "#a9a9a9",          //darkgrey:
                    "#006400",          //darkgreen:
                    "#bdb76b",          //darkkhaki:
                    "#8b008b",          //darkmagenta:
                    "#556b2f",          //darkolivegreen:
                    "#ff8c00",          //darkorange:
                    "#9932cc",          //darkorchid:
                    "#8b0000",          //darkred:
                    "#e9967a",          //darksalmon:
                    "#9400d3",          //darkviolet:
                    "#ff00ff",          //fuchsia:
                    "#ffd700",          //gold:
                    "#008000",          //green:
                    "#4b0082",          //indigo:
                    "#f0e68c",          //khaki:
                    "#add8e6",          //lightblue:
                    "#e0ffff",          //lightcyan:
                    "#90ee90",          //lightgreen:
                    "#d3d3d3",          //lightgrey:
                    "#ffb6c1",          //lightpink:
                    "#ffffe0",          //lightyellow:
                    "#00ff00",          //lime:
                    "#ff00ff",          //magenta:
                    "#800000",          //maroon:
                    "#000080",          //navy:
                    "#808000",          //olive:
                    "#ffa500",          //orange:
                    "#ffc0cb",          //pink:
                    "#800080",          //purple:
                    "#800080",          //violet:
                    "#c0c0c0",          //silver:
                    "#ffffff",          //white:
                    "#ffff00"           //yellow:
    };

    public static List<String> randomColors(int n) {
        List<String> list = new ArrayList<String>(Arrays.asList(colors));
        Collections.shuffle(list);
        list = list.subList(0,n);
        return list;
    }

    public static String getAColor(int n){
        return colors[n];
    }
}
