package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.ui.adapter.item.EventItem;
import com.iron.dragon.sportstogether.ui.adapter.item.HeaderItem;
import com.iron.dragon.sportstogether.ui.adapter.item.ListItem;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.iron.dragon.sportstogether.util.Util;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by seungyong on 2016-11-03.
 */

public class BulletinRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ListItem> malBulletin;
    private OnItemLongClickListener IonItemLongClickListener;
    private Context mContext;

    public BulletinRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    public void addItem(ListItem bulletin) {
        if(bulletin.getType() == ListItem.TYPE_HEADER && malBulletin.contains(bulletin)) {
            return;
        }
        malBulletin.add(bulletin);
        notifyDataSetChanged();
    }
    public void setItem(ArrayList<ListItem> bulletins) {
        malBulletin = bulletins;
        notifyDataSetChanged();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final CircleImageView mivProfileImage;
        private final View mView;
        private final TextView mtvNickName;
        private final TextView mtvComment;
        private final TextView mtvTime;
        public ViewHolderItem(View itemView) {
            super(itemView);
            mView = itemView;
            mtvNickName = (TextView) itemView.findViewById(R.id.tvNickName);
            mtvComment = (TextView) itemView.findViewById(R.id.tvComment);
            mivProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
            mtvTime = (TextView)itemView.findViewById(R.id.tvTime);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            onItemHolderLongClick(this);
            return true;
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        View mView;
        TextView mtvDate;
        public ViewHolderHeader(View itemView) {
            super(itemView);
            mView = itemView;
            mtvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }


    @Override
    public int getItemViewType(int position)
    {
        return malBulletin.get(position).getType();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_header, parent, false);
            return new ViewHolderHeader(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_item, parent, false);
            return new ViewHolderItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        if (type == ListItem.TYPE_HEADER) {
            final ViewHolderHeader viewHolderHeader = (ViewHolderHeader)holder;
            HeaderItem header = (HeaderItem) malBulletin.get(position);
            viewHolderHeader.mtvDate.setText(header.getDate());
        } else {
            final ViewHolderItem viewHolderItem = (ViewHolderItem)holder;
            EventItem item = (EventItem) malBulletin.get(position);
            viewHolderItem.mtvNickName.setText(item.getBulletin().getUsername());
            viewHolderItem.mtvComment.setText(item.getBulletin().getComment());
            viewHolderItem.mtvTime.setText(Util.getStringTime(item.getBulletin().getDate()));
            if(StringUtil.isEmpty(((EventItem) malBulletin.get(position)).getBulletin().getImage())) {
                Picasso.with(mContext).load(R.drawable.default_user).resize(50, 50)
                        .centerCrop()
                        .into(viewHolderItem.mivProfileImage);
            } else {
                String url = "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload?filename=" + ((EventItem) malBulletin.get(position)).getBulletin().getImage();
                Logger.d("url = " + url);
                Picasso.with(mContext).load(url).resize(50, 50)
                        .centerCrop()
                        .into(viewHolderItem.mivProfileImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return malBulletin == null ? 0 : malBulletin.size();
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(AdapterView<?> parent, View itemView, int adapterPosition, long itemId);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        IonItemLongClickListener = listener;
    }

    private void onItemHolderLongClick(ViewHolderItem itemHolder) {
        if (IonItemLongClickListener != null) {
            IonItemLongClickListener.onItemLongClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }
}