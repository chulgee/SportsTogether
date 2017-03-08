package com.iron.dragon.sportstogether.ui.adapter.item;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.ui.activity.BuddyActivity;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenter;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.iron.dragon.sportstogether.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by P10950 on 2017-03-08.
 */

public class BuddyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BuddyAdapter";
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    private List<Profile> mItems;
    private BuddyActivity mActivity;
    private BuddyPresenter mPresenter;
    private Profile mBuddy;

    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void onItemClick(View v, Profile item);
        public void onItemChatClick(View v, Profile item);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public BuddyAdapter(Context context, BuddyPresenter presenter, Profile buddy){
        mPresenter = presenter;
        mActivity = (BuddyActivity)context;
        mItems = new ArrayList<Profile>();
        mBuddy = buddy;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if(viewType == VIEW_TYPE_HEADER){
            v = LayoutInflater.from(mActivity).inflate(R.layout.buddy_list_header, parent, false);
            return new HeaderViewHolder(v);
        }else if(viewType == VIEW_TYPE_ITEM){
            v = LayoutInflater.from(mActivity).inflate(R.layout.buddy_list_item, parent, false);
            return new ItemViewHolder(v);
        }
        return new ItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderViewHolder){
            HeaderViewHolder hHolder = (HeaderViewHolder)holder;
            hHolder.tv_buddy_header_title.setText("동네친구 목록");
            hHolder.tv_buddy_header_subtitle.setText(StringUtil.getStringFromLocation(mActivity, mBuddy.getLocationid())+" 친구들입니다.");
        }else if(holder instanceof ItemViewHolder){
            Profile item = mItems.get(position-1);
            ItemViewHolder iHolder = (ItemViewHolder)holder;
            iHolder.tv_title.setText(item.getUsername());
            if(item.getUnread() > 0){
                iHolder.tv_buddy_unread.setVisibility(View.VISIBLE);
            }else
                iHolder.tv_buddy_unread.setVisibility(View.GONE);
            iHolder.civ_thumb.setImageResource(R.drawable.default_user);
            if(item.getImage() != null && !item.getImage().isEmpty()){
                String url = Const.MAIN_URL + "/upload_profile?filename=" + item.getImage();
                Log.v(TAG, "onBindViewHolder url:"+url);
                Picasso.with(mActivity).load(url).placeholder(R.drawable.default_user).resize(50,50).centerInside().into(iHolder.civ_thumb);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0?VIEW_TYPE_HEADER:VIEW_TYPE_ITEM);
    }

    public Profile getItem(int index){
        return mItems.get(index);
    }

    public void setItem(List<Profile> buddies){
        mItems = buddies;
    }

    public void addItem(Profile item){
        mItems.add(item);
    }

    public void removeItem(int index){
        mItems.remove(index);
    }

    public boolean checkUnread(SharedPreferences sharedPreferences, String key){
        Iterator<Profile> iter = mItems.iterator();
        while(iter.hasNext()){
            Profile item = (Profile)iter.next();
            Log.v(TAG, "key="+key+", item.room="+item.getUsername());
            if(item.getUsername().equals(key)){
                int count = sharedPreferences.getInt(key, 0);
                item.setUnread(count);
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView tv_buddy_header_title;
        TextView tv_buddy_header_subtitle;

        public HeaderViewHolder(View v) {
            super(v);
            tv_buddy_header_title = (TextView) v.findViewById(R.id.tv_buddy_header_title);
            tv_buddy_header_subtitle = (TextView) v.findViewById(R.id.tv_buddy_header_subtitle);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        View v_row;
        CircleImageView civ_thumb;
        TextView tv_title;
        TextView tv_buddy_unread;
        TextView tv_subtitle;
        ImageView iv_chat;

        public ItemViewHolder(View v) {
            super(v);
            civ_thumb = (CircleImageView)v.findViewById(R.id.civ_thumb);
            tv_title = (TextView)v.findViewById(R.id.tv_buddy_title);
            tv_buddy_unread = (TextView)v.findViewById(R.id.tv_buddy_unread);
            tv_subtitle = (TextView)v.findViewById(R.id.tv_buddy_subtitle);
            iv_chat = (ImageView)v.findViewById(R.id.iv_chat);
            v_row = v;
            v_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Profile item = mItems.get(getAdapterPosition()-1);
                    Util.setUnreadBuddy(mActivity, item.getUsername(), 0);
                    onItemClickListener.onItemClick(v, item);
                }
            });
            iv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Profile item = mItems.get(getAdapterPosition()-1);
                    Util.setUnreadBuddy(mActivity, item.getUsername(), 0);
                    onItemClickListener.onItemChatClick(v, item);
                }
            });
        }
    }
}
