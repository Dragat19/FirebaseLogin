package com.example.firebaselogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_GOOGLE_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp;
    private GoogleApiClient googleApiClient;
    private SignInButton signInGoogle;
    private LoginButton btnFacebook;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmail = (EditText) findViewById(R.id.login_email);
        etPassword = (EditText) findViewById(R.id.login_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_sign);
        signInGoogle = (SignInButton) findViewById(R.id.btn_signin_google);
        btnFacebook = (LoginButton) findViewById(R.id.btn_facebook);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        Log.w(TAG, "Callback  " + callbackManager.toString());


        incializar();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sign(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        btnFacebook.setReadPermissions(Arrays.asList("email"));
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w(TAG, "Facebook Login Success Token:  " + loginResult.getAccessToken().getToken());
                signInFacebookFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Facebook Error");
                error.printStackTrace();
            }
        });
    }

    private void incializar() {
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


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Login con Google Firebase
     **/
    private void SignInGoogleFirebase(GoogleSignInResult googleSignInResult) {


        if (googleSignInResult.isSuccess()) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Logeado Google con exito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, UserLogin.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "No Logeado Google", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "No Logeado Google", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Login con Email Firebase
     **/
    private void login(String Email, String Password) {

        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete: " + task.isSuccessful());
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logeado con exito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UserLogin.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No Logeado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Registro con Email Firebase
     **/
    private void Sign(String Email, String Password) {
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Signup con exito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Signup sin exito", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Registro con Facebook
     **/
    private void signInFacebookFirebase(AccessToken accessToken) {

        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Facebook Authentication Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, UserLogin.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Facebook Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, UserLogin.class);
                    startActivity(i);
                }
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SIGN_IN_GOOGLE_CODE) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            SignInGoogleFirebase(googleSignInResult);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
}
