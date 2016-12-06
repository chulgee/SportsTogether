package com.iron.dragon.sportstogether.data.bean;

import java.io.Serializable;

/**
 * Created by Tacademy on 2016-11-22.
 */

public class BuddyInfo implements Serializable {

    String regId;
    String mobile;
    String mac;
    String id;
    String password;
    String today;
    String alias = "he";

    public BuddyInfo(String regId, String mobile, String mac, String id, String password, String today) {
        this.regId = regId;
        this.mobile = mobile;
        this.mac = mac;
        this.id = id;
        this.password = password;
        this.today = today;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
