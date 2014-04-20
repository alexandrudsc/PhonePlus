/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.*;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.ImageView;

/**
 * Created by Alexandru on 3/3/14.
 */
public class SettingsActivity extends PreferenceActivity {

    private final String TAG=this.getClass().getCanonicalName().toUpperCase();
    private final String defaultLanguage="en_US", defaultDesign="Right-handed design", defaultSpeechRate="1.0";
    private final int ITEMS_IN_XML_GROUP=5;

    private SharedPreferences.Editor editor;

    private ConnectivityManager connectivityManager ;

    private LayoutInflater inflater;

    private AlertDialog.Builder builder;

    private ImageView imageView;
    private View dialogBackground;

    private Dialog dialog;

    private ListPreference languageListPreference;
    private ListPreference designListPreference;
    private ListPreference speechRatePreference;
    private CheckBoxPreference sensorPreference;
    private CheckBoxPreference touchPreference;
    private CheckBoxPreference ttsPreference;
    private CheckBoxPreference defaultPreference;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);



        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        languageListPreference=(ListPreference)this.findPreference("languages_list");
        designListPreference=(ListPreference)this.findPreference("design");
        speechRatePreference=(ListPreference)this.findPreference("speech_rate");
        sensorPreference=(CheckBoxPreference)this.findPreference("sensor");
        touchPreference=(CheckBoxPreference)this.findPreference("touch");
        ttsPreference=(CheckBoxPreference)this.findPreference("text_to_speech");
        defaultPreference=(CheckBoxPreference)this.findPreference("default_use");

        connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        builder=new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppBaseTheme));
        dialog=new Dialog(getApplicationContext());

        imageView=new ImageView(this);
        imageView.setImageResource(R.drawable.gradient);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));

        dialogBackground=inflater.inflate(R.layout.dialog_layout, null);

        languageListPreference.setOnPreferenceClickListener(new OnLanguagePreferenceListClick());

        languageListPreference.setOnPreferenceChangeListener(new OnLanguagePreferenceListChange());

        designListPreference.setOnPreferenceChangeListener(new OnDesignPreferenceListChange());

        speechRatePreference.setOnPreferenceChangeListener(new OnSpeechRateListChange());

        sensorPreference.setOnPreferenceChangeListener(new OnSensorPreferenceChange());

        touchPreference.setOnPreferenceChangeListener(new OnTouchPreferenceChange());

        ttsPreference.setOnPreferenceChangeListener(new TextToSpeechPreferencesChange());

        if(!ttsPreference.isChecked()){
            languageListPreference.setEnabled(false);
            speechRatePreference.setEnabled(false);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.reset_settings:
                this.setDefaultPreferences(item);
                break;
            case R.id.help_from_settings:
                this.takeTour(item);
                break;
        }
        return true;
    }

    public void takeTour(MenuItem item){
        Intent intent=new Intent(this, TourActivity.class);
        startActivity(intent);
    }

    public void setDefaultPreferences(MenuItem item){

        languageListPreference.setValue(defaultLanguage);
        designListPreference.setValue(defaultDesign);
        speechRatePreference.setValue(defaultSpeechRate);

        ttsPreference.setChecked(true);
        sensorPreference.setChecked(true);
        touchPreference.setChecked(true);
        defaultPreference.setChecked(false);

        editor.putString("language", defaultLanguage);
        editor.putString("design",  defaultDesign);
        editor.putString("speech_rate", defaultSpeechRate);
        editor.putBoolean("sensor", true);
        editor.putBoolean("touch", false);
        editor.putInt("lang_code", 0);
        editor.putBoolean("default_use", false);
        editor.commit();
    }

    private class OnLanguagePreferenceListClick implements Preference.OnPreferenceClickListener{

        @Override
        public boolean onPreferenceClick(Preference preference) {
            //Check connection to internet.Must be able to download languages

            if(!connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                //Create dialog

                builder.setTitle("Turn on Wi-Fi?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //restart settings activity
                                Intent restartIntent=new Intent(getApplicationContext(), SettingsActivity.class);
                                finish();
                                startActivity(restartIntent);
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                                finish();
                                startActivity(intent);

                            }
                        })
                        .setInverseBackgroundForced(true)
                        .setView(dialogBackground)
                        .setCancelable(false);

                AlertDialog dialog=builder.create();

                dialog.setTitle("Turn on Wi-Fi?");
                dialog.setCancelable(false);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });

                dialog.show();
                return false;
            }
            else
                return true;

        }
    }

    private class OnLanguagePreferenceListChange implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            editor.putString("language", o.toString());
            editor.commit();
            String[] lang=inflater.getContext().getResources().getStringArray(R.array.languages_code);
            int i;

            for(i=0;i<lang.length;i++)
                if (lang[i].equals(o.toString())){
                    editor.putInt("lang_code", i);
                    break;
                }

            editor.commit();
            return true;
        }
    }

    private class OnDesignPreferenceListChange implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            editor.putString("design", o.toString());
            editor.commit();
            return true;
        }
    }

    private class OnSpeechRateListChange implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            editor.putString("speech_rate", o.toString());
            editor.commit();
            return true;
        }
    }

    private class OnSensorPreferenceChange implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            boolean sensorOn;
            if(o.toString().equals("true"))
                sensorOn=true;
            else
                sensorOn=false;
            editor.putBoolean("sensor_on", sensorOn);
            editor.commit();
            return true;
        }
    }

    private class OnTouchPreferenceChange implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            boolean touchOn;
            if(o.toString().equals("true"))
                touchOn=true;
            else
                touchOn=false;
            editor.putBoolean("touch_on", touchOn);
            editor.commit();
            return true;
        }
    }

    private class OnDefaultUsePreferenceChanged implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if(o.toString().equals("true"))
                editor.putBoolean("default_use", true);
            else
                editor.putBoolean("default_use", false);
            editor.commit();
            return true;
        }
    }

    private class TextToSpeechPreferencesChange implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if(o.toString().equals("false")){
                languageListPreference.setEnabled(false);
                speechRatePreference.setEnabled(false);

                editor.putBoolean("text_to_speech", false);
            }else{
                languageListPreference.setEnabled(true);
                speechRatePreference.setEnabled(true);
                editor.putBoolean("text_to_speech", true);
            }
            editor.commit();
            return true;
        }
    }

    private class TtsCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked){
                Log.d(TAG, "Tts checked");
                /*
                languageListPreference.setEnabled(true);*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        speechRatePreference.setEnabled(true);
                    }
                });
                editor.putBoolean("text_to_speech", true);
                }
            else{
                Log.d(TAG, "Tts unchecked");
                /*
                languageListPreference.setEnabled(false);
                speechRatePreference.setEnabled(false);*/
                editor.putBoolean("text_to_speech", false);
            }
        }
    }
}
