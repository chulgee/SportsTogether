package com.iron.dragon.sportstogether.data.bean;

/**
 * Created by chulchoice on 2016-11-18.
 */
public class Bulletin {
    String regid;
    String username;
    int sportsid;
    int locationid;
    int type;
    String comment;
    long date;
    String image;

    public int getLocationid() {
        return locationid;
    }

    public int getSportsid() {
        return sportsid;
    }

    public int getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }
    public String getImage() {
        return image;
    }

    public String getComment() {
        return comment;
    }

    public long getDate() {
        return date;
    }

    public Bulletin(Builder builder){
        regid = builder.regid;
        locationid = builder.locationid;
        sportsid = builder.sportsid;
        type = builder.type;
        username = builder.username;
        comment = builder.comment;
        date = builder.date;
        image = builder.image;
    }

    public static class Builder{
        private String regid;
        private int locationid;
        private int sportsid;
        private int type;
        private String username;
        private String comment;
        private long date;
        private String image;

        public Builder  setRegid(String regid) {
            this.regid = regid;
            return this;
        }
        public Builder  setLocationid(int locationid) {
            this.locationid = locationid;
            return this;
        }

        public Builder  setSportsid(int sportsid) {
            this.sportsid = sportsid;
            return this;
        }

        public Builder  setType(int type) {
            this.type = type;
            return this;
        }

        public Builder  setUsername(String username) {

            this.username = username;
            return this;
        }

        public Builder  setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Bulletin build(){
            return new Bulletin(this);
        }

        public Builder  setDate(long date) {
            this.date = date;
            return this;
        }

        public Builder  setImage(String image) {
            this.image = image;
            return this;
        }
    }
}