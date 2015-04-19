package com.example.bluetoothnetwork.util;
import java.io.Serializable;


public interface Data extends Serializable {

	
	static final public int TEXT = 1;
	static final public int TOPOLOGY = 2;
	static final public int NEIGHBOR = 3;
	
	public User getSender();
	public User getReceiver();
	public int getType();
	


}
