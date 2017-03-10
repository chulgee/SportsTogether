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
import com.iron.dragon.sportstogether.ui.model.BuddyModel;
import com.iron.dragon.sportstogether.util.ConnectivityUtil;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;
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

    public interface  OnViewUpdateListener{
        public void onViewUpdateListener(Response response);
    }

    public interface  OnViewHandleListener{
        public void onData(Response response);
        public void onEmpty();
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

    public static void getProfile(final Context context, final Profile me, final String username, int sportsid, int locationid, final OnViewHandleListener cb) {

        if(!ConnectivityUtil.isConnected(context))
            return;

        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final Call<Profile> call = retrofit.getProfile(username, sportsid, locationid);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "getProfile response.body() = " + (response.body()!=null?response.body().toString():null));
                    Profile profile = response.body();
                    if(profile!=null && profile.getUsername() == null){
                        Toast.makeText(context, username+"님의 프로필이 서버에 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                        cb.onEmpty();
                    }else
                        cb.onData(response);
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Toast.makeText(context, "Network Problem: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getProfiles(final Context context, Profile me, Profile buddy, final OnViewUpdateListener cb){
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
        int level = -1;
        /*if(!buddy.getUsername().equals("Bulletin")){
            level = me.getLevel();
        }*/
        final Call<List<Profile>> call = retrofit.getProfiles(me.getUsername(), buddy.getSportsid(), buddy.getLocationid(), 1, level);
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                Log.d(TAG, "getProfiles response.body() = " + (response.body()!=null?response.body().toString():null));
                if(response.isSuccessful()){
                    cb.onViewUpdateListener(response);
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });

    }

    public static void getServerVersion(final Context context, final OnViewUpdateListener cb){

        if(!ConnectivityUtil.isConnected(context))
            return;

        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final Call<Integer> call = retrofit.getVersion();
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "getServerVersion response.body() = " + (response.body()!=null?response.body().toString():null));
                    cb.onViewUpdateListener(response);
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(context, "Network Problem: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void updateRegidToServer(final Context context, final OnViewUpdateListener listener){
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final String old_regid = LoginPreferences.GetInstance().GetRegid(context);
        final String new_regid = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "old_regid="+old_regid);
        Log.v(TAG, "new_regid="+new_regid);

        if(old_regid == null || old_regid.isEmpty())
            return;

        if(old_regid.equals(new_regid) == false){
            final Call<Profile> call = gitHubService.putProfilesRegid(old_regid, new_regid);
            call.enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {

                    if (response.isSuccessful()) {
                        Log.d(TAG, "updateRegidToServer response.body() = " + (response.body()!=null?response.body().toString():null));
                        LoginPreferences.GetInstance().SetRegid(context, new_regid);
                        listener.onViewUpdateListener(response);
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                    Toast.makeText(context, "Network Problem: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Log.d(TAG, "regid 동일함. 업데이트 필요없음");
            listener.onViewUpdateListener(null);
        }
    }

    public static void getServerProfiles(final Context context, String deviceid, final String regid, final OnViewUpdateListener cb){
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        final Call<List<Profile>> call = retrofit.getProfilesForDeviceId(deviceid, regid);
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "getServerProfiles response.body() = " + (response.body()!=null?response.body().toString():null));
                    List<Profile> profiles = response.body();
                    if(profiles!=null && profiles.size()>0){
                        for(Profile p : profiles) {
                            LoginPreferences.GetInstance().SetLogout(context, p.getSportsid());
                            LoginPreferences.GetInstance().saveSharedPreferencesProfile(context, p);
                        }
                        LoginPreferences.GetInstance().SetRegid(context, regid);
                        Toast.makeText(context, "서버에 저장된 프로파일을 가져왔습니다.", Toast.LENGTH_SHORT).show();
                    }
                    if(cb != null)
                        cb.onViewUpdateListener(response);
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Toast.makeText(context, "Network Problem: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
