package com.iron.dragon.sportstogether.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by P11872 on 2015-08-20.
 */
public class LoginPreferences {
    static private SharedPreferences.Editor mEditor;
    static private SharedPreferences mPref;

    public static final String USER_AUTHENTICATED = "user_authenticated"; //value is a Boolean
    private static volatile LoginPreferences mLoginPreferences;

    private static final String PROFILE_NICKNAME = "_nickname";
    private static final String PROFILE_SPORTSTYPE = "_sprotstype";
    private static final String PROFILE_LOCATION = "_location";

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

    public boolean CheckLogin(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(LoginPreferences.USER_AUTHENTICATED, false);
    }
    public String GetLocalProfileUserName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PROFILE_NICKNAME, null);
    }
    public int GetLocalProfileLocation(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PROFILE_LOCATION, 0);
    }
    public void SetLogin(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_AUTHENTICATED, true);
        editor.apply();
    }

    public void SetLocalProfile(Context context, ProfileItem profile) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PROFILE_NICKNAME, profile.get_mNickName());
        editor.putInt(PROFILE_SPORTSTYPE, profile.get_mSportsType());
        editor.putInt(PROFILE_LOCATION,  profile.get_mLocation());
        editor.apply();
    }

    public void CheckLogOut(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_AUTHENTICATED, false);
    }
    public void clear(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .clear()
                .apply();
    }

}
