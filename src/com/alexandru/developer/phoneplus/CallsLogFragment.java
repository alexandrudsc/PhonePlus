/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandru on 2/16/14.
 */
public class CallsLogFragment extends Fragment {

    private final String TAG=this.getClass().getCanonicalName().toUpperCase();

    private ListView missedCallsList;

    //Objects- to fill the side selectors list.
    //Items -for the real list
    private ArrayList<Item> items;
    private ArrayList<String> objects;

    private Context context;

    private TextToSpeech tts;

    private SideSelector sideSelector;

    private static SensorManager sm;
    private static ShakeEventListener shakeEventListener;
    private static Sensor accelerometer;

    //Useful when testing not to register two sensors at the same time
    public static boolean isSensorRegistred=false;

    public void setComponents(TextToSpeech tts){
        this.tts=tts;
    }

    public void setTextToSpeech(TextToSpeech tts){
        this.tts=tts;
        sideSelector.setTextToSpeech(tts);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        Log.d(TAG, "calls log fragment view created");

        ViewGroup frag;
        this.context=inflater.getContext();
        if(MainActivity.APPEARANCE.equals("Left-handed design"))
            frag = (ViewGroup)inflater.inflate(R.layout.missed_calls_fragment_layout_left_handed, container,  false);
        else
            frag = (ViewGroup)inflater.inflate(R.layout.missed_calls_fragment_layout, container,  false);

        //Create here the items and objects for
        //list and side selector (onPause destroys them after a time).
        items=new ArrayList<Item>();
        objects=new ArrayList<String>();

        int i;
        int languageCode= PreferenceManager.getDefaultSharedPreferences(context).getInt("lang_code", 0);
        for(i=0;i<=25;i++){
            if(languageCode==0 || languageCode==1)
                items.add(new Item(MainActivity.TRANSLATIONS[i+3], null, i));
            else
                items.add(new Item(MainActivity.TRANSLATIONS[(languageCode-1)*29+i+3], null, i));

            objects.add(Integer.toString(i));
        }

        //Create the sensor manager here
        sm=(SensorManager)this.context.getSystemService(Context.SENSOR_SERVICE);

        ListViewAdapter listAdapter=new ListViewAdapter(this.context, items, R.layout.list_view_item);

        SectionIndexedAdapter sideSelectorAdapter=new SectionIndexedAdapter(this.context,
                                                                            R.layout.list_view_item,
                                                                            objects);

        missedCallsList=(ListView)frag.findViewById(R.id.missed_calls_list);
        missedCallsList.setAdapter(listAdapter);

        if(MainActivity.touchMode)
            missedCallsList.setOnItemClickListener(new OnItemFromGroupClickListener((MainActivity) getActivity(), 1));
        shakeEventListener=new ShakeEventListener(missedCallsList, (MainActivity)this.getActivity());
        accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeEventListener.setFragmentType(1);

        //Testing not to register two sensors at once.
        //This may happen when activity is created
        //There is sensor off mode.Check that
        if((!CallsLogFragment.isSensorRegistred && !IndexFragment.isSensorRegistred)  && MainActivity.sensorOn)
           registerSensor();

        sideSelector=(SideSelector)frag.findViewById(R.id.side_selector2);

        ListView sideSelectorList=new ListView(this.context);
        sideSelectorList.setAdapter(sideSelectorAdapter);

        sideSelector.setListView(sideSelectorList);
        sideSelector.setCorespondingList(missedCallsList);
        sideSelector.setTextToSpeech(tts);

        return frag;
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterSensor();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //unregisterSensor();
    }

    public static void registerSensor(){
        sm.registerListener(shakeEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        isSensorRegistred=true;
    }

    public static void unregisterSensor(){
        sm.unregisterListener(shakeEventListener);
        isSensorRegistred=false;
    }


    private class SectionIndexedAdapter extends ArrayAdapter<String>
            implements SectionIndexer {

        public SectionIndexedAdapter(Context context, int textViewResourceId,
                                     List<String> objects) {

            super(context, textViewResourceId, objects);
        }

        public Object[] getSections() {

            String[] days = new String[26];

            for (int i = 0; i < 26; i++) {
                days[i] = String.valueOf(i);
            }

            return days;
        }


        public int getPositionForSection(int i) {

            Log.d("INDEXER", "getPositionForSection " + i);

            missedCallsList.requestFocusFromTouch();
            missedCallsList.setSelection(i);

            return (int) (getCount() * ((float)i/(float)getSections().length));
        }

        public int getSectionForPosition(int i) {
            return 0;
        }
    }

}
