package com.iron.dragon.sportstogether.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Bulletin;
import com.iron.dragon.sportstogether.http.retropit.GitHubService;
import com.iron.dragon.sportstogether.ui.adapter.BulletinRecyclerViewAdapter;
import com.iron.dragon.sportstogether.ui.adapter.item.EventItem;
import com.iron.dragon.sportstogether.ui.adapter.item.HeaderItem;
import com.iron.dragon.sportstogether.ui.adapter.item.ListItem;
import com.iron.dragon.sportstogether.ui.view.DividerItemDecoration;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iron.dragon.sportstogether.R.id.collapsingToolbarLayout;


public class BulletinListActivity extends AppCompatActivity {
    private final static int REQ_CODE_PICK_PICTURE = 1;
    private final static int REQ_CODE_TAKE_PHOTO = 2;
    private final static int REQ_CODE_CROP = 3;
    private final static int PROFILE_IMAGE_ASPECT_X = 4;
    private final static int PROFILE_IMAGE_ASPECT_Y = 5;

    @BindView(R.id.ivBulletin)
    ImageView mIvBulletin;
    @BindView(R.id.tvSportsName)
    TextView mTvSportsName;
    @BindView(R.id.tvTotalNum)
    TextView mTvTotalNum;
    @BindView(R.id.etContent)
    EditText mEtContent;
    @BindView(R.id.bt_Send)
    Button mBtSend;
    @BindView(R.id.board_recyclerviewer)
    RecyclerView mBoardRecyclerviewer;
    @BindView(R.id.tvLocation)
    TextView mTvLocation;
    @BindView(collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.commentLayout)
    LinearLayout mCommentLayout;
    @BindView(R.id.llAttachLayout)
    LinearLayout mllAttachLayout;
    private int mSportsId;
    private int mLocationId;

    private BulletinRecyclerViewAdapter mAdapter;
    private TreeMap<String, ArrayList<Bulletin>> mTMBulletinMap = new TreeMap<>();
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayoutManager layoutManager;
    private Uri mCropImagedUri;
//    Animation slideUpAnimation, slideDownAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_list_view);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        LoadData();
        InitLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
//                NavUtils.navigateUpFromSameTask(this);
                finish();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBulletinData() {
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        ArrayList<Bulletin> listOfStrings;
        final Call<List<Bulletin>> call =
                gitHubService.getBulletin(mSportsId, mLocationId, 10);
        call.enqueue(new Callback<List<Bulletin>>() {
            @Override
            public void onResponse(Call<List<Bulletin>> call, Response<List<Bulletin>> response) {
                Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                Log.d("Test", "body = " + response.body().toString());
                Log.d("Test", "message = " + response.message());
                List<Bulletin> list = response.body();

                initListView(list);
            }

            @Override
            public void onFailure(Call<List<Bulletin>> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });

    }

    private void initListView(List<Bulletin> listOfStrings) {
//        HashMap<String,Bulletin> map = new HashMap<String,Bulletin>();
//        ValueComparator bvc =  new ValueComparator();
//        TreeMap<String,Bulletin> sorted_map = new TreeMap<String,Bulletin>(bvc);
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
                        return bulletin.getDate() < t1.getDate() ? -1 : bulletin.getDate() == t1.getDate() ? 0 : 1;
                    }
                });

                for (Bulletin event : ar) {
                    EventItem item = new EventItem();
                    item.setBulletin(event);
                    listItems.add(item);
                }
            }
            mAdapter.setItem(listItems);
        }
    }

    private void LoadData() {
        Intent intent = getIntent();
        mSportsId = intent.getIntExtra("Extra_Sports", 0);
        mLocationId = LoginPreferences.GetInstance().GetLocalProfileLocation(this);
        getBulletinData();

    }

    private void InitLayout() {
        layoutManager = new LinearLayoutManager(this);
        mBoardRecyclerviewer.setLayoutManager(layoutManager);
        mTvTotalNum.setText("");
        getBuddyCount();
        TypedArray imgs = getResources().obtainTypedArray(R.array.sportsimg_bulletin);
        mIvBulletin.setImageResource(imgs.getResourceId(mSportsId, -1));
        imgs.recycle();

        Const.SPORTS sports = Const.SPORTS.values()[mSportsId];
        mCollapsingToolbarLayout.setTitle(sports.name());
        mTvLocation.setText(getString(R.string.bulletin_location, getResources().getStringArray(R.array.location)[mLocationId]));

        mAdapter = new BulletinRecyclerViewAdapter(BulletinListActivity.this);
        mBoardRecyclerviewer.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        mBoardRecyclerviewer.addItemDecoration(dividerItemDecoration);
        mAdapter.setOnItemLongClickListener(new BulletinRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(view);
                openContextMenu(view);
            }
        });

        /*slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_animation);
        mCommentLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    mCommentLayout.startAnimation(slideDownAnimation);
                }
            }
        });*/
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.commentLayout));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

//                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    bottomSheetHeading.setText(getString(R.string.text_collapse_me));
//                } else {
//                    bottomSheetHeading.setText(getString(R.string.text_expand_me));
//                }

                // Check Logs to see how bottom sheets behaves
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mFab.setVisibility(View.VISIBLE);
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mFab.setVisibility(View.GONE);
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
        });

    }

    private void getBuddyCount() {

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
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
                mTvTotalNum.setText(getString(R.string.bulletin_num, num));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Test", "error message = " + t.getMessage());
            }
        });
    }


    @OnClick({R.id.bt_Send, R.id.fab, R.id.btAttachImage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_Send:
                String content = mEtContent.getText().toString();
                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                Bulletin bulletin = new Bulletin.Builder()
                        .setRegid(LoginPreferences.GetInstance().GetRegid(this))
                        .setSportsid(mSportsId)
                        .setLocationid(mLocationId)
                        .setUsername(LoginPreferences.GetInstance().GetLocalProfileUserName(BulletinListActivity.this))
                        .setComment(content)
                        .setDate(System.currentTimeMillis())
                        .setImage(LoginPreferences.GetInstance().GetLocalProfileImage(BulletinListActivity.this))
                        .setType(1).build();
                final Call<Bulletin> call =
                        gitHubService.postBulletin(bulletin);
                call.enqueue(new Callback<Bulletin>() {
                    @Override
                    public void onResponse(Call<Bulletin> call, Response<Bulletin> response) {
                        Log.d("Test", "code = " + response.code() + " issuccessful = " + response.isSuccessful());
                        Log.d("Test", "body = " + response.body().toString());
                        Log.d("Test", "message = " + response.message());
                        if (response.isSuccessful()) {
                            Bulletin res_bulletin = response.body();

                            HeaderItem header = new HeaderItem();
                            header.setDate(Util.getStringDate(res_bulletin.getDate()));
                            EventItem item = new EventItem();
                            item.setBulletin(res_bulletin);
                            if (mAdapter.getItemCount() == 0) {
                                ArrayList<ListItem> listItems = new ArrayList<>();
                                listItems.add(header);
                                listItems.add(item);
                                mAdapter.setItem(listItems);
                            } else {
                                mAdapter.addItem(header);
                                mAdapter.addItem(item);
                            }
                            mBoardRecyclerviewer.smoothScrollToPosition(mBoardRecyclerviewer.getAdapter().getItemCount());
                            mEtContent.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<Bulletin> call, Throwable t) {
                        Log.d("Test", "error message = " + t.getMessage());
                    }
                });
                break;
            case R.id.fab:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                mCommentLayout.setVisibility(View.VISIBLE);
//                mCommentLayout.startAnimation(slideUpAnimation);
                break;
            case R.id.btAttachImage:
                ShowChangeImageActionDialog();
                break;
        }
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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderIcon(android.R.drawable.ic_menu_share);
        menu.setHeaderTitle("Menu");
        inflater.inflate(R.menu.menu_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                //some code
                return true;
            default:
                return super.onContextItemSelected(item);
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
                Logger.d("cropresult " + mCropImagedUri );
                ImageView iv = new ImageView(this);
//                BitmapDrawable.createFromPath(mCropImagedUri.getPath());
                iv.setImageBitmap(getDownsampledBitmap(this, mCropImagedUri, 150, 150));
                mllAttachLayout.addView(iv);
//                mIvProfileImage.setImageDrawable(BitmapDrawable.createFromPath(mCropImagedUri.getPath()));

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


    public Bitmap getDownsampledBitmap(Context ctx, Uri uri, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        try {
            ContentResolver ctr = ctx.getContentResolver();
            BitmapFactory.Options outDimens = getBitmapDimensions(ctr, uri);

            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);

            bitmap = downsampleBitmap(ctr, uri, sampleSize);

        } catch (Exception e) {
            //handle the exception(s)
        }

        return bitmap;
    }

    private BitmapFactory.Options getBitmapDimensions(ContentResolver contentResolver, Uri uri) throws FileNotFoundException, IOException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        InputStream is = contentResolver.openInputStream(uri);
        // if Options requested only the size will be returned
        BitmapFactory.decodeStream(is, null, outDimens);
        is.close();

        return outDimens;
    }

    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private Bitmap downsampleBitmap(ContentResolver ctr, Uri uri, int sampleSize) throws FileNotFoundException, IOException {
        Bitmap resizedBitmap;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize;

        InputStream is = ctr.openInputStream(uri);
        resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
        is.close();

        return resizedBitmap;
    }
}