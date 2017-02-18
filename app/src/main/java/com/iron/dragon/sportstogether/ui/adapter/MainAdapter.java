package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.enums.SportsType;
import com.iron.dragon.sportstogether.factory.Sports;
import com.iron.dragon.sportstogether.ui.activity.BulletinListActivity;
import com.iron.dragon.sportstogether.ui.activity.LoginActivity;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.iron.dragon.sportstogether.util.Util;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private static final String TAG = "MainAdapter";
    SportsType[] mItems;
    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;

    public MainAdapter(Context context) {
        mContext = context;
        //mItem = items;
        mItems = SportsType.values();
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        mScreenWidth = p.x;
        mScreenHeight = p.y;
        Log.v(TAG, "mScreenWidth="+mScreenWidth+", mScreenHeight="+mScreenHeight);
        final float density = context.getResources().getDisplayMetrics().density;
        Log.v(TAG, "density="+density);
        LoginPreferences.GetInstance().loadSharedPreferencesProfileAll(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sports_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SportsType item = mItems[position];
        int height=0;
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

        if(position==1 || position==(mItems.length-1))
            height = (mScreenHeight/3)/2;
        else
            height = mScreenHeight/3;

        final String str = StringUtil.getStringFromSports(mContext, item.getValue());
        holder.tv.setText(str);
        int px_char = (int)mContext.getResources().getDimension(item.getResid_str_size());
        holder.tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, px_char);

        if(item.getValue() == SportsType.Badminton.getValue()
                || item.getValue() == SportsType.Table_tennis.getValue()
                || item.getValue() == SportsType.Basketball.getValue()) {
            ViewTreeObserver viewTreeObserver = holder.tv.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int w = holder.tv.getWidth();
                    int h = holder.tv.getHeight();
                    int padding = holder.tv.getPaddingLeft() + holder.tv.getPaddingRight();
                    Log.v(TAG, "w="+w+", h="+h+", padding="+padding);
                    holder.tv.setWidth(w/str.length() + padding);
                    holder.tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        holder.iv.setAnimation(animation);
        holder.flayout_sports.getLayoutParams().height = height;
        holder.flayout_sports.getLayoutParams().width = mScreenWidth/2;
        holder.flayout_sports.setBackgroundResource(item.getResid_color());
        holder.flayout_sports.setForeground(new MyStateListDrawable(mContext));
        holder.iv.setImageResource(item.getResid_icon());
    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    @Override
    public long getItemId(int position) {
        SportsType item = mItems[position];
        return item.getValue();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv;
        FrameLayout flayout_sports;

        public ViewHolder(View view){
            super(view);
            iv = (ImageView)view.findViewById(R.id.imageView);
            tv = (TextView)view.findViewById(R.id.textView);
            flayout_sports = (FrameLayout)view.findViewById(R.id.flayout_sports);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SportsType item = mItems[getAdapterPosition()];
                    boolean login = LoginPreferences.GetInstance().IsLogin(v.getContext(), item.getValue());
                    Log.v(TAG, "login="+login);

                    Intent i = new Intent();
                    if(login){
                        Profile profile = LoginPreferences.GetInstance().loadSharedPreferencesProfile(v.getContext(), item.getValue());
                        i.putExtra("MyProfile", profile);
                        i.putExtra("Extra_Sports", item.getValue());
                        i.putExtra("Extra_SportsImg", item.getResid_image());
                        i.setClass(mContext, BulletinListActivity.class);
                    }else{
                        i.putExtra("Extra_Sports", item.getValue());
                        i.setClass(mContext, LoginActivity.class);
                    }
                    mContext.startActivity(i);
                }
            });
        }
    }

    public class MyStateListDrawable extends StateListDrawable {

        public MyStateListDrawable(Context context) {

            //int stateChecked = android.R.attr.state_checked;
            int stateFocused = android.R.attr.state_focused;
            int statePressed = android.R.attr.state_pressed;
            int stateSelected = android.R.attr.state_selected;

            addState(new int[]{ stateSelected      }, context.getResources().getDrawable(R.color.black_overlay));
            addState(new int[]{ statePressed      }, context.getResources().getDrawable(R.color.black_overlay));
            addState(new int[]{ stateFocused      }, context.getResources().getDrawable(R.color.black_overlay));
            //addState(new int[]{-stateFocused, -statePressed, -stateSelected}, context.getResources().getDrawable(R.drawable.nav_btn_default));
        }
    }
}