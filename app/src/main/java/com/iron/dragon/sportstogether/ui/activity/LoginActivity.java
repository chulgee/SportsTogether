package com.iron.dragon.sportstogether.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.data.bean.ProfileItem;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.util.Const;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    private final static int REQ_CODE_PICK_PICTURE = 1;
    private final static int REQ_CODE_TAKE_PHOTO = 2;
    private final static int REQ_CODE_CROP = 3;
    private final static int PROFILE_IMAGE_ASPECT_X = 4;
    private final static int PROFILE_IMAGE_ASPECT_Y = 5;

    @BindView(R.id.etNickName)
    protected EditText mEtNickName;
    @BindView(R.id.spAge)
    protected MaterialSpinner mSpAge;
    @BindView(R.id.spGender)
    protected MaterialSpinner mSpGender;
    @BindView(R.id.spLocation)
    protected MaterialSpinner mSpLocation;
    @BindView(R.id.etPhoneNum)
    protected EditText mEtPhoneNum;
    @BindView(R.id.spSportsType)
    protected MaterialSpinner mSpSportsType;
    @BindView(R.id.spLevel)
    protected MaterialSpinner mSpLevel;
    @BindView(R.id.bt_commit)
    protected Button mBtCommit;
    @BindView(R.id.bt_cancel)
    protected Button mBtCancel;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.profile_image)
    protected CircleImageView mProfileImage;
    @BindViews({R.id.etNickName, R.id.etPhoneNum, R.id.spAge, R.id.spGender, R.id.spLocation, R.id.spSportsType, R.id.spLevel, R.id.profile_image})
    protected List<View> nameViews;

    @BindViews({R.id.bt_commit, R.id.bt_cancel})
    protected List<View> buttonViews;

    protected Uri mCropImagedUri;
    private int mSportsId;
    Handler handler = new Handler();
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

    static final ButterKnife.Action<View> INVISIBLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setVisibility(View.GONE);
        }
    };
    static final ButterKnife.Setter<View, Boolean> VISIBLE = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
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
                            uploadFile(mCropImagedUri);
                            Log.v(TAG, "pi=" + p.toString());
                            finish();
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

    @OnClick(R.id.profile_image)
    public void onClick() {
        ShowChangeImageActionDialog();
    }

    private void ShowChangeImageActionDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.title_change_profile_image)
                .items(R.array.profile_image)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            dispatchTakePictureIntent();
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                            //retrieve data on return
                            intent.putExtra("return-data", true);

                            startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
                        }
                    }
                })
                .show();
    }

    protected void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File f = createNewFile("CROP_");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }

            if (f != null) {
                mCropImagedUri = Uri.fromFile(f);
                // Continue only if the File was successfully created
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mCropImagedUri);

                startActivityForResult(takePictureIntent, REQ_CODE_TAKE_PHOTO);
            }

        }
    }

    protected File createNewFile(String prefix) {
        if (prefix == null || "".equalsIgnoreCase(prefix)) {
            prefix = "IMG_";
        }
        File newDirectory = new File(Environment.getExternalStorageDirectory() + "/st/");
        if (!newDirectory.exists()) {
            if (newDirectory.mkdir()) {
                Log.d(getApplicationContext().getClass().getName(), newDirectory.getAbsolutePath() + " directory created");
            }
        }
        File file = new File(newDirectory, (prefix + "crop_profile_temp.jpg"));
        if (file.exists()) {
            //this wont be executed
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private void uploadFile(Uri fileUri) {
        // create upload service client
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);

        File file = new File(fileUri.getPath());//Util.getFileFromUri(getContentResolver(), fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = gitHubService.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();

                File f = createNewFile("CROP_");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                mCropImagedUri = Uri.fromFile(f);

                dispatchCropIntent(selectedImageUri);
            }
        } else if (requestCode == REQ_CODE_TAKE_PHOTO) {
            Logger.d("resultCode = " + resultCode + "mCropImagedUri = " + mCropImagedUri);
            if (resultCode == Activity.RESULT_OK) {
                if (mCropImagedUri == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.capture_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                dispatchCropIntent(mCropImagedUri);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.capture_error), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == REQ_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                mCropImagedUri = data.getData();
                Log.d(TAG, "cropresult " + mCropImagedUri + " string " + mCropImagedUri.toString());

                mProfileImage.setImageDrawable(BitmapDrawable.createFromPath(mCropImagedUri.getPath()));

//                requestThumbImage(mFile);
               /* if (mProfileDbId != -1) {
                    requestUpdateThumbImage(mFile);
                } else {
                    requestThumbImage(mFile);
                }*/

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.crop_error), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    protected void dispatchCropIntent(Uri imageCaptureUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        intent.setDataAndType(imageCaptureUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", PROFILE_IMAGE_ASPECT_X);
        intent.putExtra("aspectY", PROFILE_IMAGE_ASPECT_Y);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 480);
        intent.putExtra("scale", true);
        //retrieve data on return
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);

        startActivityForResult(intent, REQ_CODE_CROP);
    }
}

