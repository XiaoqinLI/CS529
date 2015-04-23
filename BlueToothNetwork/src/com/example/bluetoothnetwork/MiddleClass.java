package com.example.bluetoothnetwork;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import android.util.Log;

import com.example.bluetoothnetwork.util.AddNeighborData;
import com.example.bluetoothnetwork.util.Data;
import com.example.bluetoothnetwork.util.Message;
import com.example.bluetoothnetwork.util.RemoveNeighborData;
import com.example.bluetoothnetwork.util.TextData;
import com.example.bluetoothnetwork.util.TopologyData;
import com.example.bluetoothnetwork.util.User;




abstract public class MiddleClass extends LowerClass{
	

	private User thisUser;
	
	private Map<User, Set<User>> userGraph;
	
	private Set<UUID> uuidSet = new HashSet<UUID>();
	
	
	public Boolean checkUUID(UUID uuid){
		if (this.uuidSet.contains(uuid))
			return true;
		else{
			this.uuidSet.add(uuid);
			return false;
		}
	}
	
	
	public void init(User thisUser){
		
		this.thisUser = thisUser;
		this.userGraph = new HashMap<User, Set<User>>();
		Set<User> self = new HashSet<User>();
		this.userGraph.put(thisUser, self);

		
	}
	
	public byte[] serialize(Data data) throws IOException {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    ObjectOutputStream o = new ObjectOutputStream(b);
	    
	    o.writeObject(data);
	    
	    
	    return b.toByteArray();
	}
	
	
	//find out all the reachable users
	abstract void onNewUser(List<User> users);
	abstract void onUserDisconnect(List<User> users);
	//these two, called inside onNewNeighbor, onNeighborLeaving
	abstract void onMessage(Message message); //called inside onReceivingData getAllUsers();

	
	
	
	// Send Msg, if the receiver is reachable, broadcast
	Boolean sendMessage(User user, Message message){ 
		Data data = new TextData(message.getSender(),message.getReceiver(), message.getMessage());
		
		if (BFS().contains(message.getReceiver())){
			Set<String> availableDevices = this.getAllConnectedDevices();
			for (String s: availableDevices)
				try {
					this.sendData( s, serialize(data));
				} catch (IOException e) {
				
					e.printStackTrace();
				}
			return true;
			
		}else
			return false;
	
		
			
	}
	
	
	public List<User> getAllUsers(){
		
		return BFS();
	}
	
	
	
	
	@Override
	// step 1: add the link into its current topology, test whether a new neighbor coming
	// step 2: to broadcast newNeighbor msg, which is to urge anyone to broadcast their own topology
	// step 3: broadcast its own updated direct neighbors.
	void onNewNeighbor(User user) {
		// add this edge in the graph
		
		// thisUser -> user
		List<User> current = BFS();
		if (!current.contains(user)){
			current.clear();
			current.add(user);
			this.onNewUser(current);
		}
		
		this.userGraph.get(thisUser).add(user);
		
		// user -> thisUser
		if (this.userGraph.containsKey(user)){
			this.userGraph.get(user).add(thisUser);
			
		}else{
			Set<User> newSet = new HashSet<User>();
			newSet.add(thisUser);
			this.userGraph.put(user, newSet);
		}
		
		// broadcast ADDNEIGHBORdata
	
		Data neighborData = new AddNeighborData(this.thisUser, null, user);
		
		Set<String> availableDevices = this.getAllConnectedDevices();
		for (String s: availableDevices)
			try {
				this.sendData( s, serialize(neighborData));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		// broadcast updated topology
		Data thisTopoData = new TopologyData(this.thisUser, null, this.userGraph.get(thisUser));
		for (String s: availableDevices)
			try {
				this.sendData( s, serialize(thisTopoData));
			} catch (IOException e) {
				e.printStackTrace();
			}
		

		
	}

	@Override
	// step 1: break the edge in the topology
	// step 2: broadcast removeDataMsg
	// step 3: check the current status
	void onNeighborLeaving(User user) {
		
		
		
		
		this.userGraph.get(thisUser).remove(user);
		this.userGraph.get(user).remove(thisUser);
		
		
		Data neighborData = new RemoveNeighborData(this.thisUser, null, user);
		Set<String> availableDevices = this.getAllConnectedDevices();
		for (String s: availableDevices)
			try {
				this.sendData( s, serialize(neighborData));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
		List<User> usersContained = BFS();
		Set<User> allUsers = this.userGraph.keySet();
		
		List<User> usersLeaving = new ArrayList<User>();
		usersLeaving.addAll(allUsers);
		
		usersLeaving.removeAll(usersContained);
		this.onUserDisconnect(usersLeaving);
		
	}
	
	// this is to return the current reachable user sets.
	public List<User> BFS(){
		List<User> result = new ArrayList<User>();
		
		Set<User> visited = new HashSet<User>();
		
		Queue<User> queue = new LinkedList<User>();
		queue.add(thisUser);
		visited.add(thisUser);
		
		User element;
		while (!queue.isEmpty()){
			element = queue.remove();
			
			result.add(element);
			
			if (this.userGraph.containsKey(element) && !this.userGraph.get(element).isEmpty()){
			Set<User> adjacency = this.userGraph.get(element);
			
			
			for (User a: adjacency){
				if (!visited.contains(a)){
					queue.add(a);
					visited.add(a);
				}
				
			}
			}
			
			
		}
		
		Log.i("BFS", ""+result.size());
		
		return result;
		
		
	}
	

	@Override
	void onReceivingData(Data data) {
		// check uuid, do not analyze msgs appear before
		if (checkUUID(data.getUUID())) {
			Log.i(""+data.getType(), "Received before");
			return;
		}
		else if (data.getType() == Data.TEXT){
			
			Log.i(""+data.getType(), "uuid: "+data.getUUID());
			// if is receiver
			if (data.getReceiver().equals(this.thisUser) ){
				Message msg = new Message(data.getSender(), data.getReceiver(), ((TextData)data).getText());
				this.onMessage(msg);
				Log.i("MiddleClass", ""+((TextData)data).getText());

			}else if (!((TextData)data).checkFootprint(thisUser)){
				// if is not the receiver
				Set<String> availableDevices = this.getAllConnectedDevices();
				for (String s: availableDevices)
					try {
						this.sendData( s, serialize(data));
					} catch (IOException e) {
						e.printStackTrace();
					}
				Log.i("MiddleClass", ""+"this is for" + data.getReceiver());
			}
		}
		
		
		
		
		else if (data.getType() == Data.TOPOLOGY){
			Log.i("TopoMsg","this is from " + data.getSender().getUserName());
			
			// Link state
			if (!((TopologyData)data).checkFootprint(thisUser)){
				// step 1: check if same. if not, update, and call onNewUser
				if (this.userGraph.get(thisUser).contains(data.getSender())){
					// case 1: if sender not exists
					if (!this.userGraph.containsKey(data.getSender())
						||	!this.userGraph.get(data.getSender()).equals(((TopologyData)data).getDirectNeighbors())){
						this.userGraph.put(data.getSender(), ((TopologyData)data).getDirectNeighbors());
						Log.i("case1"," ");
						List<User> newUsers = BFS();
						newUsers.remove(this.thisUser);
						Log.i("newUser#: ", ""+newUsers.size());
						this.onNewUser(newUsers);
						Set<String> availableDevices = this.getAllConnectedDevices();
						for (String s: availableDevices)
							try {
								this.sendData( s, serialize(data));
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
					
					
					
					
				}else{
					// case 2: if the sender is not within the usergraph
					this.userGraph.put(data.getSender(), ((TopologyData)data).getDirectNeighbors());
					Log.i("case2"," ");
					List<User> newUsers = BFS();
					newUsers.remove(this.thisUser);
					this.onNewUser(newUsers);
					
					Set<String> availableDevices = this.getAllConnectedDevices();
					for (String s: availableDevices)
						try {
							this.sendData( s, serialize(data));
						} catch (IOException e) {
							e.printStackTrace();
						}
					
				}
				
				
			}
		}
			
			
			
		
		
		
		
		
		
		else if (data.getType() == Data.ADDNEIGHBOR){
			// update neighbors
			// only need to broadcast this msg, because an addNeighborMsg will not update thisUser's
			// topology
			
			if (!((AddNeighborData)data).checkFootprint(thisUser)){	
			
				Log.i("AddNewNeighbor", "this is from " + data.getSender().getUserName());
				Data topoData = new TopologyData(this.thisUser, null, this.userGraph.get(thisUser));
			
				Set<String> availableDevices = this.getAllConnectedDevices();
				for (String s: availableDevices)
					if (!data.getSender().equals(this.thisUser))
						try {
							this.sendData(s, serialize(topoData));
							this.sendData(s, serialize(data));
						} catch (IOException e) {
					
							e.printStackTrace();
						}
			}
		}
		
			
			
			
			
		else if (data.getType() == Data.REMOVENEIGHBOR){
			// update neighbors
			User userToRemove1 = ((RemoveNeighborData)data).getToRemove();
			User userToRemove2 = data.getSender();
			
			
			
			if (!((RemoveNeighborData)data).checkFootprint(thisUser)){
			
				
				if (this.userGraph.get(userToRemove1).contains(userToRemove2))
					this.userGraph.get(userToRemove1).remove(userToRemove2);
				if (this.userGraph.get(userToRemove2).contains(userToRemove1))
					this.userGraph.get(userToRemove2).remove(userToRemove1);
				
				List<User> usersContained = BFS();
				Set<User> allUsers = this.userGraph.keySet();
				
				List<User> usersLeaving = new ArrayList<User>();
				usersLeaving.addAll(allUsers);
				
				usersLeaving.removeAll(usersContained);
				this.onUserDisconnect(usersLeaving);
				
				
				
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
			
		
		
		
		
	
	}
	
	
	
}