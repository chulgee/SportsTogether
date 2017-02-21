package com.iron.dragon.sportstogether.gcm;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.util.Const;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 1단계 등록 담당 서비스
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FidService";

    public MyFirebaseInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        Log.v(TAG, "onTokenRefresh");

        String regid = FirebaseInstanceId.getInstance().getToken();
        LoginPreferences.GetInstance().SetRegid(getApplicationContext(), regid);
        SportsApplication app = (SportsApplication)getApplication();
        app.setRegid(regid);

        RetrofitHelper.updateRegidToServer(getApplicationContext());
    }
}
