package com.logim.main.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author David Prieto Rivera 
 * Class to create and delete databases of the app
 * 
 */
public class Database extends SQLiteOpenHelper {

	// SQL Sentence to create Identities Table of the User
	String sqlCreate = "CREATE TABLE Identities (codigo TEXT, nombre TEXT, email TEXT, password TEXT, url TEXT, creation TEXT, visited TEXT, count TEXT, userid TEXT, user TEXT)";
	String sqlCreate2 = "CREATE TABLE UserInfo (user TEXT, userid TEXT, appid TEXT, type TEXT, limite TEXT, signaturekey TEXT, mail TEXT, facebook TEXT, twitter TEXT,weibo TEXT,premexp TEXT)";
	String sqlCreate3= "CREATE TABLE UserPin (userid TEXT, pin TEXT, hasPin TEXT, email TEXT)";
	String sqlCreate4= "CREATE TABLE UserSalt (userid TEXT, salt TEXT, email TEXT)";
	/**
	 * 
	 * @param context
	 * @param nombre
	 * @param factory
	 * @param version
	 *            Class Constructor
	 */
	public Database(Context context, String nombre, CursorFactory factory,
			int version) {
		super(context, nombre, factory, version);
	}

	

	/**
	 * Create Database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Se ejecuta la sentencia SQL de creación de la tabla
		db.execSQL(sqlCreate);
		db.execSQL(sqlCreate2);
		db.execSQL(sqlCreate3);
		db.execSQL(sqlCreate4);

	}
	

	/**
	 * Upgrading Database
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnterior,
			int versionNueva) {

		// Se elimina la versión anterior de la tabla
		db.execSQL("DROP TABLE IF EXISTS Identities");
		db.execSQL("DROP TABLE IF EXISTS UserInfo");
		db.execSQL("DROP TABLE IF EXISTS UserPin");
		db.execSQL("DROP TABLE IF EXISTS UserSalt");

		// Se crea la nueva versión de la tabla
		db.execSQL(sqlCreate);
		db.execSQL(sqlCreate2);
		db.execSQL(sqlCreate3);
		db.execSQL(sqlCreate4);

	}

}
