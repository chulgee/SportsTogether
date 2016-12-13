package com.iron.dragon.sportstogether.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by P16018 on 2016-12-02.
 */

public class Util {
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
}
