package com.example.bluetoothnetwork.util;

import java.io.Serializable;


public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8966257220596016755L;
	private long id;
	private String userName;
	public User(String userName, long id){
		this.setUserName(userName);
		this.setId(id);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public boolean equals(Object o) {
		return ((User) o).getId() == this.getId();
	}
	@Override
	public int hashCode() {
		Long l = Long.valueOf(id);
		return l.hashCode();
	}
}
