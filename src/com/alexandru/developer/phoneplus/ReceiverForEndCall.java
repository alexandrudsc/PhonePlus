/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Alexandru on 3/12/14.
 */
public class ReceiverForEndCall extends BroadcastReceiver {

    public  final String TAG=this.getClass().getCanonicalName().toUpperCase();
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w(TAG, state);
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Intent intent1=new Intent(context, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(Build.VERSION.SDK_INT>=11)
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addCategory(Intent.CATEGORY_LAUNCHER);
                context.startActivity(intent1);
            }
        }
    }
}
