package com.iron.dragon.sportstogether.ui.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.iron.dragon.sportstogether.BuildConfig;
import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.SportsApplication;
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.enums.SportsType;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.http.retrofit.RetrofitHelper;
import com.iron.dragon.sportstogether.util.Const;
import com.iron.dragon.sportstogether.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private static final int REQ_CODE_DANGER_PERMISSION = 1;
    private static final int REQ_CODE_OVERLAY_WINDOW = 3;
    private static final String[] mPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    private static final String[] mPermissionsStr = {"파일쓰기", "폰정보읽기"};

    Handler handler = new Handler();

    LocalBroadcastManager mLocalBr;
    BroadcastReceiver mLocalBrReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.LOCAL_ACTION_GO_TO_MAIN)){
                handleGoToMain();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_act);

        mLocalBr = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.LOCAL_ACTION_GO_TO_MAIN);
        mLocalBr.registerReceiver(mLocalBrReceiver, filter);

        RetrofitHelper.getServerVersion(this, new RetrofitHelper.VersionListener() {
            @Override
            public void onLoaded(int versioncode) {
                if (BuildConfig.DEBUG || BuildConfig.VERSION_CODE >= versioncode) {
                    requestPermission(SplashActivity.this);
                } else {
                    showScreenToUpdate();
                }
            }

            @Override
            public void onFailed() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SplashActivity.this.finish();
                    }
                }, 1500);
            }
        });
    }

    public void showScreenToUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("업데이트 필요");
        StringBuffer sb = new StringBuffer();
        sb.append("새로운 버전이 마켓에 등록되었습니다. \n" +
                "아래 버튼을 눌러 업데이트해주세요");
        builder.setMessage(sb.toString());
        builder.setCancelable(false);
        builder.setPositiveButton("마켓으로 이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SplashActivity.this, "마켓으로 이동합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.iron.dragon.sportstogether"));
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_DANGER_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    checkOverlayPermission(this);
                } else {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                            sb.append(mPermissionsStr[i]+" ");
                        }
                    }
                    Toast.makeText(this, sb.toString()+"권한을 허락해주세요", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBr.unregisterReceiver(mLocalBrReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "requestCode="+requestCode+", resultCode="+resultCode);

        if(requestCode == REQ_CODE_OVERLAY_WINDOW){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                handleGoToMain();
                //Toast.makeText(this, "Overlay available", Toast.LENGTH_SHORT).show();
            } else {
                finish();
                Toast.makeText(this, "오버레이 권한을 허락해 주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void requestPermission(Context context){
        List<String> reqList = getReqPermissions(this, mPermissions);
        if (reqList.size() > 0) {
            String[] arr = new String[reqList.size()];
            reqList.toArray(arr);
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<arr.length; i++){
                for(int j=0; j<mPermissions.length; j++){
                    if(arr[i].equals(mPermissions[j])){
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[j])) {
                            sb.append(mPermissionsStr[j] + " ");
                        }
                    }
                }
            }
            if(!sb.toString().isEmpty())
                Toast.makeText(context, sb.toString()+" 권한은 앱 사용에 반드시 필요합니다.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, mPermissions, REQ_CODE_DANGER_PERMISSION);
        } else {
            checkOverlayPermission(context);
        }
    }

    List<String> getReqPermissions(Context context, String[] permissions){
        List<String> reqPermissions = new ArrayList<String>();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(String permission : permissions){
                if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    reqPermissions.add(permission);
                }
            }
        }
        Log.v(TAG, "req perm "+reqPermissions.toString());
        return reqPermissions;
    }

    void checkOverlayPermission(Context ctx){

        boolean canOverlay = canOverlayWindow(ctx);

        if(canOverlay){
            handleGoToMain();
        }
        else
            Log.v(TAG, "wait result on onActivityResult");
    }

    void handleGoToMain(){
        boolean isLogin = false;

        isLogin = LoginPreferences.GetInstance().isLogin(this);

        Log.v(TAG, "[token] handleGoToMain isLogin="+isLogin);
        if(!isLogin){
            Log.v(TAG, "[token] start fetching profiles from server");
            String deviceid = Util.getDeviceId(this);
            String regid = FirebaseInstanceId.getInstance().getToken();
            Log.v(TAG, "[token] handleGoToMain deviceid="+deviceid+", regid="+regid);
            if(regid == null || regid.isEmpty()) {
                Toast.makeText(this, "등록ID를 생성합니다. 잠시 기다려 주십시요", Toast.LENGTH_SHORT).show();
                // wait until a token refreshes,  and then trigger it at onTokenRefresh
            }else {
                RetrofitHelper.getServerProfiles(this, deviceid, regid, new RetrofitHelper.OnProfileListener() {
                    @Override
                    public void onProfileLoaded() {
                        Log.v(TAG, "[token] onProfileLoaded goToMain");
                        goToMain();
                    }
                });
            }
        }else{
            goToMain();
        }
    }

    public void goToMain(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }

    public void restartApp(){
        Intent mStartActivity = new Intent(this, SplashActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
        finish();
    }

    public void relaunchApp(){
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        Log.v(TAG, "relaunchApp");
        finish();
    }

    public boolean canOverlayWindow(Context context) {
        boolean ret = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_CODE_OVERLAY_WINDOW);
        } else {
            ret = true;
        }

        return ret;
    }

}
