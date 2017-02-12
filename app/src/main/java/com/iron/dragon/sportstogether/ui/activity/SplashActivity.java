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
import android.util.Log;
import android.widget.Toast;

import com.iron.dragon.sportstogether.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private static final int REQ_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQ_CODE_OVERLAY_WINDOW = 2;

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
            case REQ_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkOverlayPermission(this);

                } else {
                    Toast.makeText(this, "퍼미션을 허락해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }
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

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(context, "You must allow this permission to use this app", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_WRITE_EXTERNAL_STORAGE);

        } else {

            checkOverlayPermission(context);

        }
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
