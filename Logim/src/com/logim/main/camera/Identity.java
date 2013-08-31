package com.logim.main.camera;

import org.json.JSONException;
import org.json.JSONObject;

public class Identity {

	String identifier;
	String name;
	String url;
	String login;
	String password;
	String creation;
	String lastUse;
	String countUse;
	
	
	public Identity(String identifier, String name, String url, String login,
			String password) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.url = url;
		this.login = login;
		this.password = password;
	}
	
	public Identity(String identifier, String name, String url, String login,
			String password, String creation, String lastUse, String countUse )
	{
		super();
		this.identifier = identifier;
		this.name = name;
		this.url = url;
		this.login = login;
		this.password = password;
		this.creation=creation;
		this.lastUse=lastUse;
		this.countUse=countUse;
		
		
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public JSONObject generateJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("identifier", identifier);
			json.put("name", name);
			json.put("originUrl", url);
			json.put("username", login);
			json.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
	public JSONObject generateJsonComplete() {
		JSONObject json = new JSONObject();
		try {
			json.put("identifier", identifier);
			json.put("name", name);
			json.put("originUrl", url);
			json.put("username", login);
			json.put("password", password);
			json.put("creation", creation);
			json.put("lastUse",lastUse);
			json.put("countUse",countUse);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
}
