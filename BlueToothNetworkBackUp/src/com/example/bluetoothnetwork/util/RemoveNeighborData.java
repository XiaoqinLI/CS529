package com.example.bluetoothnetwork.util;

import java.util.UUID;


public class RemoveNeighborData implements Data{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5905212656747057901L;
	private User sender;
	private User receiver;
		
    private User removed;
    private UUID uuid;
	
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
	
	public RemoveNeighborData(User sender, User receiver, User removed){
		this.sender = sender;
		this.receiver = receiver;
		this.removed = removed;
		this.uuid = UUID.randomUUID();
	}


	public User getToRemove(){
		return this.removed;
	}
	
	public int getType(){
		return Data.REMOVENEIGHBOR;
	}

	@Override
	public UUID getUUID() {
		// TODO Auto-generated method stub
		return uuid;
	}
	
}
