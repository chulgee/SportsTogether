package com.iron.dragon.sportstogether.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;
import com.iron.dragon.sportstogether.SportsApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_act);

        requestPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_DANGER_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getDeviceId();
                    checkOverlayPermission(this);
                } else {
                    Toast.makeText(this, "퍼미션을 허락해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "requestCode="+requestCode+", resultCode="+resultCode);

        if(requestCode == REQ_CODE_OVERLAY_WINDOW){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                handleGoToMain();
                Toast.makeText(this, "Overlay available", Toast.LENGTH_SHORT).show();
            } else {
                finish();
                Toast.makeText(this, "Overlay not available", Toast.LENGTH_SHORT).show();
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
            getDeviceId();
            checkOverlayPermission(context);
        }
    }

    void getDeviceId(){
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String uid = tm.getDeviceId();
        SportsApplication.setDeviceID(uid);
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

        if(canOverlay)
            handleGoToMain();
        else
            Log.v(TAG, "wait result on onActivityResult");
    }

    void handleGoToMain(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
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
