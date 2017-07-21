package com.example.firebaselogin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by albertsanchez on 21/7/17.
 */

public class LoginActivity extends AppCompatActivity {
    private ImageView imgLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        imgLogo = (ImageView)findViewById(R.id.img_logo);

        Picasso.with(this).load("http://apps.playtown.com.ar/set/public/landing/assets/images/logo2.png")
                        .centerCrop()
                        .resize(250,250)
                        .into(imgLogo);
    }
}
