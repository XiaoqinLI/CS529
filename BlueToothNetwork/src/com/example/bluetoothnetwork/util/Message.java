package com.example.bluetoothnetwork.util;

/**
 * The message data model used in upper layer UI
 *
 */
public class Message {
	private String message;
	private User sender;
	private User receiver;
	public Message(User sender, User receiver, String message){
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public String toString(){
		return "send from: " + sender + "\n" + message;
	}
	public User getReceiver() {
		return receiver;
	}
	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
}
