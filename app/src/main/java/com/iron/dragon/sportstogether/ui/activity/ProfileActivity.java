package com.iron.dragon.sportstogether.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.data.bean.ProfileItem;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends LoginActivity  implements ISimpleDialogListener {
    private final String TAG = getClass().getName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
//                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setCustomView(R.menu.profile_menu);
        InitLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit: {
                ButterKnife.apply(nameViews, ENABLED, false);
                ButterKnife.apply(buttonViews, VISIBLE, false);
                return true;
            }
            case android.R.id.home: {
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.bt_commit, R.id.bt_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_commit:
                String regid = FirebaseInstanceId.getInstance().getToken();
                Log.v(TAG, "등록 id :" + regid);
                LoginPreferences.GetInstance().SetRegid(getApplicationContext(), regid);
                SportsApplication app = (SportsApplication) getApplication();
                app.setRegid(regid);
                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final ProfileItem pi = new ProfileItem();
                pi.set_mNickName(mEtNickName.getText().toString());
                pi.set_mAge(mSpAge.getSelectedItemPosition());
                pi.set_mGender(mSpGender.getSelectedItemPosition());
                pi.set_mLocation(mSpLocation.getSelectedItemPosition());
                pi.set_mPhoneNum(mEtPhoneNum.getText().toString());
                pi.set_mSportsType(mSpSportsType.getSelectedItemPosition());
                pi.set_mLevel(mSpLevel.getSelectedItemPosition());

                Profile p = new Profile(pi);
                p.setRegid(regid);
                Log.v(TAG, "등록 profile=" + p.toString());
                final Call<Profile> call =
                        gitHubService.putProfiles(regid, p);

                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());

                        if (response.isSuccessful()) {
                            Log.d("Test", "body = " + response.body().toString());
                            Profile p = response.body();
                            finish();
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), "" + response.code(), Toast.LENGTH_SHORT).show();
                            if (response.code() == 409) {
                                finish();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        Log.d("Test", "error message = " + t.getMessage());
                    }
                });
                break;
            case R.id.bt_cancel:
                ShowExitEditorDialog();
                break;
        }
    }

    private void ShowExitEditorDialog() {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager()).setTitle(R.string.edit)
                .setMessage(R.string.edit_cancel_message)
                .setPositiveButtonText(android.R.string.yes)
                .setNegativeButtonText(android.R.string.no)
                .show();
    }
    protected void InitLayout() {
        super.InitLayout();
        ButterKnife.apply(nameViews, DISABLE);
        ButterKnife.apply(buttonViews, INVISIBLE);
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        finish();
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {

    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }
}
