package com.uhungry.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;

import com.uhungry.R;
import com.crashlytics.android.Crashlytics;
import com.uhungry.session.SessionManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(SplashActivity.this);

                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(SplashActivity.this, WellcomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.putExtra("from","Splash");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }

            }
        }, 2500);
    }

}
