package com.example.bluetoothnetwork;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.util.Log;

import com.example.bluetoothnetwork.util.AddNeighborData;
import com.example.bluetoothnetwork.util.Data;
import com.example.bluetoothnetwork.util.Message;
import com.example.bluetoothnetwork.util.RemoveNeighborData;
import com.example.bluetoothnetwork.util.TextData;
import com.example.bluetoothnetwork.util.User;




abstract public class MiddleClass extends LowerClass{
	
	private List<User> userList;
	private User thisUser;
	private Set<UUID> uuidSet = new HashSet<UUID>();
	
	public void init(User thisUser){
		this.userList = new ArrayList<User>();
		this.thisUser = thisUser;
		
//		List<User> dusers = new ArrayList<User>();
//		User user1 = new User("Li Ding", 0);
//		dusers.add(user1);
//		User user2 = new User("Yiding Xu", 1);
//		dusers.add(user2);
//		User user3 = new User("Jieru Song", 2);
//		dusers.add(user3);
//		User user4 = new User("Vincent Li", 3);
//		dusers.add(user4);
//		User user5 = new User("Frank Underwood", 4);
//		dusers.add(user5);
//		User user6 = new User("Water White", 5);
//		dusers.add(user6);
//		for(User temp : dusers){
//			onNewNeighbor(temp);
//		}
		
	}
	
	public byte[] serialize(Data data) throws IOException {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    ObjectOutputStream o = new ObjectOutputStream(b);
	    
	    o.writeObject(data);
	    
	    
	    return b.toByteArray();
	}
	//AbstractMessage was actually the message type I used, but feel free to choose your own type

	
	
	//find out all the reachable users
	abstract void onNewUser(List<User> users);
	abstract void onUserDisconnect(List<User> users);
	//these two, called inside onNewNeighbor, onNeighborLeaving
	abstract void onMessage(Message message); //called inside onReceivingData getAllUsers();

	
	Boolean sendMessage(User user, Message message){ 
		Data data = new TextData(message.getSender(),message.getReceiver(), message.getMessage());
		if (this.userList.contains(user)){
			Set<String> availableDevices = this.getAllConnectedDevices();
			for (String s: availableDevices)
				try {
					this.sendData( s, serialize(data));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return true;
		}
		else{
			Log.i("MiddleClass", "no such user");
			return false;
		}
		
			
	}
	
	
	public List<User> getAllUsers(){
		return this.userList;
		}
	
	
	
	
	@Override
	void onNewNeighbor(User user) {
		// TODO Auto-generated method stub
		this.userList.add(user);
		//List<User> temp = new ArrayList<User>();
		//temp.add(user);
		
		Data neighborData = new AddNeighborData(this.thisUser, null, this.userList);
		
		Set<String> availableDevices = this.getAllConnectedDevices();
		for (String s: availableDevices)
			try {
				this.sendData( s, serialize(neighborData));
			} catch (IOException e) {
				e.printStackTrace();
			}
		//this.onNewUser(temp);
		this.onNewUser(userList);
		
	}

	@Override
	void onNeighborLeaving(User user) {
		// TODO Auto-generated method stub
		if (this.getAllConnectedDevices().isEmpty()){
			
			this.onUserDisconnect(this.userList);
			this.userList.clear();
			return;
		}
		
		
		
		this.userList.remove(user);
		
		
		Data neighborData = new RemoveNeighborData(this.thisUser, null, user);
		Set<String> availableDevices = this.getAllConnectedDevices();
		for (String s: availableDevices)
			try {
				this.sendData( s, serialize(neighborData));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		List<User> userLeaving = new ArrayList<User>();
		userLeaving.add(user);
		this.onUserDisconnect(userLeaving);
		
	}

	@Override
	void onReceivingData(Data data) {
		// TODO Auto-generated method stub
		if (data.getType() == Data.TEXT){
			if (this.checkUUID(data.getUUID())) return;
			
			if (data.getReceiver().equals(this.thisUser) ){
				Message msg = new Message(data.getSender(), data.getReceiver(), ((TextData)data).getText());
				this.onMessage(msg);
				Log.i("MiddleClass", ""+((TextData)data).getText());

			}else if (!((TextData)data).checkFootprint(thisUser)){
				Set<String> availableDevices = this.getAllConnectedDevices();
				for (String s: availableDevices)
					try {
						this.sendData( s, serialize(data));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				Log.i("MiddleClass", ""+"this is for" + data.getReceiver());
			}
		}
		
		
		
		
		if (data.getType() == Data.TOPOLOGY){
			// do not need in flooding strategy
		}
		
		
		
		
		
		if (data.getType() == Data.ADDNEIGHBOR){
			// update neighbors
			List<User> upcomingUsers = ((AddNeighborData)data).getNeighbors();
			
			if (!this.checkEquality(data.getSender(), upcomingUsers)){
				this.onNewUser(this.userList);
				Log.i("userlist#", ""+this.userList.size());
				int sff = 0;
				Set<String> availableDevices = this.getAllConnectedDevices();
				for (String s: availableDevices)
					if (!data.getSender().equals(this.thisUser))
						try {
							this.sendData(s, serialize(data));
						} catch (IOException e) {
						
							e.printStackTrace();
						}
					}
			}
		if (data.getType() == Data.REMOVENEIGHBOR){
			// update neighbors
			User userToRemove = ((RemoveNeighborData)data).getToRemove();
			
			
			
			
			if (this.userList.contains(userToRemove)){
				List<User> userLeaving = new ArrayList<User>();
				userLeaving.add(userToRemove);
				this.onUserDisconnect(userLeaving);
				Set<String> availableDevices = this.getAllConnectedDevices();
				for (String s: availableDevices)
					if (!data.getSender().equals(this.thisUser))
						try {
							this.sendData(s, serialize(data));
						} catch (IOException e) {
						
							e.printStackTrace();
						}
					}
			}
			
		//Log.i("MiddleClass", ""+this.userList.size());
		
		
	}
	
	
	private Boolean checkEquality(User sender, List<User> upcomingUsers){
		
		Boolean result = true;
		for (User u: upcomingUsers){
			// if not equal to thisUser, or not in the current neighborlist, need to set\
			// false, add it to current neighborlist
			if (!u.equals(this.thisUser) && !this.userList.contains(u)){
				result = false;
				this.userList.add(u);
			}
		}
		
		for (User u: this.userList){
			if (!u.equals(sender) && !upcomingUsers.contains(u)){
				result = false;
				break;
			}
		}
		return result;
	}
	
	public Boolean checkUUID(UUID uuid){
		if (this.uuidSet.contains(uuid))
			return true;
		else{
			this.uuidSet.add(uuid);
			return false;
		}
	}
	
}
