package com.iron.dragon.sportstogether.retrofit;

import android.net.Uri;

/**
 * Created by chulchoice on 2016-11-18.
 */
public class BulletinInfo {

    int locationid;
    int sportsid;
    int comment;
    String date;////tes

    BulletinInfo(Builder builder){
        locationid = builder.locationid;
        sportsid = builder.sportsid;
        comment = builder.comment;
        date = builder.date;
    }

    public class Builder{
        int locationid;
        int sportsid;
        int comment;
        String date;

        public void setLocationid(int locationid) {
            this.locationid = locationid;
        }

        public void setSportsid(int sportsid) {
            this.sportsid = sportsid;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public BulletinInfo build(){
            return new BulletinInfo(this);
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
