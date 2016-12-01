package com.iron.dragon.sportstogether;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iron.dragon.sportstogether.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.retrofit.BulletinInfo;
import com.iron.dragon.sportstogether.retrofit.GitHubService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BulletinListView extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private int mSportsId;
    private int mLocationId;
    BulletinRecyclerViewAdapter mAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_list_view);
        InitLayout();
	    LoadData();
    }

    private void LoadData() {
        mSportsId = getIntent().getIntExtra("Extra_Sports", 0);
        mLocationId = LoginPreferences.GetInstance().GetLocalProfileLocation(this);
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);

        final Call<List<BulletinInfo>> call =
                gitHubService.getBulletin(mSportsId, mLocationId, 10);
        call.enqueue(new Callback<List<BulletinInfo>>() {
            @Override
            public void onResponse(Call<List<BulletinInfo>> call, Response<List<BulletinInfo>> response) {
                android.util.Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                android.util.Log.d("Test", "body = " + response.body().toString());
                android.util.Log.d("Test", "message = " + response.message());
                List<BulletinInfo> list = response.body();
                ArrayList<BulletinInfo> listOfStrings = new ArrayList<BulletinInfo>(list.size());
                Iterator<BulletinInfo> itrTemp = list.iterator();

                while(itrTemp.hasNext()){
                    listOfStrings.add(itrTemp.next());
                }
                android.util.Log.d("Test", "BulletinInfo Size = " + list.size());
                android.util.Log.d("Test", "BulletinInfolistOfStrings Size = " + listOfStrings.size());
//                listOfStrings.addAll(listOfStrings);
//                ArrayList<BulletinInfo> listOfStrings = new ArrayList<BulletinInfo>((Collection<? extends BulletinInfo>) Arrays.asList(list))
                mAdapter = new BulletinRecyclerViewAdapter(BulletinListView.this, listOfStrings);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<BulletinInfo>> call, Throwable t) {
                android.util.Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    private void InitLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.board_recyclerviewer);
        Button btsend = (Button)findViewById(R.id.bt_Send);
        final EditText etcontent = (EditText)findViewById(R.id.etContent);
        btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etcontent.getText().toString();
                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                BulletinInfo bulletin = new BulletinInfo.Builder()
                        .setSportsid(mSportsId)
                        .setLocationid(mLocationId)
                        .setUsername(LoginPreferences.GetInstance().GetLocalProfileUserName(BulletinListView.this))
                        .setComment(content)
                        .setDate(String.valueOf(System.currentTimeMillis()))
                        .setType(1).build();
                final Call<BulletinInfo>call =
                        gitHubService.postBulletin(bulletin);
                call.enqueue(new Callback<BulletinInfo>() {
                    @Override
                    public void onResponse(Call<BulletinInfo> call, Response<BulletinInfo> response) {
                        android.util.Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                        android.util.Log.d("Test", "body = " + response.body().toString());
                        android.util.Log.d("Test", "message = " + response.message());
                        if(response.isSuccessful()) {
                            mAdapter.addItem(response.body());
                        }
//                listOfStrings.addAll(listOfStrings);
//                ArrayList<BulletinInfo> listOfStrings = new ArrayList<BulletinInfo>((Collection<? extends BulletinInfo>) Arrays.asList(list))
                    }

                    @Override
                    public void onFailure(Call<BulletinInfo> call, Throwable t) {
                        android.util.Log.d("Test", "error message = " + t.getMessage());
                    }
                });
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }


}
