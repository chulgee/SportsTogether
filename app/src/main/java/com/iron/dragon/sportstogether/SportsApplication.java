package com.iron.dragon.sportstogether;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.Const;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by chulchoice on 2016-11-28.
 */

public class SportsApplication extends Application {
    private static final String TAG = "SportsApplication";
    private Socket socket;
    private String regid;
    private Profile myProfile;


    {
        try {
            socket = IO.socket(Const.MAIN_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if(activity instanceof ChatActivity){
                    Log.v(TAG, "onActivityCreated: "+activity.toString());//+" name: "+((ChatActivity)activity).getBuddy().getUsername());
                    //Toast.makeText(SportsApplication.this, "onActivityCreated: "+activity.toString()+" name: "+((ChatActivity)activity).getBuddy().getUsername(), Toast.LENGTH_SHORT).show();
                    //mChatRoom.put(((ChatActivity)activity).getBuddy().getUsername(), activity);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if(activity instanceof ChatActivity) {
                    Log.v(TAG, "onActivityDestroyed: "+activity.toString());//+" name: "+((ChatActivity)activity).getBuddy().getUsername());
                    //Toast.makeText(SportsApplication.this, "onActivityDestroyed: "+activity.toString()+" name: "+((ChatActivity)activity).getBuddy().getUsername(), Toast.LENGTH_SHORT).show();
                    //mChatRoom.remove(((ChatActivity) activity).getBuddy().getUsername());
                }
            }
        });
    }
}
