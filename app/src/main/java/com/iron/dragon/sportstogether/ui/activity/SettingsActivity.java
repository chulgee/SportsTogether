package com.iron.dragon.sportstogether.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.viewmodel.SettingsViewModel;
import com.iron.dragon.sportstogether.databinding.SettingsActBinding;


public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel mViewModel;
    private SettingsActBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new SettingsViewModel(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.settings_act);
        mBinding.setViewModel(mViewModel);
    }

    @Override
    protected void onPause() {
        mViewModel.SaveSetting(mBinding.buddySwitch.isChecked(), mBinding.chatSwitch.isChecked());
        super.onPause();
    }
}
