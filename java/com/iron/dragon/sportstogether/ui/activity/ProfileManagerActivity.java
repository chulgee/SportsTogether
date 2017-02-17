package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
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
        mBinding.setViewModel(mViewModel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        InitLayout();
        mViewModel.LoadProfilesData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void InitLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mBinding.profileManagerContent.profilesRecyclerviewer.setLayoutManager(layoutManager);

        final ProfileManagerRecyclerViewAdapter adapter = new ProfileManagerRecyclerViewAdapter(this);
        mBinding.profileManagerContent.profilesRecyclerviewer.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProfileManagerRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProfileManagerRecyclerViewAdapter.ViewHolderItem viewHolderItem, View view, int position, long itemId) {
                Intent i = new Intent();
                i.putExtra("MyProfile", LoginPreferences.GetInstance().loadSharedPreferencesProfile(getApplicationContext(), adapter.getItem(position).getProfile().getSportsid()));
                i.setClass(ProfileManagerActivity.this, ProfileActivity.class);
                startActivityForResult(i, 0);
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mBinding.profileManagerContent.profilesRecyclerviewer.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("onActivityResult");
        if(requestCode == 0 ) {
            Logger.d("resultCode = "  + resultCode);
            if(resultCode == RESULT_OK) {
                refreshData();
            }
        }
    }

    private void refreshData() {
        ProfileManagerRecyclerViewAdapter adapter = (ProfileManagerRecyclerViewAdapter) mBinding.profileManagerContent.profilesRecyclerviewer.getAdapter();
        adapter.clearItem();
        mViewModel.LoadProfilesData();
        mBinding.profileManagerContent.profilesRecyclerviewer.getAdapter().notifyDataSetChanged();
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
