package com.iron.dragon.sportstogether.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.service.FloatingService;
import com.iron.dragon.sportstogether.ui.activity.BuddyActivity;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.DbUtil;
import com.iron.dragon.sportstogether.util.PushWakeLock;
import com.iron.dragon.sportstogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 4단계 메세지 수신 담당 서비스
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FMService";
    private Vibrator mVibe;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        println("onMessageReceived() remoteMessage="+remoteMessage.getData());
        Map<String, String> data = remoteMessage.getData();
        //JSONObject jo = new JSONObject(data);
        Gson gson = new Gson();

        String str_profile = data.get("profile");
        String str_message = data.get("message");
        Profile buddy = gson.fromJson(str_profile, Profile.class);
        Message message = gson.fromJson(str_message, Message.class);

        Log.v(TAG, "From server, message : "+message.toString());
        Log.v(TAG, "From server,   buddy : "+buddy.toString());

        if(message.getSender().equals("server")){
            Intent i = new Intent(this, BuddyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("Buddy", buddy);
            Log.v(TAG, "Buddy : "+buddy.getUsername());
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setSmallIcon(R.drawable.friend_icon_normal);
            builder.setContentText(buddy.getUsername()+"님이 들어왔어요");
            builder.setContentTitle("새로운 친구 입장");
            builder.setContentIntent(pi);
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_HIGH);

            Util.setUnreadBuddy(getApplicationContext(), buddy.getUsername(), 1);

            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, builder.build());

            // show new friend coming gif on current window
            Intent intent = new Intent(this, FloatingService.class);
            if(FloatingService.getFloating()){
                stopService(intent);
            }
            intent.putExtra("Message", message);
            intent.putExtra("Buddy", buddy);
            startService(intent);
        }else{

            message.setFrom(Message.PARAM_FROM_OTHER);
            message.setRoom(message.getSender());
            Log.v(TAG, "Chat buddy="+buddy);
            Log.v(TAG, "Chat message="+message);

            DbUtil.insert(getApplicationContext(), message);
            Util.plusUnreadChat(getApplicationContext(), message.getRoom());
            // post noti to go to ChatActivity
            Message startMessage = new Message.Builder(Message.PARAM_FROM_OTHER).msgType(Message.PARAM_TYPE_LOG).sender(message.getSender()).receiver(message.getReceiver())
                    .message("Conversation gets started").date(new Date().getTime()).image(message.getImage()).build();
            Intent i = new Intent(this, ChatActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("Message", startMessage);
            i.putExtra("Buddy", buddy);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setSmallIcon(R.drawable.friend_icon_normal);
            //builder.setTicker(message.getSender()+": "+message.getMessage());
            builder.setContentTitle("함께 운동해요");
            builder.setContentText(message.getSender()+": "+message.getMessage());
            builder.setContentIntent(pi);
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_HIGH);
            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, builder.build());
        }

        // wake device
        mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibe.vibrate(300);
        PushWakeLock.acquireWakeLock(this, 5000);
    }

    private void println(String data){
        Log.d(TAG, data);
    }
}
