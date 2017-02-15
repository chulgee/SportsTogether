package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
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
import com.iron.dragon.sportstogether.ui.activity.BulletinListActivity;
import com.iron.dragon.sportstogether.ui.activity.LoginActivity;
import com.iron.dragon.sportstogether.util.StringUtil;

import java.util.Random;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private static final String TAG = "MainAdapter";
    SportsType[] mItems;
    private Context mContext;
    private int mScreenWidth;

    private final Random mRandom;

    public MainAdapter(Context context) {
        mContext = context;
        //mItem = items;
        mItems = SportsType.values();
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        mScreenWidth = p.x;
        mRandom = new Random();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sports_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SportsType item = mItems[position];

        double positionHeight = getPositionRatio(position);

        int height=0;
        /*if(position==1 || position==(mItems.length-1))
            height = 400;
        else
            height = 800;*/
        holder.tv.setText(StringUtil.getStringFromSports(mContext, item.getValue()));
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        holder.iv.setAnimation(animation);

        height = (int) (positionHeight * 400);
        holder.flayout_sports.getLayoutParams().height = height;
        holder.flayout_sports.getLayoutParams().width = mScreenWidth / 2;
//        Logger.d("position = " + position + " height =" + height + "mScreenWidth = " + mScreenWidth);

        holder.flayout_sports.setBackgroundResource(item.getResid_color());
        holder.flayout_sports.setForeground(new MyStateListDrawable(mContext));
        holder.iv.setImageResource(item.getResid_icon());

    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }
    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() ) + 1.0; // height will be 1.0 - 1.5 the width
    }
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

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