package com.example.bluetoothnetwork.util;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
public class AddNeighborData implements Data{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6136867753139319954L;
	private User sender;
	private User receiver;
		
    private User neighbor;
    private UUID uuid;
    private long time;
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
	
	public AddNeighborData(User sender, User receiver, User neighbor){
		this.sender = sender;
		this.receiver = receiver;
		this.neighbor = neighbor;
		this.uuid = UUID.randomUUID();
		this.time = System.currentTimeMillis();
		this.footprint.add(sender);
	
	}
	
	public Boolean checkFootprint(User current){
		if (this.footprint.contains(current))
			return true;
		else{
			this.footprint.add(current);
			return false;
		}
	}
	public User getNeighbor(){
		return this.neighbor;
	}
	
	public int getType(){
		return Data.ADDNEIGHBOR;
	}
	@Override
	public UUID getUUID() {
		return this.uuid;
	}
	@Override
	public long getTime() {
		return time;
	}
	
}