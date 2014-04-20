/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Alexandru on 2/12/14.
 * Contains method that returns lists of contacts grouped by first letter
 * Contains method that returns calls log list grouped by day
 */
public class ResourcesCreator {

    private final String TAG=this.getClass().getCanonicalName().toUpperCase();

    private ArrayList<ArrayList<Item>> contactsList=new ArrayList<ArrayList<Item>>();
    private ArrayList<ArrayList<Item>> callsLogList=new ArrayList<ArrayList<Item>>();
    private ArrayList<String> contactsId=new ArrayList<String>();

    public static ArrayList <PhotoWithContactPositionAndName> bitmapContactPhoto=
                        new ArrayList<PhotoWithContactPositionAndName>();

    private Cursor contactsCursor;
    private Cursor  callsLogCursor;
    private ContentResolver contentResolver;

    public ResourcesCreator(Cursor contacts, Cursor callsLog, ContentResolver contentResolver){
        this.callsLogCursor=callsLog;
        this.contactsCursor=contacts;
        this.contentResolver=contentResolver;
    }

    private void createContactsList(){
        char letter;
        int id=0;
        String[] columns=contactsCursor.getColumnNames();

        Log.d(TAG, columns[3].toString());

        //Check if contactsCursor not void
        if(contactsCursor.moveToFirst()==true){

            String newContactName=contactsCursor.getString(contactsCursor.getColumnIndex(columns[1]));
            String newPhoneNumber=contactsCursor.getString(contactsCursor.getColumnIndex(columns[2]));
            String photoId=contactsCursor.getString(contactsCursor.getColumnIndex(columns[3]));
            bitmapContactPhoto.add(new PhotoWithContactPositionAndName( 0, getContactPhoto(photoId), newContactName) );
            Log.d(TAG+"  photo", " 0"+ photoId);
            contactsId.add(contactsCursor.getString(contactsCursor.getColumnIndex(columns[0])));

            //List to be used to store temporarily the contacts of a single group
            ArrayList<Item> list=new ArrayList<Item>();

            //If there are contacts whose names begin with something else than letters, add them to first group
            while(newContactName.charAt(0)!='A'){
                list.add(new Item(newContactName, newPhoneNumber, id));
                id++;
                Log.d(TAG, newContactName);
                contactsCursor.moveToNext();
                try{
                    newContactName=contactsCursor.getString(contactsCursor.getColumnIndex(columns[1]));
                    newPhoneNumber=contactsCursor.getString(contactsCursor.getColumnIndex(columns[2]));
                    contactsId.add(  contactsCursor.getString(contactsCursor.getColumnIndex(columns[0])) );

                    photoId=contactsCursor.getString(contactsCursor.getColumnIndex(columns[3]));
                    bitmapContactPhoto.add(new PhotoWithContactPositionAndName( id, getContactPhoto(photoId), newContactName) );

                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }

            }
            for(letter='A';letter<='Z'; letter++){              //The groups
                int contactNumberInList=0;
                     //Group coresponding to the current letter
                while(!contactsCursor.isAfterLast() && newContactName.charAt(0)==letter){
                    list.add(contactNumberInList, new Item(newContactName, newPhoneNumber, id));
                    id++;
                    contactNumberInList++;
                    contactsCursor.moveToNext();
                    try{
                        newContactName=contactsCursor.getString(contactsCursor.getColumnIndex(columns[1]));
                        newPhoneNumber=contactsCursor.getString(contactsCursor.getColumnIndex(columns[2]));
                        contactsId.add(  contactsCursor.getString(contactsCursor.getColumnIndex(columns[0])) );

                        photoId=contactsCursor.getString(contactsCursor.getColumnIndex(columns[3]));
                        bitmapContactPhoto.add(new PhotoWithContactPositionAndName( id, getContactPhoto(photoId), newContactName) );

                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                }
                contactsList.add(letter - 'A', list);
                list=new ArrayList<Item>();
            }
        }
        contactsCursor.close();

        }

    private void createCallsLogList(){
        int day, id=0;
        long dayToNanoseconds= (long) 864e4;
        long currentTime=System.currentTimeMillis();

        String[] columns=callsLogCursor.getColumnNames();

        //check if callsLogCursor not void
        if(callsLogCursor.moveToFirst()==true){
            String newContact=callsLogCursor.getString(callsLogCursor.getColumnIndex(columns[4]) );
            String newNumber=callsLogCursor.getString(callsLogCursor.getColumnIndex(columns[2]));
            long time=callsLogCursor.getLong(callsLogCursor.getColumnIndex(columns[0]));

            for(day=1; day<=26;day++){
                ArrayList<Item> list=new ArrayList<Item>();
                while(!callsLogCursor.isAfterLast() && currentTime-time<=day* dayToNanoseconds){

                    list.add( new Item(newContact, newNumber, id));
                    id++;
                    callsLogCursor.moveToNext();
                    try{
                        newContact=callsLogCursor.getString(callsLogCursor.getColumnIndex(columns[4]));
                        newNumber=callsLogCursor.getString(callsLogCursor.getColumnIndex(columns[2]));
                        time=callsLogCursor.getLong(callsLogCursor.getColumnIndex(columns[0]));
                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                }

                this.callsLogList.add(day-1, list);
            }
        }
        callsLogCursor.close();
    }

    public ArrayList<ArrayList<Item>> getContactsList(){
        createContactsList();
        return this.contactsList;
    }

    public ArrayList<ArrayList<Item>> getCallsLogList(){
        createCallsLogList();
        return  this.callsLogList;
    }

    //Retrieve the byte-array holding photo and convert it into Bitmap
    public Bitmap getContactPhoto(String PhotoID) {

        Uri photoUri;
        try {
             photoUri= ContentUris.withAppendedId(
                ContactsContract.Data.CONTENT_URI, Long.parseLong(PhotoID));
        }catch (NumberFormatException e){
            return null;
        }

        AssetFileDescriptor assetFileDescriptor;
        try {
            assetFileDescriptor=contentResolver.openAssetFileDescriptor(photoUri, "r");
        }catch (FileNotFoundException e){
            return null;
        }
        FileDescriptor fileDescriptor=assetFileDescriptor.getFileDescriptor();

        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, null);
        return bitmap;
    }

    public static class PhotoWithContactPositionAndName{
        public int contactPosition;
        public Bitmap photo;
        public String name;

        public PhotoWithContactPositionAndName(int contactPosition, Bitmap bitmap, String name){
            this.contactPosition=contactPosition;
            this.photo=bitmap;
            this.name=name;
        }
    }
}
