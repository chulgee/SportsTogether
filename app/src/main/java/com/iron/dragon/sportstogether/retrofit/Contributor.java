package com.iron.dragon.sportstogether.retrofit;

/**
 * Created by seungyong on 2016-11-09.
 */

public class Contributor {

    int userid;
    String html_url;

    String nickname;

    @Override
    public String toString() {
        return userid + " (" + nickname + ")";
    }
}