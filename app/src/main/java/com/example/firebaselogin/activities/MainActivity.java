package com.example.firebaselogin.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.firebaselogin.R;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private ImageView img;
    private GlideDrawableImageViewTarget imageViewTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        Glide.with(this).load(R.drawable.splash).into(imageViewTarget);

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, img, "logo_login");
                startActivity(intent, options.toBundle());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        }, 4000);
    }

    @Override
    protected void initViews() {
        img = (ImageView) findViewById(R.id.img_logo);
        imageViewTarget = new GlideDrawableImageViewTarget(img, 1);
    }

    @Override
    protected void initListeners() {

    }
}
