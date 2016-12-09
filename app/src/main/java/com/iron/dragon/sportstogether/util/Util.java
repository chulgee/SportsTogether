package com.iron.dragon.sportstogether.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;
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
        String dateText = df2.format(date);
        return dateText;
    }

    public static File getFileFromUri(ContentResolver resolver , Uri uri){
        Cursor cursor = resolver.query(uri, null, null, null, null );
        assert cursor != null;
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();
        return new File(path);
    }
}
