package com.iron.dragon.sportstogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iron.dragon.sportstogether.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.retrofit.BulletinInfo;
import com.iron.dragon.sportstogether.retrofit.GitHubService;
import com.iron.dragon.sportstogether.util.Const;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BulletinListView extends AppCompatActivity {

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
    private int mSportsId;
    private int mLocationId;
    BulletinRecyclerViewAdapter mAdapter;
    private int mSportsImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_bulletin_list_view);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        LoadData();
        InitLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getBulletinData() {
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        ArrayList<BulletinInfo> listOfStrings;
        final Call<List<BulletinInfo>> call =
                gitHubService.getBulletin(mSportsId, mLocationId, 10);
        call.enqueue(new Callback<List<BulletinInfo>>() {
            @Override
            public void onResponse(Call<List<BulletinInfo>> call, Response<List<BulletinInfo>> response) {
                Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                Log.d("Test", "body = " + response.body().toString());
                Log.d("Test", "message = " + response.message());
                List<BulletinInfo> list = response.body();
                ArrayList<BulletinInfo> listOfStrings = new ArrayList<BulletinInfo>(list.size());
                Iterator<BulletinInfo> itrTemp = list.iterator();

                while (itrTemp.hasNext()) {
                    listOfStrings.add(itrTemp.next());
                }
                Log.d("Test", "BulletinInfo Size = " + list.size());
                Log.d("Test", "BulletinInfolistOfStrings Size = " + listOfStrings.size());
                initListView(listOfStrings);
            }

            @Override
            public void onFailure(Call<List<BulletinInfo>> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });

    }

    private void initListView(ArrayList<BulletinInfo> listOfStrings) {
        mAdapter.setItem(listOfStrings);
    }

    private void LoadData() {
        Intent intent = getIntent();
        mSportsId = intent.getIntExtra("Extra_Sports", 0);
        mLocationId = LoginPreferences.GetInstance().GetLocalProfileLocation(this);
        mSportsImg = intent.getIntExtra("Extra_SportsImg", 0);
        getBulletinData();
        Log.d("Test", "mSportsImg = " + mSportsImg);


    }

    private void InitLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBoardRecyclerviewer.setLayoutManager(layoutManager);
        mTvTotalNum.setText(getString(R.string.bulletin_num, 5));
        mIvBulletin.setImageResource(mSportsImg);
        Log.d("Test", "iv = " + mSportsImg + "resource = " + R.drawable.badminton);
        Const.SPORTS sports = Const.SPORTS.values()[mSportsId];
        mTvSportsName.setText(sports.name());
        mTvLocation.setText(getString(R.string.bulletin_location, getResources().getStringArray(R.array.location)[mLocationId]));

        mAdapter = new BulletinRecyclerViewAdapter(BulletinListView.this);
        mBoardRecyclerviewer.setAdapter(mAdapter);
        mAdapter.setOnItemLongClickListener(new BulletinRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu( view );
                openContextMenu( view );
            }
        });

    }


    @OnClick(R.id.bt_Send)
    public void onClick() {
        String content = mEtContent.getText().toString();
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        BulletinInfo bulletin = new BulletinInfo.Builder()
                .setSportsid(mSportsId)
                .setLocationid(mLocationId)
                .setUsername(LoginPreferences.GetInstance().GetLocalProfileUserName(BulletinListView.this))
                .setComment(content)
                .setDate(String.valueOf(System.currentTimeMillis()))
                .setType(1).build();
        final Call<BulletinInfo> call =
                gitHubService.postBulletin(bulletin);
        call.enqueue(new Callback<BulletinInfo>() {
            @Override
            public void onResponse(Call<BulletinInfo> call, Response<BulletinInfo> response) {
                Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                Log.d("Test", "body = " + response.body().toString());
                Log.d("Test", "message = " + response.message());
                if (response.isSuccessful()) {
                    mAdapter.addItem(response.body());
                    mEtContent.setText("");
                }
            }

            @Override
            public void onFailure(Call<BulletinInfo> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_chat:
                //some code
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
