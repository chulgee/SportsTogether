package com.iron.dragon.sportstogether.enums;

import com.iron.dragon.sportstogether.R;

/**
 * Created by P10950 on 2017-02-06.
 */

public enum LevelType {

    lv1(0, R.string.lv1),
    lv2(1, R.string.lv2),
    lv3(2, R.string.lv3),
    lv4(3, R.string.lv4),
    lv5(4, R.string.lv5),
    lv6(5, R.string.lv6);

    int value;
    int resid;

    LevelType(int value, int resid){
        this.value = value;
        this.resid = resid;
    }

    public int getValue(){
        return value;
    }

    public int getResid(){
        return resid;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
