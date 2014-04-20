/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
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

public class IndexFragment extends Fragment {

    public final String TAG=this.getClass().getCanonicalName();

	private ListView indexList;

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

        Log.d(TAG, "Creating indexFragment view");

        ViewGroup frag;
        this.context=inflater.getContext();
        if(MainActivity.APPEARANCE.equals("Left-handed design"))
		    frag=(ViewGroup)inflater.inflate(R.layout.index_layout_left_handed, container,  false);
        else
            frag=(ViewGroup)inflater.inflate(R.layout.index_layout, container, false);

        //Create here the items and objects for
        //list and side selector (onPause destroys them after a time).
        items=new ArrayList<Item>();
        objects=new ArrayList<String>();
        char c;

        //Same class for contact item and for index(day of missed call) item.So =>null parameters
        for(c='A';c<='Z';c++){
            items.add(new Item(Character.toString(c), null, c-'A'));
            objects.add(Character.toString(c));
        }

        //Create the sensor manager here
        sm=(SensorManager)this.context.getSystemService(Context.SENSOR_SERVICE);

		ListViewAdapter listAdapter=new ListViewAdapter(this.context, items, R.layout.list_view_item);

		SectionIndexedAdapter sideSelectorAdapter=new SectionIndexedAdapter(this.context,
													R.layout.list_view_item,
													objects);


		indexList=(ListView)frag.findViewById(R.id.index_list);
        indexList.setAdapter(listAdapter);

        if(MainActivity.touchMode)
            indexList.setOnItemClickListener(new OnItemFromGroupClickListener((MainActivity) getActivity(), 0));

        shakeEventListener=new ShakeEventListener(indexList, (MainActivity)this.getActivity());
        accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeEventListener.setFragmentType(0);

        //Creation of this=creation of main activity
        //Obviously, this sensor must be registred.Carefull not do register two sensors at once
        //(this may happen when activity is first created).
        //There is sensor off mode.Check that.
        if((!CallsLogFragment.isSensorRegistred && !IndexFragment.isSensorRegistred)  && MainActivity.sensorOn){
            registerSensor();
            Log.d(TAG, "sensor registered from fragment");
        }

		sideSelector=(SideSelector)frag.findViewById(R.id.side_selector);

		ListView sideSelectorList=new ListView(this.context);
		sideSelectorList.setAdapter(sideSelectorAdapter);

        sideSelector.setListView(sideSelectorList);
		sideSelector.setCorespondingList(indexList);
		sideSelector.setTextToSpeech(tts);

		return frag;
	}

	@Override
	public void onPause(){
		super.onPause();
		unregisterSensor();
        Log.d(TAG, "Index paused");
	}


    public static void registerSensor(){

        sm.registerListener(shakeEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL );
        isSensorRegistred=true;
    }

    public static void unregisterSensor(){
        sm.unregisterListener(shakeEventListener);
        isSensorRegistred=false;
    }



	private class SectionIndexedAdapter extends ArrayAdapter<String>
										implements SectionIndexer{

		public SectionIndexedAdapter(Context context, int textViewResourceId,
									List<String> objects) {

			super(context, textViewResourceId, objects);
		}

		public Object[] getSections() {

			String[] chars = new String[SideSelector.ALPHABET.length];

			for (int i = 0; i < SideSelector.ALPHABET.length; i++) {
			    chars[i] = String.valueOf(SideSelector.ALPHABET[i]);
			}
			
			return chars;
		}
		
		
		public int getPositionForSection(int i) {
		
			Log.d("INDEXER", "getPositionForSection " + i);
			
			indexList.requestFocusFromTouch();
			indexList.setSelection(i);
			
			return (int) (getCount() * ((float)i/(float)getSections().length));
		}
		
		public int getSectionForPosition(int i) {
			return 0;
		}
	}
	
}
