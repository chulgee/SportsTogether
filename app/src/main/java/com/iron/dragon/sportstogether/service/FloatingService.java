package com.iron.dragon.sportstogether.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.IBinder;
import android.support.v7.view.WindowCallbackWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.fragment.ChatFragment;

public class FloatingService extends Service implements View.OnTouchListener {
    private static final String TAG = "FloatingService";
    int start_x, start_y;
    int prev_x, prev_y;
    private WindowManager.LayoutParams mParams;
    WindowManager wm;
    View view;
    Message mMessage;
    ImageView iv_new_friend;
    AnimationDrawable mNewFriendAni;
    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        view = View.inflate(this, R.layout.floating_buddy, null);
        view.setOnTouchListener(this);

        iv_new_friend = (ImageView)view.findViewById(R.id.iv_new_friend);
        iv_new_friend.setBackgroundResource(R.drawable.run);
        //mNewFriendAni = (AnimationDrawable)iv_new_friend.getBackground();
        mMessage = (Message)intent.getSerializableExtra(ChatFragment.PARAM_FRAG_MSG);
        Log.v(TAG, "onStartCommand intent="+intent);
        wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        //LinearLayout layout = new LinearLayout(this);
        //layout.addView(view);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.v(TAG, "onViewAttachedToWindow");
                //mNewFriendAni.start();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.v(TAG, "onViewDetachedFromWindow");
            }
        });
        TextView tv = (TextView)view.findViewById(R.id.tv_floating_title);
        tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(FloatingService.this, ChatActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("Message", mMessage);
                        startActivity(i);
                        wm.removeView(view);
                        Toast.makeText(FloatingService.this, "h...", Toast.LENGTH_SHORT).show();
                    }
                });
        ImageView iv = (ImageView)view.findViewById(R.id.iv_floating_cancel);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(view);
                Toast.makeText(FloatingService.this, "hello,", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });
        //mParams = new WindowManager.LayoutParams(300, 300, WindowManager.LayoutParams.TYPE_PHONE,
          //      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                , PixelFormat.TRANSPARENT);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        wm.addView(view, mParams);
        return START_STICKY;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                start_x = (int)event.getRawX();
                start_y = (int)event.getRawY();
                prev_x = mParams.x;
                prev_y = mParams.y;
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int)(event.getRawX() - start_x);//이동거리
                int y = (int)(event.getRawY() - start_y);//이동거리
                mParams.x = prev_x + x;
                mParams.y = prev_y + y;
                wm.updateViewLayout(view, mParams);
                break;
            case MotionEvent.ACTION_UP:
                View tv = view.findViewById(R.id.tv_floating_title);

                break;
        }

        return true;
    }
}
