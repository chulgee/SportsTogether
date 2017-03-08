package com.iron.dragon.sportstogether.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.ui.activity.BuddyActivity;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.model.BuddyModel;
import com.iron.dragon.sportstogether.util.Util;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

import static android.content.ContentValues.TAG;


/**
 * Created by chulchoice on 2017-01-25.
 */

public class BuddyPresenterImpl implements BuddyPresenter{

    BuddyView view;
    Context context;
    BuddyModel model;
    Profile me;

    public BuddyPresenterImpl(Context context, BuddyModel model){
        this.context = context;
        this.model = model;
        view = (BuddyActivity)context;
    }

    @Override
    public void onRowClick(View v, Profile item) {
        // show dialog for budy's info
        view.showDialog(item);
    }

    @Override
    public void onChatClick(View v, Profile buddy) {
        Profile me = LoginPreferences.GetInstance().loadSharedPreferencesProfile(context, buddy.getSportsid());
        Log.v(TAG, "buddy: " + buddy.toString());
        Log.v(TAG, "me: " + me.toString());
        Intent i = new Intent(context, ChatActivity.class);
        Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(buddy.getUsername())
                .message("Conversation gets started").date(new Date().getTime()).image(buddy.getImage()).build();
        i.putExtra("Message", message);
        i.putExtra("Buddy", buddy);
        context.startActivity(i);
    }

    @Override
    public void getProfiles(final Context context, Profile buddy) {
        final BuddyView listener = (BuddyView)context;
        model.getProfiles(buddy, new RetrofitHelper.OnViewUpdateListener() {
            @Override
            public void onViewUpdateListener(Response response) {
                List<Profile> profiles = (List<Profile>)response.body();
                for(Profile p : profiles){
                    int count = Util.getUnreadBuddy(context, p.getUsername());
                    p.setUnread(count);
                }
                listener.updateView(profiles);
            }
        });
    }
}
