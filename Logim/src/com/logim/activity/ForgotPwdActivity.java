package com.logim.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.logim.db.UserPinDao;
import com.logim.main.R;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.view.ProgressView;

public class ForgotPwdActivity extends Activity {

	ProgressDialog pDialog;
	EditText editText;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		context=ForgotPwdActivity.this;
		SysApplication.getInstance().addActivity(this); 
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		editText = (EditText) findViewById(R.id.forgot_email);
		Intent intent = getIntent();
		String email = intent.getStringExtra("email");
		if (email != null) {
			editText.setText(email);
			editText.setEnabled(false);
			editText.setSelected(false);
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
        	goBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			goBack();
			return true;

		}
		default:

			return super.onKeyUp(keyCode, event);
		}
	}
	
	void goBack() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	public void recordarPassword(View v) {
		String email=editText.getText().toString();
		boolean validParameters = checkParameters(email);
		String url = "https://service.mylogim.com/api1/user/forgotpass";
		if (validParameters) {
			// String identifier = getStringFromFile("identifier.txt");
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				String[] args = new String[] { url,email };
				pDialog = new ProgressView(this);
				new RecordarPassword().execute(args);
			} else {
				Toast.makeText(this, this.getString(R.string.toast_no_network),
						Toast.LENGTH_LONG).show();
			}

		}

	}

	public boolean checkParameters(String email) {

		Boolean valid = true;

		if (email.equals("")) {
			Toast.makeText(this, this.getString(R.string.toast_fault_email),
					Toast.LENGTH_LONG).show();
			valid = false;

		} else if (!email.contains("@")) {
			
			Toast.makeText(this.getBaseContext(),
					(this.getBaseContext()).getString(R.string.toast_email_incorrect),
					Toast.LENGTH_LONG).show();
			valid = false;

		}
		return valid;

	}
	
	private class RecordarPassword extends AsyncTask<String, Integer, String> {

		String email;
		@Override
		protected String doInBackground(String... params) {

			
			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p= new Pair("email",params[1]);
			email=params[1];
			listParam.add(p);
			String result = Utils.doHttpRequest(1, listParam, params[0], "POST",
					false);
			return result;

		}

		protected void onPostExecute(String result) {
			try {
				if (!result.equals("")) {
					JSONObject json = new JSONObject(result);
				
					if (json.getString("status").equals("success")) {
						Database database= new Database(ForgotPwdActivity.this, "DBIdentities", null, 1);
						Database databaseSalt= new Database(ForgotPwdActivity.this, "DBUserSalt", null, 1);
						SQLiteDatabase db = database.getWritableDatabase();
						SQLiteDatabase db4 = databaseSalt.getWritableDatabase();
						String[] args={email};				
						db.execSQL("DELETE FROM Identities WHERE user=?", args);
						db.close();
						
						UserPinDao userPinDao = new UserPinDao(context);
						userPinDao.updateClearPinByEmail(email);
						
						db4.execSQL("DELETE FROM UserSalt WHERE email=?", args);
						db4.close();
						database.close();
						databaseSalt.close();
						Toast.makeText(
								context,
								context.getString(
										R.string.toast_done),
								Toast.LENGTH_LONG).show();
						pDialog.cancel();
					} else {

					
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_email_incorrect),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

					}
				} else {
					Toast.makeText(context,
							context.getString(R.string.toast_server_error),
							Toast.LENGTH_LONG).show();
					pDialog.cancel();
				}
			} catch (JSONException e) { 
				
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					RecordarPassword.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

}
