/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;

/**
 * Created by Alexandru on 3/1/14.
 */
public class ViewPagerChangePageListener implements ViewPager.OnPageChangeListener {

    private TextToSpeech tts;

    private Context context;

    public ViewPagerChangePageListener(TextToSpeech tts){
        this.tts=tts;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        //There is sensor off mode.Check that
        if(MainActivity.sensorOn)
            if(i==0){
                CallsLogFragment.unregisterSensor();

                //Test not to double register a sensor
                if(!MainActivity.isContactsFromIndexShown)
                    IndexFragment.registerSensor();
                tts.speak(MainActivity.Index_TR, TextToSpeech.QUEUE_FLUSH, null);
            }else{
                IndexFragment.unregisterSensor();

                if(!MainActivity.isContactsFromCallsShown)
                    CallsLogFragment.registerSensor();
                tts.speak(MainActivity.MissedCalls_TR, TextToSpeech.QUEUE_FLUSH, null);
            }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
