package com.iron.dragon.sportstogether.http.retrofit;

import com.iron.dragon.sportstogether.http.CallbackWithExists;
import com.iron.dragon.sportstogether.http.RetryableCallback;
import com.orhanobut.logger.Logger;

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
}
