package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.databinding.NewsListItemBinding;
import com.iron.dragon.sportstogether.ui.adapter.item.NewsListItem;
import com.iron.dragon.sportstogether.ui.fragment.NewsFragment;

import java.util.ArrayList;

/**
 * Created by P16018 on 2017-01-17.
 */

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NewsListItem> malNews;
    private Context mContext;

    public NewsRecyclerViewAdapter(NewsFragment newsFragment) {
        mContext = newsFragment.getContext();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NewsListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.news_list_item, parent, false);
        return new ViewHolderItem(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NewsRecyclerViewAdapter.ViewHolderItem viewHolderItem = (NewsRecyclerViewAdapter.ViewHolderItem) holder;
        viewHolderItem.mBinding.setNewsitem(malNews.get(position));
    }

    @Override
    public int getItemCount() {
        return malNews==null? 0 : malNews.size();
    }

    public void setItem(ArrayList<NewsListItem> list) {
        malNews = list;
        notifyDataSetChanged();
    }


    public class ViewHolderItem extends RecyclerView.ViewHolder {
        private final NewsListItemBinding mBinding;
        ViewHolderItem(NewsListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewholderitem(this);
        }
    }

    @SuppressWarnings("deprecation")
    @BindingAdapter({"bind:hyperlink"})
    public static void setHyperlink(View v, String link) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            ((TextView)v).setText(
                    Html.fromHtml(link));
        } else {
            ((TextView)v).setText(
                    Html.fromHtml(link, Html.FROM_HTML_MODE_LEGACY));
        }
        ((TextView)v).setMovementMethod(LinkMovementMethod.getInstance());
    }

}

