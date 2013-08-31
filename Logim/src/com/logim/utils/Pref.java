package com.logim.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Pref {
	
	private final static String PREFFILE = "logim_pref";
	private SharedPreferences share;
	private Editor editor;
	
	public Pref(Context context) {
		this.share = context.getSharedPreferences(PREFFILE, Context.MODE_PRIVATE);
		this.editor = share.edit();
	}
	
	public boolean getCameraTutorials() {
		boolean value = share.getBoolean("CameraTutorials", true);
		if (value) {
			setCameraTutorials(false);
		}
		
		return value;
	}
	
	public void setCameraTutorials(boolean cameraTutorials) {
		editor.putBoolean("CameraTutorials", cameraTutorials);
		editor.commit();
	}
	
	public boolean getMainTutorials() {
		boolean value = share.getBoolean("MainTutorials", true);
		if (value) {
			setMainTutorials(false);
		}
		
		return value;
	}
	
	public void setMainTutorials(boolean mainTutorials) {
		editor.putBoolean("MainTutorials", mainTutorials);
		editor.commit();
	}
	
	public String getLanguage() {
		String value = share.getString("Language", Locale.getDefault().getLanguage());
		setLanguage(value);
		
		return value;
	}
	
	public void setLanguage(String language) {
		editor.putString("Language", language);
		editor.commit();
	}
	
	public boolean isScanStart() {
		boolean value = share.getBoolean("ScanStart", false);
		setScanStart(value);
		
		return value;
	}
	
	public void setScanStart(boolean scanStart) {
		editor.putBoolean("ScanStart", scanStart);
		editor.commit();
	}
	
	public boolean isBlocked() {
		boolean value = share.getBoolean("Blocked", false);
		setScanStart(value);
		
		return value;
	}
	
	public void setBlocked(boolean blocked) {
		editor.putBoolean("Blocked", blocked);
		editor.commit();
	}
}
