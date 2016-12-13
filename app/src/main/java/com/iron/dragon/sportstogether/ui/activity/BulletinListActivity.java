package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.ui.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.ui.adapter.item.EventItem;
import com.iron.dragon.sportstogether.ui.adapter.item.HeaderItem;
import com.iron.dragon.sportstogether.ui.adapter.item.ListItem;
import com.iron.dragon.sportstogether.ui.view.DividerItemDecoration;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iron.dragon.sportstogether.R.id.collapsingToolbarLayout;


public class BulletinListActivity extends AppCompatActivity {

    @BindView(R.id.ivBulletin)
    ImageView mIvBulletin;
    @BindView(R.id.tvSportsName)
    TextView mTvSportsName;
    @BindView(R.id.tvTotalNum)
    TextView mTvTotalNum;
    @BindView(R.id.etContent)
    EditText mEtContent;
    @BindView(R.id.bt_Send)
    Button mBtSend;
    @BindView(R.id.board_recyclerviewer)
    RecyclerView mBoardRecyclerviewer;
    @BindView(R.id.tvLocation)
    TextView mTvLocation;
    @BindView(collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private int mSportsId;
    private int mLocationId;

    BulletinRecyclerViewAdapter mAdapter;
    TreeMap<String,ArrayList<Bulletin>> mTMBulletinMap = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_list_view);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        LoadData();
        InitLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
//                NavUtils.navigateUpFromSameTask(this);
                finish();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBulletinData() {
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        ArrayList<Bulletin> listOfStrings;
        final Call<List<Bulletin>> call =
                gitHubService.getBulletin(mSportsId, mLocationId, 10);
        call.enqueue(new Callback<List<Bulletin>>() {
            @Override
            public void onResponse(Call<List<Bulletin>> call, Response<List<Bulletin>> response) {
                Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                Log.d("Test", "body = " + response.body().toString());
                Log.d("Test", "message = " + response.message());
                List<Bulletin> list = response.body();

                initListView(list);
            }

            @Override
            public void onFailure(Call<List<Bulletin>> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });

    }

    private void initListView(List<Bulletin> listOfStrings) {
//        HashMap<String,Bulletin> map = new HashMap<String,Bulletin>();
//        ValueComparator bvc =  new ValueComparator();
//        TreeMap<String,Bulletin> sorted_map = new TreeMap<String,Bulletin>(bvc);
        if(listOfStrings.size() != 0) {
            for (Bulletin bulletin : listOfStrings) {
                String sDate = Util.getStringDate(bulletin.getDate());
                if(mTMBulletinMap.get(sDate) == null) {
                    ArrayList<Bulletin> items = new ArrayList<>();
                    items.add(bulletin);
                    mTMBulletinMap.put(sDate, items);
                } else {
                    mTMBulletinMap.get(sDate).add(bulletin);
                }
            }
            ArrayList<ListItem> listItems = new ArrayList<>();

            for (String date : mTMBulletinMap.keySet()) {
                Logger.d("convert data = " + date);
                HeaderItem header = new HeaderItem();
                header.setDate(date);
                listItems.add(header);
                ArrayList<Bulletin> ar = mTMBulletinMap.get(date);
                Collections.sort(ar, new Comparator<Bulletin>() {
                    @Override
                    public int compare(Bulletin bulletin, Bulletin t1) {
                        return bulletin.getDate() < t1.getDate() ? -1 : bulletin.getDate() == t1.getDate() ? 0 : 1;
                    }
                });

                for (Bulletin event : ar) {
                    EventItem item = new EventItem();
                    item.setBulletin(event);
                    listItems.add(item);
                }
            }
            mAdapter.setItem(listItems);
        }
    }

    private void LoadData() {
        Intent intent = getIntent();
        mSportsId = intent.getIntExtra("Extra_Sports", 0);
        mLocationId = LoginPreferences.GetInstance().GetLocalProfileLocation(this);
        getBulletinData();

    }
    LinearLayoutManager layoutManager;
    private void InitLayout() {
         layoutManager = new LinearLayoutManager(this);
        mBoardRecyclerviewer.setLayoutManager(layoutManager);
        mTvTotalNum.setText("");
        getBuddyCount();
        TypedArray imgs = getResources().obtainTypedArray(R.array.sportsimg_bulletin);
        mIvBulletin.setImageResource(imgs.getResourceId(mSportsId, -1));
        imgs.recycle();

        Const.SPORTS sports = Const.SPORTS.values()[mSportsId];
        mCollapsingToolbarLayout.setTitle(sports.name());
        mTvLocation.setText(getString(R.string.bulletin_location, getResources().getStringArray(R.array.location)[mLocationId]));

        mAdapter = new BulletinRecyclerViewAdapter(BulletinListActivity.this);
        mBoardRecyclerviewer.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mBoardRecyclerviewer.addItemDecoration(dividerItemDecoration);
        mAdapter.setOnItemLongClickListener(new BulletinRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(view);
                openContextMenu(view);
            }
        });

    }

    private void getBuddyCount() {

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        final Call<String> call =
                gitHubService.getBuddyCount(mSportsId, mLocationId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String num = "" + 0;
                try {
                    JSONArray jsonArray = new JSONArray(response.body());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        num = jObject.get("COUNT(*)").toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mTvTotalNum.setText(getString(R.string.bulletin_num, num));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }


    @OnClick(R.id.bt_Send)
    public void onClick() {
        String content = mEtContent.getText().toString();
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        Bulletin bulletin = new Bulletin.Builder()
                .setSportsid(mSportsId)
                .setLocationid(mLocationId)
                .setUsername(LoginPreferences.GetInstance().GetLocalProfileUserName(BulletinListActivity.this))
                .setComment(content)
                .setDate(System.currentTimeMillis())
                .setImage(LoginPreferences.GetInstance().getLocalProfile(this).getImage())
                .setType(1).build();
        final Call<Bulletin> call =
                gitHubService.postBulletin(bulletin);
        call.enqueue(new Callback<Bulletin>() {
            @Override
            public void onResponse(Call<Bulletin> call, Response<Bulletin> response) {
                Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                Log.d("Test", "body = " + response.body().toString());
                Log.d("Test", "message = " + response.message());
                if (response.isSuccessful()) {
                    Bulletin res_bulletin = response.body();

                    HeaderItem header = new HeaderItem();
                    header.setDate(Util.getStringDate(res_bulletin.getDate()));
                    EventItem item = new EventItem();
                    item.setBulletin(res_bulletin);
                    if(mAdapter.getItemCount() == 0) {
                        ArrayList<ListItem> listItems = new ArrayList<>();
                        listItems.add(header);
                        listItems.add(item);
                        mAdapter.setItem(listItems);
                    } else {
                        mAdapter.addItem(header);
                        mAdapter.addItem(item);
                    }
                    mBoardRecyclerviewer.smoothScrollToPosition(mBoardRecyclerviewer.getAdapter().getItemCount());
                    mEtContent.setText("");
                }
            }

            @Override
            public void onFailure(Call<Bulletin> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderIcon(android.R.drawable.ic_menu_share);
        menu.setHeaderTitle("Menu");
        inflater.inflate(R.menu.menu_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                //some code
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}