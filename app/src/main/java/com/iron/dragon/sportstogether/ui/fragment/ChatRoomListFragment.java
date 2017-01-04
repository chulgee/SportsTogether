package com.iron.dragon.sportstogether.ui.fragment;

import android.content.Context;
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

import com.iron.dragon.sportstogether.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016-08-12.
 */
public class ChatRoomListFragment extends Fragment {

    private static final String TAG = "ChatRoomListFragment";
    RecyclerView lv_room;
    MyAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_chatroom, container, false);

        lv_room = (RecyclerView) rootView.findViewById(R.id.lv_room);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lv_room.setLayoutManager(llm);
        mAdapter = new MyAdapter(getActivity(), null);
        lv_room.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter.addItem("room1");
        mAdapter.addItem("room2");
        mAdapter.addItem("room3");
        mAdapter.notifyDataSetChanged();
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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room, null);
            return new ViewCache(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewCache vh = (ViewCache)holder;
            String title = items.get(position);
            vh.tv_title.setText(title);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void addItem(String str){
            items.add(str);
        }

        class ViewCache extends RecyclerView.ViewHolder{
            ImageView iv_thumb;
            TextView tv_title;
            TextView tv_subtitle;
            public ViewCache(View v) {
                super(v);
                iv_thumb = (ImageView)v.findViewById(R.id.iv_thumb);
                tv_title = (TextView)v.findViewById(R.id.tv_room_title);
                tv_subtitle = (TextView)v.findViewById(R.id.tv_room_subtitle);
            }
        }
    }
}
