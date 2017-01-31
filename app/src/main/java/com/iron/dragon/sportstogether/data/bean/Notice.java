package com.iron.dragon.sportstogether.data.bean;

import java.io.Serializable;

/**
 * Created by chulchoice on 2017-01-31.
 */

public class Notice implements Serializable{
    public String title;
    public String body;
    public String writer;
    public long time;
    public int type;

    public Notice(String title, String body, String writer, long time, int type) {
        this.title = title;
        this.body = body;
        this.writer = writer;
        this.time = time;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", writer='" + writer + '\'' +
                ", time=" + time +
                ", type=" + type +
                '}';
    }
}
