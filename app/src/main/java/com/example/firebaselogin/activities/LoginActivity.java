package com.example.firebaselogin.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaselogin.R;
import com.example.firebaselogin.SetApplication;
import com.example.firebaselogin.utils.AuthenticationFirebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by albertsanchez on 21/7/17.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener , FirebaseAuth.AuthStateListener {

    //Flags
    private static final String TAG = "LoginActivity";
    private static final int SIGN_IN_GOOGLE_CODE = 1;

    //Firebase
    private GoogleApiClient googleApiClient;

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
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        etEmail = (TextInputLayout) findViewById(R.id.login_email2);
        etPassword = (TextInputLayout) findViewById(R.id.login_password2);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (TextView) findViewById(R.id.txSignup);
        btnGoogle = (RelativeLayout) findViewById(R.id.btn_signin_google);
        btnFacebook = (RelativeLayout) findViewById(R.id.btn_facebook);

        /* Inicializacion Firebase */
        initFirebase();


        /* Implementando View */
        Picasso.with(this).load("http://apps.playtown.com.ar/set/public/landing/assets/images/logo2.png")
                .centerCrop()
                .resize(250, 250)
                .into(imgLogo);


        /* Listener */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strEmail = etEmail.getEditText().getText().toString();
                String strPassword = etPassword.getEditText().getText().toString();
                if (!validateEmail(strEmail)) {
                } else {
                    if (!validatePass(strPassword)) {
                    } else {
                        SetApplication.authenticationFirebase.loginEmail(LoginActivity.this, strEmail, strPassword);
                    }
                }
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(LoginActivity.this, imgLogo, "logo_login");
                startActivity(i, options.toBundle());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(SetApplication.authenticationFirebase.googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetApplication.authenticationFirebase.loginFacebook(LoginActivity.this, mSimpleFacebook);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
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

    public void initFirebase() {

        //Inicializacion de firebase
        onAuthStateChanged(SetApplication.authenticationFirebase.firebaseAuth);

        //Inicializacion de Google
        SetApplication.authenticationFirebase.listenerInitGoggle(this,this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SIGN_IN_GOOGLE_CODE:
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                SetApplication.authenticationFirebase.signInGoogleFirebase(LoginActivity.this, googleSignInResult);
                break;

            default:
                mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private boolean validateEmail(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalidate e-mail");
            return false;
        }
        etEmail.setError(null);
        return true;
    }

    private boolean validatePass(String pass) {
        if (pass.isEmpty()) {
            etPassword.setError("Empty Password");
            return false;
        }
        etPassword.setError(null);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.w(TAG, "onAuthStateChanged - Sign_in " + user.getUid());
            Log.w(TAG, "onAuthStateChanged - Sign_in " + user.getEmail());
            Intent intent = new Intent(LoginActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.w(TAG, "onAuthStateChanged - Sign_out");
        }
    }
}
