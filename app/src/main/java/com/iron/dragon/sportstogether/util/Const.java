package com.iron.dragon.sportstogether.util;

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
