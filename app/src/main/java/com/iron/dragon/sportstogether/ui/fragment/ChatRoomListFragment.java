package com.iron.dragon.sportstogether.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.provider.ChatMessageVO;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.view.UnreadView;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

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
    Realm realm;

    public interface OnClickCallback{
        void rowOnClicked(View v, int position);
        void deleteOnClicked(View v, int position);
    }

    SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            boolean found = false;
            Iterator<ChatMessageVO> iter = mAdapter.mItems.iterator();
            while(iter.hasNext()){
                ChatMessageVO item = (ChatMessageVO)iter.next();
                Log.v(TAG, "key="+key+", item.room="+item.getCOLUMN_ROOM());
                if(item.getCOLUMN_ROOM().equals(key)){
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
        realm = Realm.getDefaultInstance();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    public void init(View rootView){

        lv_room = (RecyclerView) rootView.findViewById(R.id.lv_room);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lv_room.setLayoutManager(llm);
        mAdapter = new MyAdapter(getContext(), null, new OnClickCallback(){

            @Override
            public void rowOnClicked(View v, int position) {
                ChatMessageVO item = mAdapter.getItem(position);
                ArrayList<Profile> profiles = LoginPreferences.GetInstance().loadSharedPreferencesProfileAll(getActivity());
                if(profiles != null && profiles.size() > 0){
                    final Profile me = profiles.get(0);
                    Log.v(TAG, "rowOnClicked item="+item.toString());
                    RetrofitHelper.loadProfile(getActivity(), me, item.getCOLUMN_ROOM(), item.getCOLUMN_SPORTSID(), item.getCOLUMN_LOCATIONID(), new RetrofitHelper.ProfileListener() {
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
                }else {
                    Toast.makeText(getActivity(), "프로파일이 한개 이상 존재해야 합니다. 로그인해주세요", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void deleteOnClicked(View v, int position) {
                removeChatRoom(position, mAdapter.getItem(position).getCOLUMN_ROOM());
            }

        });
        lv_room.setAdapter(mAdapter);

        mPref = getActivity().getSharedPreferences(Const.PREF_UNREAD_CHAT, Context.MODE_PRIVATE);
        mPref.registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    private void loadChatRoom(){

        RealmResults<ChatMessageVO> messages = getMessageList();
        if (messages.size() > 0) {
            List<ChatMessageVO> list = new ArrayList<>();
            Observable.from(messages)
                    .distinct(ChatMessageVO::getCOLUMN_ROOM)
                    .subscribe(chatMessageVO -> {
                                int count = Util.getUnreadChat(getActivity(), chatMessageVO.getCOLUMN_ROOM());
                                chatMessageVO.setUnread(count);
                                list.add(chatMessageVO);
                            }
                            , Throwable::printStackTrace
                            , () -> {
                                mAdapter.setItems(list);
                                mAdapter.notifyDataSetChanged();
                            });

        }
    }


    private RealmResults<ChatMessageVO> getMessageList() {
        return realm.where(ChatMessageVO.class).findAllSorted("COLUMN_DATE", Sort.ASCENDING);
    }

    public void fetchAvaTar(final String room, final String filename){
        // me와 buddy 아바타 가져오기q
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

    private void removeChatRoom(final int index, final String roomName){
        final RealmResults<ChatMessageVO> message = realm.where(ChatMessageVO.class).equalTo("COLUMN_ROOM", roomName).findAll();
        realm.executeTransaction(new Realm.Transaction() {
             @Override
             public void execute(Realm realm) {

                 message.deleteAllFromRealm();
                 mAdapter.removeItem(index);
                 mAdapter.notifyItemRemoved(index);
             }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<ChatMessageVO> mItems;
        private OnClickCallback mListener;

        public MyAdapter(Context context, List<ChatMessageVO> items, OnClickCallback listener) {
            mListener = listener;
            if(mItems == null)
                this.mItems = new ArrayList<ChatMessageVO>();
            else
                this.mItems = items;
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
            ChatMessageVO item = mItems.get(position);

            vh.tv_title.setText(item.getCOLUMN_ROOM() +" 님과의 대화");
            int count = item.getUnread();
            if(count > 0){
                vh.cv_unread.setUnreadCount(count);
                vh.cv_unread.setVisibility(View.VISIBLE);
                vh.cv_unread.invalidate();
            }else{
                vh.cv_unread.setVisibility(View.GONE);
            }
            vh.civ_thumb.setImageResource(R.drawable.default_user);
            if(item.getCOLUMN_IMAGE()  != null && !item.getCOLUMN_IMAGE().isEmpty()){
                String url = Const.MAIN_URL + "/upload_profile?filename=" + item.getCOLUMN_IMAGE();
                Log.v(TAG, "onBindViewHolder image url:"+url);
                Picasso.with(getActivity()).load(url).placeholder(R.drawable.default_user).resize(50,50).centerInside().into(vh.civ_thumb);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public ChatMessageVO getItem(int index){
            return mItems.get(index);
        }

        public void addItem(ChatMessageVO item){
            mItems.add(item);
        }

        public void setItems(List<ChatMessageVO> list){
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
}
