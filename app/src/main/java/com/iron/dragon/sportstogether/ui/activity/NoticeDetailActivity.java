package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.Notice;
import com.iron.dragon.sportstogether.util.Util;

public class NoticeDetailActivity extends AppCompatActivity {

    TextView tv_notice_detail_time;
    TextView tv_notice_detail_title;
    TextView tv_notice_detail_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_detail_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("공지내용");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        Notice item = (Notice)i.getSerializableExtra("notice");

        tv_notice_detail_time = (TextView)findViewById(R.id.tv_notice_detail_time);
        tv_notice_detail_title = (TextView)findViewById(R.id.tv_notice_detail_title);
        tv_notice_detail_body = (TextView)findViewById(R.id.tv_notice_detail_body);

        tv_notice_detail_time.setText(Util.getStringTime(item.time));
        tv_notice_detail_title.setText(item.title);
        tv_notice_detail_body.setText(item.body);
    }
}
