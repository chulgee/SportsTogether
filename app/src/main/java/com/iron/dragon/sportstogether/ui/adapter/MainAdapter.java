package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sports_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SportsType item = mItems[position];
        int height=0;
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

        Log.v(TAG, "30="+Util.getDpToPixel(mContext, 35)+", 50="+Util.getDpToPixel(mContext, 50));
        //Log.v(TAG, "w="+Util.getPixelToDp(mContext, wm.getDefaultDisplay().getWidth())+", h="+Util.getPixelToDp(mContext, wm.getDefaultDisplay().getHeight()));

        if(position==1 || position==(mItems.length-1))
            height = mScreenHeight/4;
        else
            height = mScreenHeight/2;
        if(item.getValue() == SportsType.Badminton.getValue()) {
            holder.tv.setWidth(Util.getDpToPixel(mContext, SportsType.Badminton.getResid_str_size()));
        }else if(item.getValue() == SportsType.Table_tennis.getValue()){
            holder.tv.setWidth(Util.getDpToPixel(mContext, SportsType.Table_tennis.getResid_str_size()));
        }else if(item.getValue() == SportsType.Basketball.getValue()){
            holder.tv.setWidth(Util.getDpToPixel(mContext, SportsType.Basketball.getResid_str_size()));
        }

        /*if(item.getValue() == SportsType.Badminton.getValue()) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP|Gravity.LEFT);
            holder.tv.setLayoutParams(lp);
            holder.tv.setRotation(-90);
        }*/
        holder.tv.setTextSize(StringUtil.getStringSizeFromSports(mContext, item.getValue()));
        holder.tv.setText(StringUtil.getStringFromSports(mContext, item.getValue()));

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