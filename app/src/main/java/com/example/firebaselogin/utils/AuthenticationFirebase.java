package com.example.firebaselogin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.firebaselogin.R;
import com.example.firebaselogin.activities.LoginActivity;
import com.example.firebaselogin.activities.UserLoginActivity;
import com.example.firebaselogin.activities.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import java.util.List;

/**
 * Created by albertsanchez on 25/7/17.
 */

public class AuthenticationFirebase {

    private static Permission[] permissions = new Permission[]{
            Permission.USER_PHOTOS,
            Permission.EMAIL,
            Permission.PUBLISH_ACTION
    };

    private Context ctx;
    private static final String TAG = "AuthenticationFirebase";

    public AuthenticationFirebase(Context ctx) {
        this.ctx = ctx;

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(ctx.getString(R.string.app_id))
                .setNamespace("SomosTenis")
                .setPermissions(permissions)
                .build();

        SimpleFacebook.setConfiguration(configuration);

    }


    /**
     * Login con Email Firebase
     **/
    public static void loginEmail(final Context ctx, FirebaseAuth firebaseAuth, String Email, String Password) {

        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener((Activity) ctx, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete: " + task.isSuccessful());
                if (task.isSuccessful()) {
                    Toast.makeText(ctx, "Login Authentication Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ctx, UserLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);
                } else {
                    Toast.makeText(ctx, "Login Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Registro con Email Firebase
     **/
    public static void signEmail(final Context ctx, FirebaseAuth firebaseAuth, String Email, String Password) {
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener((Activity) ctx, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ctx, "Signup Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Signup Unsuccess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Registro Google Firebase
     **/
    public static void signInGoogleFirebase(final Context ctx, FirebaseAuth firebaseAuth, GoogleSignInResult googleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + googleSignInResult.isSuccess());
        if (googleSignInResult.isSuccess()) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener((Activity) ctx, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ctx, "Google SignIn success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ctx, UserLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                    } else {
                        Toast.makeText(ctx, "Google Unsuccess", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(ctx, "Google SignIn Unsuccess", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Registro con Facebook
     **/
    public static void loginFacebook(final Context ctx, final FirebaseAuth firebaseAuth, final SimpleFacebook simpleFacebook) {
        simpleFacebook.login(new OnLoginListener() {
            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                Log.d("TokenFacebook ", accessToken);
                AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken);
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener((Activity) ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ctx, "Facebook Authentication Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ctx, UserLoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ctx.startActivity(intent);
                        } else {
                            Toast.makeText(ctx, "Facebook Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel Error");
            }

            @Override
            public void onException(Throwable throwable) {
                Log.d(TAG, "onException Error");
            }

            @Override
            public void onFail(String reason) {
                Log.d(TAG, "onFail Error");
            }

        });
    }


    /**
     * LogOut Facebook
     **/
    public static void logoutFacebok(final Context context, SimpleFacebook simpleFacebook) {
        simpleFacebook.logout(new OnLogoutListener() {
            @Override
            public void onLogout() {

            }

        });
    }

    /**
     * SignOut con Firebase
     **/
    public static void signOutFirebase(final Context ctx, FirebaseAuth firebaseAuth,GoogleApiClient googleApiClient){
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()){
                    Intent intent = new Intent(ctx, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);

                }else {
                    Toast.makeText(ctx, "Error in Google Sign Out", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
