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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.service.FloatingService;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.DbUtil;
import com.iron.dragon.sportstogether.util.PushWakeLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
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
        if(message.getSender().equals("server")){
            Intent i = new Intent(this, FloatingService.class);
            i.putExtra("Message", message);
            startService(i);
        }else{
            message.setFrom(Message.PARAM_FROM_OTHER);
            message.setRoom(message.getSender());
            Log.v(TAG, "buddy="+buddy);
            Log.v(TAG, "message="+message);

            DbUtil.insert(getApplicationContext(), message);

            Intent i = new Intent(this, ChatActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Message startMessage = new Message.Builder(Message.PARAM_FROM_OTHER).msgType(Message.PARAM_TYPE_LOG).sender(message.getSender()).receiver(message.getReceiver())
                    .message("Conversation gets started").date(new Date().getTime()).image(message.getImage()).build();
            i.putExtra("Message", startMessage);
            i.putExtra("Buddy", buddy);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setSmallIcon(R.drawable.friend_icon_normal);
            builder.setTicker(message.getSender()+": "+message.getMessage());
            builder.setContentTitle("함께 운동해요");
            builder.setContentIntent(pi);
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_HIGH);
            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, builder.build());

            mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            mVibe.vibrate(300);
            PushWakeLock.acquireWakeLock(this, 5000);
        }

    }

    private void loadBuddyProfile(String buddy, final Profile me){
        // buddy의 profile 가져오기
        Log.v(TAG, "buddy="+buddy+", sportsid="+me.getSportsid()+", locationid="+me.getLocationid());
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
        final Call<String> call =
                retrofit.getProfiles(buddy, me.getSportsid(), me.getLocationid(), 0);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("loadBuddyProfile", "code = " + response.code() + " is successful = " + response.isSuccessful());
                Log.d("loadBuddyProfile", "body = " + response.body().toString());
                Log.d("loadBuddyProfile", "message = " + response.toString());
                if (response.isSuccessful()) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.body().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Profile buddy = null;
                    try {
                        String command = obj.getString("command");
                        String code = obj.getString("code");
                        JSONArray arr = obj.getJSONArray("message");
                        buddy = gson.fromJson(arr.get(0).toString(), Profile.class);
                        Log.v(TAG, "buddy: "+buddy.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    /*Intent i = new Intent(getActivity(), ChatActivity.class);
                    Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(buddy.getUsername())
                            .message("Conversation get started").date(new Date().getTime()).image(null).build();
                    i.putExtra("Message", message);
                    i.putExtra("Buddy", buddy);
                    startActivity(i);*/
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    private void println(String data){
        Log.d(TAG, data);
    }
}
