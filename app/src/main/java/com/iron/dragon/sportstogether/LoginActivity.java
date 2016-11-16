package com.iron.dragon.sportstogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.ProfileItem;
import com.iron.dragon.sportstogether.retrofit.GitHubService;
import com.iron.dragon.sportstogether.retrofit.Profile;
import com.iron.dragon.sportstogether.util.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends ProfileActivity {
    private final String TAG = getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        InitLayout();

    }

    @Override
    protected void InitLayout() {
        super.InitLayout();
        final EditText etNickName = (EditText)findViewById(R.id.etNickName);
        final EditText etAge = (EditText)findViewById(R.id.etAge);
        final EditText etGender = (EditText)findViewById(R.id.etGender);
        final EditText etLocation = (EditText)findViewById(R.id.etLocation);
        final EditText etPhoneNum = (EditText)findViewById(R.id.etPhoneNum);
        final EditText etSportType = (EditText)findViewById(R.id.etSportType);
        final EditText etLevel = (EditText)findViewById(R.id.etLevel);



        findViewById(R.id.bt_commit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final ProfileItem pi = new ProfileItem();
                pi.set_mNickName(etNickName.getText().toString());
                pi.set_mAge(etAge.getText().length() == 0? 0 :Integer.parseInt(etAge.getText().toString()));
                pi.set_mGender(etGender.getText().length() == 0? 0 : Integer.parseInt(etGender.getText().toString()));
                pi.set_mLocation(etLocation.getText().toString());
                pi.set_mPhoneNum(etPhoneNum.getText().toString());
                pi.set_mSportsType(etSportType.getText().toString());
                pi.set_mLevel(etLevel.getText().length() == 0? 0 :Integer.parseInt(etLevel.getText().toString()));

                final Profile p = new Profile(pi);
                final Call<Profile> call =
                        gitHubService.repoContributors(p);

                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        if(response.isSuccessful()) {
                            setLoagged();
                            saveLocalProfile(pi);
                            ToBoardListView();
                        } else {
                            ToastUtil.show(getApplicationContext(), "NickName is Existed");

                        }

                        android.util.Log.d("Test", "code = " + response.code() + response.isSuccessful());
                        android.util.Log.d("Test", "message = " + response.message());

                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        android.util.Log.d("Test", "error message = " + t.getMessage());
                    }
                });
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

