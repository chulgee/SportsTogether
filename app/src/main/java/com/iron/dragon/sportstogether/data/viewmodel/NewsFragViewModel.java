package com.iron.dragon.sportstogether.data.viewmodel;

import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.iron.dragon.sportstogether.data.bean.News;
import com.iron.dragon.sportstogether.data.bean.News_Info;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.ui.adapter.item.NewsListItem;
import com.iron.dragon.sportstogether.ui.fragment.NewsFragment;
import com.iron.dragon.sportstogether.util.Const;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by P16018 on 2017-01-17.
 */

public class NewsFragViewModel extends BaseObservable {
    private NewsFragment mFragment;
    private GitHubService gitHubService;
    public NewsFragViewModel(NewsFragment fragment) {
        mFragment = fragment;
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.NEWS_URL);
        gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
    }

    public void LoadNewsData() {

        final Call<News> call =
                gitHubService.getNews("스포츠");

        RetrofitHelper.enqueueWithRetry(call, new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                Log.d(TAG, "server contacted at: " + call.request().url());
                Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                Log.d("Test", "body = " + response.body().toString());
                Log.d("Test", "message = " + response.message());

                News list = response.body();
                initListView(list.getNewsInfo());
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    private void initListView(List<News_Info> newsInfo) {
        ArrayList<NewsListItem> listItems = new ArrayList<>();
        for(News_Info news:newsInfo) {
            NewsListItem pf = new NewsListItem();
            pf.setNews(news);
            listItems.add(pf);
        }
        new ThumbImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listItems);
        mFragment.setListItem(listItems);
    }

    private class ThumbImageTask extends AsyncTask<ArrayList<NewsListItem>, Integer, Bitmap> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Bitmap doInBackground(ArrayList<NewsListItem>... args) {
            Document doc = null;
            ArrayList<NewsListItem> items = args[0];
            int index = 0;
            for(NewsListItem item :items) {
                try {
                    doc = Jsoup.connect(item.getNews().getLink()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements titles = doc.select("head meta");
                for(Element element:titles) {
                    if(element.attr("property").equals("og:image")) {
                        item.setNewsImage(element.attr("content"));
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mFragment.InvalidateAdapter();
        }
    }
}
