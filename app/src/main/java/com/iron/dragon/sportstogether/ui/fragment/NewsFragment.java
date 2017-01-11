package com.iron.dragon.sportstogether.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;

/**
 * Created by user on 2016-08-12.
 */
public class NewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.news_frag, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.textView);

        return rootView;
    }

}
