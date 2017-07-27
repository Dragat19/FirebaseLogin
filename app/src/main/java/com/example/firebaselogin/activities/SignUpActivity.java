package com.example.firebaselogin.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.firebaselogin.R;
import com.example.firebaselogin.SetApplication;
import com.example.firebaselogin.adapter.SignUpAdapter;
import com.example.firebaselogin.fragment.SignUpItemFragment;
import com.example.firebaselogin.utils.CustomViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by albertsanchez on 25/7/17.
 */

public class SignUpActivity extends BaseActivity implements SignUpItemFragment.OnListener {

    //Flags
    private static final String TAG = "SignUpActivity";

    //View and Viewpager
    private RelativeLayout btnNext;
    private ImageView imgLogo;
    private TextView tvNext;
    private CustomViewPager viewPager;
    private SignUpAdapter adapter;
    private ArrayList<String> attrs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initListeners();

        Picasso.with(this).load("http://apps.playtown.com.ar/set/public/landing/assets/images/logo2.png")
                .centerCrop()
                .resize(250, 250)
                .into(imgLogo);


        /** Adaptador Viewpager Tour **/
        adapter.addFragment(SignUpItemFragment.newInstance(0));
        adapter.addFragment(SignUpItemFragment.newInstance(1));
        adapter.addFragment(SignUpItemFragment.newInstance(2));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
    }


    @Override
    protected void initViews() {

        viewPager = (CustomViewPager) findViewById(R.id.signup_viewpager);
        imgLogo = (ImageView) findViewById(R.id.logo_signup);
        btnNext = (RelativeLayout) findViewById(R.id.btnNextPager);
        tvNext = (TextView) findViewById(R.id.next_text);
        adapter = new SignUpAdapter(getSupportFragmentManager());
        attrs = new ArrayList<>();
    }

    @Override
    protected void initListeners() {

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tv = tvNext.getText().toString();
                if (tv.equals(getString(R.string.btnNext_signup))) {
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            boolean validateNames = ((SignUpItemFragment) adapter.getItem(0)).validateNames();
                            attrs.addAll(((SignUpItemFragment) adapter.getItem(0)).getAttributes());
                            if (validateNames == true) {
                                nextPage();
                            }
                            break;
                        case 1:
                            Log.d(TAG, "Posicion 1");
                            boolean validateNames2 = ((SignUpItemFragment) adapter.getItem(1)).validateUser();
                            attrs.addAll(((SignUpItemFragment) adapter.getItem(1)).getAttributes());
                            if (validateNames2 == true) {
                                nextPage();
                            }
                            break;

                        default:
                            Log.d(TAG, "def nextPage();");
                            nextPage();
                            break;
                    }
                } else {
                    ((SignUpItemFragment) adapter.getItem(2)).validatePass();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() > 0) {
            previousPage();
        } else {
            supportFinishAfterTransition();
            super.onBackPressed();
        }
    }


    /**
     * Callback
     **/
    @Override
    public void onRegister(String pass) {
        Log.d(TAG, "Registro " + attrs.get(0) + " " + attrs.get(1) + " " + attrs.get(2) + " " + attrs.get(3) + " " + pass);
        SetApplication.authenticationFirebase.signEmail(this, attrs.get(2), pass);
        finish();
    }

    /**
     * Metodos Implementados
     **/
    private void nextPage() {

        int currentPage = viewPager.getCurrentItem();
        int totalPages = viewPager.getAdapter().getCount();

        int nextPage = currentPage + 1;
        if (nextPage == 2) {
            tvNext.setText("Register");
        }
        viewPager.setCurrentItem(nextPage, true);
    }

    private void previousPage() {

        int currentPage = viewPager.getCurrentItem();
        int previousPage = currentPage - 1;
        tvNext.setText(getString(R.string.btnNext_signup));
        viewPager.setCurrentItem(previousPage, true);

    }


}
