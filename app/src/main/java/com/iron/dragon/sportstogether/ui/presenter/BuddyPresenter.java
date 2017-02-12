package com.iron.dragon.sportstogether.ui.presenter;

import android.view.View;

import com.iron.dragon.sportstogether.data.bean.Profile;

import java.util.List;

/**
 * Created by chulchoice on 2017-01-25.
 */

public interface BuddyPresenter {

    void onRowClick(View v, Profile item);
    void onChatClick(View v, Profile item);

    interface BuddyView{
        void updateView(List<Profile> profiles);
        void showDialog(Profile item);
    }
    void loadProfiles(Profile buddy);
}
