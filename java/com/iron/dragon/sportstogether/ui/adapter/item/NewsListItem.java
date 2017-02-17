package com.iron.dragon.sportstogether.ui.adapter.item;

import com.iron.dragon.sportstogether.data.bean.News_Info;

/**
 * Created by P16018 on 2017-01-17.
 */

public class NewsListItem {
    private News_Info mNews;
    public void setNews(News_Info news) {
        mNews= news;
    }
    public News_Info getNews() {
        return mNews;
    }

    public String getNewsHyperLink() {
        return "<a href=" + getNews().getLink() + ">" + getNews().getLink() +  "</a>";
    }

    public void setNewsImage(String newsImage) {
        mNews.setImage(newsImage);
    }
}
