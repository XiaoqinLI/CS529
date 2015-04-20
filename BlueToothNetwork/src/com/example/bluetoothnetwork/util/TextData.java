package com.example.bluetoothnetwork.util;

import java.util.HashSet;
import java.util.Set;

public class TextData implements Data {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6666678786504404906L;
	private User sender;
	private User receiver;
		
    private String text;
    private Set<User> footprint = new HashSet<User>();
	
	@Override
	public User getSender() {
		// TODO Auto-generated method stub
		return this.sender;
	}

	@Override
	public User getReceiver() {
		// TODO Auto-generated method stub
		return this.receiver;
	}
	
	public TextData(User sender, User receiver, String text){
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
	
	}
	
	public Boolean checkFootprint(User current){
		if (this.footprint.contains(current))
			return true;
		else{
			this.footprint.add(current);
			return false;
		}
	}


	public String getText(){
		return this.text;
	}
	
	public int getType(){
		return Data.TEXT;
	}

}
