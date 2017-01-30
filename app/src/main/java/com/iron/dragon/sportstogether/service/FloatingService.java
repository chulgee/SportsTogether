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
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.ui.activity.BuddyActivity;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.fragment.ChatFragment;

import pl.droidsonroids.gif.GifImageView;

public class FloatingService extends Service implements View.OnTouchListener, View.OnClickListener{
    private static final String TAG = "FloatingService";
    int start_x, start_y;
    int prev_x, prev_y;
    private WindowManager.LayoutParams mParams;
    WindowManager wm;
    View view;
    Message mMessage;
    Profile mBuddy;
    boolean isMove = false;
    //AnimationDrawable mNewFriendAni;

    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        view = View.inflate(this, R.layout.floating_buddy, null);
        mMessage = (Message)intent.getSerializableExtra(ChatFragment.PARAM_FRAG_MSG);
        mBuddy = (Profile)intent.getSerializableExtra("Buddy");

        Log.v(TAG, "onStartCommand intent="+intent);

        initView();

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
                isMove = false;
                return false;

            case MotionEvent.ACTION_MOVE:
                int x = (int)(event.getRawX() - start_x);//이동거리
                int y = (int)(event.getRawY() - start_y);//이동거리
                mParams.x = prev_x + x;
                mParams.y = prev_y + y;
                wm.updateViewLayout(view, mParams);
                isMove = true;
                return true;

            case MotionEvent.ACTION_UP:
                return isMove;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_floating_cancel){
            wm.removeView(view);
            Toast.makeText(FloatingService.this, "just destroy it,", Toast.LENGTH_SHORT).show();
            stopSelf();
        }else if(v.getId() == R.id.tv_floating_title){
            wm.removeView(view);
            Toast.makeText(FloatingService.this, "go to buddy list,", Toast.LENGTH_SHORT).show();
            stopSelf();
            Intent i = new Intent(FloatingService.this, BuddyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("Buddy", mBuddy);
            Log.v(TAG, "Buddy : "+mBuddy.toString());
            startActivity(i);
        }else if(v.getId() == R.id.iv_floating_image){
            Toast.makeText(FloatingService.this, "텍스트를 눌러주셈~", Toast.LENGTH_SHORT).show();
        }
    }

    void initView(){

        ImageView iv_floating_image;
        TextView tv_floating_title;
        ImageView iv_floating_cancel;

        iv_floating_image = (ImageView)view.findViewById(R.id.iv_floating_image);
        iv_floating_image.setOnTouchListener(this);
        iv_floating_image.setOnClickListener(this);

        tv_floating_title = (TextView)view.findViewById(R.id.tv_floating_title);
        tv_floating_title.setOnTouchListener(this);
        tv_floating_title.setOnClickListener(this);

        iv_floating_cancel = (ImageView)view.findViewById(R.id.iv_floating_cancel);
        iv_floating_cancel.setOnTouchListener(this);
        iv_floating_cancel.setOnClickListener(this);

        /*mNewFriendAni = (AnimationDrawable)iv_new_friend.getBackground();
        LinearLayout layout = new LinearLayout(this);
        layout.addView(view);
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
        });*/

        mParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                , PixelFormat.TRANSPARENT);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        wm.addView(view, mParams);
    }
}
