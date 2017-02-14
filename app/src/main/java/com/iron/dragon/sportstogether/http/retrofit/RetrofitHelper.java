package com.iron.dragon.sportstogether.http.retrofit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.CallbackWithExists;
import com.iron.dragon.sportstogether.http.RetryableCallback;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.Const;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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

    public interface PROFILE_CALLBACK{
        public void onLoaded(Profile profile);
    }

    public interface VERSION_CALLBACK{
        public void onLoaded(int versioncode);
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

    public static void loadProfile(final Context context, final Profile me, String username, int sportsid, int locationid, final PROFILE_CALLBACK cb) {
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
            }
        });
    }

    public static void getServerVersion(final Context context, final VERSION_CALLBACK cb){
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
            }
        });
    }
}
