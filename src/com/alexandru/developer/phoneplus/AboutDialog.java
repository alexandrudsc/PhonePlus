/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Alexandru on 3/19/14.
 */
public class AboutDialog extends Dialog {

    private Context mContext;

    public AboutDialog(Context context) {
        super(context);
        this.mContext=context;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LinearLayout ll=(LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.about_dialog_layout, null);
        setContentView(ll);
    }

    @Override
    public void onAttachedToWindow (){
        super.onAttachedToWindow();
        ImageView imageView=(ImageView)this.findViewById(R.id.gplus_account);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse("https://plus.google.com/u/0/105620305121883379160/posts");
                Intent intent=new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });
    }

}
