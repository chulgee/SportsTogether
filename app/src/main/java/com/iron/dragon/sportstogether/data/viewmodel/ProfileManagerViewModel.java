package com.iron.dragon.sportstogether.data.viewmodel;

import android.databinding.BaseObservable;

import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.ui.activity.ProfileManagerActivity;
import com.iron.dragon.sportstogether.ui.adapter.item.ProfileItem;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by P16018 on 2017-01-13.
 */

public class ProfileManagerViewModel extends BaseObservable {
    ProfileManagerActivity mActivity;
    private GitHubService gitHubService;

    public ProfileManagerViewModel(ProfileManagerActivity profileManagerActivity) {
        mActivity = profileManagerActivity;
        gitHubService = GitHubService.retrofit.create(GitHubService.class);
    }

    public void LoadProfilesData() {
        initListView(LoginPreferences.GetInstance().loadSharedPreferencesProfileAll(mActivity.getApplicationContext()));
    }

    private void initListView(List<Profile> list) {
        Logger.d("initListView listsize=  " + list.size());
        ArrayList<ProfileItem> listItems = new ArrayList<>();
        for(Profile profile:list) {
            ProfileItem pf = new ProfileItem();
            pf.setProfile(profile);
            listItems.add(pf);
        }
        mActivity.setListItem(listItems);
    }
}
