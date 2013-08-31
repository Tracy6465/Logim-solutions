package com.logim.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logim.main.utils.Database;
import com.logim.vo.UserInfoVo;

public class UserInfoDao {
	
	private Database dbUtil;
	private SQLiteDatabase db;
	
	public UserInfoDao(Context context) {
		dbUtil = new Database(context, DBValue.DB_USERINFO, null, 1);
	}
	
	public void insertUserInfo(UserInfoVo userInfo) {
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_UserInfo.USEREMAIL, userInfo.getUseremail());
		cv.put(DBValue.Table_UserInfo.USERID, userInfo.getUserid());
		cv.put(DBValue.Table_UserInfo.APPID, userInfo.getAppid());
		cv.put(DBValue.Table_UserInfo.TYPE, userInfo.getType());
		cv.put(DBValue.Table_UserInfo.LIMITE, userInfo.getIdentitiesstoragelimit());
		cv.put(DBValue.Table_UserInfo.SIGNATUREKEY, userInfo.getSignaturekey());
		cv.put(DBValue.Table_UserInfo.ISMAIL, userInfo.getRefemail());
		cv.put(DBValue.Table_UserInfo.ISFACEBOOK, userInfo.getReffacebook());
		cv.put(DBValue.Table_UserInfo.ISTWITTER, userInfo.getReftwitter());
		cv.put(DBValue.Table_UserInfo.ISWEIBO, userInfo.getRefweibo());
		cv.put(DBValue.Table_UserInfo.PREMEXP, userInfo.getPremexp());
		
		db = dbUtil.getWritableDatabase();
		db.insert(DBValue.TABLE_USERINFO, null, cv);
	}
	
	public void deleteUserInfo(String userId) {
		String whereClause = DBValue.Table_UserInfo.USERID + " = ?";
		String[] whereArgs = {userId+ ""};
		
		db = dbUtil.getWritableDatabase();
		db.delete(DBValue.TABLE_USERINFO, whereClause, whereArgs);
	}
	
	public void deleteUserInfo() {
		db = dbUtil.getWritableDatabase();
		db.delete(DBValue.TABLE_USERINFO, null, null);
	}
	
	public void updateUserInfo(UserInfoVo userInfo) {
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_UserInfo.USEREMAIL, userInfo.getUseremail());
		cv.put(DBValue.Table_UserInfo.USERID, userInfo.getUserid());
		cv.put(DBValue.Table_UserInfo.APPID, userInfo.getAppid());
		cv.put(DBValue.Table_UserInfo.TYPE, userInfo.getType());
		cv.put(DBValue.Table_UserInfo.LIMITE, userInfo.getIdentitiesstoragelimit());
		cv.put(DBValue.Table_UserInfo.SIGNATUREKEY, userInfo.getSignaturekey());
		cv.put(DBValue.Table_UserInfo.ISMAIL, userInfo.getRefemail());
		cv.put(DBValue.Table_UserInfo.ISFACEBOOK, userInfo.getReffacebook());
		cv.put(DBValue.Table_UserInfo.ISTWITTER, userInfo.getReftwitter());
		cv.put(DBValue.Table_UserInfo.ISWEIBO, userInfo.getRefweibo());
		cv.put(DBValue.Table_UserInfo.PREMEXP, userInfo.getPremexp());
		
		String whereClause = DBValue.Table_UserInfo.USERID + " = ?";
		String[] whereArgs = {userInfo.getUserid()};
		db = dbUtil.getWritableDatabase();
		db.update(DBValue.TABLE_USERINFO, cv, whereClause, whereArgs);
	}
	
	public void updateSocial(String userId, String limit, String socialTab) {
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_UserInfo.LIMITE, limit);
		cv.put(socialTab, "true");
		
		String whereClause = DBValue.Table_UserInfo.USERID + " = ?";
		String[] whereArgs = {userId};
		db = dbUtil.getWritableDatabase();
		db.update(DBValue.TABLE_USERINFO, cv, whereClause, whereArgs);
	}
	
	public UserInfoVo selectUser() {
		db = dbUtil.getReadableDatabase();
		Cursor c = db.query(DBValue.TABLE_USERINFO, DBValue.Table_UserInfo.COL_USERINFO, 
				null, null , null, null, null);
		
		UserInfoVo userinfo = new UserInfoVo();
		if (c.getCount() == 0) {
			return null;
		}
		else {
			while (c.moveToNext()) {
				userinfo.setUseremail(c.getString(0));
				userinfo.setUserid(c.getString(1));
				userinfo.setAppid(c.getString(2));
				userinfo.setType(c.getString(3));
				userinfo.setIdentitiesstoragelimit(c.getString(4));
				userinfo.setSignaturekey(c.getString(5));
				userinfo.setRefemail(c.getString(6));
				userinfo.setReffacebook(c.getString(7));
				userinfo.setReftwitter(c.getString(8));
				userinfo.setRefweibo(c.getString(9));
				userinfo.setPremexp(c.getString(10));
			}
		}
		
		return userinfo;
	}
	
	public UserInfoVo selectUserInfo(String userid) {
		db = dbUtil.getReadableDatabase();
		String selection = DBValue.Table_UserInfo.USERID + " = ?";
		String[] selectionArgs = new String[]{userid};
		Cursor c = db.query(DBValue.TABLE_USERINFO, DBValue.Table_UserInfo.COL_USERINFO, 
				selection, selectionArgs, null, null, null);
		
		UserInfoVo userinfo = new UserInfoVo();
		if (c.getCount() == 0) {
			return null;
		}
		else {
			while (c.moveToNext()) {
 				userinfo.setUseremail(c.getString(0));
				userinfo.setUserid(c.getString(1));
				userinfo.setAppid(c.getString(2));
				userinfo.setType(c.getString(3));
				userinfo.setIdentitiesstoragelimit(c.getString(4));
				userinfo.setSignaturekey(c.getString(5));
				userinfo.setRefemail(c.getString(6));
				userinfo.setReffacebook(c.getString(7));
				userinfo.setReftwitter(c.getString(8));
				userinfo.setRefweibo(c.getString(9));
				userinfo.setPremexp(c.getString(10));
			}
		}
		
		return userinfo;
	}
	
	public String selectUserId(String userEmail) {
		db = dbUtil.getReadableDatabase();
		String[] columns = new String[]{DBValue.Table_UserInfo.USERID};
		String selection = DBValue.Table_UserInfo.USEREMAIL + " = ?";
		String[] selectionArgs = new String[]{userEmail};
		Cursor c = db.query(DBValue.TABLE_USERINFO, columns, 
				selection, selectionArgs, null, null, null);
		
		String userId = "";
		if (c.getCount() != 0) {
			while (c.moveToNext()) {
				userId = c.getString(0);
			}
		}
		
		return userId;
	}
	
	public String selectType(String userId) {
		db = dbUtil.getReadableDatabase();
		String[] columns = new String[]{DBValue.Table_UserInfo.TYPE};
		String selection = DBValue.Table_UserInfo.USERID + " = ?";
		String[] selectionArgs = new String[]{userId};
		Cursor c = db.query(DBValue.TABLE_USERINFO, columns, 
				selection, selectionArgs, null, null, null);
		
		String type = "";
		if (c.getCount() != 0) {
			while (c.moveToNext()) {
				type = c.getString(0);
			}
		}
		
		return type;
	}
	
	public String selectLimit(String userId) {
		db = dbUtil.getReadableDatabase();
		String[] columns = new String[]{DBValue.Table_UserInfo.LIMITE};
		String selection = DBValue.Table_UserInfo.USERID + " = ?";
		String[] selectionArgs = new String[]{userId};
		Cursor c = db.query(DBValue.TABLE_USERINFO, columns, 
				selection, selectionArgs, null, null, null);
		
		String limite = "";
		if (c.getCount() != 0) {
			while (c.moveToNext()) {
				limite = c.getString(0);
			}
		}
		
		return limite;
	}
	
	public String[] selectSocial(String userId) {
		db = dbUtil.getReadableDatabase();
		String[] columns = new String[]{DBValue.Table_UserInfo.ISMAIL,
				DBValue.Table_UserInfo.ISFACEBOOK, 
				DBValue.Table_UserInfo.ISTWITTER,
				DBValue.Table_UserInfo.ISWEIBO};
		String selection = DBValue.Table_UserInfo.USERID + " = ?";
		String[] selectionArgs = new String[]{userId};
		Cursor c = db.query(DBValue.TABLE_USERINFO, columns, 
				selection, selectionArgs, null, null, null);
		
		String[] social = new String[4];
		if (c.getCount() != 0) {
			while (c.moveToNext()) {
				social[0] = c.getString(0);
				social[1] = c.getString(1);
				social[2] = c.getString(2);
				social[3] = c.getString(3);
			}
		}
		
		return social;
	}
	
	public void close() {
		db.close();
	}
}
