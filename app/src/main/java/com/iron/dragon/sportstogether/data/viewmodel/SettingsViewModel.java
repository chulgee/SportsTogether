package com.iron.dragon.sportstogether.data.viewmodel;

import android.databinding.BaseObservable;
import android.util.Log;

import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Settings;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.ui.activity.SettingsActivity;
import com.iron.dragon.sportstogether.util.Const;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

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
        final Call<Settings> call =
                gitHubService.putSettings(LoginPreferences.GetInstance().GetRegid(mActivity.getApplicationContext()), update);
        call.enqueue(new Callback<Settings>() {
            @Override
            public void onResponse(Call<Settings> call, Response<Settings> response) {
                Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Settings> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });


    }
}
