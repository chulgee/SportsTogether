package com.iron.dragon.sportstogether.http.retrofit;

import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.util.Const;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by seungyong on 2016-11-09.
 */

public interface GitHubService {

    @GET("profiles")
    Call<List<Profile>> getProfiles(
    );

    // 친구 목록 가져오려면 reqFriends = 1 넣는다. otherwise, 자신 포함됨.
    @GET("profiles")
    Call<String> getProfiles(
            @Query("username") String username, @Query("sportsid") int sportsid, @Query("locationid") int locationid, @Query("reqFriends") int reqFriends
    );

    @GET("profiles/{id}")
    Call<List<Profile>> getProfiles(
            @Path("id") int id
    );

    @POST("profiles")
    Call<Profile> postProfiles(
            @Body Profile profile
    );

    @PUT("profiles/{id}")
    Call<Profile> putProfiles(
            @Path("id") String id, @Body Profile profile
    );

    @PUT("profiles/regid/{id}")
    Call<Profile> putProfilesRegid(
            @Path("id") String id, @Body Profile profile
    );

    @DELETE("profiles/{id}")
    Call<Profile> deleteProfiles(
            @Path("id") String id
    );

    @GET("bulletin")
    Call<List<Bulletin>> getBulletin(
    );

    @GET("bulletin")
    Call<List<Bulletin>> getBulletin(
            @Query("sportsid") int sportsid, @Query("locationid") int locationid, @Query("reqNum") int reqNum
    );

    @DELETE("bulletin/{id}")
    Call<Bulletin> deleteBulletin(
            @Path("id") int id
    );

    @POST("bulletin")
    Call<Bulletin> postBulletin(
            @Body Bulletin BulletinInfo
    );

    @GET("buddy_count")
    Call<String> getBuddyCount(
            @Query("sportsid") int sportsid, @Query("locationid") int locationid
    );

    @Multipart
    @POST("upload_profile")
    Call<ResponseBody> upload_profile(@Part("description") RequestBody description,
                                      @Part MultipartBody.Part file);

    @Multipart
    @POST("upload_bulletin")
    Call<ResponseBody> upload_post(@Part("description") RequestBody description,
                                   @Part MultipartBody.Part file);

    @Multipart
    @POST("upload_profile/withpartmap")
    Call<ResponseBody> upload_profileWithPartMap(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);

    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Const.MAIN_URL)
            .client(httpClient.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
