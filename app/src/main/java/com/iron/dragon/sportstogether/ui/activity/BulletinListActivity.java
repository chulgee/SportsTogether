package com.iron.dragon.sportstogether.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.viewmodel.BulletinListViewModel;
import com.iron.dragon.sportstogether.databinding.ActivityBulletinListViewBinding;
import com.iron.dragon.sportstogether.ui.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.ui.adapter.item.EventItem;
import com.iron.dragon.sportstogether.ui.adapter.item.HeaderItem;
import com.iron.dragon.sportstogether.ui.adapter.item.ListItem;
import com.iron.dragon.sportstogether.ui.view.DividerItemDecoration;
import com.iron.dragon.sportstogether.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;


public class BulletinListActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;

    @BindViews({R.id.bt_Send, R.id.btAttachImage})
    protected List<View> buttonViews;

    BulletinRecyclerViewAdapter mAdapter;

    static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setEnabled(false);
        }
    };
    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
            view.setEnabled(true);
        }
    };

    BulletinListViewModel mViewModel;
    ActivityBulletinListViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new BulletinListViewModel(this, getIntent().getIntExtra("Extra_Sports", 0));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bulletin_list_view);
        binding.setViewModel(mViewModel);

        ButterKnife.bind(this);
        setSupportActionBar(binding.toolbar);
        InitLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.boardRecyclerviewer.setLayoutManager(layoutManager);
        binding.tvTotalNum.setText(getString(R.string.bulletin_num));
        mAdapter = new BulletinRecyclerViewAdapter(BulletinListActivity.this);
        binding.boardRecyclerviewer.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        binding.boardRecyclerviewer.addItemDecoration(dividerItemDecoration);
        mAdapter.setOnItemLongClickListener(new BulletinRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(view);
                openContextMenu(view);
            }
        });
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.commentLayout));

        bottomSheetBehavior.setBottomSheetCallback(mViewModel.BottomSheetCallback());
    }

    public void setBottomSheetState(int state) {
        bottomSheetBehavior.setState(state);
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
        switch (item.getItemId()) {
            case R.id.action_chat:
                //some code
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

    public void showErrorCaptureImage(int capture_error) {
        ToastUtil.show(getApplicationContext(), getString(capture_error));
    }

    public void addImageView(Bitmap downsampledBitmap) {
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(downsampledBitmap);
        binding.commentLayout.llAttachLayout.addView(iv);
    }

    public void showErrorCropImage(int crop_error) {
        ToastUtil.show(getApplicationContext(), getString(crop_error));
    }

    public void showPosting(HeaderItem header, EventItem item) {
        if (mAdapter.getItemCount() == 0) {
            ArrayList<ListItem> listItems = new ArrayList<>();
            listItems.add(header);
            listItems.add(item);
            mAdapter.setItem(listItems);
        } else {
            mAdapter.addItem(header);
            mAdapter.addItem(item);
        }

        binding.commentLayout.etContent.setText("");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        View view = getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        binding.boardRecyclerviewer.smoothScrollToPosition(binding.boardRecyclerviewer.getAdapter().getItemCount());
        binding.commentLayout.llAttachLayout.removeAllViews();
    }

    public void waitForPosting() {
        binding.progressView.startAnimation();
        binding.progressView.setVisibility(View.VISIBLE);
        ButterKnife.apply(buttonViews, DISABLE);
        binding.progressView.startAnimation();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.progressView.stopAnimation();
                binding.progressView.setVisibility(View.INVISIBLE);
                ButterKnife.apply(buttonViews, ENABLED, false);

            }
        }, 3000);
    }

    public void setListItem(ArrayList<ListItem> listItems) {
        mAdapter.setItem(listItems);
    }

    public void FabUpdateVisible(int visible) {
        binding.fab.setVisibility(visible);
    }
}