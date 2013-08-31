package com.logim.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logim.main.utils.Database;
import com.logim.vo.UserPinVo;

public class UserPinDao {
	
	private Database dbUtil;
	private SQLiteDatabase db;
	
	public UserPinDao(Context context) {
		dbUtil = new Database(context, DBValue.DB_USERPIN, null, 1);
	}
	
	public void insertUserPin(UserPinVo userPin) {
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_UserPin.USERID, userPin.getUserid());
		cv.put(DBValue.Table_UserPin.PIN, userPin.getPin());
		cv.put(DBValue.Table_UserPin.HASPIN, userPin.getHasPin());
		cv.put(DBValue.Table_UserPin.EMAIL, userPin.getEmail());
		
		db = dbUtil.getWritableDatabase();
		db.insert(DBValue.TABLE_USERPIN, null, cv);
	}
	
	public void updateUserPin(UserPinVo userPin) {
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_UserPin.USERID, userPin.getUserid());
		cv.put(DBValue.Table_UserPin.PIN, userPin.getPin());
		cv.put(DBValue.Table_UserPin.HASPIN, userPin.getHasPin());
		cv.put(DBValue.Table_UserPin.EMAIL, userPin.getEmail());
		
		String whereClause = DBValue.Table_UserPin.USERID + " = ?";
		String[] whereArgs = {userPin.getUserid()};
		db = dbUtil.getWritableDatabase();
		db.update(DBValue.TABLE_USERPIN, cv, whereClause, whereArgs);
	}
	
	public void updateClearPinById(String userId) {
		String whereClause = DBValue.Table_UserPin.USERID + " = ?";
		String[] whereArgs = {userId};
		
		updateClearPin(whereClause, whereArgs);
	}
	
	public void updateClearPinByEmail(String userEmail) {
		String whereClause = DBValue.Table_UserPin.EMAIL + " = ?";
		String[] whereArgs = {userEmail};
		
		updateClearPin(whereClause, whereArgs);
	}
	
	public void updateClearPin(String whereClause, String[] whereArgs) {
		ContentValues cv = new ContentValues();
		cv.put(DBValue.Table_UserPin.PIN, "null");
		cv.put(DBValue.Table_UserPin.HASPIN, "false");
		
		db = dbUtil.getWritableDatabase();
		db.update(DBValue.TABLE_USERPIN, cv, whereClause, whereArgs);
	}
	
	public UserPinVo selectUserPinForSet(String userid, String useremail) {
		db = dbUtil.getReadableDatabase();
		String selection = DBValue.Table_UserPin.USERID + " = ?";
		String[] selectionArgs = new String[]{userid};
		Cursor c = db.query(DBValue.TABLE_USERPIN, DBValue.Table_UserPin.COL_USERPIN, 
				selection, selectionArgs, null, null, null);
		
		UserPinVo userPin = new UserPinVo();
		if (c.getCount() == 0) {
			userPin.setUserid(userid);
			userPin.setPin("null");
			userPin.setHasPin("false");
			userPin.setEmail(useremail);
			insertUserPin(userPin);
		}
		else {
			while (c.moveToNext()) {
				userPin.setUserid(c.getString(0));
				userPin.setPin(c.getString(1));
				userPin.setHasPin(c.getString(2));
				userPin.setEmail(c.getString(3));
			}
		}
		
		return userPin;
	}
	
	public UserPinVo selectUserPinForCheck(String userid) {
		db = dbUtil.getReadableDatabase();
		String selection = DBValue.Table_UserPin.USERID + " = ?";
		String[] selectionArgs = new String[]{userid};
		Cursor c = db.query(DBValue.TABLE_USERPIN, DBValue.Table_UserPin.COL_USERPIN, 
				selection, selectionArgs, null, null, null);
		
		UserPinVo userPin = new UserPinVo();
		if (c.getCount() == 0) {
			return null;
		}
		else {
			while (c.moveToNext()) {
				userPin.setUserid(c.getString(0));
				userPin.setPin(c.getString(1));
				userPin.setHasPin(c.getString(2));
				userPin.setEmail(c.getString(3));
			}
		}
		
		return userPin;
	}
	
	public void close() {
		db.close();
	}
}
