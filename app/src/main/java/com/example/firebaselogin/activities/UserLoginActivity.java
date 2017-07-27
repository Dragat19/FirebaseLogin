package com.example.firebaselogin.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebaselogin.R;
import com.example.firebaselogin.SetApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by albertsanchez on 19/7/17.
 */

public class UserLoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {

    //Flags
    private static final String TAG = "UserLoginActivity";

    //View
    private TextView tvDataUser;
    private Button btnSingOut;
    private ImageView imgPerfil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        initViews();
        initListeners();

        //Inicializacion de Firebase
        onAuthStateChanged(SetApplication.authenticationFirebase.firebaseAuth);

        //Inicializacion de Google
        SetApplication.authenticationFirebase.listenerInitGoggle(this,this);

    }



    @Override
    protected void initViews() {
        //Cast
        tvDataUser = (TextView) findViewById(R.id.data_user);
        btnSingOut = (Button) findViewById(R.id.btn_signout);
        imgPerfil = (ImageView) findViewById(R.id.imgPhoto);
    }

    @Override
    protected void initListeners() {
         /* Listener */
        btnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetApplication.authenticationFirebase.logoutFacebok(UserLoginActivity.this);
                SetApplication.authenticationFirebase.signOutFirebase(UserLoginActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SetApplication.authenticationFirebase.mSimpleFacebook = SimpleFacebook.getInstance(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        SetApplication.authenticationFirebase.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SetApplication.authenticationFirebase.removeAuthStateListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            tvDataUser.setText("Id: " + user.getUid() + "\r\n" + "Email: " + user.getEmail());
            Picasso.with(UserLoginActivity.this).load(user.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(imgPerfil);
        } else {
            Log.w(TAG, "onAuthStateChanged - Sign_out");
        }
    }


}
