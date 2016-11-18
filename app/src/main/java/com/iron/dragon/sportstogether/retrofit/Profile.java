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

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }

    public int getGender() {
        return gender;
    }

    public int getLocationid() {
        return locationid;
    }

    public String getPhone() {
        return phone;
    }

    public int getSportsid() {
        return sportsid;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("username="+username)
                .append(", sportsid="+sportsid)
                .append(", locationid="+locationid)
                .append(", age="+age)
                .append(", gender="+gender)
                .append(", phone="+phone)
                .append(", level="+level);
        return sb.toString();
    }
}
