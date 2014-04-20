/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Alexandru on 3/10/14.
 * Click listener for contact.Enabled when touch mode is on
 */
public class OnContactClickListener implements ListView.OnItemClickListener {

    private final String TAG=this.getClass().getCanonicalName().toUpperCase();

    private Activity activity;

    public OnContactClickListener(Activity activity){
        this.activity=activity;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        TextView phoneNumber=(TextView)view.findViewById(R.id.phone_number);

        Intent callIntent=new Intent();
        callIntent.setAction(Intent.ACTION_CALL);
        Log.d(TAG, phoneNumber.getText().toString());
        callIntent.setData(Uri.parse("tel:" + phoneNumber.getText()));

        view.getContext().startActivity(callIntent);
        activity.finish();
    }
}
