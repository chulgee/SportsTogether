package com.iron.dragon.sportstogether.ui.adapter.item;

import com.iron.dragon.sportstogether.data.bean.Bulletin;

/**
 * Created by P16018 on 2016-12-08.
 */

public class EventItem extends ListItem {
    private Bulletin mBulletin;

    @Override
    public int getType() {
        return TYPE_EVENT;
    }

    public void setBulletin(Bulletin bulletin) {
        mBulletin = bulletin;
    }
    public Bulletin getBulletin() {
        return mBulletin;
    }
}
