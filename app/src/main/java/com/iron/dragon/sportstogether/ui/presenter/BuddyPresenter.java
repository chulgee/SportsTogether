package com.iron.dragon.sportstogether.ui.presenter;

import android.content.Context;
import android.view.View;

import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;

import java.util.List;

/**
 * Created by chulchoice on 2017-01-25.
 */

public interface BuddyPresenter {

    // to handle Ui event : view -> model
    void onRowClick(View v, Profile item);
    void onChatClick(View v, Profile item);

    // to handle Ui update : model -> view
    interface BuddyView{
        void updateView(List<Profile> profiles);
        void showDialog(Profile item);
    }

    // logic api
    void getProfiles(Context context, Profile buddy);

}
