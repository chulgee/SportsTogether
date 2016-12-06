package com.iron.dragon.sportstogether.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.factory.SportsFactory;
import com.iron.dragon.sportstogether.factory.Factory;
import com.iron.dragon.sportstogether.ui.adapter.MainAdapter;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by user on 2016-08-12.
 */
public class SportsFragment extends Fragment {
    ListView listView;
    //SingerAdapter adapter;
    Factory mFactory;
    RecyclerView mList;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_sports, container, false);

        mList = (RecyclerView)rootView.findViewById(R.id.list);

        mLayoutManager = new GridLayoutManager(getActivity(),2);
        mList.setLayoutManager(mLayoutManager);

        String[] sports = getResources().getStringArray(R.array.sportstype);
        mFactory = new SportsFactory();
        for(String type:sports) {
            mFactory.create(type);

        }
        Log.v(TAG, "getList="+mFactory.getList().toString());
        mAdapter = new MainAdapter(getActivity(), mFactory.getList());
        mList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




    }

}
