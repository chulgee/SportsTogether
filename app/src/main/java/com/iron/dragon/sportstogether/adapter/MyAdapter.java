package com.iron.dragon.sportstogether.adapter;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iron.dragon.sportstogether.MainActivity;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.abs.Sports;
import com.iron.dragon.sportstogether.retrofit.Bulletin;
import com.iron.dragon.sportstogether.retrofit.GitHubService;
import com.iron.dragon.sportstogether.retrofit.ProfileWithId;

import static com.iron.dragon.sportstogether.util.Const.SPORTS;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Sports> mDataset;
    private Context mContext;

    public MyAdapter(Context context, List<Sports> items) {
        mDataset = items;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv;
        public TextView tv;

        public ViewHolder(View view){
            super(view);
            iv = (ImageView)view.findViewById(R.id.imageView);
            tv = (TextView)view.findViewById(R.id.textView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //holder.iv.setImageResource(R.drawable.a);
        int id;
        int res = 0;
        id = (int)getItemId(position);
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
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ""+position, Toast.LENGTH_SHORT).show();
                /*GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final Call<Bulletin> call =
                        gitHubService.deleteBulletin(2);
                call.enqueue(new Callback<Bulletin>() {
                    @Override
                    public void onResponse(Call<Bulletin> call, Response<Bulletin> response) {
                        android.util.Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                        android.util.Log.d("Test", "body = " + response.body().toString());
                        android.util.Log.d("Test", "message = " + response.message());
                    }

                    @Override
                    public void onFailure(Call<Bulletin> call, Throwable t) {
                        android.util.Log.d("Test", "error message = " + t.getMessage());
                    }
                });*/
            }
        });
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if(mDataset == null)
            ret = 0;
        else
            ret = mDataset.size();
        return ret;
    }

    @Override
    public long getItemId(int position) {
        Sports sports = mDataset.get(position);
        return sports.getId();
    }
}