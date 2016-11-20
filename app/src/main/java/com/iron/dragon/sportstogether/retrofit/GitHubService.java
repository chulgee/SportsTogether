package com.iron.dragon.sportstogether.retrofit;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by seungyong on 2016-11-09.
 */

public interface GitHubService {

    @GET("profiles")
    Call<List<ProfileWithId>> getProfiles(
    );

    @GET("profiles")
    Call<List<ProfileWithId>> getProfiles(
            @Query("username") String username, @Query("locationid") int locationid, @Query("sportsid") int sportsid
    );

    @GET("profiles/{id}")
    Call<List<ProfileWithId>> getProfiles(
            @Path("id") int id
    );

    @POST("profiles")
    Call<Profile> postProfiles(
            @Body Profile profile
    );

    @PUT("profiles/{id}")
    Call<Profile> putProfiles(
            @Path("id") int id, @Body Profile profile
    );

    @DELETE("profiles/{id}")
    Call<Profile> deleteProfiles(
            @Path("id") int id
    );

    @GET("bulletin")
    Call<List<Bulletin>> getBulletin(
    );

    @GET("bulletin")
    Call<List<Bulletin>> getBulletin(
            @Query("sportsid") int sportsid, @Query("locationid") int locationid, @Query("reqNum") int reqNum
    );



    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com/")
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
