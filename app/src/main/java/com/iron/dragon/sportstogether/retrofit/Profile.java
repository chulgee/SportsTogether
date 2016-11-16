package com.iron.dragon.sportstogether.retrofit;

import com.iron.dragon.sportstogether.data.ProfileItem;

/**
 * Created by seungyong on 2016-11-10.
 */

public class Profile {
    public Profile(ProfileItem profile) {
        this.username = profile.get_mNickName();
        this.age = profile.get_mAge();
        this.gender = profile.get_mGender();
        this.locationid = profile.get_mLocation();
        this.phone = profile.get_mPhoneNum();
        this.sportsid = profile.get_mSportsType();
        this.level = profile.get_mLevel();
    }
    protected String username;
    protected int age;
    protected int gender;
    protected int locationid;
    protected String phone;
    protected int sportsid;
    protected int level;
}
