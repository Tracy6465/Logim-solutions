package com.logim.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.logim.db.UserIdentityDao;
import com.logim.db.UserInfoDao;
import com.logim.db.UserPinDao;
import com.logim.main.R;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.utils.AccountPref;
import com.logim.utils.Pref;
import com.logim.view.ProgressView;

public class ChangePassword extends Activity {

	ProgressDialog pDialog;
	String email;
	String password;
	String globalSalt;
	String userid;
	String identityCounter;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		context=ChangePassword.this;
		SysApplication.getInstance().addActivity(this);
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		TextView texto= (TextView) findViewById(R.id.textChange);
		texto.setTypeface(Typeface.DEFAULT_BOLD);
		Intent intent = getIntent();
		email = intent.getStringExtra("email");
		password = intent.getStringExtra("password");
		userid = intent.getStringExtra("userid");
		identityCounter = intent.getStringExtra("identities");
		globalSalt = intent.getStringExtra("salt");

	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
        case android.R.id.home:
        	goBack();
        	break;
        }
        
        return super.onOptionsItemSelected(item);
    }

	/**
	 * Overrides the functionality of the Back Button
	 */
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

	private void goBack() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	public void forgotPassword(View v) {
		Intent intent = new Intent(context, ForgotPwdActivity.class);
		intent.putExtra("email", email);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	public void changePassword(View v) {
		EditText editText2 = (EditText) findViewById(R.id.antiguo_password);
		String antiguopassword=editText2.getText().toString();
		EditText changeEt = (EditText) findViewById(R.id.change_password);
		String newpassword = changeEt.getText().toString();
		EditText retypeEt = (EditText) findViewById(R.id.retype_password);
		String retypepassword = retypeEt.getText().toString();
		
		boolean validParameters = checkParameters(antiguopassword, newpassword, retypepassword);
		String url = "https://service.mylogim.com/api1/user/changepass";
		if (validParameters) {
			// String identifier = getStringFromFile("identifier.txt");
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				String[] args = new String[] { url,email,antiguopassword,newpassword };
				pDialog = new ProgressView(this);
				new CambiarPassword().execute(args);
			} else {
				Toast.makeText(this, this.getString(R.string.toast_no_network),
						Toast.LENGTH_LONG).show();
			}

		}

	}

	public boolean checkParameters(String antiguopassword, String newpassword, String retypepassword) {

		Boolean valid = true;
	

		if (antiguopassword.equals("")) {
			Toast.makeText(this, this.getString(R.string.toast_fault_password_old),
					Toast.LENGTH_LONG).show();
			valid = false;

		}
		else if (newpassword.equals("")) {
			Toast.makeText(this, this.getString(R.string.toast_fault_password_new),
					Toast.LENGTH_LONG).show();
			valid = false;

		}else if (!antiguopassword.equals(password)) {
			
			Toast.makeText(this.getBaseContext(),
					(this.getBaseContext()).getString(R.string.toast_fault_password_origin),
					Toast.LENGTH_LONG).show();
			valid = false;

		}
		else if(newpassword.length()<6)
		{
			Toast.makeText(context, context.getString(R.string.toast_fault_password_length),
					Toast.LENGTH_LONG).show();
			valid = false;
			
			
		}
		else if(!newpassword.equals(retypepassword)) {
			Toast.makeText(context, context.getString(R.string.toast_fault_Password_different),
					Toast.LENGTH_LONG).show();
			valid = false;
		}
		return valid;

	}

	private class CambiarPassword extends AsyncTask<String, Integer, String> {

		
		String newpassword;
		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p= new Pair("email",params[1]);
			listParam.add(p);
			 p= new Pair("password",params[2]);
			
			listParam.add(p);
			 p= new Pair("newpassword",params[3]);
			 newpassword=params[3];
			listParam.add(p);
			String result = Utils.doHttpRequest(3, listParam, params[0], "POST",
					false);
			return result;

		}

		protected void onPostExecute(String result) {
			try {
				if (!result.equals("")) {
					JSONObject json = new JSONObject(result);
				
					if (json.getString("status").equals("success")) {
						Database databaseSalt= new Database(ChangePassword.this, "DBUserSalt", null, 1);
						
						SQLiteDatabase db4 = databaseSalt.getWritableDatabase();
						
						UserInfoDao userInfoDao = new UserInfoDao(context);
						String userId = userInfoDao.selectUserId(email);
						
						String[] args2={userId};
						UserIdentityDao userIdentityDao = new UserIdentityDao(context);
						userIdentityDao.deleteIdentities(userId);
						
						UserPinDao userPinDao = new UserPinDao(context);
						userPinDao.updateClearPinById(userid);
						
						db4.execSQL("DELETE FROM UserSalt WHERE userid=?", args2);
						db4.close();
						db4 = databaseSalt.getWritableDatabase();
						
						Security.getKey(newpassword+Secure.ANDROID_ID);//Seguir orden apto para 4.0
						byte[] salt=Security.salt;
						
						String encsalt=Base64.encodeWebSafe(salt, true);
				
						if (db4 != null) {
							
							db4.execSQL("INSERT INTO UserSalt (userid, salt,email) "
									+ "VALUES ('"
									+ userid
									+ "', '"
									+ encsalt + "', '" + email +"')");

						}
						db4.close();
						databaseSalt.close();
						identityCounter="0";
						globalSalt=encsalt;
						password=newpassword;
						Toast.makeText(
								context,
								context.getString(R.string.toast_done),
								Toast.LENGTH_LONG).show();
						pDialog.cancel();
						
						AccountPref accountPref = new AccountPref(context);
						accountPref.clear();
						
						new Pref(context).setScanStart(false);
						
						Intent intent = new Intent(ChangePassword.this, SignInActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						finish();
					} else {

						if (json.getString("message").equals("Authentication failed")) {
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_email_incorrect),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						}
						else if (json.getString("message").equals("Invalid request")) {
							Toast.makeText(
									context,
									context.getString(R.string.toast_fault_password_length),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						}
						else
						{
							Toast.makeText(context,
									context.getString(R.string.toast_server_error),//Another type of JSON Error Response
									Toast.LENGTH_SHORT).show();
							pDialog.cancel();
						}
						

					}
				} else {
					Toast.makeText(context,
							context.getString(R.string.toast_server_error),
							Toast.LENGTH_LONG).show();
					pDialog.cancel();
				}
			} catch (JSONException e) { // TODO Auto-generated catch block
				
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					CambiarPassword.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

}
