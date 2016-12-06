package com.iron.dragon.sportstogether.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.ui.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BulletinListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_list_view);
        InitLayout();
	    LoadData();
    }

    private void LoadData() {
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        final Call<List<Bulletin>> call =
                gitHubService.getBulletin(1, 1, 10);
        call.enqueue(new Callback<List<Bulletin>>() {
            @Override
            public void onResponse(Call<List<Bulletin>> call, Response<List<Bulletin>> response) {
                android.util.Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                android.util.Log.d("Test", "body = " + response.body().toString());
                android.util.Log.d("Test", "message = " + response.message());
                List<Bulletin> list = response.body();
                ArrayList<Bulletin> listOfStrings = new ArrayList<Bulletin>(list.size());
                Iterator<Bulletin> itrTemp = list.iterator();

                while(itrTemp.hasNext()){
                    listOfStrings.add(itrTemp.next());
                }
                android.util.Log.d("Test", "Bulletin Size = " + list.size());
                android.util.Log.d("Test", "BulletinlistOfStrings Size = " + listOfStrings.size());
//                listOfStrings.addAll(listOfStrings);
//                ArrayList<Bulletin> listOfStrings = new ArrayList<Bulletin>((Collection<? extends Bulletin>) Arrays.asList(list))
                mRecyclerView.setAdapter(new BulletinRecyclerViewAdapter(BulletinListActivity.this, listOfStrings));
            }

            @Override
            public void onFailure(Call<List<Bulletin>> call, Throwable t) {
                android.util.Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    private void InitLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.board_recyclerviewer);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }


}
