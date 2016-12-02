package com.iron.dragon.sportstogether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.messaging.FirebaseMessaging;
import com.iron.dragon.sportstogether.data.LoginPreferences;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends Activity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        setContentView(R.layout.activity_fullscreen);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(IsLogged()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 3000);
    }
    private boolean IsLogged() {
        return LoginPreferences.GetInstance().CheckLogin(this);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }

}
