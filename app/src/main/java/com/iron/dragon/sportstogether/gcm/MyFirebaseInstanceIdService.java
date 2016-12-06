package com.iron.dragon.sportstogether.gcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;

/**
 * 1단계 등록 담당 서비스
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FidService";

    public MyFirebaseInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        println("onTokenRefresh");

        String regid = FirebaseInstanceId.getInstance().getToken();
        LoginPreferences.GetInstance().SetRegid(getApplicationContext(), regid);
        SportsApplication app = (SportsApplication)getApplication();
        app.setRegid(regid);
        println("onTokenRefresh regid id : "+regid);

        // regid변경시 서버쪽에 알려주는 루틴 추가 필요
    }

    private void println(String data){
        Log.d(TAG, data);
    }
}
