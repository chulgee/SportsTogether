package com.iron.dragon.sportstogether.ui.adapter.item;

/**
 * Created by P16018 on 2016-12-08.
 */

public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_EVENT = 1;

    abstract public int getType();
}
