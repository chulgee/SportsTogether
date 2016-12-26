package com.iron.dragon.sportstogether.data.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chulchoice on 2016-11-28.
 */

public class Message  implements Serializable{

    public static final int TYPE_CHAT_MESSAGE = 0;
    public static final int TYPE_CHAT_ACTION = 1;
    public static final int TYPE_CHAT_LOG = 2;
    public static final int PARAM_MSG_IN = 10;
    public static final int PARAM_MSG_OUT = 11;

    private int type;
    private int msgType;
    private String sender;
    private String receiver;
    private String message;
    private long date;
    private String image;
    private int sportsid;
    private int locationid;

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

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public long getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", msgType=" + msgType +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", image=" + image +
                '}';
    }

    public static class Builder{
        private int type;
        private int msgType;
        private String message;
        private String sender;
        private String receiver;
        private long date;
        private String image;
        private int sportsid;
        private int locationid;

        public Builder(int type){
            this.type = type;
        }

        public Builder msgType(int msgType){
            this.msgType = msgType;
            return this;
        }

        public Builder sender(String sender){
            this.sender = sender;
            return this;
        }

        public Builder receiver(String receiver){
            this.receiver = receiver;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder date(long date){
            this.date = date;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder sportsid(int sportsid){
            this.sportsid = sportsid;
            return this;
        }

        public Builder locationid(int locationid){
            this.locationid = locationid;
            return this;
        }

        public Message build(){
            Message message = new Message();
            message.type = this.type;
            message.msgType = this.msgType;
            message.sender = this.sender;
            message.receiver = this.receiver;
            message.message = this.message;
            message.date = this.date;
            message.image = this.image;
            message.sportsid = this.sportsid;
            message.locationid = this.locationid;
            return message;
        }
    }
}
