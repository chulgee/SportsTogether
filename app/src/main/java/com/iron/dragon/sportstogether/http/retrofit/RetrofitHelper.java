package com.iron.dragon.sportstogether.http.retrofit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.CallbackWithExists;
import com.iron.dragon.sportstogether.http.RetryableCallback;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.ConnectivityUtil;
import com.iron.dragon.sportstogether.util.Const;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P16018 on 2016-12-27.
 */

public class RetrofitHelper {

    public static final int DEFAULT_RETRIES = 3;

    public static final int SUCCESS = 1;
    public static final int EXISTS = 2;
    public static final int FAILURE = 3;
    private static final String TAG = "RetrofitHelper";

    public interface ProfileListener{
        public void onLoaded(Profile profile);
    }

    public interface VersionListener{
        public void onLoaded(int versioncode);
        public void onFailed();
    }

    public static <T> void enqueueWithRetry(Call<T> call,  final int retryCount,final Callback<T> callback) {
        call.enqueue(new RetryableCallback<T>(call, retryCount) {

            @Override
            protected void onFinalExists(Call<T> call, Response<T> response) {
            }

            @Override
            public void onFinalResponse(Call<T> call, Response<T> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFinalFailure(Call<T> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public static <T> void enqueueWithRetry(Call<T> call, final Callback<T> callback) {
        enqueueWithRetry(call, DEFAULT_RETRIES, callback);
    }


    public static <T> void enqueueWithRetryAndExist(Call<T> call, final int retryCount, final CallbackWithExists<T> callback) {
        call.enqueue(new RetryableCallback<T>(call, retryCount) {

            @Override
            protected void onFinalExists(Call<T> call, Response<T> response) {
                callback.onExists(call, response);
            }

            @Override
            public void onFinalResponse(Call<T> call, Response<T> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFinalFailure(Call<T> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
    public static <T> void enqueueWithRetryAndExist(Call<T> call, final CallbackWithExists<T> callback) {
        enqueueWithRetryAndExist(call, DEFAULT_RETRIES, callback);
    }


    public static int isCallSuccess(Response response) {
        int code = response.code();
        Logger.d("isCallSuccess = " + code);
        if(code >= 200 && code < 400) {
            return SUCCESS;
        } else if(code == 409) {
            return EXISTS;
        } else {
            return FAILURE;
        }
    }

    public static void loadProfile(final Context context, final Profile me, final String username, int sportsid, int locationid, final ProfileListener cb) {

        if(!ConnectivityUtil.isConnected(context))
            return;

        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final Call<Profile> call = retrofit.getProfile(username, sportsid, locationid);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d(TAG, "response.body() = " + (response.body()!=null?response.body().toString():null));
                        Log.d(TAG, "response.message() = " + response.message());
                        Profile profile = response.body();
                        if(profile!=null && profile.getUsername() == null){
                            Toast.makeText(context, username+"님의 프로필이 서버에 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                        }else
                            cb.onLoaded(profile);
                    }else
                        Toast.makeText(context, "데이터가 없습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
                Toast.makeText(context, "서버로부터 응답이 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getServerVersion(final Context context, final VersionListener cb){

        if(!ConnectivityUtil.isConnected(context))
            return;

        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final Call<Integer> call = retrofit.getVersion();
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d(TAG, "response.body() = " + (response.body()!=null?response.body().toString():null));
                        Log.d(TAG, "response.message() = " + response.message());
                        int versioncode = response.body();
                        cb.onLoaded(versioncode);
                    }else
                        Toast.makeText(context, "데이터가 없습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
                Toast.makeText(context, "서버로부터 응답이 없습니다", Toast.LENGTH_SHORT).show();
                cb.onFailed();
            }
        });
    }

    public static void updateRegidToServer(final Context context){
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final String old_regid = LoginPreferences.GetInstance().GetRegid(context);
        final String new_regid = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "old_regid="+old_regid);
        Log.v(TAG, "new_regid="+new_regid);
        if(old_regid.equals(new_regid) == false){
            final Call<Profile> call = gitHubService.putProfilesRegid(old_regid, new_regid);
            call.enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());

                    if (response.isSuccessful()) {
                        LoginPreferences.GetInstance().SetRegid(context, new_regid);
                    } else {
                        Log.v(TAG, "onResponse set regid failed!");
                    }
                }

                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                    Log.d("Test", "error message = " + t.getMessage());
                    Toast.makeText(context, "서버로부터 응답이 없습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }else
            Log.d(TAG, "regid 동일함. 업데이트 필요없음" );
    }

    public interface OnProfileListener{
        public void onProfileLoaded();
    }
    public static void getServerProfiles(final Context context, String deviceid, String regid, final OnProfileListener cb){
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final Call<List<Profile>> call = retrofit.getProfilesForDeviceId(deviceid, regid);
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d(TAG, "response.body() = " + (response.body()!=null?response.body().toString():null));
                        Log.d(TAG, "response.message() = " + response.message());
                        List<Profile> profiles = response.body();
                        if(profiles!=null && profiles.size()>0){
                            for(Profile p : profiles) {
                                LoginPreferences.GetInstance().SetLogout(context, p.getSportsid());
                                LoginPreferences.GetInstance().saveSharedPreferencesProfile(context, p);
                            }
                            Toast.makeText(context, "서버에 저장된 프로파일을 가져왔습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.v(TAG, "no profiles from server");
                        }
                    }else
                        Toast.makeText(context, "데이터가 없습니다", Toast.LENGTH_SHORT).show();
                    if(cb != null)
                        cb.onProfileLoaded();
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.d(TAG, "error message = " + t.getMessage());
            }
        });
    }
}
