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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by P16018 on 2017-01-17.
 */

public class NewsFragViewModel extends BaseObservable {
    private NewsFragment mFragment;
    private GitHubService gitHubService;
    private Observable<Element> mObservable;

    public NewsFragViewModel(NewsFragment fragment) {
        mFragment = fragment;
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.NEWS_URL);
        gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
    }

    public void LoadNewsData() {

        final Call<News> call =
                gitHubService.getNews("운동");
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
        mObservable = Observable.defer(() -> Observable.from(listItems)
                .subscribeOn(Schedulers.io())
                .flatMap(item -> {
                    try {
                        return Observable.just(Jsoup.connect(item.getNews().getLink()).get())
                                .flatMap(doc-> Observable.just(doc.select("head meta")))
                                .flatMap(Observable::from)
                                .filter(element -> element.attr("property").equals("og:image"))
                                .doOnNext(element -> {
                                    item.setNewsImage(element.attr("content"));
                                });

                    } catch (IOException e) {
                        e.printStackTrace();
                    };
                    return null;
                })
                .observeOn(AndroidSchedulers.mainThread()));
        mObservable.subscribe(element-> System.out.println("modify title rx= " + element), Throwable::printStackTrace, () -> mFragment.InvalidateAdapter());

        mFragment.setListItem(listItems);
    }

    public void onDestroyView() {
        mObservable.unsubscribeOn(Schedulers.io());
    }
}

