package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.iron.dragon.sportstogether.databinding.ProfileListItemBinding;
import com.iron.dragon.sportstogether.ui.activity.ProfileActivity;
import com.iron.dragon.sportstogether.ui.activity.ProfileManagerActivity;
import com.iron.dragon.sportstogether.ui.adapter.item.ProfileItem;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by P16018 on 2017-01-13.
 */

public class ProfileManagerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ProfileItem> malProfiles;
    private Context mContext;
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
        ProfileItem profileitem = malProfiles.get(position);
        ((ViewHolderItem) holder).mBinding.getViewholderitem().profileSportsType.set(mContext.getString(R.string.profile_sports, mContext.getResources().getStringArray(R.array.sportstype)[profileitem.getProfile().getSportsid()]));
        ((ViewHolderItem) holder).mBinding.getViewholderitem().profileLocation.set(mContext.getString(R.string.profile_location, mContext.getResources().getStringArray(R.array.location)[profileitem.getProfile().getLocationid()]));
        ((ViewHolderItem) holder).mBinding.getViewholderitem().profileLevel.set(mContext.getString(R.string.profile_level, mContext.getResources().getStringArray(R.array.level)[profileitem.getProfile().getLevel()]));
    }

    @Override
    public int getItemCount() {
        return malProfiles==null? 0 : malProfiles.size();
    }

    public void setItem(ArrayList<ProfileItem> list) {
        malProfiles = list;
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
        public void onClickItem(View view, ProfileItem profile) {
            Intent i = new Intent();
            i.putExtra("MyProfile", LoginPreferences.GetInstance().loadSharedPreferencesProfile(mContext, profile.getProfile().getSportsid()));
            i.setClass(mContext, ProfileActivity.class);
            mContext.startActivity(i);
        }
    }
    @BindingAdapter({"imgRes"})
    public static void imgload(ImageView imageView, String url) {
        Logger.d("profileManagerimageLoad");
        Picasso.with(imageView.getContext()).load(url).resize(200, 200)
                .centerCrop()
                .into((ImageView) imageView);
    }


}
