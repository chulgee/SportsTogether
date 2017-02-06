package com.iron.dragon.sportstogether.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.service.FloatingService;
import com.iron.dragon.sportstogether.ui.fragment.ChatRoomListFragment;
import com.iron.dragon.sportstogether.ui.model.BuddyModel;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenter;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenterImpl;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuddyActivity extends AppCompatActivity implements BuddyPresenter.BuddyView {

    private static final String TAG = "BuddyActivity";
    RecyclerView lv_buddy;
    MyAdapter mAdapter;

    BuddyPresenter mPresenter;
    Profile mBuddy;
    DividerItemDecoration mDividerItemDecoration;
    WindowManager mWm;
    SharedPreferences mPref;

    SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            boolean found = false;
            Iterator<Profile> iter = mAdapter.mItems.iterator();
            while(iter.hasNext()){
                Profile item = (Profile)iter.next();
                Log.v(TAG, "key="+key+", item.room="+item.getUsername());
                if(item.getUsername().equals(key)){
                    int count = sharedPreferences.getInt(key, 0);
                    item.setUnread(count);
                    mAdapter.notifyDataSetChanged();
                    found = true;
                }
            }
            if(!found){
                mPresenter.loadProfile();
            }
        }
    };

    @Override
    public void updateView(List<Profile> profiles) {
        mAdapter.setItem(profiles);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDialog(Profile item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getUsername()+"님의 프로필");
        StringBuffer sb = new StringBuffer();
        sb.append("운동종목 : " + item.getSportsid()+"\n");
        sb.append("사는곳 : " + item.getLocationid()+"\n");
        sb.append("성별 : " + item.getGender()+"\n");
        sb.append("나이 : " + item.getAge()+"\n");
        sb.append("전화번호 : " + item.getPhone()+"\n");
        sb.append("레벨 : " + item.getLevel()+"\n");
        builder.setMessage(sb.toString());
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buddy_act);

        mWm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("친구 리스트");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();

        if(FloatingService.getFloating() == true){
            View view = View.inflate(this, R.layout.floating_buddy, null);
            if(view.isAttachedToWindow()){
                mWm.removeView(view);
                FloatingService.setFloating(false);
                stopService(new Intent(this, FloatingService.class));
            }else{
                Toast.makeText(this, "floating not attached", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mPref != null){
            mPref.unregisterOnSharedPreferenceChangeListener(mPrefListener);
        }
    }

    public void init(){

        mPref = getSharedPreferences(Const.PREF_UNREAD_BUDDY, Context.MODE_PRIVATE);
        mPref.registerOnSharedPreferenceChangeListener(mPrefListener);

        Intent i = getIntent();
        mBuddy = (Profile)i.getSerializableExtra("Buddy");
        lv_buddy = (RecyclerView) findViewById(R.id.lv_buddy);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_buddy.setLayoutManager(mLayoutManager);
        mDividerItemDecoration = new DividerItemDecoration(this,
                mLayoutManager.getOrientation());
        lv_buddy.addItemDecoration(mDividerItemDecoration);

        BuddyModel buddyModel = new BuddyModel(this, mBuddy.getSportsid(), mBuddy.getLocationid());
        mPresenter = new BuddyPresenterImpl(this, buddyModel);
        mAdapter = new MyAdapter(this, mPresenter);
        lv_buddy.setAdapter(mAdapter);

        mPresenter.loadProfile();
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final String TAG = "MyAdapter";
        private List<Profile> mItems;
        private Context mContext;
        private BuddyPresenter mPresenter;

        public MyAdapter(Context context, BuddyPresenter presenter){
            mPresenter = presenter;
            mContext = context;
            mItems = new ArrayList<Profile>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            Log.v(TAG, "parent="+parent);
            v = LayoutInflater.from(mContext).inflate(R.layout.buddy_list_item, parent, false);
            return new ViewCache(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewCache vh = (ViewCache)holder;
            Profile item = mItems.get(position);
            vh.tv_title.setText(item.getUsername());
            if(item.getUnread() > 0){
                vh.tv_buddy_unread.setVisibility(View.VISIBLE);
            }else
                vh.tv_buddy_unread.setVisibility(View.GONE);
            vh.civ_thumb.setImageResource(R.drawable.default_user);
            if(item.getImage() != null && !item.getImage().isEmpty()){
                String url = Const.MAIN_URL + "/upload_profile?filename=" + item.getImage();
                Log.v(TAG, "onBindViewHolder url:"+url);
                Picasso.with(BuddyActivity.this).load(url).placeholder(R.drawable.default_user).resize(50,50).centerInside().into(vh.civ_thumb);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
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


        class ViewCache extends RecyclerView.ViewHolder implements View.OnClickListener{
            View v_row;
            CircleImageView civ_thumb;
            TextView tv_title;
            TextView tv_buddy_unread;
            TextView tv_subtitle;
            ImageView iv_chat;

            public ViewCache(View v) {
                super(v);
                v_row = v;
                v.setOnClickListener(this);
                civ_thumb = (CircleImageView)v.findViewById(R.id.civ_thumb);
                tv_title = (TextView)v.findViewById(R.id.tv_buddy_title);
                tv_buddy_unread = (TextView)v.findViewById(R.id.tv_buddy_unread);
                tv_subtitle = (TextView)v.findViewById(R.id.tv_buddy_subtitle);
                iv_chat = (ImageView)v.findViewById(R.id.iv_chat);
                iv_chat.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Profile buddy = mItems.get(getAdapterPosition());
                Util.setUnreadBuddy(BuddyActivity.this, buddy.getUsername(), 0);

                if(v.getId() == v_row.getId()){
                    mPresenter.onRowClick(v, buddy);
                }else if(v.getId() == iv_chat.getId()){
                    Toast.makeText(BuddyActivity.this, "go to chat", Toast.LENGTH_SHORT).show();
                    mPresenter.onChatClick(v, buddy);
                }
            }
        }
    }

/*    class Item{
        String username;
        int sportsid;
        int locationid;
        String image;

        public Item(String username, int sportsid, int locationid, String image){
            this.username = username;
            this.image = image;
            this.sportsid = sportsid;
            this.locationid = locationid;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "username='" + username + '\'' +
                    ", sportsid=" + sportsid +
                    ", locationid=" + locationid +
                    ", image='" + image + '\'' +
                    '}';
        }
    }*/
}
