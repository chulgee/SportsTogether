package com.iron.dragon.sportstogether.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by P16018 on 2016-12-02.
 */

public class Util {
    private static final String TAG = "Util";

    public static String getStringDate(long time){
        Date date=new Date(time);
        SimpleDateFormat df2 = new SimpleDateFormat("yy-MM-dd", Locale.KOREA);
        return df2.format(date);
    }

    public static String getStringTime(long time){
        Date date=new Date(time);
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mm:ss", Locale.KOREA);
        return df2.format(date);
    }

    public static String getImageName(String path) {
        if(path == null) {
            return null;
        }
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            path = path.substring(cut + 1);
        }
        return path;
    }

    public static byte[] getDataFromUrl(String _url){
        byte[] response = {};

        try {
            URL url = new URL(_url);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            Log.v(TAG, "responseCode = " + responseCode);
            InputStream in;
            if(responseCode >= HttpURLConnection.HTTP_BAD_REQUEST){
                in = conn.getErrorStream();
            }else{
                in= new BufferedInputStream(conn.getInputStream());
            }
            byte[] buf = new byte[1024];
            int count = 0;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            while((count=in.read(buf)) != -1){
                bao.write(buf,0, count);
            }
            response = bao.toByteArray();
            bao.close();
            in.close();
            Log.v(TAG, "response = "+response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static int getUnreadChat(Context context, String room){
        SharedPreferences pref = context.getSharedPreferences(Const.PREF_UNREAD_CHAT, Context.MODE_PRIVATE);
        int count = pref.getInt(room, 0);
        Log.v(TAG, "getUnreadChat count = "+count);
        return count;
    }

    public static void plusUnreadChat(Context context, String room){
        SharedPreferences pref = context.getSharedPreferences(Const.PREF_UNREAD_CHAT, Context.MODE_PRIVATE);
        int count = pref.getInt(room, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(room, ++count);
        editor.commit();
        Log.v(TAG, "plusUnreadChat count = "+count);
    }

    public static void setUnreadChat(Context context, String room, int value){
        SharedPreferences pref = context.getSharedPreferences(Const.PREF_UNREAD_CHAT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(room, value);
        editor.commit();
        Log.v(TAG, "setUnreadChat count to "+value);
    }


    public static int getUnreadBuddy(Context context, String buddy){
        SharedPreferences pref = context.getSharedPreferences(Const.PREF_UNREAD_BUDDY, Context.MODE_PRIVATE);
        int count = pref.getInt(buddy, 0);
        Log.v(TAG, "getUnreadBuddy count = "+count);
        return count;
    }

    public static void setUnreadBuddy(Context context, String buddy, int value){
        SharedPreferences pref = context.getSharedPreferences(Const.PREF_UNREAD_BUDDY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(buddy, value);
        editor.commit();
        Log.v(TAG, "setUnreadBuddy count to "+value);
    }
}
