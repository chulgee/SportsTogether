package com.iron.dragon.sportstogether.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper;

import com.iron.dragon.sportstogether.provider.MyContentProvider;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.DbUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 2016-08-12.
 */
public class ChatRoomListFragment extends Fragment {

    private static final String TAG = "ChatRoomListFragment";
    RecyclerView lv_room;
    MyAdapter mAdapter;
    Set<String> mSet = new HashSet<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.chat_room_frag, container, false);

        lv_room = (RecyclerView) rootView.findViewById(R.id.lv_room);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lv_room.setLayoutManager(llm);
        mAdapter = new MyAdapter(getContext(), null);
        lv_room.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter.notifyDataSetChanged();

        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                Log.v(TAG, "token="+token+", cursor="+cursor);
                super.onQueryComplete(token, cookie, cursor);
                if(cursor != null && cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        String room = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_ROOM));
                        String sender = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_SENDER));
                        String receiver = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_RECEIVER));
                        Log.v(TAG, "room="+room+", sender="+sender+", receiver="+receiver);
                        mAdapter.addItem(room);
                        mAdapter.notifyDataSetChanged();
                    };
                }
            }
        };
        // TIP : inject group by clause into selection
        queryHandler.startQuery(1, null, MyContentProvider.CONTENT_URI, new String[]{DbHelper.COLUMN_ROOM, DbHelper.COLUMN_DATE, DbHelper.COLUMN_SENDER, DbHelper.COLUMN_RECEIVER}
                                , "sender=sender group by "+DbHelper.COLUMN_ROOM, null, " date asc");
    }

    private void removeChatRoom(final int index, String roomName){
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                Log.v(TAG, "onDeleteComplete result="+result);
                mAdapter.removeItem(index);
                mAdapter.notifyItemRemoved(index);
                Toast.makeText(getContext(), "deletion successful", Toast.LENGTH_SHORT).show();
            }
        };
        String where = MyContentProvider.DbHelper.COLUMN_ROOM+"=?";
        queryHandler.startDelete(1, null, MyContentProvider.CONTENT_URI, where, new String[]{roomName});
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<String> items;
        public MyAdapter(Context context, List<String> items){
            if(items == null)
                this.items = new ArrayList<String>();
            else
                this.items = items;
            Log.v(TAG, "items="+this.items);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            Log.v(TAG, "parent="+parent);
            v = LayoutInflater.from(getContext()).inflate(R.layout.chat_room_list_item, parent, false);
            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = lv_room.indexOfChild(v);
                    String item = items.get(position);
                    Toast.makeText(getContext(), "item="+item, Toast.LENGTH_SHORT).show();
                    Profile me = LoginPreferences.GetInstance().getLocalProfile(getContext());
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(item)
                            .message("Conversation get started").date(new Date().getTime()).image(null).build();
                    i.putExtra("Message", message);
                    startActivity(i);
                }
            });*/
            return new ViewCache(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewCache vh = (ViewCache)holder;
            String title = items.get(position);
            vh.tv_title.setText(title+" 와의 대화");
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void addItem(String str){
            items.add(str);
        }

        public void removeItem(int index){
            items.remove(index);
        }

        class ViewCache extends RecyclerView.ViewHolder{
            ImageView iv_thumb;
            TextView tv_title;
            TextView tv_subtitle;
            ImageView iv_delete;
            public ViewCache(View v) {
                super(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        String item = items.get(position);
                        //Toast.makeText(getContext(), "item="+item, Toast.LENGTH_SHORT).show();
                        Profile me = LoginPreferences.GetInstance().getLocalProfile(getContext());
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(item)
                                .message("Conversation get started").date(new Date().getTime()).image(null).build();
                        i.putExtra("Message", message);
                        startActivity(i);
                    }
                });
                iv_thumb = (ImageView)v.findViewById(R.id.iv_thumb);
                tv_title = (TextView)v.findViewById(R.id.tv_room_title);
                tv_subtitle = (TextView)v.findViewById(R.id.tv_room_subtitle);
                iv_delete = (ImageView)v.findViewById(R.id.iv_delete);
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        String item = items.get(position);
                        //Toast.makeText(getContext(), "iv_delete item="+item, Toast.LENGTH_SHORT).show();
                        //DbUtil.delete(getContext(), item);
                        removeChatRoom(position, item);
                    }
                });
            }
        }

    }
}
