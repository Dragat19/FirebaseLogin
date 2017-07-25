package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by albertsanchez on 21/7/17.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //Flags
    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_GOOGLE_CODE = 1;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleApiClient googleApiClient;
    private AuthenticationFirebase firebase;

    //View
    private ImageView imgLogo;
    private TextInputLayout etEmail, etPassword;
    private Button btnLogin;
    private TextView btnSignUp;
    private RelativeLayout btnGoogle;

    //Facebook
    private SimpleFacebook mSimpleFacebook;
    private RelativeLayout btnFacebook;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        //Cast
        imgLogo = (ImageView)findViewById(R.id.img_logo);
        etEmail = (TextInputLayout) findViewById(R.id.login_email);
        etPassword = (TextInputLayout) findViewById(R.id.login_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (TextView) findViewById(R.id.txSignup);
        btnGoogle = (RelativeLayout) findViewById(R.id.btn_signin_google);
        btnFacebook = (RelativeLayout) findViewById(R.id.btn_facebook);

        /* Inicializacion Facebook */
        firebase = new AuthenticationFirebase(this);

        /* Inicializacion Google */
        initGoogle();


        /* Implementando View */
        final String strEmail = etEmail.getEditText().getText().toString();
        final String strPassword = etPassword.getEditText().getText().toString();

        Picasso.with(this).load("http://apps.playtown.com.ar/set/public/landing/assets/images/logo2.png")
                        .centerCrop()
                        .resize(250,250)
                        .into(imgLogo);

        /* Listener */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.loginEmail(LoginActivity.this,firebaseAuth,strEmail,strPassword);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.signEmail(LoginActivity.this,firebaseAuth,strEmail,strPassword);
            }
        });


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.loginFacebook(LoginActivity.this,firebaseAuth,mSimpleFacebook);
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_GOOGLE_CODE) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            firebase.signInGoogleFirebase(LoginActivity.this,firebaseAuth,googleSignInResult);
        } else {
            mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void initGoogle(){

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.w(TAG, "onAuthStateChanged - Sign_in " + user.getUid());
                    Log.w(TAG, "onAuthStateChanged - Sign_in " + user.getEmail());
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
