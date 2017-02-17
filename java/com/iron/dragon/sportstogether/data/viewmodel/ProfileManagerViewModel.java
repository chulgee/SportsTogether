package com.iron.dragon.sportstogether.data.viewmodel;

import android.databinding.BaseObservable;

import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.ui.activity.ProfileManagerActivity;
import com.iron.dragon.sportstogether.ui.adapter.item.ProfileManagerItem;
import com.iron.dragon.sportstogether.util.Const;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by P16018 on 2017-01-13.
 */

public class ProfileManagerViewModel extends BaseObservable {
    private ProfileManagerActivity mActivity;
    private GitHubService gitHubService;

    public ProfileManagerViewModel(ProfileManagerActivity profileManagerActivity) {
        mActivity = profileManagerActivity;
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
    }

    public void LoadProfilesData() {
        initListView(LoginPreferences.GetInstance().loadSharedPreferencesProfileAll(mActivity.getApplicationContext()));
    }

    private void initListView(List<Profile> list) {
        Logger.d("initListView listsize=  " + list.size());
        ArrayList<ProfileManagerItem> listItems = new ArrayList<>();
        for(Profile profile:list) {
            ProfileManagerItem pf = new ProfileManagerItem();
            pf.setProfile(profile);
            listItems.add(pf);
        }
        mActivity.setListItem(listItems);
    }
}
