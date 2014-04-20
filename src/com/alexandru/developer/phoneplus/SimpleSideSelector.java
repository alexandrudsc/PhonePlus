/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class SimpleSideSelector extends View {

    public final String TAG=this.getClass().getCanonicalName().toUpperCase();

	private static final int BOTTOM_PADDING=2;

    public static int DISTANCE;

	private static float start;
	
	private int currentPosition=0;
	
	private ListView list;

    private TextToSpeech tts;

    private ArrayList<POINT> pointsToBeDrawn=new ArrayList<POINT>();
    private Paint paint;
    private int colorForTrack= Color.GREEN;

	public SimpleSideSelector(Context context) {
	    super(context);
	    init();
	}

	public SimpleSideSelector(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}

	public SimpleSideSelector(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
    }
	
	public void setCorespondingListView(ListView list){
		this.list=list;
	}

    public void setTextToSpeech(TextToSpeech tts){
        this.tts=tts;
    }
	
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw points that were chosen.Or delete them if movement stoped
        try {
            int i;
            for(i=1;i<pointsToBeDrawn.size();i++)
                canvas.drawLine(pointsToBeDrawn.get(i-1).getX(), pointsToBeDrawn.get(i-1).getY(),
                                pointsToBeDrawn.get(i).getX(), pointsToBeDrawn.get(i).getY(), paint);
        }catch (NullPointerException e){
        }
    }

	public boolean onTouchEvent(MotionEvent event){
        //Mark the begining of the movement
		if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
			start=event.getRawY();

            //At first touch begin selecting with position 0
            if(currentPosition==0){
                list.requestFocusFromTouch();
                list.setSelection(0);
            }
            ShakeEventListenerForContacts.isSimpleSelectorTouched=true;
        }

        //During movement at every DISTANCE select next or previous contact
		if(event.getActionMasked()==MotionEvent.ACTION_MOVE)
			if( Math.abs( (int)(event.getRawY()-start) )>DISTANCE){
				if(event.getRawY()-start < 0){

                    //Check to not go above the list
					if(currentPosition>0){
                        start=event.getRawY();
						currentPosition--;
						list.requestFocusFromTouch();
						list.setSelection(currentPosition);
					}
				}else{
                    //Check to not go below the list
					if(currentPosition<list.getAdapter().getCount()-1){
						currentPosition++;
                        start=event.getRawY();
						list.requestFocusFromTouch();
						list.setSelection(currentPosition);
					}

                }
                Log.d(TAG, "Position" + currentPosition);
                //Draw finger track over
                float x=event.getX(), y=event.getY();
                pointsToBeDrawn.add(new POINT(x, y));
                this.invalidate();

                Item currentItem= (Item) list.getSelectedItem();
                try{
                    tts.speak(currentItem.toString(), TextToSpeech.QUEUE_FLUSH, null);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
			}
		if(event.getActionMasked()==MotionEvent.ACTION_CANCEL || event.getAction()==MotionEvent.ACTION_UP){
			start=0;
            paint.setColor(getResources().getColor(android.R.color.transparent));
            invalidate();
            pointsToBeDrawn.clear();
            paint.setColor(colorForTrack);
        }
		return true;
	}
	  
	private int getPaddedHeight() {
        return getHeight() - BOTTOM_PADDING;
    }

    public void init(){
        paint=new Paint();
        paint.setColor(colorForTrack);
        paint.setStrokeWidth(20);
    }

    public class POINT{
        private float x,y;

        public POINT(float x, float y){
            this.x=x;
            this.y=y;
        }

        public float getX(){
            return  this.x;
        }

        public float getY() {
            return y;
        }
    }

}
