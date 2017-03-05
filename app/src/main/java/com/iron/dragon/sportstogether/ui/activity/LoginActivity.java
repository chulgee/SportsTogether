package com.iron.dragon.sportstogether.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.enums.LevelType;
import com.iron.dragon.sportstogether.http.CallbackWithExists;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.StringUtil;
import com.iron.dragon.sportstogether.util.ToastUtil;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    @BindView(R.id.bt_commit)
    protected Button mBtCommit;
    @BindView(R.id.bt_cancel)
    protected Button mBtCancel;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.ivProfile_image)
    protected CircleImageView mIvProfileImage;


    @BindViews({R.id.etNickName, R.id.etPhoneNum, R.id.spAge, R.id.spGender, R.id.spLocation, R.id.spSportsType, R.id.sbLevel, R.id.ivProfile_image})
    protected List<View> nameViews;

    @BindViews({R.id.bt_commit, R.id.bt_cancel})
    protected List<View> buttonViews;

    protected Uri mCropImagedUri;
    protected int mSportsId;
    Handler handler = new Handler();

    GitHubService gitHubService;
    @BindView(R.id.sbLevel)
    DiscreteSeekBar mSbLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Test", "LoginActivity");
        setContentView(R.layout.profile_act);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        InitData();
        Intent i = getIntent();
        processIntent(i);

        InitLayout();
        Logger.d("parent mSpSportsType.getSelectedItemPosition() = " + mSpSportsType.getSelectedItemPosition() + " mSportsId = " + mSportsId);
    }

    private void processIntent(Intent i) {
        mSportsId = i.getIntExtra("Extra_Sports", 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitData() {
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);
        String[] arr;
        List<String> list;
        ArrayAdapter<String> adapter;

        arr = StringUtil.getStringArrFromSportsType(this);
        list = new ArrayList<String>(Arrays.asList(arr));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpSportsType.setAdapter(adapter);

        arr = StringUtil.getStringArrFromLocationType(this);
        list = new ArrayList<String>(Arrays.asList(arr));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpLocation.setAdapter(adapter);

        arr = StringUtil.getStringArrFromAgeType(this);
        list = new ArrayList<String>(Arrays.asList(arr));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpAge.setAdapter(adapter);

        /*
        arr = StringUtil.getStringArrFromLevelType(this);
        list = new ArrayList<String>(Arrays.asList(arr));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpLevel.setAdapter(adapter);*/

        arr = StringUtil.getStringArrFromGenderType(this);
        list = new ArrayList<String>(Arrays.asList(arr));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpGender.setAdapter(adapter);
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

    protected void InitLayout() {
        ArrayList<Profile> profileList = LoginPreferences.GetInstance().loadSharedPreferencesProfileAll(this);
        if (profileList.size() == 0) {

        } else {
            mEtNickName.setText(profileList.get(0).getUsername());
            mSpAge.setSelection(profileList.get(0).getAge());
            mSpGender.setSelection(profileList.get(0).getGender());
            mEtPhoneNum.setText(profileList.get(0).getPhone());
            String url = null;
            if (StringUtil.isEmpty(profileList.get(0).getImage())) {
                url = "android.resource://com.iron.dragon.sportstogether/drawable/default_user";
            } else {
                url = "http://ec2-52-78-226-5.ap-northeast-2.compute.amazonaws.com:9000/upload_profile?filename=" + profileList.get(0).getImage();
            }
            Picasso.with(this).load(url).resize(50, 50)
                    .centerCrop()
                    .into(mIvProfileImage);
            mIvProfileImage.setEnabled(false);
            mEtNickName.setEnabled(false);
            mSpAge.setEnabled(false);
            mSpGender.setEnabled(false);
            mEtPhoneNum.setEnabled(false);
        }
        mSpSportsType.setSelection(mSportsId);
        mSpSportsType.setEnabled(false);

        mSbLevel.setMax(LevelType.values().length - 1);
        mSbLevel.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                seekBar.setIndicatorFormatter(StringUtil.getStringFromLevel(LoginActivity.this, value));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });

    }


    protected void saveLocalProfile(Profile p) {
        Logger.d("saveLocalProfile = " + p);
        LoginPreferences.GetInstance().saveSharedPreferencesProfile(this, p);
    }

    protected void toBulletinListActivity() {
        Intent i = new Intent();
        i.setClass(this, BulletinListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("Extra_Sports", mSportsId);
        startActivity(i);
        finish();
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
                pi.setDeviceid(SportsApplication.getDeviceID());
                pi.setUsername(mEtNickName.getText().toString());
                pi.setAge(mSpAge.getSelectedItemPosition());
                pi.setGender(mSpGender.getSelectedItemPosition());
                pi.setLocationid(mSpLocation.getSelectedItemPosition());
                pi.setPhone(mEtPhoneNum.getText().toString());
                pi.setSportsid(mSportsId);
                pi.setLevel(mSbLevel.getProgress());
                pi.setImage("");

                pi.setRegid(regid);
                Log.v(TAG, "등록 profile=" + pi.toString());
                final Call<Profile> call =
                        gitHubService.postProfiles(pi);

                RetrofitHelper.enqueueWithRetryAndExist(call, new CallbackWithExists<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());

                        Log.d("Test", "body = " + response.body().toString());
                        Profile p = response.body();

                        if (mCropImagedUri == null) {
                            saveLocalProfile(p);
                            toBulletinListActivity();

                            Log.v(TAG, "pi=" + p.toString());
                        } else {
                            uploadFile(p, mCropImagedUri);
                        }
                    }

                    @Override
                    public void onExists(Call<Profile> call, Response<Profile> response) {
                        ToastUtil.show(getApplicationContext(), "NickName is Already Exist");
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        Log.d("Test", "error message = " + t.getMessage());
                    }
                });
                break;
            case R.id.bt_cancel:
                finish();
                break;
        }
    }

    @OnClick(R.id.ivProfile_image)
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

    protected void uploadFile(Profile p, Uri fileUri) {
        // create upload_profile service client
//        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
//        GitHubService gitHubService = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

        new ResizeBitmapTask(p).execute(new File(fileUri.getPath()));//Util.getFileFromUri(getContentResolver(), fileUri);
        mCropImagedUri = null;
    }

    class ResizeBitmapTask extends AsyncTask<File, Void, File> {
        Profile profile;

        public ResizeBitmapTask(Profile p) {
            profile = p;
        }

        @Override
        protected File doInBackground(File... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            long fileSize = params[0].length();
            if (fileSize > 2 * 1024 * 1024) {
                options.inSampleSize = 4;
            } else if (fileSize < 700 * 1024) {
                return params[0];
            } else {
                options.inSampleSize = 2;
            }

            Bitmap bitmap = BitmapFactory.decodeFile(params[0].getAbsolutePath(), options);


            OutputStream out = null;
            try {
                out = new FileOutputStream(params[0]);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);
            requestThumbImage(s);
            Log.d(TAG, "requestThumbImage path " + s.getAbsolutePath() + " size " + s.length());
        }

        private void requestThumbImage(File file) {
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/JPEG"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            // add another part within the multipart request
            String username = mEtNickName.getText().toString();
            RequestBody descriptionUserName =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), username);

            int sportsId = mSpSportsType.getSelectedItemPosition();
            RequestBody descriptionSportsId =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), String.valueOf(sportsId));

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("descriptionUserName", descriptionUserName);
            map.put("descriptionSportsId", descriptionSportsId);
            // finally, execute the request
            Call<ResponseBody> call = gitHubService.upload_profileWithPartMap(map, body);

            RetrofitHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    Log.v("Upload", "success");
                    try {

                        JSONObject jObject = new JSONObject(response.body().string());//jsonArray.getJSONObject(i);
                        String image = jObject.get("data").toString();
                        profile.setImage(image);

                        saveLocalProfile(profile);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    toBulletinListActivity();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
                }
            });
        }
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
            }
        } else if (requestCode == REQ_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                mCropImagedUri = data.getData();
                Log.d(TAG, "cropresult " + mCropImagedUri + " string " + mCropImagedUri.toString());

                mIvProfileImage.setImageDrawable(BitmapDrawable.createFromPath(mCropImagedUri.getPath()));

//                requestThumbImage(mFile);
               /* if (mProfileDbId != -1) {
                    requestUpdateThumbImage(mFile);
                } else {
                    requestThumbImage(mFile);
                }*/


            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.crop_error), Toast.LENGTH_SHORT).show();
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

