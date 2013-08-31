package com.logim.db;

public class DBValue {
	
	public static String DB_USERINFO = "DBUserInfo";
	public static String TABLE_USERINFO = "UserInfo";
	public static class Table_UserInfo {
		public static String USEREMAIL = "user";
		public static String USERID = "userid";
		public static String APPID = "appid";
		public static String TYPE = "type";
		public static String LIMITE = "limite";
		public static String SIGNATUREKEY = "signaturekey";
		public static String ISMAIL = "mail";
		public static String ISFACEBOOK = "facebook";
		public static String ISTWITTER = "twitter";
		public static String ISWEIBO = "weibo";
		public static String PREMEXP = "premexp";
		
		public static String[] COL_USERINFO = new String[] {
				USEREMAIL, 
				USERID,
				APPID,
				TYPE,
				LIMITE,
				SIGNATUREKEY,
				ISMAIL,
				ISFACEBOOK,
				ISTWITTER,
				ISWEIBO,
				PREMEXP
		};
	}
	
	public static String DB_USERPIN = "DBUserPin";
	public static String TABLE_USERPIN = "UserPin";
	public static class Table_UserPin {
		public static String USERID = "userid";
		public static String PIN = "pin";
		public static String HASPIN = "hasPin";
		public static String EMAIL = "email";
		
		public static String[] COL_USERPIN = new String[] {
			USERID,
			PIN,
			HASPIN,
			EMAIL
		};
	}
	
	public static String DB_USERIDENTITY = "DBIdentities";
	public static String TABLE_USERIDENTITY = "Identities";
	public static class Table_UserIdentity {
		public static String USERID = "userid";
		
		public static String[] COL_COUNT = new String[] {
			"count(*)"
		};
	}
}
