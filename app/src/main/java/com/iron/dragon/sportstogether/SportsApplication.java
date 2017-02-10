package com.iron.dragon.sportstogether;

import android.app.Application;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.util.Const;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by chulchoice on 2016-11-28.
 */

public class SportsApplication extends Application {
    private Socket socket;
    private String regid;
    private Profile myProfile;
    private static String device_id;


    private static final String TAG = "SportsApplication";

    {
        try {
            socket = IO.socket(Const.MAIN_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public Socket getSocket(){
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public Profile getMyProfile() {
        return myProfile;
    }

    public void setMyProfile(Profile myProfile) {
        this.myProfile = myProfile;
    }

    public static String getDeviceID(){
        return device_id;
    }

    public static void setDeviceID(String id){
        device_id = id;
    }
}
