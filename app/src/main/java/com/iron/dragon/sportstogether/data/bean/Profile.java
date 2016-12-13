package com.iron.dragon.sportstogether.data.bean;

import java.io.Serializable;

/**
 * Created by seungyong on 2016-11-10.
 */

public class Profile implements Serializable{
    public Profile(ProfileItem profile) {
        this.username = profile.get_mNickName();
        this.sportsid = profile.get_mSportsType();
        this.locationid = profile.get_mLocation();
        this.age = profile.get_mAge();
        this.gender = profile.get_mGender();
        this.phone = profile.get_mPhoneNum();
        this.level = profile.get_mLevel();
        this.image = profile.get_mImage();
    }
    public Profile(String regid, String username, int sportsid, int locationid, int age, int gender, String phone, int level, String image){
        this.regid = regid;
        this.username = username;
        this.sportsid = sportsid;
        this.locationid = locationid;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.level = level;
        this.image = image;
    }

    protected String regid;
    protected String username;
    protected int sportsid;
    protected int locationid;
    protected int age;
    protected int gender;
    protected String phone;
    protected int level;
    protected String image;


    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }
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


    public void setImage(String image) {
        this.image = image;
    }
    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("regid="+regid)
                .append(", username="+username)
                .append(", sportsid="+sportsid)
                .append(", locationid="+locationid)
                .append(", age="+age)
                .append(", gender="+gender)
                .append(", phone="+phone)
                .append(", level="+level);
        return sb.toString();
    }
}
