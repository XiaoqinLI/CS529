package com.example.bluetoothnetwork.util;
import java.io.Serializable;


public interface Data extends Serializable {

	
	static final public int TEXT = 1;
	static final public int TOPOLOGY = 2;
	static final public int ADDNEIGHBOR = 3;
	static final public int REMOVENEIGHBOR = 4;
	
	public User getSender();
	public User getReceiver();
	public int getType();
	


}
