package com.example.bluetoothnetwork.util;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * implement data , which is used for notifying all reachable users the fact of edge breaks 
 */
public class RemoveNeighborData implements Data{
	private static final long serialVersionUID = -5905212656747057901L;
	private User sender;
	private User receiver;
		
    private User removed;
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
	
	public RemoveNeighborData(User sender, User receiver, User removed){
		this.sender = sender;
		this.receiver = receiver;
		this.removed = removed;
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
	
	public User getToRemove(){
		return this.removed;
	}
	
	public int getType(){
		return Data.REMOVENEIGHBOR;
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