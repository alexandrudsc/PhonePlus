/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Alexandru on 3/14/14.
 */
public class TourActivity extends FragmentActivity {
    ViewPager viewPager;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_layout);
        viewPager=(ViewPager)this.findViewById(R.id.tour_vp);
        viewPager.setAdapter(new ViewPagerAdapterTour(this.getSupportFragmentManager()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void openGoogleAccount(View view){
        Uri uri=Uri.parse("https://plus.google.com/u/0/105620305121883379160/posts");
        Intent intent=new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void nextTourFragment(View view){
        viewPager.setCurrentItem(1);
    }

    public void finishTour(View view){
        this.finish();
    }



}
