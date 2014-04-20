/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alexandru on 3/14/14.
 */
public class TourFragment extends Fragment {

    private int imgRes;
    private int id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup fragment;
        if(id!=0){
            fragment= (ViewGroup) inflater.inflate(R.layout.tour_fragment, null);
            TextView title=(TextView) fragment.findViewById(R.id.tour_frag_title);
            ListView listView=(ListView)fragment.findViewById(R.id.tour_frag_list);
            if(id==1){
                title.setText("Tips");
                String[] tips=getResources().getStringArray(R.array.tips);
                listView.setAdapter(new ArrayAdapter<String>(this.getActivity().getApplicationContext(),
                                        android.R.layout.simple_list_item_1, tips ));
            }else{
                title.setText("Frequently asked questions");
                String[] faq=getResources().getStringArray(R.array.faq);
                listView.setAdapter(new ArrayAdapter<String>(this.getActivity().getApplicationContext(),
                                        android.R.layout.simple_list_item_1, faq));
            }


        }else
            fragment=(ViewGroup) inflater.inflate(R.layout.tour_fragment1, null);

        return fragment;
    }
    public void setImageResources(int imgRes){
        this.imgRes=imgRes;
    }

    public void setId(int id) {
        this.id = id;
    }
}
