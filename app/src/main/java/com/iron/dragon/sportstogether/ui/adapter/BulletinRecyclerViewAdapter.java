package com.iron.dragon.sportstogether.ui.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.data.bean.Bulletin_image;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.databinding.BulletinListFooterBinding;
import com.iron.dragon.sportstogether.databinding.BulletinListHeaderBinding;
import com.iron.dragon.sportstogether.databinding.BulletinListItemBinding;
import com.iron.dragon.sportstogether.databinding.BulletinMyCustomViewBinding;
import com.iron.dragon.sportstogether.ui.adapter.item.BulletinEventItem;
import com.iron.dragon.sportstogether.ui.adapter.item.BulletinHeaderItem;
import com.iron.dragon.sportstogether.ui.adapter.item.BulletinListItem;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by seungyong on 2016-11-03.
 */

public class BulletinRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int FOOTER_VIEW = 2;  //listitem listheader is 0 and 1
    private int index; // Position in adapter

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private ArrayList<BulletinListItem> malBulletin;
    private OnItemLongClickListener IonItemLongClickListener;
    private OnFooterItemClickListener IonFooterItemClickListener;
    private OnImageClickListener IonImageClickListener;
    private Context mContext;

    public BulletinRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }


    public void addItem(BulletinListItem bulletin) {
        int insertIndex = 1;
        if (bulletin.getType() == BulletinListItem.TYPE_HEADER && malBulletin.contains(bulletin)) {
            return;
        } else if (bulletin.getType() == BulletinListItem.TYPE_HEADER && !malBulletin.contains(bulletin)) {
            insertIndex = 0;
        }
        malBulletin.add(insertIndex, bulletin);
        notifyItemInserted(insertIndex);

    }

    public void setItem(ArrayList<BulletinListItem> bulletins) {
        malBulletin = bulletins;
        notifyDataSetChanged();
    }

    public BulletinListItem getItem(int index) {
        return malBulletin.get(index);
    }

    public void resetItems() {
        malBulletin.clear();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        private final BulletinListItemBinding mBinding;

        public ViewHolderItem(BulletinListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewholderitem(this);
        }

        public boolean onLongClickItem(View view) {
            index = getAdapterPosition();
            BulletinListItem item = getItem(index);
            if (item instanceof BulletinEventItem) {
                Bulletin bulletin = ((BulletinEventItem) item).getBulletin();
                Profile me = (Profile) LoginPreferences.GetInstance().loadSharedPreferencesProfile(mContext, bulletin.getSportsid());
                if (me.getUsername().equals(bulletin.getUsername())) {
                    Toast.makeText(mContext, "너 잖아~", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            Logger.d("AdapterPosition = " + index);
            onItemHolderLongClick(this);
            return true;
        }
        public void onClickBulletinImage(View v) {
            Logger.d("onClickBulletinImage");
            onItemHolderImageClick(this, v);
//            zoomImageFromThumb(thumb1View, R.drawable.image1);
//            ImageViewPopUpHelper.enablePopUpOnClick(mContext, profile_image, ((ImageView)v).getDrawable());
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void profileImage(View v, String url) {
        Logger.d("url = " + url);
        Picasso.with(v.getContext()).load(url).resize(50, 50)
                .centerCrop()
                .into((CircleImageView) v);
    }

    @BindingAdapter({"bind:bulletinimageUrl"})
    public static void bulletinImage(View v, String url) {
        Logger.d("url = " + url);
        Picasso.with(v.getContext()).load(url).resize(450, 450)
                .centerCrop()
                .into((ImageView) v);
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        private final BulletinListHeaderBinding mBinding;
        public ViewHolderHeader(BulletinListHeaderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        private final BulletinListFooterBinding mBinding;
        public FooterViewHolder(BulletinListFooterBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewholderfooter(this);
        }
        public void onClickFooter(View view) {
            Logger.d("FooterViewHolder Click");
            onFooterItemHolderClick(this);
        }
    }

    public interface OnFooterItemClickListener {
        void onItemClick(AdapterView<?> parent, View itemView);
    }

    public void setOnFooterItemClickListener(OnFooterItemClickListener listener) {
        IonFooterItemClickListener = listener;
    }

    private void onFooterItemHolderClick(FooterViewHolder footerViewHolder) {
        if (IonFooterItemClickListener != null) {
            IonFooterItemClickListener.onItemClick(null, footerViewHolder.itemView);
        }
    }


    @Override
    public int getItemViewType(int position)
    {
        if (position == malBulletin.size()) {
            return FOOTER_VIEW;
        }
        return malBulletin.get(position).getType();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            BulletinListFooterBinding binding = DataBindingUtil
                    .inflate(LayoutInflater.from(mContext), R.layout.bulletin_list_footer, parent, false);
            return new FooterViewHolder(binding);
        } else if (viewType == BulletinListItem.TYPE_HEADER) {
            BulletinListHeaderBinding binding = DataBindingUtil
                    .inflate(LayoutInflater.from(mContext), R.layout.bulletin_list_header, parent, false);
            return new ViewHolderHeader(binding);
        } else {
            BulletinListItemBinding binding = DataBindingUtil
                    .inflate(LayoutInflater.from(mContext), R.layout.bulletin_list_item, parent, false);
            return new ViewHolderItem(binding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        if( type == FOOTER_VIEW) {
            final FooterViewHolder viewHolderFooter = (FooterViewHolder) holder;
            viewHolderFooter.mBinding.executePendingBindings();
        } else if (type == BulletinListItem.TYPE_HEADER) {
            final ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            viewHolderHeader.mBinding.setListitem((BulletinHeaderItem) malBulletin.get(position));
            viewHolderHeader.mBinding.executePendingBindings();
        } else {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            viewHolderItem.mBinding.setListitem((BulletinEventItem)malBulletin.get(position));

            BulletinEventItem item = (BulletinEventItem) malBulletin.get(position);

            if (item.getBulletin().getBulletin_image() != null && item.getBulletin().getBulletin_image().size() > 0 && viewHolderItem.mBinding.llAttachImage.getChildCount() == 0) {
                addViewToViewGroup(viewHolderItem.mBinding.llAttachImage);
            }
            viewHolderItem.mBinding.executePendingBindings();
        }

    }

    private void addViewToViewGroup(ViewGroup viewGroup) {
        BulletinListItemBinding outer = DataBindingUtil.findBinding(viewGroup);
        for (Bulletin_image image : outer.getListitem().getBulletin().getBulletin_image()) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            BulletinMyCustomViewBinding binding = BulletinMyCustomViewBinding.inflate(inflater, viewGroup, true);
            binding.setListimage(image);
            binding.setViewholderitem(outer.getViewholderitem());

        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.getItemViewType() == BulletinListItem.TYPE_EVENT) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            viewHolderItem.mBinding.llAttachImage.removeAllViews();
        }
    }

    @Override
    public int getItemCount() {
        if (malBulletin == null) {
            return 0;
        }
        if (malBulletin.size() == 0) {
            return 1;
        }
        return (malBulletin.size() + 1);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(AdapterView<?> parent, View itemView, int adapterPosition, long itemId);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        IonItemLongClickListener = listener;
    }

    private void onItemHolderLongClick(ViewHolderItem itemHolder) {
        if (IonItemLongClickListener != null) {
            IonItemLongClickListener.onItemLongClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public interface OnImageClickListener {
        void onImageClick(AdapterView<?> parent, View itemView, int adapterPosition, long itemId);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        IonImageClickListener = listener;
    }

    private void onItemHolderImageClick(ViewHolderItem itemHolder, View v) {
        if (IonImageClickListener != null) {
            IonImageClickListener.onImageClick(null, v,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }
}