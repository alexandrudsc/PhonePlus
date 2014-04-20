/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.CalendarContract;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

import java.util.ArrayList;

public class SideSelector extends View {
    private static String TAG = SideSelector.class.getCanonicalName();

    public static char[] ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final int BOTTOM_PADDING = 0;

    private SectionIndexer selectionIndexer = null;
    private ListView list;
    private Paint paint;
    private String[] sections;
    private TextToSpeech tts;
    private ListView corespondingList;

    private ArrayList<POINT> pointsToBeDrawn=new ArrayList<POINT>();
    private Paint paintForTrack;
    private int colorForTrack= Color.GREEN;

    public SideSelector(Context context) {
        super(context);
        init();
    }

    public SideSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //setBackgroundColor(0x99FFFFFF);
        paint = new Paint();
        paint.setColor(0xFFA6A9AA);
        paint.setTextSize(this.getPaddedHeight()/ALPHABET.length );
        paint.setTextAlign(Paint.Align.CENTER);

        paintForTrack=new Paint();
        paintForTrack.setColor(colorForTrack);
        paintForTrack.setStrokeWidth(20);

    }

    public void setTextToSpeech(TextToSpeech tts){
    	this.tts=tts;
    }
    
    public void setListView(ListView _list) {
        list = _list;
        selectionIndexer = (SectionIndexer) _list.getAdapter();

        Object[] sectionsArr = selectionIndexer.getSections();
        sections = new String[sectionsArr.length];
        for (int i = 0; i < sectionsArr.length; i++) {
            sections[i] = sectionsArr[i].toString();
        }

    }

    public void setCorespondingList(ListView list){
    	this.corespondingList=list;
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int y = (int) event.getY();
        float selectedIndex = ((float) y / (float) getPaddedHeight()) * ALPHABET.length;

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (selectionIndexer == null) {
                selectionIndexer = (SectionIndexer) list.getAdapter();

            }
            int position = selectionIndexer.getPositionForSection((int) selectedIndex);

            if (position == -1) {
                return true;
            }

            //Marck new point(add it to track) and draw it
            float xCoord=event.getX(), yCoord=event.getY();
            pointsToBeDrawn.add(new POINT(xCoord, yCoord));
            this.invalidate();

            //If possible set selection in the coresponding list
            if(position<corespondingList.getAdapter().getCount()){
            	
            	list.setSelection(position);

            	corespondingList.setSelection(position);

                try{
                    Log.d(TAG, ""+tts);
                    String name=((Item)corespondingList.getItemAtPosition(position)).toString();
                    tts.speak(name, TextToSpeech.QUEUE_FLUSH, null);

                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }


        }
        if(event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL){
            //Delete the drawings
            paintForTrack.setColor(getResources().getColor(android.R.color.transparent));
            invalidate();
            pointsToBeDrawn.clear();
            paintForTrack.setColor(colorForTrack);
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {

        int i;
        int viewHeight = getPaddedHeight();
        float charHeight = ((float) viewHeight) / (float) ALPHABET.length;

        float widthCenter = getMeasuredWidth() / 2;

        SimpleSideSelector.DISTANCE=this.getPaddedHeight()/ALPHABET.length;

        paint.setTextSize( SimpleSideSelector.DISTANCE);

        //Draw text
        for (i = 0; i < ALPHABET.length; i++) {
            try{
        	if(i==0){
        		canvas.drawText(String.valueOf(sections[i]), widthCenter, charHeight, paint);
            }
        	else
        		canvas.drawText(String.valueOf(sections[i]), widthCenter, charHeight + (i * charHeight), paint);
            }catch (NullPointerException e){

            }
        }

        //Draw points that were chosen.Or delete them if movement stoped
        try {
            for(i=1;i<pointsToBeDrawn.size();i++)
                canvas.drawLine(pointsToBeDrawn.get(i-1).getX(), pointsToBeDrawn.get(i-1).getY(),
                        pointsToBeDrawn.get(i).getX(), pointsToBeDrawn.get(i).getY(), paintForTrack);
        }catch (NullPointerException e){

        }

        super.onDraw(canvas);
    }

    private int getPaddedHeight() {
        return getHeight() - BOTTOM_PADDING;
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
