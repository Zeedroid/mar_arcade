package com.zeedroid.maparcade;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by Steve Dixon on 24/07/2017.
 */

public class Utilities {

    public static int getStatusBarHeight(Activity activity) {
        // status bar height
        int statusBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static int getActionBarHeight(Activity activity) {
        // action bar height
        int actionBarHeight = 0;
        final TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize}
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarHeight;
    }

    public static int getNavigationBarHeight(Activity activity) {
        // navigation bar height
        int navigationBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    public static void slideDown(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if (animation != null){
            if (view != null){
                view.clearAnimation();
                view.startAnimation(animation);
            }
        }
    }

    public static void slideUp(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if (animation != null){
            if (view != null){
                view.clearAnimation();
                view.startAnimation(animation);
            }
        }
    }
}
