package com.iron.dragon.sportstogether.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.ui.fragment.ChatFragment;
import com.iron.dragon.sportstogether.util.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener{
    private static final String TAG = "ChatActivity";
    boolean isConnected = false;
    Socket mSocket;
    ChatThread mThread;
    public ChatFragment mCurrentFrag;
    Profile me;

    FragmentManager fm = getFragmentManager();


    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.v(TAG, "onCreate");
        Intent i = getIntent();
        processIntent(i);
        mThread = new ChatThread();
        mThread.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processNewIntent(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSocket != null) {
            mSocket.disconnect();
        }
        isConnected = false;
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            Message message = (Message)intent.getSerializableExtra("Message");
            mCurrentFrag = ChatFragment.newInstance(message);
            Log.v(TAG, "processIntent mCurrentFrag="+mCurrentFrag);

            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.frag_chat, mCurrentFrag);
            ft.commit();
        }
    }

    private void processNewIntent(Intent intent) {
        if (intent != null) {
            Message message = (Message)intent.getSerializableExtra("Message");

            // find fragment for buddy
            String key;
            if(message.getMsgType() == Message.PARAM_MSG_OUT){
                key = message.getReceiver();
            }else{
                key = message.getSender();
            }
            mCurrentFrag = (ChatFragment)ChatFragment.getChatRoom(key);
            Log.v(TAG, "processNewIntent key ="+key+", mCurrentFrag="+mCurrentFrag);
            if(mCurrentFrag != null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(mCurrentFrag);
                ft.commit();
                //showFragment(key);
            }else{
                FragmentTransaction ft = fm.beginTransaction();
                mCurrentFrag = ChatFragment.newInstance(message);
                ft.add(R.id.frag_chat, mCurrentFrag);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onUpdateUI() {
    }

    public void send(Message message){
        mThread.send(message);
    }

    public ChatFragment createFragment(Message message){
        return ChatFragment.newInstance(message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //if(mChatRoom.size() == 1)
          //  finish();
    }

    class ChatThread extends Thread {
        public static final String TAG = "ChatThread";
        @Override
        public void run() {
            me = LoginPreferences.GetInstance().getLocalProfile(ChatActivity.this);
            createSocket();
        }

        private void createSocket(){
            // 소켓 생성 및 연결
            IO.Options options = new IO.Options();
            options.forceNew = true;
            try {
                mSocket = IO.socket(Const.MAIN_URL, options);//app.getSocket();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("login_done", onLoginDone);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on("send", onSend);
            mSocket.connect();
        }

        public void send(Message message) {
            Gson gson = new Gson();
            String json = gson.toJson(message);
            System.out.println("send() 호출됨. json="+json);

            try {
                JSONObject obj = new JSONObject();
                obj.put("sender", message.getSender());
                obj.put("receiver", message.getReceiver());
                obj.put("contents", message.getMessage());
                obj.put("date", message.getDate());
                mSocket.emit("send", obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Emitter.Listener onConnect = new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                System.out.println("Socket.IO 서버에 연결되었습니다.");

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("username", me.getUsername());
                    //obj.put("regid", mMe.getRegid());
                    //obj.put("sportsid", mMe.getSportsid());
                    //obj.put("locationid", mMe.getLocationid());
                    mSocket.emit("login", obj);
                    isConnected = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        public Emitter.Listener onLoginDone = new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                Log.v(TAG, "onLoginDone");
            }
        };

        public Emitter.Listener onDisconnect = new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                isConnected = false;
                //removeChatRoom(mCurrentFrag.opponent);
                Log.v(TAG, "onDisconnect");
                //Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();
            }
        };

        public Emitter.Listener onConnectError = new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                Log.v(TAG, "onConnectError");
                finish();
                //Toast.makeText(getApplicationContext(), "connect error", Toast.LENGTH_LONG).show();
            }
        };

        public Emitter.Listener onSend = new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                Log.v(TAG, "onSend");
                JSONObject obj = (JSONObject)args[0];
                String sender = null;
                String receiver = null;
                String contents = null;
                long date = 0;
                try {
                    sender = obj.getString("sender");
                    receiver = obj.getString("receiver");
                    contents = obj.getString("contents");
                    date = (long)obj.getLong("date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final Message message = new Message.Builder(Message.TYPE_CHAT_MESSAGE).msgType(Message.PARAM_MSG_IN).sender(sender).receiver(receiver).message(contents).date(date).build();
                Log.v(TAG, message.toString());

                Log.v(TAG, "onSend mCurrentFrag.mBuddyName="+mCurrentFrag.mBuddyName);
                if(sender.equals(mCurrentFrag.mBuddyName)) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentFrag.updateUI(message);
                        }
                    });
                    createMsgNoti(message);
                }else{
                    ChatFragment fr;
                    fr = (ChatFragment)ChatFragment.getChatRoom(sender);
                    Log.v(TAG, "onSend sender="+sender+", fr="+fr);
                    if(fr != null){
                        fr.updateUI(message);
                        createMsgNoti(message);
                    }else{
                        createMsgNoti(message);
                    }
                    //mCurrentFrag = (ChatFragment)fr;
                }

            }
        };
    }

/*    void showFragment(String buddyName){
        Iterator iter =ChatActivity.mChatRoom.keySet().iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            Log.v(TAG, "showFragment key="+key);
            Fragment fragment = mChatRoom.get(key);
            FragmentTransaction ft = fm.beginTransaction();
            if(key.equals(buddyName)) {
                Log.v(TAG, "show buddyName="+key+", fragment="+fragment);
                ft.show(fragment);
            }else{
                Log.v(TAG, "hide buddyName="+key+", fragment="+fragment);
                ft.hide(fragment);
            }
            ft.commit();
        }
    }*/

    void createMsgNoti(Message message){
        Intent i = new Intent(ChatActivity.this, ChatActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("Message", message);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.friend_icon_normal);
        builder.setContentText(message.getSender()+": "+message.getMessage());
        builder.setContentTitle("함께 운동해요");
        builder.setContentIntent(pi);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
