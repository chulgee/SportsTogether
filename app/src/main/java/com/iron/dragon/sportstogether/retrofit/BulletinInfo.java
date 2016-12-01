package com.iron.dragon.sportstogether.retrofit;

/**
 * Created by chulchoice on 2016-11-18.
 */
public class BulletinInfo {

    String username;
    private int locationid;
    private int sportsid;
    private String comment;
    private String date;////tes//zz
    private int type;

    //
    public BulletinInfo(Builder builder){
        username = builder.username;
        locationid = builder.locationid;
        sportsid = builder.sportsid;
        comment = builder.comment;
        date = builder.date;
        type = builder.type;
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

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public static class Builder{
        private String username;
        private  int locationid;
        private  int sportsid;
        private  String comment;
        private  String date;
        private  int type;


        public Builder setType(int type) {
            this.type = type;
            return this;
        }



        public Builder setUsername(String username) {this.username = username;
            return this;}
        public Builder setLocationid(int locationid) {
            this.locationid = locationid;
            return this;
        }

        public Builder setSportsid(int sportsid) {
            this.sportsid = sportsid;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public BulletinInfo build(){
            return new BulletinInfo(this);
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }
    }


}
//////