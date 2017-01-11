package com.iron.dragon.sportstogether.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.fragment.ChatFragment;

public class FloatingService extends Service implements View.OnTouchListener {
    int start_x, start_y;
    int prev_x, prev_y;
    private WindowManager.LayoutParams mParams;
    WindowManager wm;
    View view;
    Message mMessage;

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
        view = View.inflate(this, R.layout.floating_buddy, null);
        view.setOnTouchListener(this);
        wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        mParams = new WindowManager.LayoutParams(300, 300, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        wm.addView(view, mParams);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMessage = (Message)intent.getSerializableExtra(ChatFragment.PARAM_FRAG_MSG);
        return super.onStartCommand(intent, flags, startId);
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
                View iv = view.findViewById(R.id.iv_floating_cancel);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wm.removeView(view);
                        Toast.makeText(FloatingService.this, "hello,", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

        return true;
    }
}
