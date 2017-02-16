package com.iron.dragon.sportstogether.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by P10950 on 2017-02-15.
 */

public class ConnectivityUtil {

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context){
        NetworkInfo info = getNetworkInfo(context);
        boolean isConnected = (info!=null && info.isConnected());
        if(!isConnected)
            Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
        return isConnected;
    }
}
