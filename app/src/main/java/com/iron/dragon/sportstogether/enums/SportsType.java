package com.iron.dragon.sportstogether.enums;

import com.iron.dragon.sportstogether.R;

/**
 * Created by P10950 on 2017-02-06.
 */

public enum SportsType {

    Badminton(0, R.string.badminton),
    Tennis(1, R.string.tennis),
    Table_tennis(2, R.string.table_tennis),
    Soccer(3, R.string.soccer),
    Baseball(4, R.string.baseball),
    Basketball(5, R.string.basketball);

    int value;
    int resid;

    SportsType(int value, int resid){
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
