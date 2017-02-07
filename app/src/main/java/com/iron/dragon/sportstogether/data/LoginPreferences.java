package com.iron.dragon.sportstogether.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by P11872 on 2015-08-20.
 */
public class LoginPreferences {

    private static final String TAG = "LoginPreferences";
    private static volatile LoginPreferences mLoginPreferences;

    private static final String PROFILE_NICKNAME = "_nickname";
    public static final String REGID = "regid"; //value is a Boolean
    private static final String PROFILE_IMAGE = "image";

    private static final String BUDDY_ALARM = "buddy_alarm";
    private static final String CHAT_ALARM = "chat_alarm";

    private LoginPreferences(){

    }

    public static LoginPreferences GetInstance() {
        if (mLoginPreferences == null) {
            synchronized (LoginPreferences.class) {
                mLoginPreferences = new LoginPreferences();
            }
        }
        return mLoginPreferences;
    }

    public void SetBuddyAlarmOn(Context context, boolean isChecked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BUDDY_ALARM, isChecked);
        editor.apply();
    }
    public boolean GetBuddyAlarmOn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ischecked = sharedPreferences.getBoolean(BUDDY_ALARM, true);
        return ischecked;
    }
    public void SetChatAlarmOn(Context context, boolean isChecked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHAT_ALARM, isChecked);
        editor.apply();
    }
    public boolean GetChatAlarmOn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ischecked = sharedPreferences.getBoolean(CHAT_ALARM, true);
        return ischecked;
    }

    public void SetRegid(Context context, String regid){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REGID, regid);
        editor.apply();
    }

    public String GetRegid(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String regid = sharedPreferences.getString(REGID, "");
        return regid;
    }

    public boolean IsLogin(Context context, int id) {
        //String[] temp = context.getResources().getStringArray(R.array.sportstype);
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        boolean isLogin = false;
        String json = appSharedPrefs.getString(StringUtil.getStringFromSports(context, id), "");
        return !json.equals("");
    }

    // 아 먼가 이상해졌음....
    public void saveSharedPreferencesProfile(Context context, Profile profile) {
        setCommonPreference(context, profile);
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        prefsEditor.putString(StringUtil.getStringFromSports(context, profile.getSportsid()), json);
        prefsEditor.apply();
    }

    private void setCommonPreference(Context context, Profile profile) {
        ArrayList<Profile> profileList = loadSharedPreferencesProfileAll(context);
        for(Profile p : profileList) {
            p.setUsername(profile.getUsername());
            p.setAge(profile.getAge());
            p.setGender(profile.getGender());
            p.setPhone(profile.getPhone());
            SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(p);
            prefsEditor.putString(StringUtil.getStringFromSports(context, p.getSportsid()), json);
            prefsEditor.apply();
        }
    }

    public Profile loadSharedPreferencesProfile(Context context, int sportsId) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(StringUtil.getStringFromSports(context, sportsId), "");
        return gson.fromJson(json, Profile.class);
    }

    public ArrayList<Profile> loadSharedPreferencesProfileAll(Context context) {
        String[] temp = StringUtil.getStringArrFromSportsType(context);
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        ArrayList<Profile> profileList = new ArrayList<>();
        for(String key:temp) {
            String json = appSharedPrefs.getString(key, "");
            if(!json.equals("")) {
                profileList.add(gson.fromJson(json, Profile.class));
            }
        }
        return profileList;
    }


    // temp
    public void SetLogout(Context context) {
        String[] temp = StringUtil.getStringArrFromSportsType(context);
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        for(String key:temp) {
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            Gson gson = new Gson();
            prefsEditor.putString(key, null);
            prefsEditor.apply();
        }
    }
}
