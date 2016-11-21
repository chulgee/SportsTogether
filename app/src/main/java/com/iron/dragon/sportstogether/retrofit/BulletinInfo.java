package com.iron.dragon.sportstogether.retrofit;

/**
 * Created by chulchoice on 2016-11-18.
 */
public class BulletinInfo {

    String username;
    private int locationid;
    private int sportsid;
    private int comment;
    private String date;////tes//zz

    //
    BulletinInfo(Builder builder){
        username = builder.username;
        locationid = builder.locationid;
        sportsid = builder.sportsid;
        comment = builder.comment;
        date = builder.date;
    }
    public String getUsername() {
        return username;
    }

    public int getLocationid() {
        return locationid;
    }

    public int getSportsid() {
        return sportsid;
    }

    public int getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public class Builder{
        String username;
        int locationid;
        int sportsid;
        int comment;
        String date;

        public void setUsername(String username) {this.username = username;}
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
//////