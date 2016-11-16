package com.iron.dragon.sportstogether;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.iron.dragon.sportstogether.abs.Factory;
import com.iron.dragon.sportstogether.adapter.MyAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Factory mFactory;
    RecyclerView mList;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = (RecyclerView)findViewById(R.id.list);

        mLayoutManager = new GridLayoutManager(this,2);
        mList.setLayoutManager(mLayoutManager);

        String[] sports = getResources().getStringArray(R.array.sportstype);
        mFactory = new SportsFactory();
        for(String type:sports) {
            mFactory.create(type);

        }
        mAdapter = new MyAdapter(getApplicationContext(), mFactory.getList());
        mList.setAdapter(mAdapter);
    }
}
