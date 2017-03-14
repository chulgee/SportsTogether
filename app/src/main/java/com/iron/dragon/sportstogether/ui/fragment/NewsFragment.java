package com.iron.dragon.sportstogether.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.viewmodel.NewsFragViewModel;
import com.iron.dragon.sportstogether.databinding.NewsFragBinding;
import com.iron.dragon.sportstogether.ui.adapter.NewsRecyclerViewAdapter;
import com.iron.dragon.sportstogether.ui.adapter.item.NewsListItem;
import com.iron.dragon.sportstogether.ui.view.DividerItemDecoration;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by user on 2016-08-12.
 */
public class NewsFragment extends Fragment {

    private NewsFragViewModel mViewModel;
    private NewsFragBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewModel = new NewsFragViewModel(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.news_frag, container, false);
        mBinding.setViewModel(mViewModel);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitLayout();
        mViewModel.LoadNewsData();
    }


    private void InitLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        mBinding.newsRecyclerviewer.setLayoutManager(layoutManager);

        final NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(this);
        mBinding.newsRecyclerviewer.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext().getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mBinding.newsRecyclerviewer.addItemDecoration(dividerItemDecoration);
    }

    public void setListItem(ArrayList<NewsListItem> listItems) {
        NewsRecyclerViewAdapter adapter = (NewsRecyclerViewAdapter) mBinding.newsRecyclerviewer.getAdapter();
        if(adapter==null) Logger.d("setListItem adapter is null");
        adapter.setItem(listItems);
    }


    public void InvalidateAdapter() {
        NewsRecyclerViewAdapter adapter = (NewsRecyclerViewAdapter) mBinding.newsRecyclerviewer.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.onDestroyView();
    }
}
