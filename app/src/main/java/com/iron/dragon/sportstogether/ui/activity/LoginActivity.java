package com.iron.dragon.sportstogether.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    Handler handler = new Handler();
    @BindView(R.id.etNickName)
    EditText mEtNickName;
    @BindView(R.id.spAge)
    MaterialSpinner mSpAge;
    @BindView(R.id.spGender)
    MaterialSpinner mSpGender;
    @BindView(R.id.spLocation)
    MaterialSpinner mSpLocation;
    @BindView(R.id.etPhoneNum)
    EditText mEtPhoneNum;
    @BindView(R.id.spSportsType)
    MaterialSpinner mSpSportsType;
    @BindView(R.id.spLevel)
    MaterialSpinner mSpLevel;
    @BindView(R.id.bt_commit)
    Button mBtCommit;
    @BindView(R.id.bt_cancel)
    Button mBtCancel;
    private int mSportsId;

    @BindViews({ R.id.etNickName, R.id.etPhoneNum, R.id.spAge, R.id.spGender,R.id. spLocation, R.id.spSportsType, R.id.spLevel })
    List<View> nameViews;

    @BindViews({ R.id.bt_commit, R.id.bt_cancel})
    List<View> buttonViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Test", "LoginActivity");
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Intent i = getIntent();
        processIntent(i);
    }

    private void processIntent(Intent i) {
        mSportsId = i.getIntExtra("Extra_Sports", 0);
        final Profile myprofile = (Profile) i.getSerializableExtra("MyProfile");
        if (myprofile != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.this.setCurrentProfile(myprofile);
                }
            });
        }
    }

    static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setEnabled(false);
        }
    };
    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(View view, Boolean value, int index) {
            view.setEnabled(true);
        }
    };

    static final ButterKnife.Action<View> INVISIBLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setVisibility(View.GONE);
        }
    };
    static final ButterKnife.Setter<View, Boolean> VISIBLE = new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(View view, Boolean value, int index) {
            view.setVisibility(View.VISIBLE);
        }
    };
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

    }

    protected void InitLayout() {
        ButterKnife.apply(nameViews, ENABLED, false);
        ButterKnife.apply(buttonViews, VISIBLE, false);
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
                        gitHubService.postProfiles(p);

                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());

                        if (response.isSuccessful()) {
                            Log.d("Test", "body = " + response.body().toString());
                            Profile p = response.body();
                            Log.v(TAG, Const.SPORTS.BADMINTON.name());
                            Log.v(TAG, "" + Const.SPORTS.values());

                            setLogged();
                            saveLocalProfile(p);
                            toBulletinListActivity();
                            Log.v(TAG, "pi=" + p.toString());
                            finish();
                            return;
                        } else {
                            Log.v(TAG, "response=" + response);
                            Log.v(TAG, "response=" + response.message());
                            Log.v(TAG, "response=" + response.toString());
                            Log.v(TAG, "response=" + response.body());
                            Log.v(TAG, "response=" + response.code());
                            Log.v(TAG, "response=" + response.errorBody());
                            Log.v(TAG, "response=" + response.isSuccessful());
                            Log.v(TAG, "response=" + response.raw());
                            Toast.makeText(getApplicationContext(), "" + response.code(), Toast.LENGTH_SHORT).show();
                            if (response.code() == 409) {
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
                        Log.d("Test", "error message = " + t.getMessage());
                    }
                });
                break;
            case R.id.bt_cancel:
                break;
        }
    }
}

