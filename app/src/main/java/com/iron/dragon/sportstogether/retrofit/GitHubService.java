package com.iron.dragon.sportstogether.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by seungyong on 2016-11-09.
 */

public interface GitHubService {
    @POST("profiles")
    Call<Profile> repoContributors(
            @Body Profile profile
           );
    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com/")
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
