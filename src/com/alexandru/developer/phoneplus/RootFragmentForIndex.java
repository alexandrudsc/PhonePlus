/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

// Fake fragment used to define a container for the index fragment.
// I need this in order to replace correctly the index with the contacts list. 

public class RootFragmentForIndex extends Fragment{

	private TextToSpeech tts;
    private ViewGroup rootFragment;
	public void setComponents(TextToSpeech tts){
		this.tts=tts;
	}


	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
        if(savedInstanceState!=null)
            return (ViewGroup)this.getFragmentManager().getFragment(savedInstanceState, "root_index").getView();

        Log.d("ROOT_FRAGMENT_FOR_INDEX", "creating view:");
		rootFragment=(ViewGroup)inflater.inflate(R.layout.root_frame_for_index, container, false);


        FragmentTransaction transaction=this.getFragmentManager().beginTransaction();

        IndexFragment indexFragment=new IndexFragment();
        indexFragment.setComponents(tts);
        try{
            transaction.replace(R.id.root_frame1, indexFragment);
        }catch (IllegalArgumentException e){

        }
        transaction.commit();

		return rootFragment;
	}

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //Save instance of fragment to use in onCreateView, in case onDestroyView was called
        this.getFragmentManager().putFragment(outState, "root_index", this);
    }

}
