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

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.service.FloatingService;
import com.iron.dragon.sportstogether.ui.adapter.item.BuddyAdapter;
import com.iron.dragon.sportstogether.ui.model.BuddyModel;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenter;
import com.iron.dragon.sportstogether.ui.presenter.BuddyPresenterImpl;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.iron.dragon.sportstogether.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class BuddyActivity extends AppCompatActivity implements BuddyPresenter.BuddyView {
    private static final String TAG = "BuddyActivity";

    RecyclerView lv_buddy;
    TextView tb_tv_count;
    BuddyAdapter mAdapter;

    BuddyPresenter mPresenter;
    Profile mBuddy;
    DividerItemDecoration mDividerItemDecoration;
    WindowManager mWm;
    SharedPreferences mPref;

    SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            boolean found = false;
            found = mAdapter.checkUnread(sharedPreferences, key);
            if(!found){
                mPresenter.getProfiles(BuddyActivity.this, mBuddy);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buddy_act);

        mWm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mPref != null){
            mPref.unregisterOnSharedPreferenceChangeListener(mPrefListener);
        }
    }

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

    public void init(){
        // init view
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

        // init adapter
        mAdapter = new BuddyAdapter(this, mPresenter, mBuddy);
        lv_buddy.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BuddyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Profile item) {
                showDialog(item);
            }

            @Override
            public void onItemChatClick(View v, Profile item) {
                goToChat(item);
            }
        });

        // execute getting profile
        mPresenter.getProfiles(this, mBuddy);

        // handle floatingservice
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

    private void goToChat(Profile buddy){
        Profile me = LoginPreferences.GetInstance().loadSharedPreferencesProfile(this, buddy.getSportsid());
        Log.v(TAG, "buddy: " + buddy.toString());
        Log.v(TAG, "me: " + me.toString());
        Intent i = new Intent(this, ChatActivity.class);
        Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(buddy.getUsername())
                .message("Conversation gets started").date(new Date().getTime()).image(buddy.getImage()).build();
        i.putExtra("Message", message);
        i.putExtra("Buddy", buddy);
        startActivity(i);
    }
}
