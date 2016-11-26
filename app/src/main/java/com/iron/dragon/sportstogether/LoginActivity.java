package com.iron.dragon.sportstogether;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.ProfileItem;
import com.iron.dragon.sportstogether.retrofit.Error;
import com.iron.dragon.sportstogether.retrofit.GitHubService;
import com.iron.dragon.sportstogether.retrofit.Profile;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.ErrorUtil;
import com.iron.dragon.sportstogether.util.ToastUtil;

import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends ProfileActivity {
    private final String TAG = getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Test", "LoginActivity2");
        setContentView(R.layout.activity_profile);
        InitLayout();

    }

    @Override
    protected void InitLayout() {
        super.InitLayout();
        final EditText etNickName = (EditText) findViewById(R.id.etNickName);
        final EditText etAge = (EditText) findViewById(R.id.etAge);
        final MaterialSpinner spGender = (MaterialSpinner) findViewById(R.id.spGender);

        final MaterialSpinner spLocation = (MaterialSpinner) findViewById(R.id.spLocation);
        final EditText etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        final MaterialSpinner spSportType = (MaterialSpinner) findViewById(R.id.spSportsType);
        final MaterialSpinner spLevel = (MaterialSpinner) findViewById(R.id.spLevel);

        findViewById(R.id.bt_commit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final ProfileItem pi = new ProfileItem();
                pi.set_mNickName(etNickName.getText().toString());
                pi.set_mAge(etAge.getText().length() == 0 ? 0 : Integer.parseInt(etAge.getText().toString()));
                pi.set_mGender(spGender.getSelectedItemPosition());
                pi.set_mLocation(spLocation.getSelectedItemPosition());
                pi.set_mPhoneNum(etPhoneNum.getText().toString());
                pi.set_mSportsType(spSportType.getSelectedItemPosition());
                pi.set_mLevel(spLevel.getSelectedItemPosition());

                final Profile p = new Profile(pi);
                final Call<Profile> call =
                        gitHubService.postProfiles(p);

                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        if (response.isSuccessful()) {
                            android.util.Log.d("Test", "body = " + response.body().toString());
                            Profile p = response.body();
                            Log.v(TAG, Const.SPORTS.BADMINTON.name());
                            Log.v(TAG, ""+Const.SPORTS.values());

                            setLoagged();
                            saveLocalProfile(pi);
                            ToBoardListView();
                        } else {
                            Error err = ErrorUtil.parse(response);
                            ToastUtil.show(getApplicationContext(), err.getStatusCode()+", "+err.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        android.util.Log.d("Test", "error message = " + t.getMessage());
                    }
                });
            }
        });

        findViewById(R.id.bt_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /*GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final Call<List<ProfileWithId>> call =
                        gitHubService.getProfiles("김한용1", 0, 0);
                call.enqueue(new Callback<List<ProfileWithId>>() {
                    @Override
                    public void onResponse(Call<List<ProfileWithId>> call, Response<List<ProfileWithId>> response) {
                        android.util.Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                        android.util.Log.d("Test", "body = " + response.body().toString());
                        android.util.Log.d("Test", "message = " + response.message());
                    }

                    @Override
                    public void onFailure(Call<List<ProfileWithId>> call, Throwable t) {
                        android.util.Log.d("Test", "error message = " + t.getMessage());
                    }
                });*/

            }
        });

    }

    private void saveLocalProfile(ProfileItem pi) {

        LoginPreferences.GetInstance().SetLocalProfile(getApplicationContext(), pi);
    }

    private void ToBoardListView() {
        Intent i = new Intent();
        i.setClass(this, MainActivity.class);
        startActivity(i);
    }


    private void setLoagged() {
        LoginPreferences.GetInstance().SetLogin(this);
    }
}

