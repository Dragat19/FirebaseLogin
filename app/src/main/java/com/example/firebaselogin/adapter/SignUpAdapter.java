package com.example.firebaselogin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by albertsanchez on 27/7/17.
 */

public class SignUpAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> listFragment;

    public SignUpAdapter(FragmentManager fm) {
        super(fm);
        listFragment = new ArrayList<>();
    }

    public void addFragment(Fragment fragment) {
        listFragment.add(fragment);
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
