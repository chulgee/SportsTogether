package com.iron.dragon.sportstogether.ui.adapter.item;

import com.iron.dragon.sportstogether.data.bean.Profile;
import com.orhanobut.logger.Logger;

/**
 * Created by P16018 on 2017-01-13.
 */

public class ProfileManagerItem {
    private Profile mProfile;
    public void setProfile(Profile profile) {
        mProfile= profile;
    }
    public Profile getProfile() {
        return mProfile;
    }
    public String getDefaultImage() {
        Logger.d("getDefaultImage");
        return "android.resource://com.iron.dragon.sportstogether/drawable/default_user";
    }

    public String getProfileImage() {
        Logger.d("getProfileImage = " + getProfile().getImage());

        return "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_profile?filename=" + getProfile().getImage();
    }
}
