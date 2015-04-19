package com.example.bluetoothnetwork.util;

public class User {
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
	
	public boolean equals(User o) {
		return o.getId() == this.getId();
	}
	@Override
	public int hashCode() {
		Long l = Long.valueOf(id);
		return l.hashCode();
	}
}
