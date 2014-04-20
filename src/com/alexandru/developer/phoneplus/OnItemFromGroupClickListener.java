/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Alexandru on 3/10/14.
 */
public class OnItemFromGroupClickListener implements ListView.OnItemClickListener{

    private MainActivity mainActivity;
    private int fragmentType;

    public OnItemFromGroupClickListener(MainActivity activity, int fragmentType){
        this.mainActivity=activity;
        this.fragmentType=fragmentType;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mainActivity.expandList(position, fragmentType);
    }
}
