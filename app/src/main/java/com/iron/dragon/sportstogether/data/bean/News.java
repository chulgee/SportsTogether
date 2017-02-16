package com.iron.dragon.sportstogether.data.bean;

import java.util.List;

/**
 * Created by P16018 on 2017-01-17.
 */

public class News {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;

    private List<News_Info> items;
    public List<News_Info> getNewsInfo() {
        return items;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(", lastBuildDate="+lastBuildDate)
                .append(", total="+total)
                .append(", start="+start)
                .append(", display="+display)
                .append(", item="+items);
        return sb.toString();
    }
}
