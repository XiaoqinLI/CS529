package com.example.bluetoothnetwork.util;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
public class TopologyData implements Data {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5954890114173062251L;
	private User sender;
	private User receiver;
	private Set<User> directNeighbors;
	private UUID uuid;
	private long time;
	private Set<User> footprint = new HashSet<User>();
	
	public TopologyData(User sender, User receiver, Set<User> directNeighbors){
		this.sender = sender;
		this.receiver = receiver;
		this.directNeighbors = directNeighbors;
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
	
	@Override
	public User getSender() {
		// TODO Auto-generated method stub
		return sender;
	}
	@Override
	public User getReceiver() {
		// TODO Auto-generated method stub
		return receiver;
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return Data.TOPOLOGY;
	}
	@Override
	public UUID getUUID() {
		// TODO Auto-generated method stub
		return uuid;
	}
	@Override
	public long getTime() {
		// TODO Auto-generated method stub
		return time;
	}
	
	public Set<User> getDirectNeighbors(){
		return this.directNeighbors;
	}
}