package com.iron.dragon.sportstogether.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.retrofit.BulletinInfo;

import java.util.ArrayList;

/**
 * Created by seungyong on 2016-11-03.
 */

public class BulletinRecyclerViewAdapter extends RecyclerView.Adapter<BulletinRecyclerViewAdapter.ViewHolder>{
    private ArrayList<BulletinInfo> malBulletin;

    private Context mContext;
    public BulletinRecyclerViewAdapter(Context context, ArrayList<BulletinInfo> malBulletin) {
        this.mContext = context;
        this.malBulletin = malBulletin;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mtvNickName;
        TextView mtvComment;
        TextView mtvDate;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mtvNickName = (TextView) itemView.findViewById(R.id.tvNickName);
            mtvComment = (TextView) itemView.findViewById(R.id.tvComment);
            mtvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }


    }

    @Override
    public int getItemViewType(int position)
    {
        return malBulletin.get(position).getUsername().equals(LoginPreferences.GetInstance().GetLocalProfileUserName(mContext))? 1 : 2;
    }


    @Override
    public BulletinRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_item_me, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_item_them, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BulletinRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mtvNickName.setText(malBulletin.get(position).getUsername());
        holder.mtvComment.setText(malBulletin.get(position).getComment());
        holder.mtvDate.setText(malBulletin.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return malBulletin.size();
    }
}
