/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter{

	private final int NUM_PAGES=2;

	private RootFragmentForIndex fragment;

    private RootFragmentForCallsLog fragmentForCallsLog;

	private TextToSpeech tts;

	public ViewPagerAdapter(Context context, FragmentManager fm, TextToSpeech tts) {
		super(fm);

        //Import correct words.For details, look in arrays.xml file (translations)
        int languageCode= PreferenceManager.getDefaultSharedPreferences(context).getInt("lang_code", 0);
        if(languageCode==0 || languageCode==1){
            MainActivity.Index_TR= MainActivity.TRANSLATIONS[0];
            MainActivity.MissedCalls_TR= MainActivity.TRANSLATIONS[1];
            MainActivity.Contacts_TR= MainActivity.TRANSLATIONS[2];
        }else{
            MainActivity.Index_TR= MainActivity.TRANSLATIONS[(languageCode-1)*29];
            MainActivity.MissedCalls_TR= MainActivity.TRANSLATIONS[(languageCode-1)*29+1];
            MainActivity.Contacts_TR= MainActivity.TRANSLATIONS[(languageCode-1)*29+2];
        }

        Log.d("LANGUAGE", " "+languageCode);

		this.tts=tts;
	}

	@Override
	public Fragment getItem(int position) {
		if(position==0){
			fragment=new RootFragmentForIndex();
			fragment.setComponents(tts);
			return fragment;
        }
        else{
            fragmentForCallsLog=new RootFragmentForCallsLog();
            fragmentForCallsLog.setComponents(tts);
            return fragmentForCallsLog;
        }

	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

	@Override
	public int getItemPosition(Object object){
		return 0;
	}

    @Override
    public CharSequence getPageTitle(int position){
        if(position==0)
           return MainActivity.Index_TR;
        else
            return MainActivity.MissedCalls_TR;
    }

}
