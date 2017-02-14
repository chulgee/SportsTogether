package com.iron.dragon.sportstogether.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.provider.MyContentProvider;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.view.UnreadView;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_DATE;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_LOCATIONID;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_SPORTSID;

/**
 * Created by user on 2016-08-12.
 */
public class ChatRoomListFragment extends Fragment {

    private static final String TAG = "ChatRoomListFragment";
    RecyclerView lv_room;
    MyAdapter mAdapter;
    Map<String, Bitmap> mAvatarMap = new HashMap<String, Bitmap>();
    private int sportsid;
    SharedPreferences mPref;

    public interface OnClickCallback{
        void rowOnClicked(View v, int position);
        void deleteOnClicked(View v, int position);
    }

    SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            boolean found = false;
            Iterator<Item> iter = mAdapter.mItems.iterator();
            while(iter.hasNext()){
                Item item = (Item)iter.next();
                Log.v(TAG, "key="+key+", item.room="+item.room);
                if(item.room.equals(key)){
                    int count = sharedPreferences.getInt(key, 0);
                    item.setUnread(count);
                    mAdapter.notifyDataSetChanged();
                    found = true;
                }
            }
            if(!found){
                loadChatRoom();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.chat_room_frag, container, false);

        init(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadChatRoom();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mPref != null){
            mPref.unregisterOnSharedPreferenceChangeListener(mPrefListener);
        }
    }

    public void init(View rootView){

        lv_room = (RecyclerView) rootView.findViewById(R.id.lv_room);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lv_room.setLayoutManager(llm);
        mAdapter = new MyAdapter(getContext(), null, new OnClickCallback(){

            @Override
            public void rowOnClicked(View v, int position) {
                Item item = mAdapter.getItem(position);
                ArrayList<Profile> profiles = LoginPreferences.GetInstance().loadSharedPreferencesProfileAll(getActivity());
                final Profile me = profiles.get(0);
                Log.v(TAG, "rowOnClicked item="+item.toString());
                RetrofitHelper.loadProfile(getActivity(), me, item.room, item.sportsid, item.locationid, new RetrofitHelper.PROFILE_CALLBACK() {
                    @Override
                    public void onLoaded(Profile profile) {
                        Log.v(TAG, "onLoaded profile="+profile.toString());
                        Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(profile.getUsername())
                                .message("Conversation get started").date(new Date().getTime()).image(null).build();
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra("Message", message);
                        i.putExtra("Buddy", profile);
                        getActivity().startActivity(i);
                    }
                });
            }

            @Override
            public void deleteOnClicked(View v, int position) {
                removeChatRoom(position, mAdapter.getItem(position).room);
            }

        });
        lv_room.setAdapter(mAdapter);

        mPref = getActivity().getSharedPreferences(Const.PREF_UNREAD_CHAT, Context.MODE_PRIVATE);
        mPref.registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    private void loadChatRoom(){
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                Log.v(TAG, "token="+token+", cursor="+cursor);
                super.onQueryComplete(token, cookie, cursor);
                if(cursor != null && cursor.getCount()>0){
                    List<Item> list = new ArrayList<Item>();
                    while(cursor.moveToNext()){
                        String room = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_ROOM));
                        String sender = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_SENDER));
                        String receiver = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_RECEIVER));
                        String image = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_IMAGE));
                        long time = cursor.getInt(cursor.getColumnIndex(COLUMN_DATE));
                        sportsid = cursor.getInt(cursor.getColumnIndex(COLUMN_SPORTSID));
                        int locationid = cursor.getInt(cursor.getColumnIndex(COLUMN_LOCATIONID));
                        Item item = new Item(room, sender, receiver, image, time, sportsid, locationid );
                        int count = Util.getUnreadChat(getActivity(), room);
                        Log.v(TAG, "item="+item+", unread="+count);
                        item.setUnread(count);
                        list.add(item);
                    };
                    mAdapter.setItems(list);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        // TIP : inject group by clause into selection
        queryHandler.startQuery(1, null, MyContentProvider.CONTENT_URI, null
                , "sender=sender group by "+DbHelper.COLUMN_ROOM, null, " date asc");
    }

    public void fetchAvaTar(final String room, final String filename){
        // me와 buddy 아바타 가져오기
        String url = Const.MAIN_URL + "/upload_profile?filename=" + filename;
        //final Bitmap bmp;
        Log.v(TAG, "fetchAvaTar url="+url);
        Picasso.with(getActivity()).load(url).resize(50,50).centerInside().into(new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.v(TAG, "fetchAvaTar room="+room+", bitmap="+bitmap);
                mAvatarMap.put(room, bitmap);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    private void removeChatRoom(final int index, String roomName){

        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {

            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                Log.v(TAG, "onDeleteComplete result="+result);
                mAdapter.removeItem(index);
                mAdapter.notifyItemRemoved(index);
                //Toast.makeText(getContext(), "deletion successful", Toast.LENGTH_SHORT).show();
            }
        };
        String where = MyContentProvider.DbHelper.COLUMN_ROOM+"=?";
        queryHandler.startDelete(1, null, MyContentProvider.CONTENT_URI, where, new String[]{roomName});
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Item> mItems;
        private OnClickCallback mListener;

        public MyAdapter(Context context, List<Item> items, OnClickCallback listener){
            mListener = listener;

            if(mItems == null)
                this.mItems = new ArrayList<Item>();
            else
                this.mItems = items;
            Log.v(TAG, "items="+this.mItems);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            Log.v(TAG, "parent="+parent);
            v = LayoutInflater.from(getContext()).inflate(R.layout.chat_room_list_item, parent, false);
            return new ViewCache(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewCache vh = (ViewCache)holder;
            Item item = mItems.get(position);

            vh.tv_title.setText(item.room+" 님과의 대화");
            int count = item.getUnread();
            if(count > 0){
                vh.cv_unread.setUnreadCount(count);
                vh.cv_unread.setVisibility(View.VISIBLE);
                vh.cv_unread.invalidate();
            }else{
                vh.cv_unread.setVisibility(View.GONE);
            }
            vh.civ_thumb.setImageResource(R.drawable.default_user);
            if(item.image != null && !item.image.isEmpty()){
                String url = Const.MAIN_URL + "/upload_profile?filename=" + item.image;
                Log.v(TAG, "onBindViewHolder image url:"+url);
                Picasso.with(getActivity()).load(url).placeholder(R.drawable.default_user).resize(50,50).centerInside().into(vh.civ_thumb);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public Item getItem(int index){
            return mItems.get(index);
        }

        public void addItem(Item item){
            mItems.add(item);
        }

        public void setItems(List<Item> list){
            mItems = list;
        }

        public void removeItem(int index){
            mItems.remove(index);
        }

        class ViewCache extends RecyclerView.ViewHolder implements View.OnClickListener{
            View v_row;
            CircleImageView civ_thumb;
            TextView tv_title;
            TextView tv_subtitle;
            ImageView iv_delete;
            UnreadView cv_unread;

            public ViewCache(View v) {
                super(v);
                v_row = v;
                v.setOnClickListener(this);
                civ_thumb = (CircleImageView)v.findViewById(R.id.civ_thumb);
                tv_title = (TextView)v.findViewById(R.id.tv_room_title);
                tv_subtitle = (TextView)v.findViewById(R.id.tv_room_subtitle);
                iv_delete = (ImageView)v.findViewById(R.id.iv_delete);
                cv_unread = (UnreadView)v.findViewById(R.id.cv_unread);
                iv_delete.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(v.getId() == v_row.getId()){
                    mListener.rowOnClicked(v, getAdapterPosition());
                }else if(v.getId() == iv_delete.getId()){
                    mListener.deleteOnClicked(v, getAdapterPosition());
                }
            }
        }
    }

    class Item{
        String room;
        String sender;
        String receiver;
        String image;
        long lastTime;
        int sportsid;
        int locationid;
        int unread;

        public Item(String room, String sender, String receiver, String image, long date, int sportsid, int locationid){
            this.room = room;
            this.sender = sender;
            this.receiver = receiver;
            this.image = image;
            this.lastTime = date;
            this.sportsid = sportsid;
            this.locationid = locationid;
        }

        public int getUnread() {
            return unread;
        }

        public void setUnread(int unread) {
            this.unread = unread;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "room='" + room + '\'' +
                    ", sender='" + sender + '\'' +
                    ", receiver='" + receiver + '\'' +
                    ", image='" + image + '\'' +
                    ", lastTime=" + lastTime +
                    ", sportsid=" + sportsid +
                    ", locationid=" + locationid +
                    '}';
        }
    }
}
