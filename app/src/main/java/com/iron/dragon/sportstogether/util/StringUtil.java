package com.iron.dragon.sportstogether.util;

import android.content.Context;

import com.iron.dragon.sportstogether.enums.AgeType;
import com.iron.dragon.sportstogether.enums.GenderType;
import com.iron.dragon.sportstogether.enums.LevelType;
import com.iron.dragon.sportstogether.enums.LocationType;
import com.iron.dragon.sportstogether.enums.SportsType;
import com.iron.dragon.sportstogether.factory.Sports;

import java.util.Arrays;
import java.util.List;

import static android.R.attr.type;

/**
 * Created by seungyong on 2016-11-16.
 */

public class StringUtil {
    public static boolean isEmpty(String str) {
        if(str == null || str.trim().length() ==0)
            return true;

        return false;
    }

    public static boolean isAnyEmpty(String str1, String str2) {
        if(isEmpty(str1) || isEmpty(str2))
            return true;

        return false;
    }

    public static boolean isAllEmpty(String str1, String str2) {
        if(isEmpty(str1) && isEmpty(str2))
            return true;

        return false;
    }

    public static String getStringFromSports(Context context, int value){
        List<SportsType> list = Arrays.asList(SportsType.values());
        for(SportsType a : list){
            if(a.getValue() == value)
                return context.getResources().getString(a.getResid());
        }
        return "";
    }

    public static String getStringFromLocation(Context context, int value){
        List<LocationType> list = Arrays.asList(LocationType.values());
        for(LocationType a : list){
            if(a.getValue() == value)
                return context.getResources().getString(a.getResid());
        }
        return "";
    }

    public static String getStringFromAge(Context context, int value){
        List<AgeType> list = Arrays.asList(AgeType.values());
        for(AgeType a : list){
            if(a.getValue() == value)
                return context.getResources().getString(a.getResid());
        }
        return "";
    }

    public static String getStringFromLevel(Context context, int value){
        List<LevelType> list = Arrays.asList(LevelType.values());
        for(LevelType a : list){
            if(a.getValue() == value)
                return context.getResources().getString(a.getResid());
        }
        return "";
    }

    public static String getStringFromGender(Context context, int value){
        List<GenderType> list = Arrays.asList(GenderType.values());
        for(GenderType a : list){
            if(a.getValue() == value)
                return context.getResources().getString(a.getResid());
        }
        return "";
    }

    public static String getStringByType(Context context, Object _type){
        Object type = _type;
        int value = 0;

        if(type instanceof SportsType){
            List<SportsType> sports = Arrays.asList(SportsType.class.getEnumConstants());
            value = ((SportsType) type).getValue();
            for(SportsType s : sports){
                if(s.getValue() == value)
                    return context.getResources().getString(s.getResid());
            }
        }else if(type instanceof LocationType){
            List<LocationType> sports = Arrays.asList(LocationType.class.getEnumConstants());
            value = ((LocationType) type).getValue();
            for(LocationType s : sports){
                if(s.getValue() == value)
                    return context.getResources().getString(s.getResid());
            }
        }else if(type instanceof AgeType){
            List<AgeType> sports = Arrays.asList(AgeType.class.getEnumConstants());
            value = ((AgeType) type).getValue();
            for(AgeType s : sports){
                if(s.getValue() == value)
                    return context.getResources().getString(s.getResid());
            }
        }else if(type instanceof LevelType){
            List<LevelType> sports = Arrays.asList(LevelType.class.getEnumConstants());
            value = ((LevelType) type).getValue();
            for(LevelType s : sports){
                if(s.getValue() == value)
                    return context.getResources().getString(s.getResid());
            }
        }else if(type instanceof GenderType){
            List<GenderType> sports = Arrays.asList(GenderType.class.getEnumConstants());
            value = ((GenderType) type).getValue();
            for(GenderType s : sports){
                if(s.getValue() == value)
                    return context.getResources().getString(s.getResid());
            }
        }
        return "";
    }

    public static String[] getStringArrFromSportsType(Context context){
        SportsType[] st = SportsType.values();
        String[] arr = new String[st.length];
        for(int i=0; i<st.length; i++){
            arr[i] = StringUtil.getStringByType(context, st);
        }
        return arr;
    }

    public static String[] getStringArrFromLocationType(Context context){
        LocationType[] st = LocationType.values();
        String[] arr = new String[st.length];
        for(int i=0; i<st.length; i++){
            arr[i] = StringUtil.getStringByType(context, st);
        }
        return arr;
    }

    public static String[] getStringArrFromAgeType(Context context){
        AgeType[] st = AgeType.values();
        String[] arr = new String[st.length];
        for(int i=0; i<st.length; i++){
            arr[i] = StringUtil.getStringByType(context, st);
        }
        return arr;
    }
    public static String[] getStringArrFromLevelType(Context context){
        LevelType[] st = LevelType.values();
        String[] arr = new String[st.length];
        for(int i=0; i<st.length; i++){
            arr[i] = StringUtil.getStringByType(context, st);
        }
        return arr;
    }

    public static String[] getStringArrFromGenderType(Context context){
        GenderType[] st = GenderType.values();
        String[] arr = new String[st.length];
        for(int i=0; i<st.length; i++){
            arr[i] = StringUtil.getStringByType(context, st);
        }
        return arr;
    }

}
