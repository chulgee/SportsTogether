package com.iron.dragon.sportstogether.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by chulchoice on 2017-02-02.
 */

public class UnreadView extends View{

    private Paint paint;
    private RectF rect;
    private int count;

    public UnreadView(Context context) {
        super(context, null);
    }

    public UnreadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        //rect = new RectF();
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        float scale = getResources().getDisplayMetrics().density;
        paint.setTextSize(10.0f*scale);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //rect.set(0,0, getWidth(), getHeight());
        //paint.setColor(Color.WHITE);
        //canvas.drawCircle(30, 30, 40, paint);
        paint.setColor(Color.RED);
        canvas.drawCircle(25, 25, 25, paint);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(""+count, 25, 35, paint);
    }

    public void setUnreadCount(int count){
        this.count = count;
    }
}
