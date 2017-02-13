package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.CallbackWithExists;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;


public class ProfileActivity extends LoginActivity  {
    private final String TAG = getClass().getName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processIntent(getIntent());
        Logger.d("child mSpSportsType.getSelectedItemPosition() = " + mSpSportsType.getSelectedItemPosition() + " mSportsId = " + mSportsId);
    }
    private void processIntent(Intent i) {
        final Profile myprofile = (Profile) i.getSerializableExtra("MyProfile");
        mSportsId = myprofile.getSportsid();
        Logger.d("child myprofile = " + myprofile);
        if (myprofile != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ProfileActivity.this.setCurrentProfile(myprofile);
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processIntent(intent);
    }

    private void setCurrentProfile(Profile profile) {
        mEtNickName.setText(profile.getUsername());
        mSpSportsType.setSelection(profile.getSportsid());
        mSpLocation.setSelection(profile.getLocationid());
        mSpAge.setSelection(profile.getAge());
        mSpGender.setSelection(profile.getGender());
        mEtPhoneNum.setText(profile.getPhone());
        mSpLevel.setSelection(profile.getLevel());
        String url = null;
        if(StringUtil.isEmpty(profile.getImage())) {
            url = "android.resource://com.iron.dragon.sportstogether/drawable/default_user";
        } else {
            url = "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_profile?filename=" + profile.getImage();
        }
        Picasso.with(this).load(url).resize(50, 50)
                .centerCrop()
                .into(mIvProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("getItemId = " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_edit: {
                ButterKnife.apply(nameViews, ENABLED, false);
                ButterKnife.apply(buttonViews, VISIBLE, false);
                mEtNickName.setEnabled(false);
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
                GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
                gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
                final Profile pi = new Profile();
                pi.setUsername(mEtNickName.getText().toString());
                pi.setAge(mSpAge.getSelectedItemPosition());
                pi.setGender(mSpGender.getSelectedItemPosition());
                pi.setLocationid(mSpLocation.getSelectedItemPosition());
                pi.setPhone(mEtPhoneNum.getText().toString());
                pi.setSportsid(mSportsId);
                pi.setLevel(mSpLevel.getSelectedItemPosition());

                pi.setRegid(regid);
                Log.v(TAG, "등록 profile=" + pi.toString());
                final Call<Profile> call =
                        gitHubService.putProfiles(regid, pi);

                RetrofitHelper.enqueueWithRetryAndExist(call, new CallbackWithExists<Profile>() {
                    @Override
                    public void onExists(Call<Profile> call, Response<Profile> response) {
                        Toast.makeText(getApplicationContext(), "" + response.code(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());

                        if(mCropImagedUri == null) {
                            Logger.d("onClick, image sportsid = " + mSportsId);
                            Logger.d("onClick, image = " + LoginPreferences.GetInstance().loadSharedPreferencesProfile(getApplicationContext(), mSportsId));
                            pi.setImage(LoginPreferences.GetInstance().loadSharedPreferencesProfile(getApplicationContext(), mSportsId).getImage());
                            saveLocalProfile(pi);
                            toBulletinListActivity();
                        } else {
                            uploadFile(pi, mCropImagedUri);
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

    @Override
    protected void toBulletinListActivity() {
        Logger.d("setResult =" + RESULT_OK);
//        if(isEditMode()) {
            setResult(RESULT_OK);
//        }
        finish();
    }

    /*private boolean isEditMode() {
        return mBtCommit.getVisibility() == View.VISIBLE && mBtCancel.getVisibility() == View.VISIBLE;
    }*/


    private void ShowExitEditorDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.title_edit)
                .content(R.string.edit_cancel_message)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .show();
    }

    @Override
    protected void InitLayout() {
        super.InitLayout();
        ButterKnife.apply(nameViews, DISABLE);
        ButterKnife.apply(buttonViews, INVISIBLE);
    }

}
