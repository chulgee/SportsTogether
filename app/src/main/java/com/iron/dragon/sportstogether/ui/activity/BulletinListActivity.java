package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
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

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.ui.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.ui.view.DividerItemDecoration;
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
    private int mSportsId;
    private int mLocationId;

    BulletinRecyclerViewAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_bulletin_list_view_1);

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
                ArrayList<Bulletin> listOfStrings = new ArrayList<Bulletin>(list.size());
                Iterator<Bulletin> itrTemp = list.iterator();

                while (itrTemp.hasNext()) {
                    listOfStrings.add(itrTemp.next());
                }
                Log.d("Test", "Bulletin Size = " + list.size());
                Log.d("Test", "BulletinlistOfStrings Size = " + listOfStrings.size());
                initListView(listOfStrings);
            }

            @Override
            public void onFailure(Call<List<Bulletin>> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });

    }

    private void initListView(ArrayList<Bulletin> listOfStrings) {
        mAdapter.setItem(listOfStrings);
    }

    private void LoadData() {
        Intent intent = getIntent();
        mSportsId = intent.getIntExtra("Extra_Sports", 0);
        mLocationId = LoginPreferences.GetInstance().GetLocalProfileLocation(this);
        getBulletinData();


    }

    private void InitLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBoardRecyclerviewer.setLayoutManager(layoutManager);
        mTvTotalNum.setText(getString(R.string.bulletin_num, 5));

        TypedArray imgs = getResources().obtainTypedArray(R.array.sportsimg_bulletin);
        mIvBulletin.setImageResource(imgs.getResourceId(mSportsId, -1));
        imgs.recycle();

        Const.SPORTS sports = Const.SPORTS.values()[mSportsId];
        mTvSportsName.setText(sports.name());
        mTvLocation.setText(getString(R.string.bulletin_location, getResources().getStringArray(R.array.location)[mLocationId]));

        mAdapter = new com.iron.dragon.sportstogether.ui.adapter.BulletinRecyclerViewAdapter(BulletinListActivity.this);
        mBoardRecyclerviewer.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mBoardRecyclerviewer.addItemDecoration(dividerItemDecoration);
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
        Bulletin bulletin = new Bulletin.Builder()
                .setSportsid(mSportsId)
                .setLocationid(mLocationId)
                .setUsername(LoginPreferences.GetInstance().GetLocalProfileUserName(BulletinListActivity.this))
                .setComment(content)
                .setDate(System.currentTimeMillis())
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
                    mAdapter.addItem(response.body());
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