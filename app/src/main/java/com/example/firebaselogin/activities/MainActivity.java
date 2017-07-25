package com.example.firebaselogin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.firebaselogin.R;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private ImageView img;
    GlideDrawableImageViewTarget imageViewTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewTarget = new GlideDrawableImageViewTarget(img, 1);
        Glide.with(this).load(R.drawable.splash).into(imageViewTarget);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, img,"logo_login");
                startActivity(intent,options.toBundle());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        }, 2000);
    }

}
