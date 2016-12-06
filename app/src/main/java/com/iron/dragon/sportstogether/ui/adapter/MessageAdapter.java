package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulchoice on 2016-11-28.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> mMessages;

    public MessageAdapter(Context context, List<Message> messages) {
        if(messages == null){
            mMessages = new ArrayList<Message>();
        }else
            mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        if(message.getMsgType() == Message.TYPE_CHAT_MSG_NOT_ME){
            holder.tv.setText(message.getUsername());
            holder.tv2.setText(message.getMessage());
        }else{
            holder.tv3.setText(message.getUsername());
            holder.tv4.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void addMessage(Message message){
        mMessages.add(message);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv, tv2, tv3, tv4;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.textView);
            tv2 = (TextView)itemView.findViewById(R.id.textView2);
            tv3 = (TextView)itemView.findViewById(R.id.textView3);
            tv4 = (TextView)itemView.findViewById(R.id.textView4);
        }
    }
}
