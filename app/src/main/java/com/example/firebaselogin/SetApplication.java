package com.example.firebaselogin;

import android.app.Application;

import com.example.firebaselogin.utils.AuthenticationFirebase;

/**
 * Created by albertsanchez on 26/7/17.
 */

public class SetApplication extends Application {

    public static AuthenticationFirebase authenticationFirebase;
    private static SetApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticationFirebase = AuthenticationFirebase.getInstance(this);
    }
}
