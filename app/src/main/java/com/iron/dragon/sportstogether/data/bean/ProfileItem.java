package com.iron.dragon.sportstogether.data.bean;

/**
 * Created by seungyong on 2016-11-10.
 */

public class ProfileItem {
    private int _mId;
    private String _mNickName;
    private int _mAge;
    private int _mGender;
    private int _mLocation;
    private String _mPhoneNum;
    private int _mSportsType;
    private int _mLevel;

    public ProfileItem() {
    }

    public String get_mNickName() {
        return _mNickName;
    }

    public void set_mNickName(String name) {
        try {
            this._mNickName = name;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int get_mAge() {
        return _mAge;
    }

    public void set_mAge(int age) {
        try {
            this._mAge =age;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int get_mGender() {
        return _mGender;
    }

    public void set_mGender(int gender) {
        try {
            this._mGender = (gender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int get_mLocation() {
        return _mLocation;
    }

    public void set_mLocation(int location) {
        try {
            this._mLocation = location;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get_mPhoneNum() {
        return _mPhoneNum;
    }
    public void set_mPhoneNum(String phonenum) {
        try {
            this._mPhoneNum = phonenum;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int get_mSportsType() {
        return _mSportsType;
    }

    public void set_mSportsType(int type) {
        try {
            this._mSportsType = (type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int get_mLevel() {
        return _mLevel;
    }

    public void set_mLevel(int level) {
        try {
            this._mLevel = (level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
