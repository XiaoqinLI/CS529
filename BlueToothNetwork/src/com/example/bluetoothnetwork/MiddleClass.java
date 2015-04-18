package com.example.bluetoothnetwork;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.bluetoothnetwork.util.AddNeighborData;
import com.example.bluetoothnetwork.util.Data;
import com.example.bluetoothnetwork.util.Device;
import com.example.bluetoothnetwork.util.Message;
import com.example.bluetoothnetwork.util.RemoveNeighborData;
import com.example.bluetoothnetwork.util.TextData;
import com.example.bluetoothnetwork.util.User;




abstract public class MiddleClass extends LowerClass{
	
	private List<User> userList;
	private User thisUser;
	
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
		Data data = new TextData(message.getReceiver(),message.getSender(), message.getMessage());
//		if (this.userList.contains(user)){
//			List<Device> availableDevices = this.getAllConnectedDevices();
//			for (Device d: availableDevices)
//				this.sendData( d, data);
//			return true;
//		}
//		else
//			return false;
		
		this.onReceivingData(data);
		return true;
		
			
	}
	
	
	public List<User> getAllUsers(){
		return this.userList;
		}
	
	
	
	
	@Override
	void onNewNeighbor(User user) {
		// TODO Auto-generated method stub
		this.userList.add(user);
		List<User> temp = new ArrayList<User>();
		temp.add(user);
		
		Data neighborData = new AddNeighborData(this.thisUser, null, temp);
		
		Set<String> availableDevices = this.getAllConnectedDevices();
		for (String s: availableDevices)
			try {
				this.sendData( s, serialize(neighborData));
			} catch (IOException e) {
				e.printStackTrace();
			}
		this.onNewUser(temp);
		
	}

	@Override
	void onNeighborLeaving(User user) {
		// TODO Auto-generated method stub
		this.userList.remove(user);
		List<User> temp = new ArrayList<User>();
		temp.add(user);
		
		Data neighborData = new RemoveNeighborData(null, null, temp);
		Set<String> availableDevices = this.getAllConnectedDevices();
		for (String s: availableDevices)
			try {
				this.sendData( s, serialize(neighborData));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		this.onUserDisconnect(temp);
		
	}

	@Override
	void onReceivingData(Data data) {
		// TODO Auto-generated method stub
		if (data.getType() == Data.TEXT){
			if (data.getReceiver() == this.thisUser){
				Message msg = new Message(data.getSender(), data.getReceiver(), ((TextData)data).getText());
				this.onMessage(msg);
			}else{
				Set<String> availableDevices = this.getAllConnectedDevices();
				for (String s: availableDevices)
					try {
						this.sendData( s, serialize(data));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
		
		
		
		if (data.getType() == Data.TOPOLOGY){
			// do not need in flooding strategy
		}
		
		
		
		
		
		if (data.getType() == Data.NEIGHBOR){
			// update neighbors
			Set<String> availableDevices = this.getAllConnectedDevices();
			for (String s: availableDevices)
				if (!data.getSender().equals(this.thisUser))
					try {
						this.sendData(s, serialize(data));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
			
		
		
	}
	
	
	
	
	
}
