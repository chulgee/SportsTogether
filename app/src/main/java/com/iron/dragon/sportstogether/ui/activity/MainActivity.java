package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.gcm.MyFirebaseInstanceIdService;
import com.iron.dragon.sportstogether.service.FloatingService;
import com.iron.dragon.sportstogether.ui.fragment.BookFragment;
import com.iron.dragon.sportstogether.ui.fragment.ChatRoomListFragment;
import com.iron.dragon.sportstogether.ui.fragment.NewsFragment;
import com.iron.dragon.sportstogether.ui.fragment.SportsFragment;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment mSports;
    Fragment mNews;
    Fragment mBooking;
    Fragment mChatRoom;

    ViewPager pager;
    MainViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSports = new SportsFragment();
        mNews = new NewsFragment();
        mBooking = new BookFragment();
        mChatRoom = new ChatRoomListFragment();

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        //pager.setOffscreenPageLimit(3);

        adapter.addItem(mSports, "스포츠");
        adapter.addItem(mNews, "뉴스");
        adapter.addItem(mBooking, "예약");
        adapter.addItem(mChatRoom, "채팅");

        pager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        tabs.getTabAt(0).setText(adapter.getPageTitle(0));
        tabs.getTabAt(1).setText(adapter.getPageTitle(1));
        tabs.getTabAt(2).setText(adapter.getPageTitle(2));
        tabs.getTabAt(3).setText(adapter.getPageTitle(3));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                pager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "메세지 보내기", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public MainViewPagerAdapter getAdapter(){
        return adapter;
    }

    public void onFragmentChange(int position) {
        if (position == 0) {
            pager.setCurrentItem(0);
        } else if (position == 1) {
            pager.setCurrentItem(1);
        } else if (position == 2) {
            pager.setCurrentItem(2);
        }else if(position == 3){
            Toast.makeText(this, "chat room", Toast.LENGTH_SHORT).show();
            pager.setCurrentItem(3);
            adapter.notifyDataSetChanged();
        }
    }

    public static class MainViewPagerAdapter extends FragmentStatePagerAdapter {
        private static final String TAG = "MainViewPagerAdapter";
        ArrayList<Fragment> items = new ArrayList<Fragment>();
        ArrayList<String> titles = new ArrayList<String>();

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item, String title) {
            items.add(item);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getItemPosition(Object item) {
            Log.v(TAG, "this is item="+item);

            if(item instanceof ChatRoomListFragment){
                return POSITION_NONE;
            }

            return POSITION_UNCHANGED;
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i = new Intent();
        if (id == R.id.nav_setting) {
            // Handle the camera action
            LoginPreferences.GetInstance().SetLogout(this);
            Toast.makeText(this, "log out", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_myinfo) {
            i.setClass(this, ProfileManagerActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_announcement) {
            i.setClass(this, NoticeBoardActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_cs) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"chulchoicezee@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "개발자에게 문의합니다.");
            intent.putExtra(Intent.EXTRA_TEXT   , "");
            startActivity(Intent.createChooser(intent, "개발자에게 이메일 보내기"));
        } else if (id == R.id.nav_share) {
            //1. MyFirebaseInstanceIdService.sendRegidToServer(getApplicationContext(), "hello,I am regid");

            /*2. Intent intent = new Intent(this, FloatingService.class);
            if(FloatingService.getFloating()){
                stopService(intent);
            }
            Message m = new Message.Builder(Message.PARAM_FROM_OTHER).msgType(Message.PARAM_TYPE_LOG).sender("server")
                    .message("new friend coming").date(new Date().getTime()).build();
            intent.putExtra("Message", m);
            Profile buddy = new Profile("regid", "도깨비신부 지은탁 뿅뿅", 0, 0, 1, 0, "12345", 0, null);
            intent.putExtra("Buddy", buddy);
            startService(intent);*/

            try {
                final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
                final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                kakaoTalkLinkMessageBuilder.addAppLink("자세히 보기", new AppActionBuilder().setUrl("market://details?id=com.iron.dragon.sportstogether").build()); // PC 카카오톡 에서 사용하게 될 웹사이트 주소
                kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
            } catch (KakaoParameterException e) {
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
