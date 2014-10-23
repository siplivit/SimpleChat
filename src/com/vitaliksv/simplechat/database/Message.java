package com.vitaliksv.simplechat.database;

import java.util.Date;

public class Message {
	
	private int id;
	private String message;
	private String createDate;	
	private int isMyMessageFlag;

	public Message(String message, String date, int uID) {
		super();
		
		this.message = message;
		this.createDate = date;
		this.isMyMessageFlag = uID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getDate() {
		return createDate;
	}
	
	public void setDate(String createDate) {
		this.createDate = createDate;
	}
	
	public int getUserID() {
		return isMyMessageFlag;
	}
	
	public void setUserID(int isMyMessageFlag) {
		this.isMyMessageFlag = isMyMessageFlag;
	}
}
