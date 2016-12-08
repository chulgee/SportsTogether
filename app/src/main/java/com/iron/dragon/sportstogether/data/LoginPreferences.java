package com.iron.dragon.sportstogether.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.bean.Profile;

/**
 * Created by P11872 on 2015-08-20.
 */
public class LoginPreferences {
    static private SharedPreferences.Editor mEditor;
    static private SharedPreferences mPref;

    public static final String USER_AUTHENTICATED = "user_authenticated"; //value is a Boolean
    public static final String REGID = "regid"; //value is a Boolean
    private static volatile LoginPreferences mLoginPreferences;

    private static final String PROFILE_NICKNAME = "_nickname";

    private static final String PROFILE_REGID = "regid";
    private static final String PROFILE_SPORTSID = "sportsid";
    private static final String PROFILE_LOCATIONID = "locationid";
    private static final String PROFILE_AGE = "age";
    private static final String PROFILE_GENDER = "gender";
    private static final String PROFILE_PHONE = "phone";
    private static final String PROFILE_LEVEL = "level";


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

    public String GetLocalProfileUserName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PROFILE_NICKNAME, null);
    }
    public int GetLocalProfileLocation(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PROFILE_LOCATIONID, 0);
    }
    public boolean GetLogin(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(LoginPreferences.USER_AUTHENTICATED, false);
    }
    public void SetLogin(Context context, boolean logged) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_AUTHENTICATED, logged);//for test
        editor.apply();
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
    public void SetLocalProfile(Context context, Profile profile) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PROFILE_REGID, profile.getRegid());
        editor.putString(PROFILE_NICKNAME, profile.getUsername());
        editor.putInt(PROFILE_SPORTSID, profile.getSportsid());
        editor.putInt(PROFILE_LOCATIONID,  profile.getLocationid());
        editor.putInt(PROFILE_AGE, profile.getAge());
        editor.putInt(PROFILE_GENDER, profile.getGender());
        editor.putString(PROFILE_PHONE, profile.getPhone());
        editor.putInt(PROFILE_LEVEL, profile.getLevel());
        editor.apply();

        SportsApplication app = (SportsApplication)((Activity)context).getApplication();
        app.setMyProfile(profile);
    }

    public Profile getLocalProfile(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String regid = sharedPreferences.getString(PROFILE_REGID, "");
        String username = sharedPreferences.getString(PROFILE_NICKNAME, "");
        int sportsid = sharedPreferences.getInt(PROFILE_SPORTSID, 0);
        int locationid = sharedPreferences.getInt(PROFILE_LOCATIONID, 0);
        int age = sharedPreferences.getInt(PROFILE_AGE, 0);
        int gender = sharedPreferences.getInt(PROFILE_GENDER, 0);
        String phone = sharedPreferences.getString(PROFILE_PHONE, "");
        int level = sharedPreferences.getInt(PROFILE_LEVEL, 0);
        Profile profile = new Profile(regid, username, sportsid, locationid, age, gender, phone, level);
        return profile;
    }

    public void clear(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .clear()
                .apply();
    }

}
