package com.logim.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AccountPref {
	
	private final static String PREFFILE = "SignAccount";
	private SharedPreferences share;
	private Editor editor;
	
	public AccountPref(Context context) {
		this.share = context.getSharedPreferences(PREFFILE, Context.MODE_PRIVATE);
		this.editor = share.edit();
	}
	
	public String getEmail() {
		String value = share.getString("userEmail", "");
		setEmail(value);
		
		return value;
	}
	
	public void setEmail(String userEmail) {
		editor.putString("userEmail", userEmail);
		editor.commit();
	}
	
	public String getPassword() {
		String value = share.getString("userPassword", "");
		setPassword(value);
		
		return value;
	}
	
	public void setPassword(String userPassword) {
		editor.putString("userPassword", userPassword);
		editor.commit();
	}
	
	public void clear() {
		editor.clear();
		editor.commit();
	}
}
