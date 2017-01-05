package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Bulletin_image;
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

public class BulletinRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int FOOTER_VIEW = 2;  //listitem listheader is 0 and 1
    private int index; // Position in adapter

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    private ArrayList<ListItem> malBulletin;
    private OnItemLongClickListener IonItemLongClickListener;
    private OnFooterItemClickListener IonFooterItemClickListener;
    private Context mContext;

    public BulletinRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }


    public void addItem(ListItem bulletin) {
        int insertIndex = 1;
        if(bulletin.getType() == ListItem.TYPE_HEADER && malBulletin.contains(bulletin)) {
            return;
        } else if(bulletin.getType() == ListItem.TYPE_HEADER && !malBulletin.contains(bulletin)) {
            insertIndex = 0;
        }
        malBulletin.add(insertIndex, bulletin);
        notifyItemInserted(insertIndex);

    }

    public void setItem(ArrayList<ListItem> bulletins) {
        malBulletin = bulletins;
        notifyDataSetChanged();
    }

    public ListItem getItem(int index){
        return malBulletin.get(index);
    }

    public void resetItems() {
        malBulletin.clear();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final CircleImageView mivProfileImage;
        private final View mView;
        private final TextView mtvNickName;
        private final TextView mtvComment;
        private final TextView mtvTime;
        private final LinearLayout mllAttachImage;
        public ViewHolderItem(View itemView) {
            super(itemView);
            mView = itemView;
            mtvNickName = (TextView) itemView.findViewById(R.id.tvNickName);
            mtvComment = (TextView) itemView.findViewById(R.id.tvComment);
            mivProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
            mtvTime = (TextView)itemView.findViewById(R.id.tvTime);
            mllAttachImage = (LinearLayout)itemView.findViewById(R.id.llAttachImage);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            onItemHolderLongClick(this);
            index = getAdapterPosition();
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

    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        private final Button mbtMore;
        public FooterViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mbtMore =  (Button) itemView.findViewById(R.id.btMore);
            mbtMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onFooterItemHolderClick(this);
        }
    }

    public interface OnFooterItemClickListener {
        void onItemClick(AdapterView<?> parent, View itemView);
    }

    public void setOnFooterItemClickListener(OnFooterItemClickListener listener) {
        IonFooterItemClickListener = listener;
    }

    private void onFooterItemHolderClick(FooterViewHolder footerViewHolder) {
        if (IonFooterItemClickListener != null) {
            IonFooterItemClickListener.onItemClick(null, footerViewHolder.itemView);
        }
    }


    @Override
    public int getItemViewType(int position)
    {
        if (position == malBulletin.size()) {
            return FOOTER_VIEW;
        }
        return malBulletin.get(position).getType();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_footer, parent, false);
            return new FooterViewHolder(view);
        } else if (viewType == ListItem.TYPE_HEADER) {
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
        if( type == FOOTER_VIEW) {

        } else if (type == ListItem.TYPE_HEADER) {
            final ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            HeaderItem header = (HeaderItem) malBulletin.get(position);
            viewHolderHeader.mtvDate.setText(header.getDate());
        } else {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            EventItem item = (EventItem) malBulletin.get(position);
            viewHolderItem.mtvNickName.setText(item.getBulletin().getUsername());
            viewHolderItem.mtvComment.setText(item.getBulletin().getComment());
            viewHolderItem.mtvTime.setText(Util.getStringTime(item.getBulletin().getDate()));
            if (StringUtil.isEmpty(((EventItem) malBulletin.get(position)).getBulletin().getImage())) {
                Picasso.with(mContext).load(R.drawable.default_user).resize(50, 50)
                        .centerCrop()
                        .into(viewHolderItem.mivProfileImage);
            } else {
                String url = "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_profile?filename=" + item.getBulletin().getImage();
                Logger.d("url = " + url);
                Picasso.with(mContext).load(url).resize(50, 50)
                        .centerCrop()
                        .into(viewHolderItem.mivProfileImage);
            }

            if (item.getBulletin().getBulletin_image() != null && item.getBulletin().getBulletin_image().size() > 0 && viewHolderItem.mllAttachImage.getChildCount() == 0) {
                for (Bulletin_image image : item.getBulletin().getBulletin_image()) {
                    ImageView imageview = new ImageView(mContext);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMarginEnd(20);
                    imageview.setLayoutParams(lp);
                    String url = "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_bulletin?filename=" + image.getBulletinImg();
                    Picasso.with(mContext).load(url).resize(450, 450)
                            .centerCrop()
                            .into(imageview);
                    viewHolderItem.mllAttachImage.addView(imageview);
                }
            }
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.getItemViewType() == ListItem.TYPE_EVENT) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            viewHolderItem.mllAttachImage.removeAllViews();
        }
    }

    @Override
    public int getItemCount() {
        if (malBulletin == null) {
            return 0;
        }
        if (malBulletin.size() == 0) {
            return 1;
        }
        return (malBulletin.size() + 1);
//        return malBulletin == null ? 0 : malBulletin.size();
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