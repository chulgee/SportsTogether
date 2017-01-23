package com.iron.dragon.sportstogether.gcm;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
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
        println("onTokenRefresh");

        String regid = FirebaseInstanceId.getInstance().getToken();
        LoginPreferences.GetInstance().SetRegid(getApplicationContext(), regid);
        SportsApplication app = (SportsApplication)getApplication();
        app.setRegid(regid);
        println("onTokenRefresh regid id : "+regid);

        sendRegidToServer(getApplicationContext(), regid);
    }

    static public void sendRegidToServer(final Context context, final String regid){
        Log.v(TAG, "sendRegidToServer id :" + regid);
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
        Profile profile = new Profile();
        profile.setRegid(regid);
        //String curRegid = LoginPreferences.GetInstance().getLocalProfile(context).getRegid();   what??
        String curRegid = LoginPreferences.GetInstance().GetRegid(context);
        final Call<Profile> call = gitHubService.putProfilesRegid(curRegid, profile);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());

                if (response.isSuccessful()) {
                    LoginPreferences.GetInstance().SetRegid(context, regid);
                } else {
                    Log.v(TAG, "onResponse set regid failed!");
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    private void println(String data){
        Log.d(TAG, data);
    }
}
