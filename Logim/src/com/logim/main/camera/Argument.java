package com.logim.main.camera;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Argument {

	String requestIdentifier;
	String status;
	Credential credentials;
	LinkedList<Identity> identities;
	
	
	public String getRequestIdentifier() {
		return requestIdentifier;
	}
	public void setRequestIdentifier(String requestidentifier) {
		this.requestIdentifier = requestidentifier;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Argument(String requestIdentifier, String status,
			Credential credentials, LinkedList<Identity> identities) {
		super();
		this.requestIdentifier = requestIdentifier;
		this.status = status;
		this.credentials = credentials;
		this.identities = identities;
	}
	public Credential getCredentials() {
		return credentials;
	}
	public void setCredentials(Credential credentials) {
		this.credentials = credentials;
	}
	public LinkedList<Identity> getIdentities() {
		return identities;
	}
	public void setIdentities(LinkedList<Identity> identities) {
		this.identities = identities;
	}
	
	public JSONObject generateJson(boolean hasList)
	{
		JSONObject json = new JSONObject();
    	try {
	

    
    	if(hasList)
    	{
    		JSONArray jsonlist = new JSONArray();
    	
    		for(int i=0;i<identities.size();i++)
    		{
    			jsonlist.put(identities.get(i).generateJson());
    		}
    	
    		json.put("identities", jsonlist);
    	}
    	json.put("status","accepted");
		json.put("credentials", credentials.generateJson());
		
		 json.put("requestIdentifier", requestIdentifier);
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return json;
	}
	
	
}
