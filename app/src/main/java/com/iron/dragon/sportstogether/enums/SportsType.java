package com.iron.dragon.sportstogether.enums;

import com.iron.dragon.sportstogether.R;

/**
 * Created by P10950 on 2017-02-06.
 */

public enum SportsType {

/*    Badminton(0, R.string.badminton, R.drawable.badminton, R.color.sports_1),
    Tennis(1, R.string.tennis, R.drawable.tennis, R.color.sports_2),
    Table_tennis(2, R.string.table_tennis, R.drawable.table_tennis, R.color.sports_3),
    Soccer(3, R.string.soccer, R.drawable.soccer, R.color.sports_4),
    Baseball(4, R.string.baseball, R.drawable.baseball, R.color.sports_5),
    Basketball(5, R.string.basketball, R.drawable.basketball, R.color.sports_6);*/

    Badminton(0, R.string.badminton, R.color.sports_1, R.drawable.badminton_166415_640),
    Tennis(1, R.string.tennis, R.color.sports_2, R.drawable.tennis_934841_640),
    Table_tennis(2, R.string.table_tennis, R.color.sports_3, R.drawable.table_tennis_1208377_640),
    Soccer(3, R.string.soccer, R.color.sports_4, R.drawable.soccer_ball_1285164_640),
    Baseball(4, R.string.baseball, R.color.sports_5, R.drawable.hit_1407826_640),
    Basketball(5, R.string.basketball, R.color.sports_6, R.drawable.basketball_1081882_640);

    int value;
    int resid_str;
    int resid_image1;
    int resid_image2;

    SportsType(int value, int resid_str, int resid_image1, int resid_image2){
        this.value = value;
        this.resid_str = resid_str;
        this.resid_image1 = resid_image1;
        this.resid_image2 = resid_image2;
    }

    public int getValue(){
        return value;
    }

    public int getResid(){
        return resid_str;
    }

    public int getResid_image1(){
        return resid_image1;
    }

    public int getResid_image2(){
        return resid_image2;
    }
}
