package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.factory.Sports;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.ui.activity.BuddyListActivity;
import com.iron.dragon.sportstogether.ui.activity.BulletinListActivity;
import com.iron.dragon.sportstogether.ui.activity.LoginActivity;
import com.iron.dragon.sportstogether.util.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iron.dragon.sportstogether.util.Const.SPORTS;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private static final String TAG = "MainAdapter";
    private List<Sports> mDataset;
    private Context mContext;

    public MainAdapter(Context context, List<Sports> items) {
        mDataset = items;
        Log.v(TAG, "mDataset="+mDataset.toString());
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //holder.iv.setImageResource(R.drawable.a);
        int res = 0;
        SPORTS sports = SPORTS.values()[position];
        if(sports.equals(SPORTS.BADMINTON)){
            res = R.drawable.badminton;
        }else if(sports.equals(SPORTS.TENNIS)){
            res = R.drawable.tennis;
        }else if(sports.equals(SPORTS.TABLE_TENNIS)){
            res = R.drawable.table_tennis;
        }else if(sports.equals(SPORTS.SOCCER)){
            res = R.drawable.soccer;
        }else if(sports.equals(SPORTS.BASEBALL)){
            res = R.drawable.baseball;
        }else if(sports.equals(SPORTS.BASKETBALL)) {
            res = R.drawable.basketball;
        }else{
            res = R.drawable.t;
        }

        holder.tv.setText(mDataset.get(position).getName());
        holder.iv.setImageResource(res);
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        holder.iv.setAnimation(animation);
        final int finalRes = res;
        final int id = (int)getItemId(position);
        Log.d("Test", "id = " + id);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean login = LoginPreferences.GetInstance().GetLogin(mContext.getApplicationContext());
                Log.v(TAG, "login="+login);

                // if 로그인 되어있으면 게시판으로
                // else 로그인안되어 있으면 profile edit창으로
                Intent i = new Intent();
                if(login){
                    Profile profile = LoginPreferences.GetInstance().getLocalProfile(mContext);
                    i.putExtra("MyProfile", profile);
                    i.putExtra("Extra_Sports", id);
                    i.putExtra("Extra_SportsImg", finalRes);
                    i.setClass(mContext, BulletinListActivity.class);
                    mContext.startActivity(i);
                    //requestFriends();
                    Toast.makeText(mContext.getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                }else{
                    i.setClass(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                }

            }
        });
    }

    void requestFriends(){
        final Profile myProfile = LoginPreferences.GetInstance().getLocalProfile(mContext);
        String url = Const.MAIN_URL + "/profiles?" + "username=" + myProfile.getUsername() + "&sportsid=" + myProfile.getSportsid()
                                                                + "&locationid=" + myProfile.getLocationid() + "&reqFriends=" + "true";
        Log.v(TAG, "url="+url);
        final String urlPath = Const.MAIN_URL + "/profiles";
        StringRequest request = new StringRequest(
                Request.Method.GET,
                urlPath,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        println("onResponse() 호출됨 : " + response);
                        Gson gson = new Gson();
                        try {
                            JSONObject obj = new JSONObject(response);
                            String command = obj.getString("command");
                            String code = obj.getString("code");
                            JSONArray arr = obj.getJSONArray("message");
                            ArrayList<Profile> al = new ArrayList<Profile>();
                            for(int i=0; i<arr.length(); i++){
                                Profile p = gson.fromJson(arr.get(i).toString(), Profile.class);
                                al.add(p);
                                Log.v(TAG, "p="+p.toString());
                            }
                            Intent i = new Intent(mContext, BuddyListActivity.class);
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("data", al);
                            i.putExtra("buddyList", map);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //println("응답 command : " + chatData.command);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("onErrorResponse() 호출됨 : " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            public String getUrl() {
                StringBuilder sb = new StringBuilder(urlPath);
                try {
                    String  param1 = URLEncoder.encode(myProfile.getUsername(), "UTF-8");
                    sb.append("?username=").append(param1)
                            .append("&sportsid=").append(myProfile.getSportsid())
                            .append("&locationid=").append(myProfile.getLocationid())
                            .append("&reqFriends=").append("true");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v(TAG, "getUrl="+sb.toString());
                return sb.toString();
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(mContext).add(request);
        println("웹서버에 요청함 : " + Const.MAIN_URL);

        /*
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        final Call<JSONObject> call = gitHubService.getProfiles(username, position, 0, 1);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                int code = response.code();
                android.util.Log.d(TAG, "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                if(code == 200){
                    Log.v(TAG, "onResponse : "+response.toString());
                    Log.v(TAG, "onResponse : "+response.body());
                    *//*JSONArray jArr = new JSONArray(response.body());
                    Log.v(TAG, response.body().toString());
                    Gson gson = new Gson();
                    ArrayList<Profile> pArr = new ArrayList<Profile>();
                    for(int i=0; i<jArr.length(); i++){
                        Profile p = null;
                        try {
                            p = gson.fromJson(jArr.get(i).toString(), Profile.class);
                            Log.v(TAG, "profile["+i+"] : "+p.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pArr.add(p);
                    }*//*
                }else{
                    Log.v(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                android.util.Log.d(TAG, "error message = " + t.getMessage());
            }
        });*/
    }

    void println(String data){
        System.out.println(data+"\n");
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    @Override
    public long getItemId(int position) {
        Sports sports = mDataset.get(position);
        return sports.getId();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv;

        public ViewHolder(View view){
            super(view);
            iv = (ImageView)view.findViewById(R.id.imageView);
            tv = (TextView)view.findViewById(R.id.textView);
        }
    }
}