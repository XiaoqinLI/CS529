package com.example.bluetoothnetwork.util;
import java.util.List;
import java.util.UUID;


public class AddNeighborData implements Data{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6136867753139319954L;
	private User sender;
	private User receiver;
		
    private List<User> neighbors;
    
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
	
	public AddNeighborData(User sender, User receiver, List<User> neighbors){
		this.sender = sender;
		this.receiver = receiver;
		this.neighbors = neighbors;
		this.uuid = UUID.randomUUID();
		
	}


	public List<User> getNeighbors(){
		return this.neighbors;
	}
	
	public int getType(){
		return Data.ADDNEIGHBOR;
	}

	@Override
	public UUID getUUID() {
		// TODO Auto-generated method stub
		return this.uuid;
	}
	
}
