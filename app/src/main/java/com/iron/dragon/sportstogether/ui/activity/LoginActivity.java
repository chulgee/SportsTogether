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
import com.iron.dragon.sportstogether.http.retropit.CallbackWithExists;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.http.retropit.RetrofitHelper;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.ToastUtil;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    @BindView(R.id.spLevel)
    protected MaterialSpinner mSpLevel;
    @BindView(R.id.bt_commit)
    protected Button mBtCommit;
    @BindView(R.id.bt_cancel)
    protected Button mBtCancel;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.ivProfile_image)
    protected CircleImageView mIvProfileImage;
    @BindViews({R.id.etNickName, R.id.etPhoneNum, R.id.spAge, R.id.spGender, R.id.spLocation, R.id.spSportsType, R.id.spLevel, R.id.ivProfile_image})
    protected List<View> nameViews;

    @BindViews({R.id.bt_commit, R.id.bt_cancel})
    protected List<View> buttonViews;

    protected Uri mCropImagedUri;
    protected int mSportsId;
    Handler handler = new Handler();

    GitHubService gitHubService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Test", "LoginActivity");
        setContentView(R.layout.activity_profile);
        InitData();
        ButterKnife.bind(this);

    }

    private void InitData() {
            gitHubService = GitHubService.retrofit.create(GitHubService.class);
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
        ButterKnife.apply(nameViews, ENABLED, false);
        ButterKnife.apply(buttonViews, VISIBLE, false);
    }


    protected void saveLocalProfile(Profile p) {
        LoginPreferences.GetInstance().SetLocalProfile(this, p);
    }

    protected void toBulletinListActivity() {
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
                gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final ProfileItem pi = new ProfileItem();
                pi.set_mNickName(mEtNickName.getText().toString());
                pi.set_mAge(mSpAge.getSelectedItemPosition());
                pi.set_mGender(mSpGender.getSelectedItemPosition());
                pi.set_mLocation(mSpLocation.getSelectedItemPosition());
                pi.set_mPhoneNum(mEtPhoneNum.getText().toString());
                pi.set_mSportsType(mSpSportsType.getSelectedItemPosition());
                pi.set_mLevel(mSpLevel.getSelectedItemPosition());
                pi.set_mImage("");


                Profile p = new Profile(pi);
                p.setRegid(regid);
                Log.v(TAG, "등록 profile=" + p.toString());
                final Call<Profile> call =
                        gitHubService.postProfiles(p);

                RetrofitHelper.enqueueWithRetryAndExist(call, new CallbackWithExists<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Log.v(TAG, "onResponse response.isSuccessful()=" + response.isSuccessful());

                        Log.d("Test", "body = " + response.body().toString());
                        Profile p = response.body();

                        Log.v(TAG, Const.SPORTS.BADMINTON.name());
                        Log.v(TAG, "" + Const.SPORTS.values());

                        setLogged();

                        if(mCropImagedUri == null) {
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
//        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);

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

