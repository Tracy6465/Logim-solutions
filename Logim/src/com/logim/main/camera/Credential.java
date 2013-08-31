package com.logim.main.camera;

import org.json.JSONException;
import org.json.JSONObject;

public class Credential {

	String appId;
	String nounce;
	String signature;
	String url;

	public Credential(String _appid, String _nounce, String _url) {
		appId = _appid;
		nounce = _nounce;
		url=_url;
	}

	public void setSignature(String signaturekey) {
		signature = Hashing.hmacSha1(url+appId + nounce, signaturekey);
	}
	
	public String getSignature()
	{
		return signature;
	}

	public JSONObject generateJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("appId", appId);
			json.put("nounce", nounce);
			json.put("signature", signature);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}

}
