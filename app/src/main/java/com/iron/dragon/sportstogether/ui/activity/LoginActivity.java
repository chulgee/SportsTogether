package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.data.bean.ProfileItem;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.util.Const;

import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iron.dragon.sportstogether.R.id.spAge;

public class LoginActivity extends ProfileActivity {
    private final String TAG = getClass().getName();

    Handler handler = new Handler();
    private int mSportsId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Test", "LoginActivity");
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        processIntent(i);

        InitLayout();
    }

    private void processIntent(Intent i){
        mSportsId = i.getIntExtra("Extra_Sports", 0);
        final Profile myprofile = (Profile)i.getSerializableExtra("MyProfile");
        if(myprofile != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.this.setCurrentProfile(myprofile);
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processIntent(intent);
    }

    private void setCurrentProfile(Profile profile){
        EditText username = (EditText)findViewById(R.id.etNickName);
        username.setText(profile.getUsername());
        MaterialSpinner sportsid = (MaterialSpinner)findViewById(R.id.spSportsType);
        sportsid.setSelection(profile.getSportsid());
        MaterialSpinner locationid = (MaterialSpinner)findViewById(R.id.spLocation);
        locationid.setSelection(profile.getLocationid());
        MaterialSpinner age = (MaterialSpinner)findViewById(spAge);
        age.setSelection(profile.getAge());
        MaterialSpinner gender = (MaterialSpinner)findViewById(R.id.spGender);
        gender.setSelection(profile.getGender());
        EditText phone = (EditText)findViewById(R.id.etPhoneNum);
        phone.setText(profile.getPhone());
        MaterialSpinner level = (MaterialSpinner)findViewById(R.id.spLevel);
        level.setSelection(profile.getLevel());

    }

    @Override
    protected void InitLayout() {
        super.InitLayout();
        final EditText etNickName = (EditText) findViewById(R.id.etNickName);
        final MaterialSpinner etAge = (MaterialSpinner) findViewById(spAge);
        final MaterialSpinner spGender = (MaterialSpinner) findViewById(R.id.spGender);

        final MaterialSpinner spLocation = (MaterialSpinner) findViewById(R.id.spLocation);
        final EditText etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        final MaterialSpinner spSportType = (MaterialSpinner) findViewById(R.id.spSportsType);
        final MaterialSpinner spLevel = (MaterialSpinner) findViewById(R.id.spLevel);

        findViewById(R.id.bt_commit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                String regid = FirebaseInstanceId.getInstance().getToken();
                Log.v(TAG, "등록 id :"+regid);
                LoginPreferences.GetInstance().SetRegid(getApplicationContext(), regid);
                SportsApplication app = (SportsApplication)getApplication();
                app.setRegid(regid);
                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final ProfileItem pi = new ProfileItem();
                pi.set_mNickName(etNickName.getText().toString());
                //pi.set_mAge(spAge.getText().length() == 0 ? 0 : Integer.parseInt(etAge.getText().toString()));
                pi.set_mGender(spGender.getSelectedItemPosition());
                pi.set_mLocation(spLocation.getSelectedItemPosition());
                pi.set_mPhoneNum(etPhoneNum.getText().toString());
                pi.set_mSportsType(spSportType.getSelectedItemPosition());
                pi.set_mLevel(spLevel.getSelectedItemPosition());

                Profile p = new Profile(pi);
                p.setRegid(regid);
                Log.v(TAG, "등록 profile="+p.toString());
                final Call<Profile> call =
                        gitHubService.postProfiles(p);

                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Log.v(TAG, "onResponse response.isSuccessful()="+response.isSuccessful());

                        if (response.isSuccessful()) {
                            android.util.Log.d("Test", "body = " + response.body().toString());
                            Profile p = response.body();
                            Log.v(TAG, Const.SPORTS.BADMINTON.name());
                            Log.v(TAG, ""+Const.SPORTS.values());

                            setLogged();
                            saveLocalProfile(p);
                            toBulletinListActivity();
                            Log.v(TAG, "pi="+p.toString());
                            finish();
                            return;
                        } else {
                            Log.v(TAG, "response="+response);
                            Log.v(TAG, "response="+response.message());
                            Log.v(TAG, "response="+response.toString());
                            Log.v(TAG, "response="+response.body());
                            Log.v(TAG, "response="+response.code());
                            Log.v(TAG, "response="+response.errorBody());
                            Log.v(TAG, "response="+response.isSuccessful());
                            Log.v(TAG, "response="+response.raw());
                            Toast.makeText(getApplicationContext(), ""+response.code(), Toast.LENGTH_SHORT).show();
                            if(response.code() == 409){
                                setLogged();
                                toBulletinListActivity();
                                finish();
                                return;
                            }
/*                            Error err = ErrorUtil.parse(response);
                            if(err != null)
                                ToastUtil.show(getApplicationContext(), err.getStatusCode()+", "+err.getMessage());*/
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

            }
        });

    }

    private void saveLocalProfile(Profile p) {

        LoginPreferences.GetInstance().SetLocalProfile(this, p);
    }

    private void toBulletinListActivity() {
        Intent i = new Intent();
        i.setClass(this, BulletinListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("Extra_Sports", mSportsId);
        startActivity(i);
        finish();
    }


    private void setLogged() {
        LoginPreferences.GetInstance().SetLogin(getApplicationContext(), true);
    }
}

