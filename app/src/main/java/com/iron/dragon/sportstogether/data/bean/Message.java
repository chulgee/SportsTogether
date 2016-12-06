package com.iron.dragon.sportstogether.data.bean;

import java.util.Date;

/**
 * Created by chulchoice on 2016-11-28.
 */

public class Message {

    public static final int TYPE_CHAT_MESSAGE = 0;
    public static final int TYPE_CHAT_ACTION = 1;
    public static final int TYPE_CHAT_LOG = 2;
    public static final int TYPE_CHAT_MSG_ME = 10;
    public static final int TYPE_CHAT_MSG_NOT_ME = 11;

    private int type;
    private int msgType;
    private String message;
    private String username;
    private Date date;

    private Message(){}

    public int getType() {
        return type;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public Date getDate() {
        return date;
    }

    public static class Builder{
        private int type;
        private int msgType;
        private String message;
        private String username;
        private Date date;

        public Builder(int type){
            this.type = type;
        }

        public Builder msgType(int msgType){
            this.msgType = msgType;
            return this;
        }

        public Builder username(String username){
            this.username = username;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder date(Date date){
            this.date = date;
            return this;
        }

        public Message build(){
            Message message = new Message();
            message.type = this.type;
            message.msgType = this.msgType;
            message.username = this.username;
            message.message = this.message;
            message.date = this.date;
            return message;
        }
    }
}
