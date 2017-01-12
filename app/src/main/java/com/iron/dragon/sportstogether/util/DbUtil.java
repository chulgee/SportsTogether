package com.iron.dragon.sportstogether.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.iron.dragon.sportstogether.provider.MyContentProvider;
import com.iron.dragon.sportstogether.data.bean.Message;

import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_IMAGE;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_LOCATIONID;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_SPORTSID;


/**
 * Created by P10950 on 2017-01-10.
 */

public class DbUtil {

    private static final String TAG = "DbUtil";

    public static void insert(Context context, Message message){

        ContentValues values = new ContentValues();
        values.put(MyContentProvider.DbHelper.COLUMN_ROOM, message.getRoom());
        values.put(MyContentProvider.DbHelper.COLUMN_DATE, message.getDate());
        values.put(MyContentProvider.DbHelper.COLUMN_SENDER, message.getSender());
        values.put(MyContentProvider.DbHelper.COLUMN_RECEIVER, message.getReceiver());
        values.put(MyContentProvider.DbHelper.COLUMN_MESSAGE, message.getMessage());
        values.put(MyContentProvider.DbHelper.COLUMN_FROM, message.getFrom());
        values.put(MyContentProvider.DbHelper.COLUMN_MESSAGE_TYPE, message.getMsgType());
        values.put(COLUMN_SPORTSID, message.getSportsid());
        values.put(COLUMN_LOCATIONID, message.getLocationid());
        values.put(COLUMN_IMAGE, message.getImage());
        Uri uri = context.getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
        Log.v(TAG, "insert uri="+uri);
    }

    public static void delete(Context context, String roomName){
        String where = MyContentProvider.DbHelper.COLUMN_ROOM+"=?";
        int count = context.getContentResolver().delete(MyContentProvider.CONTENT_URI, where, new String[]{roomName});
        Log.v(TAG, "delete count="+count);
    }
}
