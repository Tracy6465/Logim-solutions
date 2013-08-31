package com.logim.main.social;

public class Constants {
	public static String CONSUMER_KEY = "X7uujiSwL83XBtfOxdrhw";
	public static String CONSUMER_SECRET = "U6XTiDpZgH0Y17jKcGho0FQUEvSD9M37IDaiWNdOc";
	
	public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow-twitter";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	private static String userid;
	private static String  password;
	private static String postmessage;
	public static String getPostmessage() {
		return postmessage;
	}
	public static void setPostmessage(String postmessage) {
		Constants.postmessage = postmessage;
	}
	public static String getUserid() {
		return userid;
	}
	public static void setUserid(String userid) {
		Constants.userid = userid;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		Constants.password = password;
	}
	
}
