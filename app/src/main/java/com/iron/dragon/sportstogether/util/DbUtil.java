package com.iron.dragon.sportstogether.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.iron.dragon.sportstogether.MyContentProvider;
import com.iron.dragon.sportstogether.data.bean.Message;

import java.util.Date;


/**
 * Created by P10950 on 2017-01-10.
 */

public class DbUtil {

    private static final String TAG = "DbUtil";

    public static void insert(Context context, Message message){
        String[] projection = {
                MyContentProvider.DbHelper.COLUMN_ID,
                MyContentProvider.DbHelper.COLUMN_DATE,
                MyContentProvider.DbHelper.COLUMN_SENDER,
                MyContentProvider.DbHelper.COLUMN_RECEIVER,
                MyContentProvider.DbHelper.COLUMN_MESSAGE
        };

        ContentValues values = new ContentValues();
        values.put(MyContentProvider.DbHelper.COLUMN_ROOM, message.getRoom());
        values.put(MyContentProvider.DbHelper.COLUMN_DATE, message.getDate());
        values.put(MyContentProvider.DbHelper.COLUMN_SENDER, message.getSender());
        values.put(MyContentProvider.DbHelper.COLUMN_RECEIVER, message.getReceiver());
        values.put(MyContentProvider.DbHelper.COLUMN_MESSAGE, message.getMessage());
        values.put(MyContentProvider.DbHelper.COLUMN_FROM, message.getFrom());
        values.put(MyContentProvider.DbHelper.COLUMN_MESSAGE_TYPE, message.getMsgType());
        Uri uri = context.getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
        Log.v(TAG, "insert uri="+uri);
    }
}
