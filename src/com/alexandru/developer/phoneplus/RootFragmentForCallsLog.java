/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;


import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

// Fake fragment used to define a container for the index fragment.
// I need this in order to replace correctly the index with the contacts list.

public class RootFragmentForCallsLog extends Fragment{

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

        rootFragment=(ViewGroup)inflater.inflate(R.layout.root_frame_for_calls_log, container, false);

        FragmentTransaction transaction=this.getFragmentManager().beginTransaction();

        CallsLogFragment callsLogFragment=new CallsLogFragment();
        callsLogFragment.setComponents(tts);

        transaction.replace(R.id.root_frame2, callsLogFragment);
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
