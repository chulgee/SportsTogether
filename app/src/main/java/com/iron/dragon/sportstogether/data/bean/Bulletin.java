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

    public String getComment() {
        return comment;
    }

    public long getDate() {
        return date;
    }

    public Bulletin(Builder builder){
        locationid = builder.locationid;
        sportsid = builder.sportsid;
        type = builder.type;
        username = builder.username;
        comment = builder.comment;
        date = builder.date;
    }

    public static class Builder{
        int locationid;
        int sportsid;
        int type;
        String username;
        String comment;
        long date;

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
    }
}