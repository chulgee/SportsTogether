package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.bean.BuddyInfo;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.data.LoginPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BuddyListActivity extends AppCompatActivity {

    ListView lv;
    BuddyInfo myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_list);
        lv = (ListView)findViewById(R.id.lv);

        Intent intent = getIntent();
        //processIntent(intent);



        myInfo = (BuddyInfo)intent.getSerializableExtra("myInfo");
        HashMap<String, Object> map = (HashMap<String, Object>)intent.getSerializableExtra("buddyList");
        ArrayList<Profile> arr = (ArrayList<Profile>)map.get("data");
        MyAdpater adpater = new MyAdpater(arr);
        lv.setAdapter(adpater);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(BuddyListActivity.this, ""+position, View.LENGTH_SHORT).show();
                Intent intent = new Intent(BuddyListActivity.this, ChatActivity.class);

                Profile buddy = (Profile) parent.getAdapter().getItem(position);
                intent.putExtra("BuddyProfile", buddy);

                Profile me = (Profile) LoginPreferences.GetInstance().getLocalProfile(BuddyListActivity.this);
                intent.putExtra("MyProfile", me);

                startActivity(intent);
            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processIntent(intent);
    }

    private void processIntent(Intent i){
        HashMap<String, Objects> map = (HashMap<String, Objects>)i.getSerializableExtra("buddylist");
        //ArrayList<Profile> al = (ArrayList<Profile>)map.get("data");
    }

    class MyAdpater extends BaseAdapter {
        ArrayList<Profile> items = new ArrayList<Profile>();

        public MyAdpater(ArrayList<Profile> items) {
            this.items = items;
        }

        public void setItem(ArrayList<Profile> profiles){
            items = profiles;
        }

        @Override
        public int getCount() {
            if(items != null){
                return items.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(items != null){
                return items.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder vh;

            if(v == null){
                v = (View)getLayoutInflater().inflate(R.layout.list_item, null);
                vh = new ViewHolder();
                vh.etId = (TextView)v.findViewById(R.id.lv_id);
                vh.etPassword = (TextView)v.findViewById(R.id.lv_alias);
                vh.etToday = (TextView)v.findViewById(R.id.lv_today);
                v.setTag(vh);
            }else{
                vh = (ViewHolder)v.getTag();
            }
            vh.etId.setText(items.get(position).getUsername());
            vh.etToday.setText(items.get(position).getPhone());

            return v;
        }
    }
    class ViewHolder{
        TextView etId;
        TextView etPassword;
        TextView etToday;
    }
}
