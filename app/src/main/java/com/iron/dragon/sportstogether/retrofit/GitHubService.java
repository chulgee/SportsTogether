package com.iron.dragon.sportstogether.retrofit;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by seungyong on 2016-11-09.
 */

public interface GitHubService {
    @POST("profiles")
    Call<Profile> postProfiles(
            @Body Profile profile
           );

    @GET("profiles")
    Call<List<ProfileWithId>> getProfiles(
            );

    @GET("profiles/{id}")
    Call<List<ProfileWithId>> getProfiles(
            @Path("id") int id);

    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com/")
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
