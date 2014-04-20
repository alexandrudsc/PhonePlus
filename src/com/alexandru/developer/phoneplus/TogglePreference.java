/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

/**
 * Created by Alexandru on 3/16/14.
 */
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TogglePreference extends Preference {

    private ToggleButton btn;

    public TogglePreference(Context context) {
        super(context);
        btn = new ToggleButton(context);
    }

    public TogglePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        btn = new ToggleButton(context);
    }

    public TogglePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        btn = new ToggleButton(context);
    }

    public View getView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new LinearLayout(getContext());
            ((LinearLayout) convertView)
                    .setOrientation(LinearLayout.HORIZONTAL) ;
            convertView.setPadding(25, 0, 0, 25);
            TextView txtInfo = new TextView(getContext());
            txtInfo.setTextSize(20);

            txtInfo.setText(this.getSummary().toString());

            ((LinearLayout) convertView).addView(txtInfo,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));



            ((LinearLayout) convertView).addView(btn);
        }

        return convertView;
    }

    public void setOnChangeCheckedListener(CompoundButton.OnCheckedChangeListener l){
        this.btn.setOnCheckedChangeListener(l);
    }
}
