package com.iron.dragon.sportstogether;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.iron.dragon.sportstogether.adapter.BoardRecyclerViewAdapter;

public class BoardListView extends AppCompatActivity {

    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list_view);
        InitLayout();
    }

    private void InitLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.board_recyclerviewer);
        mRecyclerView.setAdapter(new BoardRecyclerViewAdapter());
    }

}
