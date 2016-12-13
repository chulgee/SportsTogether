package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.ui.adapter.MessageAdapter;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.util.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {
    Socket mSocket;
    RecyclerView mListView;
    MessageAdapter messageAdapter;

    Profile buddy;
    Profile me;

    TextView buddyAlias;
    Button sendButton;
    EditText etChatMessage;
    String chatMessage;
    boolean isConnected = false;
    ChatThread thread;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mListView = (RecyclerView) findViewById(R.id.chatListView);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(this, null);
        mListView.setAdapter(messageAdapter);

        thread = new ChatThread();
        thread.start();

        buddyAlias = (TextView) findViewById(R.id.buddyAlias);
        sendButton = (Button)findViewById(R.id.sendButton);
        etChatMessage = (EditText)findViewById(R.id.etChatMessage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 대화 아이템 만들기
                chatMessage = etChatMessage.getText().toString();
                //ChatItem ci = new ChatItem(ChatItem.WRITER_TYPE_ME, me.getId(), me.getId(), chatMessage, new Date());
                //chatListAdapter.addItem(ci);
                Message message = new Message.Builder(Message.TYPE_CHAT_MESSAGE).msgType(Message.TYPE_CHAT_MSG_ME).username(me.getUsername()).message(chatMessage).date(new Date()).build();
                messageAdapter.addMessage(message);
                messageAdapter.notifyDataSetChanged();

                // buddy에게 대화 아이템 보내기.
                /*JSONObject obj = new JSONObject();
                try {
                    obj.put("sender", me.getId());
                    obj.put("receiver", buddy.getId());
                    obj.put("contents", chatMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.send("send", obj);*/
                thread.send(me.getUsername(), buddy.getUsername(), chatMessage);
            }
        });


        //chatListAdapter.addItem(new ChatItem(ChatItem.WRITER_TYPE_YOU, "test01", "윤정", "어디쯤이야?", new Date()));
        //chatListAdapter.addItem(new ChatItem(ChatItem.WRITER_TYPE_YOU, "test01", "윤정", "다들 기다리고 있어. 빨리 와~", new Date()));
        //chatListAdapter.addItem(new ChatItem(ChatItem.WRITER_TYPE_ME, "test02", "종철이", "거의 다왔어 ^^ 보고싶다\n친구들!", new Date()));


        Intent intent = getIntent();
        processIntent(intent);
    }

    public Emitter.Listener onConnectError = new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            Toast.makeText(getApplicationContext(), "connect error", Toast.LENGTH_LONG).show();
        }
    };



    public void println(String data){
        System.out.println(data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);

        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isConnected = false;
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            buddy = (Profile) intent.getSerializableExtra("BuddyProfile");
            me = (Profile)intent.getSerializableExtra("MyProfile");
            buddyAlias.setText(buddy.getUsername());
        }
    }

    class ChatThread extends Thread {
        @Override
        public void run() {
            createSocket();
        }

        private void createSocket(){
            // 소켓 생성 및 연결
            //ChatApplication app = (ChatApplication) getApplication();
            IO.Options options = new IO.Options();
            options.forceNew = true;
            try {
                mSocket = IO.socket(Const.MAIN_URL, options);//app.getSocket();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener(){
                @Override
                public void call(Object... args) {
                    isConnected = false;
                    Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();

                }
            });
            mSocket.on("send", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    String paramSender = null;
                    String paramReceiver = null;
                    String paramContents = null;
                    try {
                        paramSender = obj.getString("sender");
                        paramReceiver = obj.getString("receiver");
                        paramContents = obj.getString("contents");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    println("sender:"+paramSender+", receiver:"+paramReceiver+", contents:"+paramContents);
                    Message message = new Message.Builder(Message.TYPE_CHAT_MESSAGE).msgType(Message.TYPE_CHAT_MSG_NOT_ME).username(paramSender).message(paramContents).date(new Date()).build();
                    messageAdapter.addMessage(message);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.notifyDataSetChanged();
                        }
                    });

                }
            });
            mSocket.connect();
            println("connecting");
        }

        public void send(String sender, String receiver, String contents) {
            println("send() 호출됨.");

            try {
                JSONObject obj = new JSONObject();
                obj.put("sender", sender);
                obj.put("receiver", receiver);
                obj.put("contents", contents);

                mSocket.emit("send", obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Emitter.Listener onConnect = new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                println("Socket.IO 서버에 연결되었습니다.");

                println("Socket.IO 서버에 연결되었습니다.");

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("username", me.getUsername());
                    obj.put("regid", me.getRegid());
                    obj.put("sportsid", me.getSportsid());
                    obj.put("locationid", me.getLocationid());

                    mSocket.emit("login", obj);
                    //Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_LONG).show();
                    isConnected = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected){
                        println("Socket.IO 서버에 연결되었습니다.");

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("id", me.getId());
                            obj.put("password", me.getPassword());
                            obj.put("alias", me.getAlias());
                            obj.put("today", me.getToday());

                            mSocket.emit("login", obj);
                            Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_LONG).show();
                            isConnected = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });*/
            }
        };
    }
}
