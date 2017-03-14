package com.iron.dragon.sportstogether.data.viewmodel;

import android.databinding.BaseObservable;

import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Settings;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.ui.activity.SettingsActivity;
import com.iron.dragon.sportstogether.util.Const;
import com.orhanobut.logger.Logger;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by P16018 on 2017-02-06.
 */

public class SettingsViewModel extends BaseObservable {
    private SettingsActivity mActivity;
    public SettingsViewModel(SettingsActivity settingsActivity) {
        mActivity = settingsActivity;
    }

    public void switchBuddyAlarmChanged(boolean isChecked) {
        LoginPreferences.GetInstance().SetBuddyAlarmOn(mActivity.getApplicationContext(), isChecked);
    }
    public void switchChatAlarmChanged(boolean isChecked) {
        LoginPreferences.GetInstance().SetChatAlarmOn(mActivity.getApplicationContext(), isChecked);
    }

    public void SaveSetting(boolean buddycheck, boolean chatcheck) {
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        GitHubService gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
        Settings update = new Settings(buddycheck, chatcheck);

        gitHubService.putSettings(LoginPreferences.GetInstance().GetRegid(mActivity.getApplicationContext()), update)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(setting -> Logger.v("onResponse response.isSuccessful()=" + setting.toString()));


    }
}
