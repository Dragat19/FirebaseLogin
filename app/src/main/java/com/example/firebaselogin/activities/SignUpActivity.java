package com.example.firebaselogin.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.firebaselogin.utils.AuthenticationFirebase;
import com.example.firebaselogin.utils.CustomViewPager;
import com.example.firebaselogin.R;
import com.example.firebaselogin.fragment.SignUpItemFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by albertsanchez on 25/7/17.
 */

public class SignUpActivity extends AppCompatActivity implements SignUpItemFragment.OnListener {

    //Flags
    private static final String TAG = "SignUpActivity";

    //Firebase
    private FirebaseAuth firebaseAuth;
    private AuthenticationFirebase firebase;

    //View and Viewpager
    private RelativeLayout btnNext;
    private ImageView imgLogo;
    private TextView tvNext;
    private CustomViewPager viewPager;
    private SignUpPagerAdapter adapter;
    private ArrayList<String> attrs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Cast
        viewPager = (CustomViewPager) findViewById(R.id.signup_viewpager);
        imgLogo = (ImageView) findViewById(R.id.logo_signup);
        btnNext = (RelativeLayout) findViewById(R.id.btnNextPager);
        tvNext = (TextView) findViewById(R.id.next_text);
        firebaseAuth = FirebaseAuth.getInstance();

        Picasso.with(this).load("http://apps.playtown.com.ar/set/public/landing/assets/images/logo2.png")
                .centerCrop()
                .resize(250, 250)
                .into(imgLogo);


        /** Adaptador Viewpager Tour **/
        adapter = new SignUpPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SignUpItemFragment.newInstance(0));
        adapter.addFragment(SignUpItemFragment.newInstance(1));
        adapter.addFragment(SignUpItemFragment.newInstance(2));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        attrs = new ArrayList<>();

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
    public void onRegister(String pass) {
        Log.d(TAG,"Registro "+attrs.get(0)+" "+attrs.get(1)+" "+attrs.get(2)+" "+attrs.get(3)+" "+pass);
        firebase.signEmail(SignUpActivity.this,firebaseAuth,attrs.get(2),pass);
        finish();
    }

    public class SignUpPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> listFragment;

        public void addFragment(Fragment fragment) {
            listFragment.add(fragment);
        }

        public SignUpPagerAdapter(FragmentManager fm) {
            super(fm);
            listFragment = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return listFragment != null ? listFragment.size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

    }

    private void nextPage() {
        int currentPage = viewPager.getCurrentItem();
        int totalPages = viewPager.getAdapter().getCount();

        int nextPage = currentPage + 1;
        if (nextPage == 1) {

        }
        if (nextPage == 2) {
            tvNext.setText("Register");
        }

        viewPager.setCurrentItem(nextPage, true);
    }

    private void previousPage() {
        int currentPage = viewPager.getCurrentItem();
        int previousPage = currentPage - 1;
        tvNext.setText("Siguiente");
        viewPager.setCurrentItem(previousPage, true);
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


}
