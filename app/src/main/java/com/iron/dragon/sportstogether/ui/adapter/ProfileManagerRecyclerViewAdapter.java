package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.databinding.ProfileListItemBinding;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.ui.activity.ProfileManagerActivity;
import com.iron.dragon.sportstogether.ui.adapter.item.ProfileManagerItem;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by P16018 on 2017-01-13.
 */

public class ProfileManagerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ProfileManagerRecyclerViewAdapter";
    private ArrayList<ProfileManagerItem> malProfiles;
    private Context mContext;
    OnItemClickListener IonItemClickListener;
    public ProfileManagerRecyclerViewAdapter(ProfileManagerActivity profileManagerActivity) {
        mContext = profileManagerActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProfileListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.profile_list_item, parent, false);
        return new ViewHolderItem(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ProfileManagerRecyclerViewAdapter.ViewHolderItem viewHolderItem = (ProfileManagerRecyclerViewAdapter.ViewHolderItem) holder;
        viewHolderItem.mBinding.setProfileitem(malProfiles.get(position));
        ProfileManagerItem profileitem = malProfiles.get(position);
        //((ViewHolderItem) holder).mBinding.getViewholderitem().profileSportsType.set(mContext.getString(R.string.profile_sports, mContext.getResources().getStringArray(R.array.sportstype)[profileitem.getProfile().getSportsid()]));
        //Log.v(TAG, "");
        ((ViewHolderItem) holder).mBinding.getViewholderitem().profileSportsType.set(StringUtil.getStringFromSports(mContext, profileitem.getProfile().getSportsid()));
        ((ViewHolderItem) holder).mBinding.getViewholderitem().profileLocation.set(StringUtil.getStringFromLocation(mContext, profileitem.getProfile().getLocationid()));
        ((ViewHolderItem) holder).mBinding.getViewholderitem().profileLevel.set(StringUtil.getStringFromLevel(mContext, profileitem.getProfile().getLevel()));
    }

    @Override
    public int getItemCount() {
        return malProfiles==null? 0 : malProfiles.size();
    }

    public void setItem(ArrayList<ProfileManagerItem> list) {
        malProfiles = list;
    }

    public ProfileManagerItem getItem(int position) {
        return malProfiles.get(position);
    }

    public void clearItem() {
        malProfiles.clear();
    }


    public class ViewHolderItem extends RecyclerView.ViewHolder {
        private final ProfileListItemBinding mBinding;
        public ObservableField<String> profileSportsType;
        public ObservableField<String> profileLocation;
        public ObservableField<String> profileLevel;
         ViewHolderItem(ProfileListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
             profileSportsType = new ObservableField<>();
             profileLocation = new ObservableField<>();
             profileLevel = new ObservableField<>();
            mBinding.setViewholderitem(this);
        }
        public void onClickItem(View view) {
            onItemHolderClick(this, view);
        }
        public void onClickDeleteProfile(View view, ProfileManagerItem profile) {
            GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
            GitHubService gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
            gitHubService.deleteProfiles(profile.getProfile().getUsername(), profile.getProfile().getSportsid())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Profile>() {
                        @Override
                        public void call(Profile profile) {
                            Logger.v("onResponse response.isSuccessful()=" + profile.toString());
                            LoginPreferences.GetInstance().SetLogout(mContext, profile.getSportsid());
                            malProfiles.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());

                        }
                    });

        }
    }
    @BindingAdapter({"imgRes"})
    public static void imgload(ImageView imageView, String url) {
        Logger.d("updateImage url = " + url);
        Logger.d("profileManagerimageLoad");
        Picasso.with(imageView.getContext()).load(url).resize(200, 200)
                .centerCrop()
                .into((ImageView) imageView);
    }

    public interface OnItemClickListener {
        void onItemClick(ViewHolderItem viewHolderItem, View view, int adapterPosition, long itemId);
    }

    public void setOnItemClickListener(ProfileManagerRecyclerViewAdapter.OnItemClickListener listener) {
        IonItemClickListener = listener;
    }

    private void onItemHolderClick(ViewHolderItem itemHolder, View view) {
        if (IonItemClickListener != null) {
            IonItemClickListener.onItemClick(null, view, itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

}
