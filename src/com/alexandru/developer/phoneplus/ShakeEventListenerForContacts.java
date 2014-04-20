/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;

/**
 * Created by Alexandru on 2/16/14.
 */
public class ShakeEventListenerForContacts implements SensorEventListener {

    public static final String TAG=ShakeEventListener.class.getCanonicalName();

    private final double limitValueForGravity=3.4;
    private final int timeBetweenTwoEvents=40;
    private final double limitValueForLinearAcceleration=9;

    //Only permit calling if the contact was selected, not if was selected by default
    public static boolean isSimpleSelectorTouched;

    private long currentTime, lastUpdate=System.currentTimeMillis();

    private float[] accelValues;
    private float[] gravityValues=new float[3];

    MainActivity activity;

    private ListView list;

    private Intent callIntent=null;

    //The fragment's tyep or position in the vp
    private int fragmentType;

    public ShakeEventListenerForContacts(ListView list, MainActivity activity, int fragmentNumber){
        this.list=list;
        this.list.clearFocus();
        this.activity=activity;
        this.fragmentType=fragmentNumber;
        isSimpleSelectorTouched=false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentTime=System.currentTimeMillis();
        if(currentTime-lastUpdate>timeBetweenTwoEvents ){

            lastUpdate=currentTime;

            if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                accelValues=sensorEvent.values.clone();
            }

            gravityValues[0]= ShakeEventListener.ALPHA*gravityValues[0]+(1- ShakeEventListener.ALPHA)*accelValues[0];
            gravityValues[1]= ShakeEventListener.ALPHA*gravityValues[0]+(1- ShakeEventListener.ALPHA)*accelValues[1];
            gravityValues[2]= ShakeEventListener.ALPHA*gravityValues[0]+(1- ShakeEventListener.ALPHA)*accelValues[2];

            if((accelValues[0]>limitValueForLinearAcceleration || accelValues[0]<(-1)*limitValueForLinearAcceleration)
                                    && isSimpleSelectorTouched ){

                Item currentContact=(Item)list.getSelectedItem();
                try{
                    String phoneNumber=currentContact.getPhoneNumber();
                    Log.d(TAG, currentContact.toString());
                    if(callIntent==null){
                        callIntent=new Intent();
                        callIntent.setAction(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+phoneNumber));

                        activity.startActivity(callIntent);
                        activity.finish();
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
            if(gravityValues[0]>limitValueForGravity)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.backToMainFragment(fragmentType);
                    }
                });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
