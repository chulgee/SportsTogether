package com.iron.dragon.sportstogether.adapter;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.abs.Sports;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulchoice on 2016-10-12.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Sports> mDataset;
    private Context mContext;

    public MyAdapter(Context context, List<Sports> items) {
        mDataset = items;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv;
        public TextView tv;

        public ViewHolder(View view){
            super(view);
            iv = (ImageView)view.findViewById(R.id.imageView);
            tv = (TextView)view.findViewById(R.id.textView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.iv.setImageResource(R.drawable.a);
        int id;
        int res = 0;
        id = (int)getItemId(position);

        switch(id){
            case 0:
                res = R.drawable.badminton;
                break;
            case 1:
                res = R.drawable.basketball;
                break;
            case 2:
                res = R.drawable.pingpong;
                break;
            case 3:
                res = R.drawable.t;
                break;
        }

        holder.tv.setText(mDataset.get(position).getName());
        holder.iv.setImageResource(res);
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        holder.iv.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if(mDataset == null)
            ret = 0;
        else
            ret = mDataset.size();
        return ret;
    }

    @Override
    public long getItemId(int position) {
        Sports sports = mDataset.get(position);
        return sports.getId();
    }
}