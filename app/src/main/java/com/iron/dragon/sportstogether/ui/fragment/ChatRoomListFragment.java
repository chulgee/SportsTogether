package com.iron.dragon.sportstogether.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
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

import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_DATE;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_LOCATIONID;
import static com.iron.dragon.sportstogether.provider.MyContentProvider.DbHelper.COLUMN_SPORTSID;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.provider.MyContentProvider;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.DbUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 2016-08-12.
 */
public class ChatRoomListFragment extends Fragment {

    private static final String TAG = "ChatRoomListFragment";
    RecyclerView lv_room;
    MyAdapter mAdapter;
    Map<String, Bitmap> mAvatarMap = new HashMap<String, Bitmap>();

    public interface OnClickCallback{
        void rowOnClicked(View v, int position);
        void deleteOnClicked(View v, int position);
    }

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

        asyncLoadChatRoom();
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
                //Toast.makeText(getContext(), "this item="+item, Toast.LENGTH_SHORT).show();
                Profile me = LoginPreferences.GetInstance().getLocalProfile(getContext());
                loadBuddyProfile(item.room, me);
            }

            @Override
            public void deleteOnClicked(View v, int position) {
                asyncRemoveChatRoom(position, mAdapter.getItem(position).room);
            }

        });
        lv_room.setAdapter(mAdapter);
    }

    private void loadBuddyProfile(String buddy, final Profile me){
        // buddy의 profile 가져오기
        Log.v(TAG, "buddy="+buddy+", sportsid="+me.getSportsid()+", locationid="+me.getLocationid());
        GitHubService retrofit = GitHubService.retrofit.create(GitHubService.class);
        final Call<String> call =
                retrofit.getProfiles(buddy, me.getSportsid(), me.getLocationid(), 0);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("loadBuddyProfile", "code = " + response.code() + " is successful = " + response.isSuccessful());
                Log.d("loadBuddyProfile", "body = " + response.body().toString());
                Log.d("loadBuddyProfile", "message = " + response.toString());
                if (response.isSuccessful()) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.body().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Profile buddy = null;
                    try {
                        String command = obj.getString("command");
                        String code = obj.getString("code");
                        JSONArray arr = obj.getJSONArray("message");
                        buddy = gson.fromJson(arr.get(0).toString(), Profile.class);
                        Log.v(TAG, "buddy: "+buddy.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(buddy.getUsername())
                            .message("Conversation get started").date(new Date().getTime()).image(null).build();
                    i.putExtra("Message", message);
                    i.putExtra("Buddy", buddy);
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    private void asyncLoadChatRoom(){
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
                        String image = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_IMAGE));
                        long time = cursor.getInt(cursor.getColumnIndex(COLUMN_DATE));
                        int sportsid = cursor.getInt(cursor.getColumnIndex(COLUMN_SPORTSID));
                        int locationid = cursor.getInt(cursor.getColumnIndex(COLUMN_LOCATIONID));
                        Item item = new Item(room, sender, receiver, image, time, sportsid, locationid );
                        Log.v(TAG, "item="+item);

                        mAdapter.addItem(item);
                        mAdapter.notifyDataSetChanged();
                        if(item.image!=null){
                            fetchAvaTar(room, item.image);
                        }
                    };
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

    private void asyncRemoveChatRoom(final int index, String roomName){

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
            Bitmap bmp = mAvatarMap.get(mItems.get(position).room);
            Log.v(TAG, "mItems.get(position).room="+mItems.get(position).room+", bmp="+bmp);
            if(bmp != null){
                vh.iv_thumb.setImageBitmap(bmp);
            }else{
                vh.iv_thumb.setImageResource(R.drawable.default_user);
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

        public void removeItem(int index){
            mItems.remove(index);
        }

        class ViewCache extends RecyclerView.ViewHolder implements View.OnClickListener{
            View v_row;
            ImageView iv_thumb;
            TextView tv_title;
            TextView tv_subtitle;
            ImageView iv_delete;

            public ViewCache(View v) {
                super(v);
                v_row = v;
                v.setOnClickListener(this);
                iv_thumb = (ImageView)v.findViewById(R.id.iv_thumb);
                tv_title = (TextView)v.findViewById(R.id.tv_room_title);
                tv_subtitle = (TextView)v.findViewById(R.id.tv_room_subtitle);
                iv_delete = (ImageView)v.findViewById(R.id.iv_delete);
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
        public Item(String room, String sender, String receiver, String image, long date, int sportsid, int locationid){
            this.room = room;
            this.sender = sender;
            this.receiver = receiver;
            this.image = image;
            this.lastTime = date;
            this.sportsid = sportsid;
            this.locationid = locationid;
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
