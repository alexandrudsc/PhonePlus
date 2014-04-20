/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class EndCallListener extends PhoneStateListener {

	private boolean isPhoneCalling = false;
	private Context context;
	final String LOG_TAG = "LOGGING 123";

	private Activity activity;
	
	public EndCallListener(Context context){
		super();
		this.activity=activity;
		this.context=context;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

		if (TelephonyManager.CALL_STATE_RINGING == state) {
			// phone ringing
			Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			
		}

		if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
			// active
			Log.i(LOG_TAG, "OFFHOOK");
			
			isPhoneCalling = true;
		}

		if (TelephonyManager.CALL_STATE_IDLE == state) {
			// run when class initial and phone call ended, 
			// need detect flag from CALL_STATE_OFFHOOK
			Log.i(LOG_TAG, "IDLE");

			if (isPhoneCalling) {

				Log.i(LOG_TAG, "restart app");
				
				Intent intent=new Intent(context, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setAction(Intent.ACTION_MAIN);
				isPhoneCalling = false;
				context.startActivity(intent);
				
				
			}

		}
	}
}
