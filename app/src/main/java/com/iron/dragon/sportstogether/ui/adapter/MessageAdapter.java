package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.util.Util;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chulchoice on 2016-11-28.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_MSG = 0;
    public static final int VIEW_TYPE_LOG = 1;
    public static final int VIEW_TYPE_ACTION = 2;
    private static final String TAG = "MessageAdapter";
    private List<Message> mMessages;
    RecyclerView rView;
    Context context;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        if(messages == null){
            mMessages = new ArrayList<Message>();
        }else
            mMessages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        Log.v(TAG, "viewtype="+viewType);
        if(viewType == Message.TYPE_CHAT_MESSAGE){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null);
            return new ViewHolder(v);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_action, null);
            return new ViewHolder_action(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        Message message = mMessages.get(position);
        if(type == Message.TYPE_CHAT_MESSAGE){
            ViewHolder vh = (ViewHolder)holder;
            String url = "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_profile?filename=" + message.getImage();
            Logger.d("url = " + url);
            if(message.getMsgType() == Message.PARAM_MSG_OUT){
                vh.rl1.setVisibility(View.VISIBLE);
                vh.tv1.setText(message.getSender());
                vh.tv2.setText(message.getMessage());
                vh.tv5.setText(Util.getStringTime(message.getDate()));
                vh.rl2.setVisibility(View.GONE);
                if(message.getImage() != null){
                    Picasso.with(context).load(url).resize(50, 50)
                            .centerCrop()
                            .into(vh.civ1);
                }else{
                    vh.civ1.setImageResource(R.drawable.buddy);
                }
            }else{
                vh.rl2.setVisibility(View.VISIBLE);
                vh.tv3.setText(message.getSender());
                vh.tv4.setText(message.getMessage());
                vh.tv6.setText(Util.getStringTime(message.getDate()));
                vh.rl1.setVisibility(View.GONE);
                if(message.getImage() != null){
                    Picasso.with(context).load(url).resize(50, 50)
                            .centerCrop()
                            .into(vh.civ1);
                }
            }

        }else{
            ViewHolder_action vh = (ViewHolder_action) holder;
            vh.tv1.setText(message.getMessage());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void addMessage(Message message){
        mMessages.add(message);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv1, tv2, tv3, tv4, tv5, tv6;
        CircleImageView civ1, civ2;
        RelativeLayout rl1, rl2;
        public ViewHolder(View itemView) {
            super(itemView);
            tv1 = (TextView)itemView.findViewById(R.id.textView1);
            tv2 = (TextView)itemView.findViewById(R.id.textView2);
            tv3 = (TextView)itemView.findViewById(R.id.textView3);
            tv4 = (TextView)itemView.findViewById(R.id.textView4);
            tv5 = (TextView)itemView.findViewById(R.id.textView5);
            tv6 = (TextView)itemView.findViewById(R.id.textView6);
            civ1 = (CircleImageView)itemView.findViewById(R.id.imageView);
            civ2 = (CircleImageView)itemView.findViewById(R.id.imageView2);
            rl1 = (RelativeLayout)itemView.findViewById(R.id.layout1);
            rl2 = (RelativeLayout)itemView.findViewById(R.id.layout2);
        }
    }

    public class ViewHolder_action extends RecyclerView.ViewHolder{
        TextView tv1;
        public ViewHolder_action(View itemView) {
            super(itemView);
            tv1 = (TextView)itemView.findViewById(R.id.tv_chat_item_action);
        }
    }
}
