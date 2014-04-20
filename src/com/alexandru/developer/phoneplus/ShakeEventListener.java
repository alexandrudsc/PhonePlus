/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ListView;

public class ShakeEventListener implements SensorEventListener {

	public static final String TAG=ShakeEventListener.class.getCanonicalName().toUpperCase();

    private final double limitValueForGravity=3.4;
    private final int timeBetweenTwoEvents=40;

    private int fragmentType;       //0-index fragment, 1-missed calls fragment

	private float[] accelValues;
    private float[] gravityValues=new float[3];

    private long lastUpdate=0, currentTime=0;

    //Alpha=1/(t+dt); high-pass filter
    public static final float ALPHA= (float)0.8;

    private ListView list;
	
	private MainActivity activity;

    private TextToSpeech tts;

    private SensorManager sm;

    //If
	public boolean contactsFragmentVisible=false;

	public ShakeEventListener(ListView list, MainActivity activity){
		this.list=list;
		this.activity=activity;
        Log.d(TAG, "Shake event listener created");
	}

    public void setComponents(TextToSpeech tts,  SensorManager sm){
        this.tts=tts;
        this.sm=sm;
    }

    public void setFragmentType(int fragmentType){
        this.fragmentType=fragmentType;
    }
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
        currentTime=System.currentTimeMillis();

        //Consider only shake events occured at  60 milliseconds
        if(currentTime-lastUpdate>timeBetweenTwoEvents){

            lastUpdate=currentTime;

            switch(event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    accelValues=event.values.clone();
                    break;
            }

            gravityValues[0]=ALPHA*gravityValues[0]+(1-ALPHA)*accelValues[0];
            gravityValues[1]=ALPHA*gravityValues[0]+(1-ALPHA)*accelValues[1];
            gravityValues[2]=ALPHA*gravityValues[0]+(1-ALPHA)*accelValues[2];

            Log.d(TAG, ""+gravityValues[1]+" "+fragmentType);
            if(gravityValues[0]<(-1)*limitValueForGravity){

                Log.d(TAG, ""+list.getSelectedItemPosition());

                activity.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if(!contactsFragmentVisible){
                            try{
                                int selectedPosition=list.getSelectedItemPosition();
                                if(selectedPosition>=0){
                                    activity.expandList(selectedPosition, fragmentType);
                                    contactsFragmentVisible=true;
                                }
                            }catch(ArrayIndexOutOfBoundsException e){
                                e.printStackTrace();
                            }

                        }
                    }
                });

            }else
                ;
        }
	}
}
