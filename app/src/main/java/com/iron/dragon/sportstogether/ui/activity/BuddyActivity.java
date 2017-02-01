package com.iron.dragon.sportstogether.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.ui.model.BuddyModel;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenter;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenterImpl;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuddyActivity extends AppCompatActivity implements BuddyPresenter.BuddyView {

    RecyclerView lv_buddy;
    MyAdapter mAdapter;

    BuddyPresenter mPresenter;
    Profile mBuddy;
    DividerItemDecoration mDividerItemDecoration;

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
    }

    public void init(){

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
            /*Log.v(TAG, "mItems.get(position).room="+mItems.get(position).room+", bmp="+bmp);
            if(bmp != null){
                vh.civ_thumb.setImageBitmap(bmp);
            }else{
                vh.civ_thumb.setImageResource(R.drawable.default_user);
            }*/
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
            TextView tv_subtitle;
            ImageView iv_chat;

            public ViewCache(View v) {
                super(v);
                v_row = v;
                v.setOnClickListener(this);
                civ_thumb = (CircleImageView)v.findViewById(R.id.civ_thumb);
                tv_title = (TextView)v.findViewById(R.id.tv_buddy_title);
                tv_subtitle = (TextView)v.findViewById(R.id.tv_buddy_subtitle);
                iv_chat = (ImageView)v.findViewById(R.id.iv_chat);
                iv_chat.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(v.getId() == v_row.getId()){
                    mPresenter.onRowClick(v, mItems.get(getAdapterPosition()));
                }else if(v.getId() == iv_chat.getId()){
                    Toast.makeText(BuddyActivity.this, "go to chat", Toast.LENGTH_SHORT).show();
                    mPresenter.onChatClick(v, mItems.get(getAdapterPosition()));
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
