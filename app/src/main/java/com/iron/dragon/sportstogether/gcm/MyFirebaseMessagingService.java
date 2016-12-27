package com.iron.dragon.sportstogether.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;

import java.util.Date;
import java.util.Map;

/**
 * 4단계 메세지 수신 담당 서비스
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FMService";
    private Vibrator mVibe;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        println("onMessageReceived()");
        Map<String, String> data = remoteMessage.getData();
        String sender = data.get("sender");
        String receiver = data.get("receiver");
        String contents = data.get("contents");
        String str_date = data.get("date");
        long date = 0;
        if(str_date != null)
            date = Long.getLong(str_date);
        //Toast.makeText(this, "수신데이터 -> sender: "+sender+", receiver: "+receiver+", contents: "+contents+", date:"+str_date, Toast.LENGTH_LONG).show();
        println("수신데이터 -> sender: "+sender+", receiver: "+receiver+", contents: "+contents+", date:"+str_date);

        Message message = new Message.Builder(Message.TYPE_CHAT_MESSAGE).msgType(Message.PARAM_MSG_IN).sender(sender)
                .receiver(receiver).message(contents).date(date).build();

        Intent i = new Intent(this, ChatActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("Message", message);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.friend_icon_normal);
        builder.setTicker(sender+": "+contents);
        builder.setContentTitle("함께 운동해요");
        builder.setContentIntent(pi);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());

        mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibe.vibrate(300);

    }

    private void println(String data){
        Log.d(TAG, data);
    }
}
