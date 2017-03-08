package com.iron.dragon.sportstogether.util;

import android.content.Context;
import android.util.Log;

import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.provider.ChatMessageVO;
import com.iron.dragon.sportstogether.provider.MyContentProvider;

import io.realm.Realm;


/**
 * Created by P10950 on 2017-01-10.
 */

public class DbUtil {

    private static final String TAG = "DbUtil";

    public static void insert(Context context, final Message message){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int count = 0;
                if(realm.where(ChatMessageVO.class).count() != 0) {
                    count = realm.where(ChatMessageVO.class).max("COLUMN_ID").intValue() + 1;
                }
                ChatMessageVO messageVO = realm.createObject(ChatMessageVO.class, count);
                messageVO.setCOLUMN_ROOM(message.getRoom());
                messageVO.setCOLUMN_DATE(message.getDate());
                messageVO.setCOLUMN_SENDER(message.getSender());
                messageVO.setCOLUMN_RECEIVER(message.getReceiver());
                messageVO.setCOLUMN_MESSAGE(message.getMessage());
                messageVO.setCOLUMN_FROM(message.getFrom());
                messageVO.setCOLUMN_MESSSAGE_TYPE(message.getMsgType());
                messageVO.setCOLUMN_SPORTSID(message.getSportsid());
                messageVO.setCOLUMN_LOCATIONID(message.getLocationid());
                messageVO.setCOLUMN_IMAGE(message.getImage());
            }
        });
        realm.close();
    }

    public static void delete(Context context, String roomName){
        String where = MyContentProvider.DbHelper.COLUMN_ROOM+"=?";
        int count = context.getContentResolver().delete(MyContentProvider.CONTENT_URI, where, new String[]{roomName});
        Log.v(TAG, "delete count="+count);
    }
}
