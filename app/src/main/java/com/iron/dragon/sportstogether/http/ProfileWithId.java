package com.iron.dragon.sportstogether.http;

import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.data.bean.ProfileItem;

/**
 * Created by seungyong on 2016-11-09.
 */

public class ProfileWithId extends Profile {

    private int id;

    public ProfileWithId(ProfileItem profile) {
        super(profile);
    }
    @Override
    public String toString() {
        return id + " (" + username + ")";
    }
}