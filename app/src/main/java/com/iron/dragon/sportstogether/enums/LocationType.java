package com.iron.dragon.sportstogether.enums;

import com.iron.dragon.sportstogether.R;

/**
 * Created by P10950 on 2017-02-06.
 */

public enum LocationType {

    Gangnam_gu(0, R.string.Gangnam_gu),
    Gangdong_gu(1, R.string.Gangdong_gu),
    Gangbuk_gu(2, R.string.Gangbuk_gu),
    Gangseo_gu(3, R.string.Gangseo_gu),
    Gwanak_gu(4, R.string.Gwanak_gu),
    Gwangjin_gu(5, R.string.Gwangjin_gu),
    Guro_gu(6, R.string.Guro_gu),
    Geumcheon_gu(7, R.string.Geumcheon_gu),
    Nowon_gu(8, R.string.Nowon_gu),
    Dobong_gu(9, R.string.Dobong_gu),
    Dongdaemun_gu(10, R.string.Dongdaemun_gu),
    Dongjag_gu(11, R.string.Dongjag_gu),
    Mapo_gu(12, R.string.Mapo_gu),
    Seodaemun_gu(13, R.string.Seodaemun_gu),
    Seocho_gu(14, R.string.Seocho_gu),
    Seongdong_gu(15, R.string.Seongdong_gu),
    Seongbuk_gu(16, R.string.Seongbuk_gu),
    Songpa_gu(17, R.string.Songpa_gu),
    Yangcheon_gu(18, R.string.Yangcheon_gu),
    Yeongdeungpo_gu(19, R.string.Yeongdeungpo_gu),
    Yongsan_gu(20, R.string.Yongsan_gu),
    Eunpyeong_gu(21, R.string.Eunpyeong_gu),
    Jongno_gu(22, R.string.Jongno_gu),
    Jung_gu(23, R.string.Jung_gu),
    Jungnang_gu(24, R.string.Jungnang_gu);

    int value;
    int resid;

    LocationType(int value, int resid){
        this.value = value;
        this.resid = resid;
    }

    public int getValue(){
        return value;
    }

    public int getResid(){
        return resid;
    }
}
