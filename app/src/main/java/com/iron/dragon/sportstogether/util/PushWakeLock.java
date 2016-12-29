package com.iron.dragon.sportstogether.util;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by P10950 on 2016-12-29.
 */

public class PushWakeLock {

    private static final String TAG = "PushWakeLock";
    private static PowerManager.WakeLock sWakeLock;

    public static void acquireWakeLock(Context context, int time){
        Log.v(TAG, "acquireWakeLock sWakeLock="+sWakeLock);
        if(sWakeLock != null && sWakeLock.isHeld()) {
            return;
        }
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        sWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "my lock");
        sWakeLock.acquire(time);    }

    public static void releaseWakeLock(){
        Log.v(TAG, "releaseWakeLock sWakeLock="+sWakeLock);
        if(sWakeLock.isHeld()){
            sWakeLock.release();
            sWakeLock = null;
        }
    }
}
