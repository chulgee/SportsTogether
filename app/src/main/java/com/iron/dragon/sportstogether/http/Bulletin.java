package com.iron.dragon.sportstogether.http;

/**
 * Created by chulchoice on 2016-11-18.
 */
public class Bulletin {

    int locationid;
    int sportsid;
    int type;
    String username;
    int comment;
    long date;

    Bulletin(Builder builder){
        locationid = builder.locationid;
        sportsid = builder.sportsid;
        type = builder.type;
        username = builder.username;
        comment = builder.comment;
        date = builder.date;
    }

    public class Builder{
        int locationid;
        int sportsid;
        int type;
        String username;
        int comment;
        long date;

        public void setLocationid(int locationid) {
            this.locationid = locationid;
        }

        public void setSportsid(int sportsid) {
            this.sportsid = sportsid;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public Bulletin build(){
            return new Bulletin(this);
        }

        public void setDate(long date) {
            this.date = date;
        }
    }
}