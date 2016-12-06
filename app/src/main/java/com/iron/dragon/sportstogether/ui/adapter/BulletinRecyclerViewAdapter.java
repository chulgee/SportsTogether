package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;

import java.util.ArrayList;

/**
 * Created by seungyong on 2016-11-03.
 */

public class BulletinRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Bulletin> malBulletin;

    private Context mContext;
    public BulletinRecyclerViewAdapter(Context context, ArrayList<Bulletin> malBulletin) {
        this.mContext = context;
        this.malBulletin = malBulletin;
    }

    public class ViewHolderMe extends RecyclerView.ViewHolder {
        View mView;
        TextView mtvComment;
        TextView mtvDate;
        public ViewHolderMe(View itemView) {
            super(itemView);
            mView = itemView;
            mtvComment = (TextView) itemView.findViewById(R.id.tvComment);
            mtvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

    public class ViewHolderThem extends RecyclerView.ViewHolder {
        View mView;
        TextView mtvNickName;
        TextView mtvComment;
        TextView mtvDate;
        public ViewHolderThem(View itemView) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_item_me, parent, false);
            return new ViewHolderMe(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_item_them, parent, false);
            return new ViewHolderThem(view);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 1) {
            ViewHolderMe viewHolderMe = (ViewHolderMe)holder;
            viewHolderMe.mtvComment.setText(malBulletin.get(position).getComment());
            //viewHolderMe.mtvDate.setText(malBulletin.get(position).getDate());
        } else {
            ViewHolderThem viewHolderThem = (ViewHolderThem)holder;
            viewHolderThem.mtvNickName.setText(malBulletin.get(position).getUsername());
            viewHolderThem.mtvComment.setText(malBulletin.get(position).getComment());
            //viewHolderThem.mtvDate.setText(malBulletin.get(position).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return malBulletin.size();
    }
}
