package com.iron.dragon.sportstogether.data.bean;

import java.util.List;

/**
 * Created by P16018 on 2016-12-20.
 */

public class Bulletin_image {
    public Bulletin_image(String image) {
        this.bulletinImg = image;
    }

    public String getBulletinImg() {
        return bulletinImg;
    }
    private String bulletinImg;

    List<Bulletin_image> bulletin_image;
    public String getBulletinImage() {
        return "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_profile?filename=" + getBulletinImg();
    }
}
