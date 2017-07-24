package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnLoginListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_GOOGLE_CODE = 2;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextInputLayout etEmail, etPassword;
    private Button btnLogin;
    private TextView btnSignUp;
    private ImageView imgLogo;
    private GoogleApiClient googleApiClient;
    private RelativeLayout signInGoogle;

    //Facebook
    private SimpleFacebook mSimpleFacebook;
    private RelativeLayout btnFacebook2;

    Permission[] permissions = new Permission[] {
            Permission.USER_PHOTOS,
            Permission.EMAIL,
            Permission.PUBLISH_ACTION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        etEmail = (TextInputLayout) findViewById(R.id.login_email);
        etPassword = (TextInputLayout) findViewById(R.id.login_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (TextView) findViewById(R.id.txSignup);
        signInGoogle = (RelativeLayout) findViewById(R.id.btn_signin_google);
        btnFacebook2 = (RelativeLayout) findViewById(R.id.btn_facebook);
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        FacebookSdk.sdkInitialize(getApplicationContext());

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.app_id))
                .setNamespace("SomosTenis")
                .setPermissions(permissions)
                .build();

        SimpleFacebook.setConfiguration(configuration);


        Picasso.with(this).load("http://apps.playtown.com.ar/set/public/landing/assets/images/logo2.png")
                .centerCrop()
                .resize(250, 250)
                .into(imgLogo);


        inicializar();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(etEmail.getEditText().getText().toString(), etPassword.getEditText().getText().toString());
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sign(etEmail.getEditText().getText().toString(), etPassword.getEditText().getText().toString());
            }
        });

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        final OnLoginListener onLoginListener = new OnLoginListener() {

            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                // change the state of the button or do whatever you want
                Log.d(TAG, "Logged in: "+ accessToken);
                signInFacebookFirebase(accessToken);

            }

            @Override
            public void onCancel() {
                // user canceled the dialog
            }

            @Override
            public void onFail(String reason) {
                // failed to login
            }

            @Override
            public void onException(Throwable throwable) {
                // exception from facebook
            }

        };

        btnFacebook2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSimpleFacebook.login(onLoginListener);
            }
        });






       /* btnFacebook.setReadPermissions(Arrays.asList("email"));
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
        });*/
    }

    private void inicializar() {

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

    /**
     * Login con Google Firebase
     **/
    private void signInGoogleFirebase(GoogleSignInResult googleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + googleSignInResult.isSuccess());
        if (googleSignInResult.isSuccess()) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Logeado Google con exito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, UserLogin.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "No Logeado Google", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Google SignIn Unsuccess", Toast.LENGTH_SHORT).show();
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
    private void signInFacebookFirebase(String accessToken) {

        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken);

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
                }
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
            signInGoogleFirebase(googleSignInResult);
        } else {
            mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
            //callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
