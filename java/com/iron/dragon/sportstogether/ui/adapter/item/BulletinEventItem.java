package com.iron.dragon.sportstogether.ui.adapter.item;

import com.iron.dragon.sportstogether.data.bean.Bulletin;

/**
 * Created by P16018 on 2016-12-08.
 */

public class BulletinEventItem extends BulletinListItem {
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

    public String getDefaultImage() {
        return "android.resource://com.iron.dragon.sportstogether/drawable/default_user";
    }

    public String getProfileImage() {
        return "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_profile?filename=" + getBulletin().getImage();
    }
}
