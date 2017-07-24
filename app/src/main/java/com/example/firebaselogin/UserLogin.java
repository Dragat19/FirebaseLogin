package com.example.firebaselogin;

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

/**
 * Created by albertsanchez on 19/7/17.
 */

public class UserLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "UserLogin";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView tvDataUser;
    private Button btnSingOut;
    private GoogleApiClient googleApiClient;
    private ImageView imgPerfil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        tvDataUser = (TextView)findViewById(R.id.data_user);
        btnSingOut = (Button) findViewById(R.id.btn_signout);
        imgPerfil = (ImageView)findViewById(R.id.imgPhoto);
        inicializar();

        btnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            signOut();
            }
        });
    }

    private void signOut(){
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()){
                    Intent i = new Intent(UserLogin.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    Toast.makeText(UserLogin.this, "Error in Google Sign Out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }


    private void inicializar() {
        firebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    tvDataUser.setText("Id: "+ user.getUid()+"\r\n"+"Email: "+ user.getEmail());
                    Picasso.with(UserLogin.this).load(user.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(imgPerfil);
                } else {
                    Log.w(TAG, "onAuthStateChanged - Sign_out");
                }

            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
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
