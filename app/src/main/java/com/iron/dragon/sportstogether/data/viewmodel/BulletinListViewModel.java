package com.iron.dragon.sportstogether.data.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.Gson;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.data.bean.Bulletin_image;
import com.iron.dragon.sportstogether.data.bean.Message;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.http.retropit.RetrofitHelper;
import com.iron.dragon.sportstogether.ui.activity.BulletinListActivity;
import com.iron.dragon.sportstogether.ui.activity.ChatActivity;
import com.iron.dragon.sportstogether.ui.adapter.item.EventItem;
import com.iron.dragon.sportstogether.ui.adapter.item.HeaderItem;
import com.iron.dragon.sportstogether.ui.adapter.item.ListItem;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.ImageUtil;
import com.iron.dragon.sportstogether.util.Util;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by P16018 on 2017-01-05.
 */

public class BulletinListViewModel extends BaseObservable {
    public final ObservableBoolean swipeRefreshViewRefreshing = new ObservableBoolean(false);
    public final ObservableBoolean topSheetEnabled = new ObservableBoolean(false);
    public final ObservableField bottomSheetState= new ObservableField<>();
    public final ObservableField topSheetState= new ObservableField<>();

    private final static int REQ_CODE_PICK_PICTURE = 1;
    private final static int REQ_CODE_TAKE_PHOTO = 2;
    private final static int REQ_CODE_CROP = 3;
    private final static int PROFILE_IMAGE_ASPECT_X = 4;
    private final static int PROFILE_IMAGE_ASPECT_Y = 5;
    private static final int REQ_THRESHOLD = 20;

    private Uri mCropImagedUri;
    private final int mLocationId;
    private BulletinListActivity mActivity;
    private int mSportsId;
    private String numberOfUsersLoggedIn;
    private GitHubService gitHubService;
    private ArrayList<Uri> mAlCropImageUri = new ArrayList<>();
    private int tempfix;
    private int mPageNum = 1;
    private boolean loading;
    private String Content;

    private TreeMap<String, ArrayList<Bulletin>> mTMBulletinMap = new TreeMap<>(Collections.reverseOrder());

    public void excuteChatting(ListItem listItem) {
        Log.v(TAG, "listItem: " + listItem);
        if (listItem instanceof EventItem) {
            Bulletin bulletin = ((EventItem) listItem).getBulletin();
            Log.v(TAG, "bulletin: " + bulletin.toString());
            executeHttp(bulletin);
        }
    }
    private void executeHttp(Bulletin bulletin) {
        String username = bulletin.getUsername();
        int sportsid = bulletin.getSportsid();
        int locationid = bulletin.getLocationid();
        int reqFriends = 0;
        final Call<String> call =
                gitHubService.getProfiles(username, sportsid, locationid, reqFriends);
        Logger.d("executeHttp before");
        RetrofitHelper.enqueueWithRetry(call, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("executeHttp Test", "code = " + response.code() + " is successful = " + response.isSuccessful());
                Log.d("executeHttp Test", "body = " + response.body().toString());
                Log.d("executeHttp Test", "message = " + response.toString());
                //JSONObject obj = (JSONObject)response.body();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                try {
                    String command = obj.getString("command");
                    String code = obj.getString("code");
                    JSONArray arr = obj.getJSONArray("message");
                    Profile buddy = gson.fromJson(arr.get(0).toString(), Profile.class);
                    Profile me = LoginPreferences.GetInstance().getLocalProfile(mActivity.getApplicationContext());
                    Log.v(TAG, "buddy: " + buddy.toString());
                    Log.v(TAG, "me: " + me.toString());
                    Intent i = new Intent(mActivity, ChatActivity.class);
                    Message message = new Message.Builder(Message.PARAM_FROM_ME).msgType(Message.PARAM_TYPE_LOG).sender(me.getUsername()).receiver(buddy.getUsername())
                            .message("Conversation get started").date(new Date().getTime()).image(buddy.getImage()).build();
                    i.putExtra("Message", message);
                    mActivity.startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    public void RefreshData() {
        mTMBulletinMap.clear();
        setLoading(true);
        getBulletinData(REQ_THRESHOLD * (++mPageNum));
    }


    private enum State {
        EXPANDED,
        COLLAPSED,
        IDLE;
    }

    public void setContent(String content) {
        this.Content = content;
        notifyPropertyChanged(BR.content);

    }

    @Bindable
    public String getContent() {
        return Content;
    }

    public BulletinListViewModel(BulletinListActivity bulletinListActivity, int extra_sports) {
        mActivity = bulletinListActivity;
        mSportsId = extra_sports;
        mLocationId = LoginPreferences.GetInstance().GetLocalProfileLocation(mActivity);
        gitHubService = GitHubService.retrofit.create(GitHubService.class);

        getBuddyCount();

    }


    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
    }

    @Bindable
    public boolean getLoading() {
        return loading;
    }

    public void LoadBulletinData() {
        setLoading(true);
        mActivity.startLoadingProgress();
        getBulletinData(mPageNum * REQ_THRESHOLD);
    }

    public String getLocation() {
        return mActivity.getString(R.string.bulletin_location, mActivity.getResources().getStringArray(R.array.location)[mLocationId]);
    }

    private void getBulletinData(int num) {

        final Call<List<Bulletin>> call =
                gitHubService.getBulletin(mSportsId, mLocationId, num);

        RetrofitHelper.enqueueWithRetry(call, new Callback<List<Bulletin>>() {
            @Override
            public void onResponse(Call<List<Bulletin>> call, Response<List<Bulletin>> response) {
                Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                Log.d("Test", "body = " + response.body().toString());
                Log.d("Test", "message = " + response.message());
                List<Bulletin> list = response.body();
                initListView(list);
                setLoading(false);
                mActivity.stopLoadingProgress();
                swipeRefreshViewRefreshing.set(false);
            }

            @Override
            public void onFailure(Call<List<Bulletin>> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    private void initListView(List<Bulletin> listOfStrings) {
        if (listOfStrings.size() != 0) {
            for (Bulletin bulletin : listOfStrings) {
                String sDate = Util.getStringDate(bulletin.getDate());
                if (mTMBulletinMap.get(sDate) == null) {
                    ArrayList<Bulletin> items = new ArrayList<>();
                    items.add(bulletin);
                    mTMBulletinMap.put(sDate, items);
                } else {
                    mTMBulletinMap.get(sDate).add(bulletin);
                }
            }
            ArrayList<ListItem> listItems = new ArrayList<>();

            for (String date : mTMBulletinMap.keySet()) {
                Logger.d("convert data = " + date);
                HeaderItem header = new HeaderItem();
                header.setDate(date);
                listItems.add(header);
                ArrayList<Bulletin> ar = mTMBulletinMap.get(date);
                Collections.sort(ar, new Comparator<Bulletin>() {
                    @Override
                    public int compare(Bulletin bulletin, Bulletin t1) {
                        return bulletin.getDate() > t1.getDate() ? -1 : bulletin.getDate() == t1.getDate() ? 0 : 1;
                    }
                });

                for (Bulletin event : ar) {
                    EventItem item = new EventItem();
                    item.setBulletin(event);
                    listItems.add(item);
                }

            }
            mActivity.setListItem(listItems);

        }
    }

    public int getSportsMainImage() {
        TypedArray imgs = mActivity.getResources().obtainTypedArray(R.array.sportsimg_bulletin);
        int resource = imgs.getResourceId(mSportsId, -1);
        imgs.recycle();
        return resource;
    }

    @BindingAdapter({"imgRes"})
    public static void imgload(ImageView imageView, int resid) {
        imageView.setImageResource(resid);
    }

    @Bindable
    public String getNumberOfUsersLoggedIn() {
        return this.numberOfUsersLoggedIn;
    }


    public void setNumberOfUsersLoggedIn(String isLoaded) {
        Logger.d("setNumberBuddy = " + isLoaded);
        this.numberOfUsersLoggedIn = isLoaded;
        notifyPropertyChanged(BR.numberOfUsersLoggedIn);
    }

    public String getTitle() {
        Const.SPORTS sports = Const.SPORTS.values()[mSportsId];
        return sports.name();
    }

    public void getBuddyCount() {
        final Call<String> call =
                gitHubService.getBuddyCount(mSportsId, mLocationId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String num = "" + 0;
                try {
                    JSONArray jsonArray = new JSONArray(response.body());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        num = jObject.get("COUNT(*)").toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setNumberOfUsersLoggedIn(mActivity.getString(R.string.bulletin_num, num));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }

    public void onClickAttachImage(View v) {
        MaterialDialog.Builder b = new MaterialDialog.Builder(mActivity)
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

                            mActivity.startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
                        }
                    }
                });
        mActivity.ShowChangeImageActionDialog(b);
    }

    protected void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
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

                mActivity.startActivityForResult(takePictureIntent, REQ_CODE_TAKE_PHOTO);
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
                Log.d(mActivity.getApplicationContext().getClass().getName(), newDirectory.getAbsolutePath() + " directory created");
            }
        }
        File file = new File(newDirectory, (prefix + "crop_profile_temp" + tempfix + ".jpg"));
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
    public void onClickFab(View v) {
        mActivity.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void onClickSend(View v) {
        Logger.d("clickclick");
        final Bulletin bulletin = new Bulletin.Builder()
                .setRegid(LoginPreferences.GetInstance().GetRegid(mActivity))
                .setSportsid(mSportsId)
                .setLocationid(mLocationId)
                .setUsername(LoginPreferences.GetInstance().GetLocalProfileUserName(mActivity))
                .setComment(Content)
                .setDate(System.currentTimeMillis())
                .setImage(LoginPreferences.GetInstance().GetLocalProfileImage(mActivity))
                .setType(1).build();
        final Call<Bulletin> call =
                gitHubService.postBulletin(bulletin);
        call.enqueue(new Callback<Bulletin>() {
            @Override
            public void onResponse(Call<Bulletin> call, Response<Bulletin> response) {
                if (response.isSuccessful()) {
                    Bulletin res_bulletin = response.body();
                    Logger.d("response Data = " + res_bulletin.getComment());
                    if (mAlCropImageUri.size() == 0) {
                        updatePosting(bulletin);
                    } else {
                        uploadFile(res_bulletin.getid(), bulletin);
                    }
                }
            }

            @Override
            public void onFailure(Call<Bulletin> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });

    }

    private void updatePosting(Bulletin res_bulletin) {
        HeaderItem header = new HeaderItem();
        header.setDate(Util.getStringDate(res_bulletin.getDate()));
        EventItem item = new EventItem();
        item.setBulletin(res_bulletin);
        mActivity.showPosting(header, item);
        postDone();


    }

    private void postDone() {
        tempfix = 0;
        mAlCropImageUri.clear();
        setContent("");
    }

    private void uploadFile(int getid, Bulletin bulletin) {
        int count = mAlCropImageUri.size();
        new ResizeBitmapTask(getid, bulletin).execute(mAlCropImageUri);//Util.getFileFromUri(getContentResolver(), fileUri);
//            mCropImagedUri = null;
    }

    public void setOnActivityResult(int requestCode, int resultCode, Intent data) {
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
                    mActivity.showToast(mActivity.getString(R.string.capture_error));
                    return;
                }

                dispatchCropIntent(mCropImagedUri);
            } else {
                mActivity.showToast(mActivity.getString(R.string.capture_error));
            }

        } else if (requestCode == REQ_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                mCropImagedUri = data.getData();
                Logger.d("cropresult " + mCropImagedUri);
                mAlCropImageUri.add(mCropImagedUri);
                ImageView iv = new ImageView(mActivity);
                mActivity.addImageView(ImageUtil.getDownsampledBitmap(mActivity.getContentResolver(), mCropImagedUri, 150, 150));
                tempfix++;
            } else {
                mActivity.showToast(mActivity.getString(R.string.crop_error));
            }
        }
    }

    public AppBarLayout.OnOffsetChangedListener getOffsetCahngedListener() {
        return new AppBarLayout.OnOffsetChangedListener() {
            private BulletinListViewModel.State state;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != BulletinListViewModel.State.EXPANDED) {
                        topSheetEnabled.set(true);
                        Logger.d("onOffsetChanged State = EXPANDED");
                    }
                    state = BulletinListViewModel.State.EXPANDED;
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != BulletinListViewModel.State.COLLAPSED) {
                        topSheetEnabled.set(false);
                        Logger.d("onOffsetChanged State = COLLAPSED");

                    }
                    state = BulletinListViewModel.State.COLLAPSED;
                } else {
                    if (state != BulletinListViewModel.State.IDLE) {
                        topSheetEnabled.set(false);
                        Logger.d("onOffsetChanged State = IDLE");

                    }
                    state = BulletinListViewModel.State.IDLE;
                }
            }
        };
    }

    private class ResizeBitmapTask extends AsyncTask<ArrayList<Uri>, File, Void> {

        private final int mPostId;
        private final Bulletin mBulletin;
        List<Bulletin_image> list_image = new ArrayList<>();

        public ResizeBitmapTask(int getid, Bulletin bulletin) {
            mPostId = getid;
            mBulletin = bulletin;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<Uri>... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            ArrayList<Uri> uri_list = params[0];
            Logger.d("url_list size = " + uri_list.size());
            for (Uri uri : uri_list) {
                File file = new File(uri.getPath());
                Logger.d("File Upload Name = " + file.getAbsolutePath());
                long fileSize = file.length();
                if (fileSize > 2 * 1024 * 1024) {
                    options.inSampleSize = 4;
                } else if (fileSize < 700 * 1024) {
                    publishProgress(file);
                    continue;
                } else {
                    options.inSampleSize = 2;
                }

                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);


                OutputStream out = null;
                try {
                    out = new FileOutputStream(file);

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
                publishProgress(file);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(File... values) {
            super.onProgressUpdate(values);
            Logger.d("onProgressUpdate values = " + values[0]);
            requestThumbImage(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mBulletin.setBulletinImage(list_image);
            setLoading(true);
            mActivity.startLoadingProgress();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updatePosting(mBulletin);
                    mActivity.stopLoadingProgress();
                    setLoading(false);
                }
            }, 3000);

        }

        private void requestThumbImage(File file) {
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/JPEG"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            // add another part within the multipart request
            String descriptionString = String.valueOf(mPostId);
            RequestBody description =
                    RequestBody.create(
                            MediaType.parse("text/html"), descriptionString);

            // finally, execute the request
            Call<ResponseBody> call = gitHubService.upload_post(description, body);

            RetrofitHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    Log.v("Upload", "success");
                    JSONObject jObject = null;//jsonArray.getJSONObject(i);
                    try {
                        jObject = new JSONObject(response.body().string());
                        String image = jObject.get("data").toString();
                        list_image.add(new Bulletin_image(image));

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
                }
            });
        }
    }

    protected void dispatchCropIntent(Uri imageCaptureUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Point screenSize = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);
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

        mActivity.startActivityForResult(intent, REQ_CODE_CROP);
    }


    @BindingAdapter({"bind:bottomSheet"})
    public static void bottomlistener(View v, BottomSheetBehavior.BottomSheetCallback b) {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(v.findViewById(R.id.commentLayout));
        bottomSheetBehavior.setBottomSheetCallback(b);
    }

    public final BottomSheetBehavior.BottomSheetCallback getBottomSheetCallback() {
        return  new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                // Check Logs to see how bottom sheets behaves
                Logger.d("onStateChanged =" + newState);
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetState.set("collasped");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetState.set("expanded");
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:

                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }
            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        };
    }

    public SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!loading) {
                    Logger.d("reach last");
                    setLoading(true);
                    mTMBulletinMap.clear();
                    mActivity.clearAdapter();
                    getBulletinData(REQ_THRESHOLD);
                }
            }
        };
    }
}