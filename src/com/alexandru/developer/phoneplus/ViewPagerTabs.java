/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.Gravity.CENTER;


/**
 * Created by Alexandru on 2/22/14.
 */
public class ViewPagerTabs extends LinearLayout {

    public final String TAG=this.getClass().getCanonicalName();

    private Context context;

    private LinearLayout tabs;

    private AttributeSet attrs;

    private ViewPager viewPager;

    private String[] tabsTitles;

    private int tabBackground=R.drawable.background_tab;

    private int textColor=Color.BLACK;

    private int totalLength;

    private int NUM_PAGES;

    private TextToSpeech tts;

    public ViewPagerTabs(Context context){
        super(context);
        this.context=context;
    }

    public ViewPagerTabs(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context=context;
        this.attrs= attrs;
    }

    @Override
    public void onDraw(Canvas canvas){
        this.setOrientation(LinearLayout.HORIZONTAL);
        super.onDraw(canvas);
    }

    public void redraw(){

        int i;
        Log.d(TAG, "Drawing");
        for(i=0;i<NUM_PAGES;i++){

            TextView textView=new TextView(this.context);
            textView.setWidth(this.getWidth()*tabsTitles[i].length()/totalLength);
            textView.setHeight(this.getHeight());
            textView.setTextSize(27);
            textView.setTypeface(null, Typeface.BOLD);

            textView.setGravity(CENTER);

            LayoutParams params=new LayoutParams(
                                                                   LayoutParams.MATCH_PARENT,
                                                                   LayoutParams.MATCH_PARENT,
                                                                   1.0f);

            textView.setLayoutParams(params);

            textView.setText(tabsTitles[i]);
            textView.setTextColor(textColor);
            textView.setBackgroundResource(tabBackground);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView1 = (TextView) view;
                    int i;
                    for (i = 0; i < NUM_PAGES; i++)
                        if (textView1.getText().toString().toLowerCase().equals(
                                viewPager.getAdapter().getPageTitle(i).toString().toLowerCase()))
                            viewPager.setCurrentItem(i, true);
                    try {
                        tts.speak(textView1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    } catch (NullPointerException e) {

                    }
                }
            });

            this.addView(textView);

            //If not last, add a divider
            if( i!=(NUM_PAGES-1)){

                ImageView divider=new ImageView(this.context);

                LayoutParams paramsForDivider=new LayoutParams(
                        3, this.getHeight());

                divider.setLayoutParams(paramsForDivider);

                divider.setBackgroundColor(Color.BLACK);

                this.addView(divider);
            }
            Log.d(TAG, textView.getText().toString() + i);



        }
        this.invalidate();
    }

    public void setViewPager(ViewPager viewPager){
        this.viewPager=viewPager;
        this.setTabsTitles(this.getTabsTitles());
    }

    public void setTts(TextToSpeech tts){
        this.tts=tts;
    }

    public void setTabBackground(int tabBackground){
        this.tabBackground=tabBackground;
    }

    public void setTextColor(int textColor){
        this.textColor=textColor;
    }

    private int calculateTitlesLength(String[] tabsTitles){
        int i, length=0;
        for(i=0;i<tabsTitles.length;i++)
            length+=tabsTitles[i].length();
        return length;
    }

    public void setTabsTitles(String[] tabsTitles){
        this.tabsTitles=tabsTitles;
        totalLength=calculateTitlesLength(this.tabsTitles);
    }

    private String[] getTabsTitles(){
        int i, n;
        NUM_PAGES=this.viewPager.getAdapter().getCount();
        String[] titles=new String[NUM_PAGES];
        for(i=0;i<NUM_PAGES;i++){
            titles[i]=viewPager.getAdapter().getPageTitle(i).toString();
        }
        return titles;
    }

    private void setNumPages(){
        NUM_PAGES=this.viewPager.getAdapter().getCount();
    }

}