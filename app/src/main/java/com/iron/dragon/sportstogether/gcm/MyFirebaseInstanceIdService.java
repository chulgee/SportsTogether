package com.iron.dragon.sportstogether.gcm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.ui.activity.MainActivity;
import com.iron.dragon.sportstogether.ui.activity.SplashActivity;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;

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
        String regid = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "[token] onTokenRefresh regid="+regid);
        final Context context = getApplicationContext();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            //read phone state사용불가 이후 시퀀스에서 getserverprofile해야함.
            Log.v(TAG, "[token] try to get server profiles later");
        }else{
            Log.v(TAG, "[token] start getting server profiles");
            String deviceid = Util.getDeviceId(getApplicationContext());
            // regid가 업데이트되on면 서버의 regid를 업데이트 시키고,
            // 기존 로컬 프로파일을 제거하고 서버 프로파일을 로컬에 저장한다.
            RetrofitHelper.getServerProfiles(getApplicationContext(), deviceid, regid, new RetrofitHelper.OnViewUpdateListener() {
                @Override
                public void onViewUpdateListener(Response response) {
                    Toast.makeText(getApplicationContext(), "등록ID가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Const.LOCAL_ACTION_GO_TO_MAIN);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }
            });
        }



        /*String regid = FirebaseInstanceId.getInstance().getToken();
        LoginPreferences.GetInstance().SetRegid(context, regid);
        SportsApplication app = (SportsApplication)getApplication();
        app.setRegid(regid);*/

        //RetrofitHelper.updateRegidToServer(getApplicationContext());
    }
}
