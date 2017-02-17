package com.iron.dragon.sportstogether.factory;

import java.util.List;

/**
 * Created by chulchoice on 2016-11-02.
 */
public abstract class Factory {

    protected abstract Sports createSports(String name);
    protected abstract void registerSports(Sports sports);
    public abstract List getList();

    public final Sports create(String name){
        Sports sports = createSports(name);
        registerSports(sports);
        return sports;
    }
}
