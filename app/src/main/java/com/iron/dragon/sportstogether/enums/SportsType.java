package com.iron.dragon.sportstogether.enums;

import com.iron.dragon.sportstogether.R;

/**
 * Created by P10950 on 2017-02-06.
 */

public enum SportsType {

    Badminton(0, R.string.badminton, R.drawable.badminton_166415_640),
    Tennis(1, R.string.tennis, R.drawable.tennis_934841_640),
    Table_tennis(2, R.string.table_tennis, R.drawable.table_tennis_1208377_640),
    Soccer(3, R.string.soccer, R.drawable.soccer_ball_1285164_640),
    Baseball(4, R.string.baseball, R.drawable.hit_1407826_640),
    Basketball(5, R.string.basketball, R.drawable.basketball_1081882_640);

    int value;
    int resid_str;
    int resid_image;

    SportsType(int value, int resid_str, int resid_image){
        this.value = value;
        this.resid_str = resid_str;
        this.resid_image = resid_image;
    }

    public int getValue(){
        return value;
    }

    public int getResid(){
        return resid_str;
    }

    public int getResid_image(){
        return resid_image;
    }
}
