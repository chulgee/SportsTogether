package com.iron.dragon.sportstogether.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.iron.dragon.sportstogether.ui.model.BuddyModel;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenter;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenterImpl;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.iron.dragon.sportstogether.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuddyActivity extends AppCompatActivity implements BuddyPresenter.BuddyView {

    private static final String TAG = "BuddyActivity";
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    RecyclerView lv_buddy;
    TextView tb_tv_count;
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
                mPresenter.loadProfiles(mBuddy);
            }
        }
    };

    @Override
    public void updateView(List<Profile> profiles) {
        tb_tv_count.setText("친구 현재 "+profiles.size()+"명");
        mAdapter.setItem(profiles);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDialog(Profile item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getUsername()+"님의 프로필");
        StringBuffer sb = new StringBuffer();
        sb.append("운동종목 : " + StringUtil.getStringFromSports(this, item.getSportsid())+"\n");
        sb.append("사는곳 : " + StringUtil.getStringFromLocation(this, item.getLocationid())+"\n");
        sb.append("성별 : " + StringUtil.getStringFromGender(this, item.getGender())+"\n");
        sb.append("나이 : " + StringUtil.getStringFromAge(this, item.getAge())+"\n");
        sb.append("레벨 : " + StringUtil.getStringFromLevel(this, item.getLevel())+"\n");
        String phone = item.getPhone();
        if(phone == null || phone.isEmpty() || phone.equals("0")) phone = "없음";
        sb.append("전화번호 : " + phone+"\n");
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
        tb_tv_count = (TextView)findViewById(R.id.tb_tv_count);

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

        mPresenter.loadProfiles(mBuddy);
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

            if(viewType == VIEW_TYPE_HEADER){
                v = LayoutInflater.from(mContext).inflate(R.layout.buddy_list_header, parent, false);
                return new HeaderViewHolder(v);
            }else if(viewType == VIEW_TYPE_ITEM){
                v = LayoutInflater.from(mContext).inflate(R.layout.buddy_list_item, parent, false);
                return new ItemViewHolder(v);
            }
            return new ItemViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(holder instanceof HeaderViewHolder){
                HeaderViewHolder hHolder = (HeaderViewHolder)holder;
                hHolder.tv_buddy_header_title.setText("동네친구 목록");
                hHolder.tv_buddy_header_subtitle.setText(StringUtil.getStringFromLocation(BuddyActivity.this, mBuddy.getLocationid())+" 친구들입니다.");
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
                    Picasso.with(BuddyActivity.this).load(url).placeholder(R.drawable.default_user).resize(50,50).centerInside().into(iHolder.civ_thumb);
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

        class HeaderViewHolder extends RecyclerView.ViewHolder{
            TextView tv_buddy_header_title;
            TextView tv_buddy_header_subtitle;

            public HeaderViewHolder(View v) {
                super(v);
                tv_buddy_header_title = (TextView) v.findViewById(R.id.tv_buddy_header_title);
                tv_buddy_header_subtitle = (TextView) v.findViewById(R.id.tv_buddy_header_subtitle);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            View v_row;
            CircleImageView civ_thumb;
            TextView tv_title;
            TextView tv_buddy_unread;
            TextView tv_subtitle;
            ImageView iv_chat;

            public ItemViewHolder(View v) {
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
                Profile buddy = mItems.get(getAdapterPosition()-1);
                Util.setUnreadBuddy(BuddyActivity.this, buddy.getUsername(), 0);

                if(v.getId() == v_row.getId()){
                    mPresenter.onRowClick(v, buddy);
                }else if(v.getId() == iv_chat.getId()){
                    mPresenter.onChatClick(v, buddy);
                }
            }
        }
    }
}
