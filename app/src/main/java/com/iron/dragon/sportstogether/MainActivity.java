package com.iron.dragon.sportstogether;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.iron.dragon.sportstogether.abs.Factory;
import com.iron.dragon.sportstogether.adapter.MyAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_";
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

        mFactory = new SportsFactory();
        mFactory.create("배드민턴");
        mFactory.create("탁구");
        mFactory.create("테니스");
        mFactory.create("축구");

        mAdapter = new MyAdapter(getApplicationContext(), mFactory.getList());
        mList.setAdapter(mAdapter);
    }
}
