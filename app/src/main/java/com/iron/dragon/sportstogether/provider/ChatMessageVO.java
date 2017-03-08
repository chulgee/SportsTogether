package com.iron.dragon.sportstogether.provider;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by P16018 on 2017-03-06.
 */

public class ChatMessageVO extends RealmObject {
    @PrimaryKey
    int COLUMN_ID;

    String COLUMN_ROOM;
    long COLUMN_DATE;
    String COLUMN_SENDER;
    String COLUMN_RECEIVER;
    String COLUMN_MESSAGE;
    int COLUMN_FROM;
    int COLUMN_MESSSAGE_TYPE;
    int COLUMN_SPORTSID;
    int COLUMN_LOCATIONID;
    String COLUMN_IMAGE;
    @Ignore
    private int mUnread;

    public String getCOLUMN_ROOM() {
        return COLUMN_ROOM;
    }

    public void setCOLUMN_ROOM(String COLUMN_ROOM) {
        this.COLUMN_ROOM = COLUMN_ROOM;
    }

    public long getCOLUMN_DATE() {
        return COLUMN_DATE;
    }

    public void setCOLUMN_DATE(long COLUMN_DATE) {
        this.COLUMN_DATE = COLUMN_DATE;
    }

    public String getCOLUMN_SENDER() {
        return COLUMN_SENDER;
    }

    public void setCOLUMN_SENDER(String COLUMN_SENDER) {
        this.COLUMN_SENDER = COLUMN_SENDER;
    }

    public String getCOLUMN_RECEIVER() {
        return COLUMN_RECEIVER;
    }

    public void setCOLUMN_RECEIVER(String COLUMN_RECEIVER) {
        this.COLUMN_RECEIVER = COLUMN_RECEIVER;
    }

    public String getCOLUMN_MESSAGE() {
        return COLUMN_MESSAGE;
    }

    public void setCOLUMN_MESSAGE(String COLUMN_MESSAGE) {
        this.COLUMN_MESSAGE = COLUMN_MESSAGE;
    }

    public int getCOLUMN_FROM() {
        return COLUMN_FROM;
    }

    public void setCOLUMN_FROM(int COLUMN_FROM) {
        this.COLUMN_FROM = COLUMN_FROM;
    }

    public int getCOLUMN_MESSSAGE_TYPE() {
        return COLUMN_MESSSAGE_TYPE;
    }

    public void setCOLUMN_MESSSAGE_TYPE(int COLUMN_MESSSAGE_TYPE) {
        this.COLUMN_MESSSAGE_TYPE = COLUMN_MESSSAGE_TYPE;
    }

    public int getCOLUMN_SPORTSID() {
        return COLUMN_SPORTSID;
    }

    public void setCOLUMN_SPORTSID(int COLUMN_SPORTSID) {
        this.COLUMN_SPORTSID = COLUMN_SPORTSID;
    }

    public int getCOLUMN_LOCATIONID() {
        return COLUMN_LOCATIONID;
    }

    public void setCOLUMN_LOCATIONID(int COLUMN_LOCATIONID) {
        this.COLUMN_LOCATIONID = COLUMN_LOCATIONID;
    }

    public String getCOLUMN_IMAGE() {
        return COLUMN_IMAGE;
    }

    public void setCOLUMN_IMAGE(String COLUMN_IMAGE) {
        this.COLUMN_IMAGE = COLUMN_IMAGE;
    }

    public int getUnread() {
        return mUnread;
    }

    public void setUnread(int unread) {
        mUnread = unread;
    }

    @Override
    public String toString() {
        return "Item{" +
                "room='" + COLUMN_ROOM + '\'' +
                ", sender='" + COLUMN_SENDER + '\'' +
                ", receiver='" + COLUMN_RECEIVER + '\'' +
                ", image='" + COLUMN_IMAGE + '\'' +
                ", lastTime=" + COLUMN_DATE +
                ", sportsid=" + COLUMN_SPORTSID +
                ", locationid=" + COLUMN_LOCATIONID +
                '}';
    }
}
