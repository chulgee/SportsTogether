package com.iron.dragon.sportstogether.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

/**
 * Created by seungyong on 2016-11-16.
 */

public class ToastUtil {

    /**
     * 2초
     * @param c
     * @param message
     */
    public static void show(Context c, String message) {
        if(c == null || StringUtil.isEmpty(message))
            return;

        try {
            Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            // ignore
        }
    }

    /**
     * 3.5 초
     * @param c
     * @param message
     */
    public static void showLong(Context c, String message) {
        if(c == null || StringUtil.isEmpty(message))
            return;

        try {
            Toast.makeText(c, message, Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            // ignore
        }
    }

    /**
     * 커스텀..
     * @param c
     * @param message
     * @param miliseconds
     */
    public static void show(final Context c, final String message, int miliseconds) {
        if(c == null || StringUtil.isEmpty(message))
            return;

        int duration = miliseconds;
        if(duration < 2000)
            duration = 2000;

        final Toast t = Toast.makeText(c, message, Toast.LENGTH_SHORT);
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                t.show();
            }

            @Override
            public void onFinish() { }
        }.start();
    }
}