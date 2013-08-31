package com.logim.activity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;

/**
 * 
 * @author David Prieto Rivera
 *	Class with methods for reading and writing data of an intern file.
 */

public class FileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_file);
	}

	/**
	 * 
	 * @return String stored in a file
	 */

	public String getStringFromFile(String file)

	{
		String texto = "";
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(
					openFileInput(file)));

			texto = fin.readLine();

			fin.close();
		} catch (Exception ex) {
			Log.e("Files", "Error reading the file from intern memory");
		}
		return texto;
	}

	/**
	 * 
	 * Store a String into a File
	 */

	public void setStringtoFile(String file, String param)

	{
		OutputStreamWriter fout;
		try {
			fout = new OutputStreamWriter(openFileOutput(file,
					Context.MODE_PRIVATE));
			fout.write(param);
			fout.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_file, menu);
		return true;
	}

}
