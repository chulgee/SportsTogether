package com.iron.dragon.sportstogether.ui.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chulchoice on 2017-01-27.
 */

public class BuddyModel {

    private static final String TAG = "BuddyModel";
    Context context;
    ArrayList<Profile> buddies;
    Profile me;

    public interface BuddyModelCallback{
        void onLoad(List<Profile> buddies);
    }

    public BuddyModel(Context context, int sportsid, int locationid) {
        this.context = context;
        buddies = new ArrayList<Profile>();
        me = LoginPreferences.GetInstance().loadSharedPreferencesProfile(context, sportsid);
    }

    public void loadProfile(Profile buddy, final BuddyModelCallback cb){
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
        int level = -1;
        if(!buddy.getUsername().equals("Bulletin")){
            level = me.getLevel();
        }
        final Call<String> call =
                retrofit.getProfiles(me.getUsername(), buddy.getSportsid(), buddy.getLocationid(), 1, level);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("loadProfile", "code = " + response.code() + " is successful = " + response.isSuccessful());
                Log.d("loadProfile", "body = " + response.body().toString());
                Log.d("loadProfile", "message = " + response.toString());
                if (response.isSuccessful()) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.body().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Profile buddy = null;
                    try {
                        String command = obj.getString("command");
                        String code = obj.getString("code");
                        JSONArray arr = obj.getJSONArray("message");
                        if(arr != null){
                            for(int i=0; i<arr.length(); i++){
                                buddy = gson.fromJson(arr.get(i).toString(), Profile.class);
                                buddies.add(buddy);
                                Log.v(TAG, "buddy["+i+"]: "+buddy.toString());
                                int count = Util.getUnreadBuddy(context, buddy.getUsername());
                                buddy.setUnread(count);
                            }
                            cb.onLoad(buddies);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });

    }
}
