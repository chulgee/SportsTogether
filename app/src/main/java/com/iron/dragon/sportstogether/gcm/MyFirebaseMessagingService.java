package com.iron.dragon.sportstogether.gcm;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;

import java.util.Map;

/**
 * 4단계 메세지 수신 담당 서비스
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        println("onMessageReceived()");

        Map<String, String> data = remoteMessage.getData();
        String sender = data.get("sender");
        String receiver = data.get("receiver");
        String contents = data.get("contents");

        println("수신데이터 -> sender: "+sender+", receiver: "+receiver+", contents: "+contents);

        sendDataToChatActivity(sender, receiver, contents);
    }

    public void sendDataToChatActivity(String sender, String receiver, String contents){
        Intent i = new Intent(getApplicationContext(), ChatActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("sender", sender);
        i.putExtra("receiver", receiver);
        i.putExtra("contents", contents);
        startActivity(i);
    }

    private void println(String data){
        Log.d(TAG, data);
    }
}
