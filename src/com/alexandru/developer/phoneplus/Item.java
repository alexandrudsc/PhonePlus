/*
 * Copyright © 2014 Alexandru Dascălu.
 * com.alex.independent.developer
 */

package com.alexandru.developer.phoneplus;

public class Item extends Object {
	
	private String name;
	private String phoneNumber;

    private int contact_id;

    private int id;
	private boolean state;

	public Item(String name, String phoneNumber, int id) {
        this.name=name;
		this.id=id;
        this.phoneNumber=phoneNumber;
	}
	
	@Override
	public String toString(){
		return name;
	}

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

	public boolean getState(){
		return this.state;
	}
	
	public void setState(boolean state){
		this.state=state;
	}
	
	public int getItemId(){
		return this.id;
	}

    public int getContact_id(){
        return this.contact_id;
    }
	
}
