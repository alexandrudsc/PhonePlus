/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Alexandru on 3/14/14.
 */
public class ViewPagerAdapterTour extends FragmentStatePagerAdapter {

    private int NUM_PAGES=3;

    public ViewPagerAdapterTour(FragmentManager fm){
        super(fm);

    }

    @Override
    public Fragment getItem(int i) {
        TourFragment tourFragment=new TourFragment();
        tourFragment.setId(i);
        if(i!=0)
            tourFragment.setImageResources(R.drawable.contacts);
        return tourFragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
