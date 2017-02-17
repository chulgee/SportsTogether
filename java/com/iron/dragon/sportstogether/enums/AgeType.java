package com.iron.dragon.sportstogether.enums;

import com.iron.dragon.sportstogether.R;

/**
 * Created by P10950 on 2017-02-06.
 */

public enum AgeType {

    in10(0, R.string.in10),
    in20(1, R.string.in20),
    in30(2, R.string.in30),
    in40(3, R.string.in40),
    in50(4, R.string.in50),
    in60(5, R.string.in60),
    in70(6, R.string.in70),
    in80(7, R.string.in80);

    int value;
    int resid;

    AgeType(int value, int resid){
        this.value = value;
        this.resid = resid;
    }

    public int getValue(){
        return value;
    }

    public int getResid(){
        return resid;
    }
}
