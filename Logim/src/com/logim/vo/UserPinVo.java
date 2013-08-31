package com.logim.vo;

public class UserPinVo {
	String userid;
	String pin;
	String hasPin;
	String email;
	
	public UserPinVo() {
		super();
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getHasPin() {
		return hasPin;
	}
	
	public boolean isHasPin() {
		return Boolean.parseBoolean(hasPin);
	}

	public void setHasPin(String hasPin) {
		this.hasPin = hasPin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
