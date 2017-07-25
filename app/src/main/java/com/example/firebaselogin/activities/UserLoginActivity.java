package com.example.firebaselogin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaselogin.R;
import com.example.firebaselogin.utils.AuthenticationFirebase;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by albertsanchez on 19/7/17.
 */

public class UserLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //Flags
    private static final String TAG = "UserLoginActivity";

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleApiClient googleApiClient;
    private AuthenticationFirebase firebase;

    private SimpleFacebook mSimpleFacebook;

    //View
    private TextView tvDataUser;
    private Button btnSingOut;
    private ImageView imgPerfil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //Cast
        tvDataUser = (TextView) findViewById(R.id.data_user);
        btnSingOut = (Button) findViewById(R.id.btn_signout);
        imgPerfil = (ImageView) findViewById(R.id.imgPhoto);

        /* Inicializacion Google */
        initGoogle();

        /* Listener */
        btnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.logoutFacebok(UserLoginActivity.this, mSimpleFacebook);
                firebase.signOutFirebase(UserLoginActivity.this, firebaseAuth, googleApiClient);
            }
        });
    }

    private void signOut() {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Intent i = new Intent(UserLoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(UserLoginActivity.this, "Error in Google Sign Out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
    }


    public void initGoogle() {

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
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
        };

        //Inicializacion de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
