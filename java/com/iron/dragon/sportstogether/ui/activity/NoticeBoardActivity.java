package com.iron.dragon.sportstogether.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Notice;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeBoardActivity extends AppCompatActivity {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;
    public static final String TAG = "NoticeBoardActivity";

    RecyclerView lv_list;
    MyAdapter mAdapter;
    GitHubService mRetrofit;
    RecyclerView.ItemDecoration mDividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("공지사항");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        lv_list = (RecyclerView)findViewById(R.id.lv_notice);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_list.setLayoutManager(mLayoutManager);
        mDividerItemDecoration = new DividerItemDecoration(this,
                mLayoutManager.getOrientation());
        lv_list.addItemDecoration(mDividerItemDecoration);
        mAdapter = new MyAdapter(this);
        lv_list.setAdapter(mAdapter);



        loadNotice();
    }

    void loadNotice(){
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
        final Call<List<Notice>> call = retrofit.getNotice(100);
        call.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                Log.d(TAG, "code = " + response.code() + " is successful = " + response.isSuccessful());
                Log.d(TAG, "body = " + response.body().toString());
                Log.d(TAG, "message = " + response.toString());
                if (response.isSuccessful()) {
                    JSONObject obj = null;
                    List<Notice> notices = response.body();
                    for(Notice n : notices)
                        Log.v(TAG, "notice="+n.toString());
                    mAdapter.mItems.addAll(notices);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Notice>> call, Throwable t) {
                Log.d(TAG, "error message = " + t.getMessage());
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        ArrayList<Notice> mItems;
        Context context;

        public MyAdapter(Context context) {
            this.context = context;
            mItems = new ArrayList<Notice>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = null;
            if(viewType == VIEW_TYPE_HEADER){
                v = LayoutInflater.from(context).inflate(R.layout.notice_list_header, parent, false);
                return new HeaderViewHolder(v);
            }else if(viewType == VIEW_TYPE_ITEM){
                v = LayoutInflater.from(context).inflate(R.layout.notice_list_item, parent, false);
                return new ItemViewHolder(v);
            }
            return new ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.v(TAG, "onBindViewHolder position="+position+", holder:"+holder);

            if(holder instanceof HeaderViewHolder){
                HeaderViewHolder vh = (HeaderViewHolder)holder;
                vh.tv1.setText("NOTICE");
                vh.tv2.setText("함께 운동하기 에서 알려드립니다.");
            }else if(holder instanceof ItemViewHolder){
                Notice item = mItems.get(position-1);
                ItemViewHolder vh = (ItemViewHolder)holder;
                vh.tv1.setText(Util.getStringTime(item.time));
                vh.tv2.setText(item.title);
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

        public class HeaderViewHolder extends RecyclerView.ViewHolder{
            TextView tv1, tv2;
            public HeaderViewHolder(View itemView) {
                super(itemView);
                tv1 = (TextView)itemView.findViewById(R.id.tv_notice_header_title);
                tv2 = (TextView)itemView.findViewById(R.id.tv_notice_header_subtitle);
            }
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tv1, tv2;
            public ItemViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Notice item = mItems.get(getAdapterPosition()-1);
                        goToDetail(item);
                    }
                });                tv1 = (TextView)itemView.findViewById(R.id.tv_notice_date);
                tv2 = (TextView)itemView.findViewById(R.id.tv_notice_title);
            }
        }
    }

    void goToDetail(Notice item){
        Intent i = new Intent(this, NoticeDetailActivity.class);
        i.putExtra("notice", item);
        startActivity(i);
    }

}
