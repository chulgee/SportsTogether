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
import com.iron.dragon.sportstogether.data.LoginPreferences;
import com.iron.dragon.sportstogether.data.bean.Profile;
import com.iron.dragon.sportstogether.enums.SportsType;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;
import com.iron.dragon.sportstogether.util.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

    public void fetchServerProfiles(String deviceid){
        if(deviceid != null && !deviceid.isEmpty()){
            GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
            GitHubService retrofit = GitHubService.ServiceGenerator.retrofit.create(GitHubService.class);

            final Call<List<Profile>> call = retrofit.getProfilesForDeviceId(deviceid);
            call.enqueue(new Callback<List<Profile>>() {
                @Override
                public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            Log.d(TAG, "response.body() = " + (response.body()!=null?response.body().toString():null));
                            Log.d(TAG, "response.message() = " + response.message());
                            List<Profile> profiles = response.body();
                            if(profiles!=null && profiles.size()>0){
                                for(Profile p : profiles) {
                                    LoginPreferences.GetInstance().saveSharedPreferencesProfile(SplashActivity.this, p);
                                }
                            }else{
                                Log.v(TAG, "no profiles from server");
                            }
                        }else
                            Toast.makeText(SplashActivity.this, "데이터가 없습니다", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SplashActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Profile>> call, Throwable t) {
                    Log.d(TAG, "error message = " + t.getMessage());
                }
            });
        }
    }

    String getDeviceId(){
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String uid = tm.getDeviceId();
        SportsApplication.setDeviceID(uid);
        return uid;
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
        SportsType[] types = SportsType.values();
        boolean isLogged = false;

        for(SportsType t : types){
            if(LoginPreferences.GetInstance().IsLogin(this, t.getValue())){
                isLogged = true;
                break;
            };
        }

        if(!isLogged){
            String deviceid = getDeviceId();
            fetchServerProfiles(deviceid);
        }

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
