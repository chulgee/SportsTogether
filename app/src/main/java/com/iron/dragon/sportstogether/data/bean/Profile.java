package com.iron.dragon.sportstogether.data.bean;

import java.io.Serializable;

/**
 * Created by seungyong on 2016-11-10.
 */

public class Profile implements Serializable{

    public Profile(){

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

    public void setSportsid(int sportsid) {
        this.sportsid = sportsid;
    }

    public void setLocationid(int locationid) {
        this.locationid = locationid;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    protected int age;
    protected int gender;
    protected String phone;
    protected int level;
    protected String image;
    protected int unread;

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
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
                .append(", level="+level)
                .append(", image="+image);
        return sb.toString();
    }
}
