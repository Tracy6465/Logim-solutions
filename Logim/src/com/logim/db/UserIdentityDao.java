package com.logim.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logim.main.utils.Database;

public class UserIdentityDao {
	
	private Database dbUtil;
	private SQLiteDatabase db;
	
	public UserIdentityDao(Context context) {
		dbUtil = new Database(context, DBValue.DB_USERIDENTITY, null, 1);
	}
	
	public int selectIdentitiesCount(String userId) {
		db = dbUtil.getReadableDatabase();
		String selection = DBValue.Table_UserIdentity.USERID + " = ?";
		String[] selectionArgs = new String[]{userId};
		Cursor c = db.query(DBValue.TABLE_USERIDENTITY, DBValue.Table_UserIdentity.COL_COUNT, 
				selection, selectionArgs, null, null, null);
		
		int count = 0;
		if (c.moveToFirst()) {
			count = c.getInt(0);
		}
		
		return count;
	}
	
	public void deleteIdentities(String userId) {
		String whereClause = DBValue.Table_UserIdentity.USERID + " = ?";
		String[] whereArgs = {userId+ ""};
		
		db = dbUtil.getWritableDatabase();
		db.delete(DBValue.TABLE_USERIDENTITY, whereClause, whereArgs);
	}
	
	public void deleteIdentities() {
		db = dbUtil.getWritableDatabase();
		db.delete(DBValue.TABLE_USERIDENTITY, null, null);
	}
}
