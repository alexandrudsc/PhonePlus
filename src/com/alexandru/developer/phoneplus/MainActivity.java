/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity implements OnInitListener{

	private static String TAG = MainActivity.class.getCanonicalName().toUpperCase();
    private final String defaultLanguage="en_US";
    private final int CHECK_TTS_CODE=1;

    public static String APPEARANCE="Right-handed design";
    public static boolean sensorOn=true;
    public static boolean touchMode=false;

    public static boolean isContactsFromIndexShown=false;
    public static boolean isContactsFromCallsShown=false;

    //Translated strings (those used by the tts).
    public static String Index_TR="Index", MissedCalls_TR="Missed calls", Contacts_TR="Contacts";
    public static String Day_Ago_TR="day ago", Days_Ago_TR="days ago";
    public static String[] TRANSLATIONS;

    private ArrayList< ArrayList<Item>> contactsList=new ArrayList<ArrayList<Item>>();
    private ArrayList<ArrayList<Item>>  missedCallsList=new ArrayList<ArrayList<Item>>();

	private TextToSpeech tts;

	private SensorManager sm;

	private FragmentManager fm;
	private FragmentTransaction transaction;
	private ViewPagerAdapter viewPagerAdapter;

	private ViewPager viewPager;

    private TelephonyManager mTM;
    private EndCallListener endCallListener;


	 // These are the Contacts columns that will be retrieved when querried,
	 // which will be in the order indicated by sortOrder
	static final String[] PROJECTION = new String[] {ContactsContract.Contacts._ID,
	            							ContactsContract.Data.DISPLAY_NAME,
	            							ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.Contacts.PHOTO_ID};

    static final String SELECTION = "( (" +
	            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND ( ( (" +
	            ContactsContract.Data.DISPLAY_NAME + " != '' ) AND  (" +
	            ContactsContract.CommonDataKinds.Phone.TYPE + "=7" +" OR " +
	            ContactsContract.CommonDataKinds.Phone.TYPE + "=2 ) ) ) )";

	    static final String sortOrder=ContactsContract.Data.DISPLAY_NAME +" ASC";


	 // These are the calls columns that we will retrieve, which will be in the order indicated by sortOrder2
	    private final String[] projection={ CallLog.Calls.DATE, CallLog.Calls.DURATION,
											CallLog.Calls.NUMBER, CallLog.Calls._ID, CallLog.Calls.CACHED_NAME,
											CallLog.Calls.TYPE, };
	    private final String selection=null;
	    private final String[] selectionArgs=null;
	    private final String sortOrder2=CallLog.Calls.DATE+" DESC";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true)){
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).commit();
            Intent tourIntent=new Intent(this, TourActivity.class);
            startActivity(tourIntent);
        }

		setContentView(R.layout.loading);


        //Default language
        TRANSLATIONS=this.getResources().getStringArray(R.array.translations);

		fm=this.getSupportFragmentManager();

		//Check if TTS installed
        Intent checkTTS=new Intent();
        checkTTS.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTS, CHECK_TTS_CODE);

		sm=(SensorManager)this.getSystemService(SENSOR_SERVICE);

        //Retrieve the loading image that occupies the entire screen to animate it
        ImageView imageView=(ImageView)this.findViewById(R.id.loading_image);

        Cursor contactsCursor=this.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                               PROJECTION,
                                                SELECTION,
                                                null,
                                                sortOrder);
        if(contactsCursor== null)
            Log.d(TAG, "Query failed");

        Cursor callsLogCursor=getContentResolver().query(CallLog.Calls.CONTENT_URI,
                                                projection,
                                                selection,
                                                selectionArgs,
                                                sortOrder2);

        ContentResolver cr=this.getContentResolver();

        //Create the 2 lists
        ResourcesCreator resourcesCreator=new ResourcesCreator(contactsCursor, callsLogCursor, cr);
        contactsList=resourcesCreator.getContactsList();
        missedCallsList=resourcesCreator.getCallsLogList();

        /*//Create the telephony manager and it's listener
        mTM=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        endCallListener=new EndCallListener(this);
        mTM.listen(endCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        */

        Resources resources=getResources();
        String[] lang=resources.getStringArray(R.array.languages);
        Log.d(TAG, lang[0]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                openSettings(item);
                break;
            case R.id.about:
                openInfo(item);
                break;
            case R.id.help:
                takeTour(item);
                break;
            case R.id.add_contact:
                createContact(item);
                break;
            default:
                break;
        }
        return true;
    }

    /*
    *Altough I register one sensor in IndexFragment's onResume
    *it's ok to initialize also in onResume of the  main activity.
    * It won't register twice.On this activity first onResume viewPager is null
    * because onResume will happen before the change of the main layout.
     */

	@Override
	public void onResume(){
        super.onResume();
        Log.d(TAG,"Reasuming main");

        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        sensorOn=sharedPreferences.getBoolean("sensor_on", true);
        touchMode=sharedPreferences.getBoolean("touch_on", true);
        APPEARANCE=sharedPreferences.getString("design", "Right-handed design");
        boolean defaultUse=sharedPreferences.getBoolean("default_use", false);

        PackageManager packageManager=getPackageManager();
        ComponentName receiver=new ComponentName(this, ReceiverForEndCall.class);

       if(defaultUse)
           packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                   PackageManager.DONT_KILL_APP);
        else
           packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                   PackageManager.DONT_KILL_APP);
        //There is sensor off mode.Check that
        if(viewPager!=null && sensorOn){
            int currentFragment=viewPager.getCurrentItem();
            if(currentFragment==0){
                if(!IndexFragment.isSensorRegistred && !isContactsFromIndexShown)
                    IndexFragment.registerSensor();

                CallsLogFragment.unregisterSensor();
                tts.speak(Index_TR, TextToSpeech.QUEUE_FLUSH, null);
                if(isContactsFromIndexShown)
                    tts.speak(Contacts_TR, TextToSpeech.QUEUE_ADD, null);
            } else{
                Log.d(TAG, "Calls log should be registred");
                IndexFragment.unregisterSensor();

                if(!CallsLogFragment.isSensorRegistred &&!isContactsFromCallsShown)
                    CallsLogFragment.registerSensor();

                tts.speak(MissedCalls_TR, TextToSpeech.QUEUE_FLUSH, null);
                if(isContactsFromCallsShown)
                    tts.speak(Contacts_TR, TextToSpeech.QUEUE_ADD, null);
            }
        }

	}

    @Override
    public void onPause(){
        super.onPause();
    }

	@Override
	public void onDestroy(){
		super.onDestroy();
		try{

            tts.stop();
		    tts.shutdown();

        }catch(NullPointerException e){
        }

        try{
            IndexFragment.unregisterSensor();
        }catch(NullPointerException e){
        }

        try{
            CallsLogFragment.unregisterSensor();
        } catch(NullPointerException e){
        }

        isContactsFromCallsShown=isContactsFromIndexShown=false;
        AudioManager audioManager=(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case CHECK_TTS_CODE:
                if(resultCode==TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                    tts = new TextToSpeech(this, this);
                else{
                    //Install tts;
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("Install TextToSpeech service?")
                           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   finish();

                               }
                           })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent installIntent=new Intent();
                                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                    installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(installIntent);
                                    finish();
                                }
                            })
                            .setCancelable(false);

                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
        }
    }

	@Override
	public void onInit(int status) {
		if(status==TextToSpeech.SUCCESS){

            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

            String language=sharedPreferences.getString("language", defaultLanguage);
            String speech_rate=sharedPreferences.getString("speech_rate", "1.0");

            Boolean ttsVolumeOn=sharedPreferences.getBoolean("text_to_speech", true);



            Log.d(TAG, "TTS "+ttsVolumeOn);
            AudioManager audioManager= (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

            if(ttsVolumeOn)
               audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            else
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            Locale locale=new Locale(language);
            tts.setLanguage(locale);


            tts.setSpeechRate(Float.valueOf(speech_rate));
            Log.d(TAG, "speech rate"+Float.valueOf(speech_rate));

            Animation fade_out= AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
            fade_out.setDuration(500);
            fade_out.setAnimationListener(new FadeOutEndListener(this));
            ImageView imageView=(ImageView)this.findViewById(R.id.loading_image);
            imageView.startAnimation(fade_out);

        }
        else
            if(status==TextToSpeech.LANG_NOT_SUPPORTED){
                Toast warning=Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT);
                warning.show();
            }
    }

    /*
     *The vp intitially contains two fake fragments with the containers for the real fragments(usefull for replacements)
     *The replacement is simple.I use a fake fragment which is actually the container for each main fragment(index and calls log).
     * The fake fragment defines a frame layout with the id root_frame1 or root_frame2
    */
    public void expandList(int position, int fragmentNumber){
        if(position>=0){
            transaction=fm.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ContactsListFragment contactsFragment=new ContactsListFragment();
            ListViewAdapter adapter;

            if(fragmentNumber==0)
                if(APPEARANCE.equals("Left-handed design"))
                    adapter=new ListViewAdapter(this, contactsList.get(position), R.layout.contact_item_layout_left_handed);
                else
                    adapter=new ListViewAdapter(this, contactsList.get(position), R.layout.contact_item_layout);
            else
                if(APPEARANCE.equals("Left-handed design"))
                    adapter=new ListViewAdapter(this, missedCallsList.get(position), R.layout.contact_item_layout_left_handed);
                else
                    adapter=new ListViewAdapter(this, missedCallsList.get(position), R.layout.contact_item_layout);


            tts.speak(Contacts_TR, TextToSpeech.QUEUE_FLUSH, null);

            contactsFragment.setComponents(adapter, tts, sm);
            contactsFragment.setFragmentType(fragmentNumber);
            if(fragmentNumber==0){
                transaction.replace(R.id.root_frame1, contactsFragment);
                isContactsFromIndexShown=true;
            }
            else{
                transaction.replace(R.id.root_frame2, contactsFragment);
                isContactsFromCallsShown=true;
            }
            transaction.addToBackStack("Contacts");

            transaction.commit();
        }
    }

    //Fragment number-it may be usefull knowing on which
    //page of the vp we return
    public void backToMainFragment(int fragmentNumber){
        switch (fragmentNumber){
            case 0:
                isContactsFromIndexShown=false;
                break;
            case 1:
                isContactsFromCallsShown=false;
                break;
        }
        fm.popBackStack();
    }


    public void openSettings(MenuItem item){
        Intent openSettingsIntent=new Intent(this, SettingsActivity.class);
        startActivity(openSettingsIntent);
    }

    public void testMenuItem(MenuItem item){
        Log.d(TAG, "Test OK");
    }


    public void createContact(MenuItem item){
        Log.d(TAG, "Creating contact");
        Intent intent = new Intent(Intent.ACTION_INSERT,
                ContactsContract.Contacts.CONTENT_URI);
        startActivity(intent);
    }

    public void openInfo(MenuItem item){
        AboutDialog dialog=new AboutDialog(this);
        dialog.setCancelable(true);
        dialog.setTitle("Developer:");
        dialog.show();
    }



    public void takeTour(MenuItem item){
        Intent intent=new Intent(this, TourActivity.class);
        startActivity(intent);
    }

    private class FadeOutEndListener implements Animation.AnimationListener{

        private Context context;

        public FadeOutEndListener(Context context){
            this.context=context;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setContentView(R.layout.activity_main);

            viewPager=(ViewPager)findViewById(R.id.view_pager);
            viewPagerAdapter=new ViewPagerAdapter(context, getSupportFragmentManager(), tts);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setOnPageChangeListener(new ViewPagerChangePageListener(tts));

            ViewPagerTabs viewPagerTabs=(ViewPagerTabs)findViewById(R.id.tabs);
            viewPagerTabs.setViewPager(viewPager);
            viewPagerTabs.setTts(tts);
            viewPagerTabs.redraw();

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
