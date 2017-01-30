package com.iron.dragon.sportstogether.http.retrofit;

import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.data.bean.News;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.util.Const;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
            @Query("username") String username, @Query("sportsid") int sportsid, @Query("locationid") int locationid, @Query("reqFriends") int reqFriends, @Query("level") int level
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

    @GET("search/news.json")
    Call<News> getNews(
            @Query("query") String query
    );

    public class ServiceGenerator {
        public static String apiBaseUrl = Const.MAIN_URL;
        static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        static OkHttpClient.Builder httpNewsClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Host", "openapi.naver.com")
                        .addHeader("User-Agent", "curl/7.49.1")
                        .addHeader("Accept", "*/*")
                        .addHeader("X-Naver-Client-Id", Const.NAVER_CLIENT_ID)
                        .addHeader("X-Naver-Client-Secret", Const.NAVER_CLIENT_SECRET); // <-- this is the important line
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        public static Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.MAIN_URL)
                .client(httpClient.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        public static void changeApiBaseUrl(String newApiBaseUrl) {
            apiBaseUrl = newApiBaseUrl;

            retrofit = new Retrofit.Builder()
                    .baseUrl(apiBaseUrl)
                    .client(httpNewsClient.build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

    }

}
