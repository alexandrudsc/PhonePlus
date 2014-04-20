/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter implements ListAdapter{

    private static final String TAG=ListViewAdapter.class.getCanonicalName().toUpperCase() ;

    private ArrayList<Item> list;

    private Context context;

    private int listViewItemBackground;

    LayoutInflater inflater;

    public ListViewAdapter(Context context, ArrayList<Item> list, int listViewItemBackground){
        this.list=list;
        this.context=context;
        this.listViewItemBackground=listViewItemBackground;
        inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        Item item=(Item)list.get(position);
        Log.d(TAG, ""+item.getItemId());
        //return item.getItemId();
        return position;
    }

    @Override
    public int getItemViewType(int arg0) {
        return R.layout.list_view_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(convertView==null){

            view=inflater.inflate(this.listViewItemBackground, null);
        }

        //test to see if index list (calls log list) or contacts list
        TextView contactName, contactPhoneNumber=null;
        QuickContactBadge contactPhoto=null;
        Item contact=list.get(position);
        if(this.listViewItemBackground==R.layout.list_view_item)
            contactName=(TextView)view.findViewById(R.id.text_view);

        else{

            contactName =(TextView)view.findViewById(R.id.contact_name);
            contactPhoneNumber=(TextView)view.findViewById(R.id.phone_number);
            contactPhoto=(QuickContactBadge)view.findViewById(R.id.contact_photo);

            contactPhoto.assignContactFromPhone(contact.getPhoneNumber(), false);

            //Check for the bitmap coresponding to this name.
            Bitmap contactBadgePhoto=null;
            int i;
            for(i=0;i< ResourcesCreator.bitmapContactPhoto.size();i++)
                if(ResourcesCreator.bitmapContactPhoto.get(i).photo!=null
                        && ResourcesCreator.bitmapContactPhoto.get(i).name.equals(contact.toString()) )
                            contactBadgePhoto= ResourcesCreator.bitmapContactPhoto.get(i).photo;

            if(contactBadgePhoto!=null)
                contactPhoto.setImageBitmap(contactBadgePhoto);
            else{
                contactPhoto.setImageResource(R.drawable.contacts);
            }

        }
        contactName.setText(contact.toString());
        try {
            contactPhoneNumber.setText(contact.getPhoneNumber());
        }catch (NullPointerException e){

        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean areAllItemsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position<(list.size());
    }

}
