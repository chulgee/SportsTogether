package com.iron.dragon.sportstogether.ui.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
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

    public void getProfiles(Profile buddy, final RetrofitHelper.OnViewUpdateListener cb){
        RetrofitHelper.getProfiles(context, me, buddy, cb);
    }
}
