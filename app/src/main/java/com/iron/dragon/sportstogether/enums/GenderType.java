package com.iron.dragon.sportstogether.enums;

import com.iron.dragon.sportstogether.R;

/**
 * Created by P10950 on 2017-02-06.
 */

public enum GenderType {

    Male(0, R.string.Male),
    Femalelv2(1, R.string.Female);

    int value;
    int resid;

    GenderType(int value, int resid){
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
