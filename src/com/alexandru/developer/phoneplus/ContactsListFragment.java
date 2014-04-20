/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ContactsListFragment extends Fragment {

	private ListViewAdapter adapter;
	private ListView list;

	private SimpleSideSelector simpleSideSelector;

    private TextToSpeech tts;

    private SensorManager sm;
    private ShakeEventListenerForContacts shakeEventListenerForContacts;

    //Locate the fragment in the vp (first or second page)
    private int fragmentType;

	public void setComponents(ListViewAdapter adapter, TextToSpeech tts, SensorManager sm){
		this.adapter=adapter;
        this.tts=tts;
        this.sm=sm;
	}

    public ListView getList(){
        return this.list;
    }

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);

        View frag;
        if(MainActivity.APPEARANCE.equals("Left-handed design"))
            frag = inflater.inflate(R.layout.contacts_fragment_layout_left_handed, null);
        else
            frag = inflater.inflate(R.layout.contacts_fragment_layout, null);

        list=(ListView)frag.findViewById(R.id.contacts_list);

		simpleSideSelector=(SimpleSideSelector)frag.findViewById(R.id.simple_side_selector);
		simpleSideSelector.setTextToSpeech(tts);

		list.setAdapter(adapter);

		simpleSideSelector.setCorespondingListView(list);

		return frag;
    }

    @Override
    public void onResume(){
        super.onResume();
        shakeEventListenerForContacts=new ShakeEventListenerForContacts(list,(MainActivity)this.getActivity(), fragmentType);
        Sensor accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //There is sensor off mode.Check that
        if(MainActivity.sensorOn)
            sm.registerListener(shakeEventListenerForContacts, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //There is a touch mode.Check that
        if(MainActivity.touchMode)
            list.setOnItemClickListener(new OnContactClickListener(this.getActivity()));
    }

    @Override
    public void onPause(){
        super.onPause();
        sm.unregisterListener(shakeEventListenerForContacts);
    }

    public void setFragmentType(int fragmentType){
        this.fragmentType=fragmentType;
    }

}
