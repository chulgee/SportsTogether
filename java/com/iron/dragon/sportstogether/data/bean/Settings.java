package com.iron.dragon.sportstogether.data.bean;

/**
 * Created by P16018 on 2017-02-06.
 */

public class Settings {
    private Boolean buddy_alarm;
    private Boolean chat_alarm;

    public Settings(boolean buddycheck, boolean chatcheck) {
        this.buddy_alarm = buddycheck;
        this.chat_alarm = chatcheck;
    }
}
