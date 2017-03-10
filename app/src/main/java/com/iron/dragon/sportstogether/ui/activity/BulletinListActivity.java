package com.iron.dragon.sportstogether.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.viewmodel.BulletinListViewModel;
import com.iron.dragon.sportstogether.databinding.BulletinActBinding;
import com.iron.dragon.sportstogether.ui.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.ui.adapter.item.BulletinEventItem;
import com.iron.dragon.sportstogether.ui.adapter.item.BulletinHeaderItem;
import com.iron.dragon.sportstogether.ui.adapter.item.BulletinListItem;
import com.iron.dragon.sportstogether.ui.view.DividerItemDecoration;
import com.iron.dragon.sportstogether.util.ToastUtil;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;


public class BulletinListActivity extends AppCompatActivity  {

    private static final String TAG = "BulletinListActivity";

    private BottomSheetBehavior bottomSheetBehavior;

    private BulletinListViewModel mViewModel;
    private BulletinActBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new BulletinListViewModel(this, getIntent().getIntExtra("Extra_Sports", 0));
        mBinding = DataBindingUtil.setContentView(this, R.layout.bulletin_act);
        mBinding.setViewModel(mViewModel);
        setSupportActionBar(mBinding.toolbar);
        InitLayout();
        mViewModel.LoadBulletinData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
//                NavUtils.navigateUpFromSameTask(this);
                finish();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPosting(BulletinHeaderItem header, BulletinEventItem item) {
        BulletinRecyclerViewAdapter adapter = (BulletinRecyclerViewAdapter) mBinding.boardRecyclerviewer.getAdapter();
        if (adapter.getItemCount() == 0) {
            ArrayList<BulletinListItem> bulletinListItems = new ArrayList<>();
            bulletinListItems.add(header);
            bulletinListItems.add(item);
            adapter.setItem(bulletinListItems);
        } else {
            adapter.addItem(header);
            adapter.addItem(item);
        }

        setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);

        hideSoftInput();

        mBinding.boardRecyclerviewer.smoothScrollToPosition(0);
        mBinding.commentLayout.llAttachLayout.removeAllViews();
    }

    private void hideSoftInput() {
        View view = getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setBottomSheetState(int stateExpanded) {
        bottomSheetBehavior.setState(stateExpanded);
    }

    public void addImageView(Bitmap downsampledBitmap) {
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(downsampledBitmap);
        mBinding.commentLayout.llAttachLayout.addView(iv);
    }

    private void InitLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mBinding.boardRecyclerviewer.setLayoutManager(layoutManager);

        final BulletinRecyclerViewAdapter adapter = new BulletinRecyclerViewAdapter(BulletinListActivity.this);
        mBinding.boardRecyclerviewer.setAdapter(adapter);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.commentLayout));
        registerForContextMenu(mBinding.boardRecyclerviewer);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mBinding.boardRecyclerviewer.addItemDecoration(dividerItemDecoration);
        adapter.setOnItemLongClickListener(new BulletinRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "onItemLongClick");
                openContextMenu(view);
            }
        });

        adapter.setOnFooterItemClickListener(new BulletinRecyclerViewAdapter.OnFooterItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemView) {
                Logger.d("onItemClickFooterItem");
                adapter.resetItems();
                mViewModel.RefreshData();
            }
        });

        adapter.setOnImageClickListener(new BulletinRecyclerViewAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(AdapterView<?> parent, View imageView, int adapterPosition, long itemId) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.bulletin_list_image_popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                ImageView btnDismiss = (ImageView)popupView.findViewById(R.id.popup_image);
                btnDismiss.setImageDrawable(((ImageView)imageView).getDrawable());
                btnDismiss.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        popupWindow.dismiss();
                    }});

                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            popupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                popupWindow.showAtLocation(imageView, Gravity.CENTER, 0, 0);
            }
        });
    }

    public void ShowChangeImageActionDialog(MaterialDialog.Builder b) {
        b.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderIcon(android.R.drawable.ic_menu_share);
        menu.setHeaderTitle("Menu");
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BulletinRecyclerViewAdapter adapter = (BulletinRecyclerViewAdapter) mBinding.boardRecyclerviewer.getAdapter();
        Log.v(TAG, "onContextItemSelected mAdapter.getIndex()= " + adapter.getIndex());

        switch (item.getItemId()) {
            case R.id.action_chat:
                // get buddy's profile
                mViewModel.excuteChatting(adapter.getItem(adapter.getIndex()));
                return true;
            case R.id.action_share:
                try {
                    final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
                    final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                    kakaoTalkLinkMessageBuilder.addAppLink("자세히 보기", new AppActionBuilder().setUrl("market://details?id=com.iron.dragon.sportstogether").build()); // PC 카카오톡 에서 사용하게 될 웹사이트 주소
                    kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, BulletinListActivity.this);
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		mViewModel.setOnActivityResult(requestCode, resultCode, data);
        
    }

    public void stopLoadingProgress() {
        Logger.d("stopLoadingProgress");
        mBinding.progressView.stopAnimation();
//        mBinding.swipeLayout.setRefreshing(false);
    }

    public void startLoadingProgress() {
        Logger.d("startLoadingProgress");
        mBinding.progressView.startAnimation();
    }

    public void setListItem(ArrayList<BulletinListItem> bulletinListItems) {
        BulletinRecyclerViewAdapter adapter = (BulletinRecyclerViewAdapter) mBinding.boardRecyclerviewer.getAdapter();
        adapter.setItem(bulletinListItems);
    }

    public void clearAdapter() {
        BulletinRecyclerViewAdapter adapter = (BulletinRecyclerViewAdapter) mBinding.boardRecyclerviewer.getAdapter();
        adapter.resetItems();
    }

    public void showToast(String string) {
        ToastUtil.show(getApplicationContext(), string);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onDestroy();
    }
}