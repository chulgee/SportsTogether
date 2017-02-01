package com.iron.dragon.sportstogether.data.viewmodel;

import android.databinding.BaseObservable;
import android.util.Log;

import com.iron.dragon.sportstogether.data.bean.News;
import com.iron.dragon.sportstogether.data.bean.News_Info;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.ui.adapter.item.NewsListItem;
import com.iron.dragon.sportstogether.ui.fragment.NewsFragment;
import com.iron.dragon.sportstogether.util.Const;

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
        mFragment.setListItem(listItems);
    }
}
