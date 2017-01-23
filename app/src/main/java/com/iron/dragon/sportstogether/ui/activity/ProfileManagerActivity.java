package com.iron.dragon.sportstogether.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.viewmodel.ProfileManagerViewModel;
import com.iron.dragon.sportstogether.databinding.ProfileManagerActBinding;
import com.iron.dragon.sportstogether.ui.adapter.ProfileManagerRecyclerViewAdapter;
import com.iron.dragon.sportstogether.ui.adapter.item.ProfileManagerItem;
import com.iron.dragon.sportstogether.ui.view.DividerItemDecoration;
import com.iron.dragon.sportstogether.util.ToastUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class ProfileManagerActivity extends AppCompatActivity {

    private ProfileManagerViewModel mViewModel;
    private ProfileManagerActBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ProfileManagerViewModel(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.profile_manager_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        InitLayout();
        mViewModel.LoadProfilesData();

    }

    private void InitLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mBinding.profileManagerContent.profilesRecyclerviewer.setLayoutManager(layoutManager);

        final ProfileManagerRecyclerViewAdapter adapter = new ProfileManagerRecyclerViewAdapter(this);
        mBinding.profileManagerContent.profilesRecyclerviewer.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mBinding.profileManagerContent.profilesRecyclerviewer.addItemDecoration(dividerItemDecoration);
    }

    public void showToast(String string) {
        ToastUtil.show(getApplicationContext(), string);
    }

    public void setListItem(ArrayList<ProfileManagerItem> list) {
        if(list == null) Logger.d("setListItem list is null");
        ProfileManagerRecyclerViewAdapter adapter = (ProfileManagerRecyclerViewAdapter) mBinding.profileManagerContent.profilesRecyclerviewer.getAdapter();
        if(adapter==null) Logger.d("setListItem adapter is null");
        adapter.setItem(list);
    }
}
