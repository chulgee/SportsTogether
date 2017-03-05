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
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.ui.fragment.ChatFragment;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.DbUtil;
import com.iron.dragon.sportstogether.util.PushWakeLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener{
    private static final String TAG = "ChatActivity";
    boolean isConnected = false;
    Socket mSocket;
    ChatThread mThread;
    public ChatFragment mCurrentFrag;
    Profile mMe;
    boolean mPaused;

    FragmentManager fm = getFragmentManager();

    Handler mHandler = new Handler();
    private int mSportsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_act);
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
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
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
            Profile buddy = (Profile)intent.getSerializableExtra("Buddy");
            mSportsId = buddy.getSportsid();
            Log.v(TAG, "processIntent buddy="+buddy);
            Log.v(TAG, "processIntent message="+message);
            mCurrentFrag = ChatFragment.newInstance(message, buddy);
            Log.v(TAG, "processIntent mCurrentFrag="+mCurrentFrag);

            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.frag_chat, mCurrentFrag);
            ft.commit();
        }
    }

    private void processNewIntent(Intent intent) {
        if (intent != null) {
            Message message = (Message)intent.getSerializableExtra("Message");
            Profile buddy = (Profile)intent.getSerializableExtra("Buddy");
            Log.v(TAG, "processNewIntent buddy="+buddy);
            Log.v(TAG, "processNewIntent message="+message);
            // find fragment for buddy
            String key;
            if(message.getFrom() == Message.PARAM_FROM_ME){
                key = message.getReceiver();
            }else{
                key = message.getSender();
            }
            mCurrentFrag = (ChatFragment)ChatFragment.getChatRoom(key);
            Log.v(TAG, "processNewIntent key ="+key+", mCurrentFrag="+mCurrentFrag);
            if(mCurrentFrag != null){
                //FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.replace(R.id.chat_frag, mCurrentFrag);
                //ft.show(mCurrentFrag);
                //ft.addToBackStack(null);
                //ft.commit();
                showFragment(key);
            }else{
                FragmentTransaction ft = fm.beginTransaction();
                mCurrentFrag = ChatFragment.newInstance(message, buddy);
                ft.add(R.id.frag_chat, mCurrentFrag);
                //ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void send(Message message){
        mThread.send(message);
    }



    class ChatThread extends Thread {
        public static final String TAG = "ChatThread";
        @Override
        public void run() {
            ArrayList<Profile> profiles = LoginPreferences.GetInstance().loadSharedPreferencesProfileAll(ChatActivity.this);
            mMe = profiles.get(0);
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
            mSocket.on("send_error", onSendError);
            mSocket.connect();
        }

        public void send(Message message) {
            Gson gson = new Gson();
            String json = gson.toJson(message);
            System.out.println("send() 호출됨. json="+json);
            try {
                JSONObject obj = new JSONObject(json);
                /*obj.put("sender", message.getSender());
                obj.put("receiver", message.getReceiver());
                obj.put("contents", message.getMessage());
                obj.put("date", message.getDate());*/
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
                    obj.put("username", mMe.getUsername());
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
                isConnected = false;
                Log.v(TAG, "onConnectError");
                finish();
                //Toast.makeText(getApplicationContext(), "connect error", Toast.LENGTH_LONG).show();
            }
        };

        public Emitter.Listener onSend = new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                Log.v(TAG, "onSend");
                JSONObject obj = (JSONObject) args[0];
                Gson gson = new Gson();
                final Message message = gson.fromJson(obj.toString(), Message.class);
                message.setFrom(Message.PARAM_FROM_OTHER);
                message.setRoom(message.getSender());
                Log.v(TAG, "onSend message="+message);
                DbUtil.insert(ChatActivity.this, message);
                Log.v(TAG, message.toString());
                String sender = message.getSender();
                PushWakeLock.acquireWakeLock(ChatActivity.this, 5000);
                Log.v(TAG, "onSend mCurrentFrag.mBuddyName="+mCurrentFrag.getBuddyName());
                if(!mPaused){ // 포그라운드 러닝상태
                    if(sender.equals(mCurrentFrag.getBuddyName())) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCurrentFrag.updateUI(message);
                            }
                        });
                    }else{
                        final ChatFragment fr = (ChatFragment)ChatFragment.getChatRoom(sender);
                        Log.v(TAG, "onSend sender="+sender+", fr="+fr);
                        if(fr != null){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fr.updateUI(message);
                                }
                            });
                        }
                        vibrateNoti();
                        createMsgNoti(message);
                    }
                }else{
                    if(sender.equals(mCurrentFrag.getBuddyName())) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCurrentFrag.updateUI(message);
                            }
                        });
                    }else{
                        final ChatFragment fr = (ChatFragment)ChatFragment.getChatRoom(sender);
                        Log.v(TAG, "onSend sender="+sender+", fr="+fr);
                        if(fr != null){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fr.updateUI(message);
                                }
                            });
                        }
                    }
                    vibrateNoti();
                    createMsgNoti(message);
                }
            }
        };

        public Emitter.Listener onSendError = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String error_message = (String)args[0];
                Log.v(TAG, "onSendError -> "+error_message);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "메세지를 보내지 못했습니다.\n"+error_message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    void showFragment(String buddyName){
        Iterator iter =ChatFragment.getChatRoom().keySet().iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            Log.v(TAG, "showFragment key="+key);            Fragment fragment = ChatFragment.getChatRoom().get(key);
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
    }

    void createMsgNoti(final Message message){
        //loadBuddyProfile(message);

        Log.v(TAG, "createMsgNoti message="+message.toString());
        RetrofitHelper.loadProfile(this, mMe, message.getSender(), message.getSportsid(), message.getLocationid(), new RetrofitHelper.ProfileListener() {
            @Override
            public void onLoaded(Profile profile) {
                Log.v(TAG, "onLoaded profile="+profile.toString());
                postMsgNoti(message, profile);
            }
        });
    }

    void postMsgNoti(Message message, Profile buddy){
        Intent i = new Intent(ChatActivity.this, ChatActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("Message", message);
        i.putExtra("Buddy", buddy);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_cardiogram);
        String str = message.getSender()+": "+message.getMessage();
        Log.v(TAG, "postMsgNoti str="+str);
        builder.setContentText(str);
        builder.setContentTitle("함께 운동해요");
        builder.setContentIntent(pi);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
    }

    void vibrateNoti(){
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(300);

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
