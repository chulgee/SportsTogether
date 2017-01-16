package com.iron.dragon.sportstogether.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Profile;

import java.util.ArrayList;

/**
 * Created by P11872 on 2015-08-20.
 */
public class LoginPreferences {

    private static volatile LoginPreferences mLoginPreferences;

    /*public static final String USER_AUTHENTICATED = "user_authenticated"; //value is a Boolean
    public static final String REGID = "regid"; //value is a Boolean

    private static final String PROFILE_NICKNAME = "_nickname";

    private static final String PROFILE_REGID = "regid";
    private static final String PROFILE_SPORTSID = "sportsid";
    private static final String PROFILE_LOCATIONID = "locationid";
    private static final String PROFILE_AGE = "age";
    private static final String PROFILE_GENDER = "gender";
    private static final String PROFILE_PHONE = "phone";
    private static final String PROFILE_LEVEL = "level";
    private static final String PROFILE_IMAGE = "image";

    private static final String MY_PROFILE = "my_profile";
*/

    private static final String PROFILE_NICKNAME = "_nickname";
    public static final String REGID = "regid"; //value is a Boolean
    private static final String PROFILE_IMAGE = "image";

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
        String[] temp = context.getResources().getStringArray(R.array.sportstype);
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        boolean isLogin = false;
        String json = appSharedPrefs.getString(context.getResources().getStringArray(R.array.sportstype)[id], "");
        return !json.equals("");
    }

    /*public String GetProfileUserName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PROFILE_NICKNAME, null);
    }
    public int GetLocalProfileLocation(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PROFILE_LOCATIONID, 0);
    }
    public String GetLocalProfileImage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PROFILE_IMAGE, null);
    }
    public boolean IsLogin(Context context) {
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

        Logger.d("profile = " + profile.toString());
        editor.putString(PROFILE_REGID, profile.getRegid());
        editor.putString(PROFILE_NICKNAME, profile.getUsername());
        editor.putInt(PROFILE_SPORTSID, profile.getSportsid());
        editor.putInt(PROFILE_LOCATIONID,  profile.getLocationid());
        editor.putInt(PROFILE_AGE, profile.getAge());
        editor.putInt(PROFILE_GENDER, profile.getGender());
        editor.putString(PROFILE_PHONE, profile.getPhone());
        editor.putInt(PROFILE_LEVEL, profile.getLevel());
        editor.putString(PROFILE_IMAGE, profile.getImage());
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
        String image = sharedPreferences.getString(PROFILE_IMAGE, "");
        return new Profile(regid, username, sportsid, locationid, age, gender, phone, level, image);
    }

    public void clear(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .clear()
                .apply();
    }
*/
    public void saveSharedPreferencesProfile(Context context, Profile profile) {
        setCommonPreference(context, profile);
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        prefsEditor.putString(context.getResources().getStringArray(R.array.sportstype)[profile.getSportsid()], json);
        prefsEditor.apply();
    }

    private void setCommonPreference(Context context, Profile profile) {
        profile.setRegid(GetRegid(context));
    }

    public Profile loadSharedPreferencesProfile(Context context, int sportsId) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(context.getResources().getStringArray(R.array.sportstype)[sportsId], "");
        return gson.fromJson(json, Profile.class);
    }

    public ArrayList<Profile> loadSharedPreferencesProfileAll(Context context) {
        String[] temp = context.getResources().getStringArray(R.array.sportstype);
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
        String[] temp = context.getResources().getStringArray(R.array.sportstype);
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
