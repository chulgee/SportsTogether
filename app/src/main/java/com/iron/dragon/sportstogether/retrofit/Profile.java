package com.iron.dragon.sportstogether.retrofit;

import com.iron.dragon.sportstogether.data.ProfileItem;

/**
 * Created by seungyong on 2016-11-10.
 */

public class Profile {
    public Profile(ProfileItem profile) {
        this.nickname = profile.get_mNickName();
        this.age = profile.get_mAge();
        this.gender = profile.get_mGender();
        this.location = profile.get_mLocation();
        this.phonenum = profile.get_mPhoneNum();
        this.sportstype = profile.get_mSportsType();
        this.level = profile.get_mLevel();
    }
    private String nickname;
    private int age;
    private int gender;
    private String location;
    private String phonenum;
    private String sportstype;
    private int level;
}
