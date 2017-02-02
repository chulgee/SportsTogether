package com.iron.dragon.sportstogether.util;

import com.iron.dragon.sportstogether.BuildConfig;

/**
 * Created by chulchoice on 2016-11-18.
 */
public class Const {
    // server 설정
    //public static final String MAIN_URL = "http://10.138.19.140:9000"; // for local test

    //public static final String MAIN_URL = "http://192.168.0.6:9000"; // for local test
    //public static final String MAIN_URL = "http://172.20.12.128:9000"; // for local test
    //public static final String MAIN_URL = "http://172.30.1.6:9000"; // for local test
    public static final String MAIN_URL = "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000"; // for aws
    public static final String NEWS_URL = "https://openapi.naver.com/v1/"; // for naver news
    public static final String NAVER_CLIENT_ID = BuildConfig.NAVER_CLIENT_ID;
    public static final String NAVER_CLIENT_SECRET = BuildConfig.NAVER_CLIENT_SECRET;
    public static final String BR_REFRESH_CHAT_LIST = "broadcast.refresh.chat.list";


    public enum SPORTS{
        BADMINTON,
        TENNIS,
        TABLE_TENNIS,
        SOCCER,
        BASEBALL,
        BASKETBALL
    }

    public enum GENDER{
        MALE,
        FEMALE
    }

    public static final String CONTENT_URI_STR = "content://com.iron.dragon.provider";
}
