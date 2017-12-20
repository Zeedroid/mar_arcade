package com.zeedroid.maparcade;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Steve Dixon on 17/07/2017.
 */

public class RouteView extends View {
    Paint paint = new Paint();

    public RouteView(Context context){
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas){
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(200,200,70,paint);
    }
}
