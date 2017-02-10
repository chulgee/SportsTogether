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

    Badminton(0, R.string.badminton, R.color.sports_1, R.drawable.ic_badminton, R.drawable.badminton_166415_640),
    Tennis(1, R.string.tennis, R.color.sports_2, R.drawable.ic_tennis, R.drawable.tennis_934841_640),
    Table_tennis(2, R.string.table_tennis, R.color.sports_3, R.drawable.ic_table_tennisg, R.drawable.table_tennis_1208377_640),
    Soccer(3, R.string.soccer, R.color.sports_4, R.drawable.ic_soccer, R.drawable.soccer_ball_1285164_640),
    Baseball(4, R.string.baseball, R.color.sports_5, R.drawable.ic_baseball, R.drawable.hit_1407826_640),
    Basketball(5, R.string.basketball, R.color.sports_6, R.drawable.ic_basketball, R.drawable.basketball_1081882_640);

    int value;
    int resid_str;
    int resid_color;
    int resid_icon;
    int resid_image;

    SportsType(int value, int resid_str, int resid_color, int resid_icon, int resid_image){
        this.value = value;
        this.resid_str = resid_str;
        this.resid_color = resid_color;
        this.resid_icon = resid_icon;
        this.resid_image = resid_image;
    }

    public int getValue(){
        return value;
    }

    public int getResid(){
        return resid_str;
    }

    public int getResid_color(){
        return resid_color;
    }

    public int getResid_icon(){
        return resid_icon;
    }

    public int getResid_image(){
        return resid_image;
    }
}
