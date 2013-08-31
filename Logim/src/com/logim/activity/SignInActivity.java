package com.logim.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.utils.AccountPref;
import com.logim.view.ProgressView;

/**
 * 
 * @author David Prieto Rivera Activity to sign in the user into his/her account
 */
public class SignInActivity extends FileActivity {

	//ATRIBUTES
	EditText userEmail;
    EditText userPassword;
	ProgressDialog pDialog;
	String password;
	String email;
	String appid;
	String signaturekey;
	String type;
	String limit;
	String identifier;
	Context context;
	String hasMail;
	String hasTwitter;
	String hasFace;
	String hasWeibo;
	String premexp;
	boolean logout;
	
	AccountPref accountPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		context = SignInActivity.this;
		
		logout = false;
		accountPref = new AccountPref(context);
		userEmail=(EditText)findViewById(R.id.signin_email);
		userPassword=(EditText)findViewById(R.id.signin_password);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		SysApplication.getInstance().addActivity(this); 
		
	}
	
	public void Sign_In(View v) {
		boolean validParameters = true;

		// get Parameters
		email = userEmail.getText().toString();
		password = userPassword.getText().toString();

		// check if parameters are valid
		validParameters = checkParameters(email, password);

		if (validParameters) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {

				pDialog = new ProgressView(context);

				String url = "https://service.mylogim.com/api1/user/info?email="
						+ email + "&password=" + password;
				String[] args = new String[] { url };
				new SignInUser().execute(args);

			} else {
				Toast.makeText(context, context.getString(R.string.toast_no_network),
						Toast.LENGTH_LONG).show();
			}

		}

	}

	private class RegisterApp extends AsyncTask<String, Integer, String> {

		String signaturek;
		String identifier;
		String password;
		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			identifier = params[1];
			Pair p = new Pair("userid", params[1]);
			listParam.add(p);
			p = new Pair("password", params[2]);
			password=params[2];
			listParam.add(p);
			signaturek = Utils.getCadenaAlfanumAleatoria(20);
			p = new Pair("signaturekey", signaturek);
			listParam.add(p);
			p = new Pair("osname", "Android");
			listParam.add(p);

			p = new Pair("osversion", Build.VERSION.RELEASE);
			listParam.add(p);
			p = new Pair("devicename", Build.DEVICE);
			
			listParam.add(p);
			p = new Pair("apntoken", Utils.getCadenaAlfanumAleatoria(10));
			listParam.add(p);

			String result = Utils.doHttpRequest(6, listParam, params[0],
					"POST", false);
			return result;

		}

		protected void onPostExecute(String result) {

			try {

				if (!result.equals("")) {
					
					JSONObject json = new JSONObject(result);
					
				
					if (json.getString("status").equals("success")) {
						String appid = json.getString("info");
					
						Database database = new Database(context, "DBUserInfo",
								null, 1);
						SQLiteDatabase db = database.getWritableDatabase();
						// Si hemos abierto correctamente la base de datos
						if (db != null) {

							
							db.execSQL("INSERT INTO UserInfo (user, userid, appid, type, limite, signaturekey, mail, facebook, twitter, weibo, premexp) "
									+ "VALUES ('"
									+ email
									+ "', '"
									+ identifier
									+ "', '"
									+ appid
									+ "', '"
									+ type
									+ "', '"
									+ limit + "', '" + signaturek + "', '"+ hasMail + "', '" + hasFace + "', '"+hasTwitter + "', '" + hasWeibo + "', '" + premexp + "')");

						}
						db.close();
						
						database = new Database(context, "DBUserSalt",
								null, 1);
						db = database.getReadableDatabase();
				
						// Si hemos abierto correctamente la base de datos
						String encsalt=null;
						if (db != null) {
							String[] args={identifier};
							Cursor c = db.rawQuery("SELECT salt FROM UserSalt WHERE userid=?",
									args);
							
							if (c.moveToFirst()) {
								encsalt=c.getString(0);
							}
							else
							{
								Security.getKey(password+Secure.ANDROID_ID);//Seguir orden apto para 4.0
								byte[] salt=Security.salt;
								
								Database database2 = new Database(context, "DBUserSalt",
										null, 1);
								encsalt=Base64.encodeWebSafe(salt, true);
								SQLiteDatabase db2 = database2.getWritableDatabase();
								if (db2 != null) {
									
									db2.execSQL("INSERT INTO UserSalt (userid, salt,email) "
											+ "VALUES ('"
											+ identifier
											+ "', '"
											+ encsalt +  "', '"+ email + "')");

								}
								db2.close();
							}
						}
						db.close();
						
						Intent intent = new Intent(context,
								PinActivity.class);
						intent.putExtra("userid", identifier);
						intent.putExtra("tipo", "main");
						intent.putExtra("id","");
						intent.putExtra("password", password);
						intent.putExtra("salt",encsalt);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

					} else {
						Toast.makeText(
								context,
								context.getString(
										R.string.toast_server_error), Toast.LENGTH_LONG)
								.show();
						pDialog.cancel();

					}
				} else {
					Toast.makeText(
							context,
							context.getString(
									R.string.toast_server_error), Toast.LENGTH_LONG)
							.show();
					pDialog.cancel();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

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
	
	/**
	 * 
	 * Return to the previous View
	 */
	void goBack() {
		Intent intent = new Intent(context, EntranceActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		finish();
	}

	/**
	 * Go to the forgot Password View
	 */
	public void forgotPassword(View v) {
		Intent intent = new Intent(context, ForgotPwdActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	boolean checkParameters(String email, String password1) {
		Boolean valid = true;

		if (email.equals("")) {
			Toast.makeText(context, context.getString(R.string.toast_fault_email),
					Toast.LENGTH_LONG).show();
			valid = false;

		} else if (!email.contains("@")) {

			Toast.makeText(context, context.getString(R.string.toast_email_incorrect),
					Toast.LENGTH_LONG).show();
			valid = false;

		}
		if (password1.equals("")) {
			Toast.makeText(context, context.getString(R.string.toast_fault_password2_empty),
					Toast.LENGTH_LONG).show();
			valid = false;

		}

		return valid;

	}

	private class SignInUser extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			String result = Utils.doHttpRequest(2, listParam, params[0], "GET",
					false);
			return result;

		}

		protected void onPostExecute(String result) {
			try {
				if (!result.equals("")) {
					JSONObject json = new JSONObject(result);
				
					if (json.getString("status").equals("success")) {
						accountPref.setEmail(email);
						accountPref.setPassword(password);
						
						String info = json.getString("info");
						JSONObject json2 = new JSONObject(info);
						
						identifier = json2.getString("userid");
						limit = json2.getString("identitiesstoragelimit");
						type = json2.getString("type");
						hasMail=json2.getString("refemail");
						hasFace=json2.getString("reffacebook");
						hasTwitter=json2.getString("reftwitter");
						hasWeibo=json2.getString("refweibo");
						premexp = json2.getString("premexp");
						Log.d("Praeda", identifier + " " + limit + " " + type
								+ " " + password);

						if (logout) {
							Database database = new Database(
									context, "DBUserInfo", null,
									1);
							SQLiteDatabase db = database.getWritableDatabase();
							db.execSQL("DELETE FROM UserInfo ");
							db.close();
							String url = "https://service.mylogim.com/api1/app/deregister";
							String[] args = new String[] { url };
							new SignOutUser().execute(args);
						} else {
							String url = "https://service.mylogim.com/api1/app/register";
							ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
							NetworkInfo networkInfo = connMgr
									.getActiveNetworkInfo();
							if (networkInfo != null
									&& networkInfo.isConnected()) {
								String[] args = new String[] { url, identifier,
										password };
								new RegisterApp().execute(args);
							} else {
								Toast.makeText(context,
										context.getString(R.string.toast_no_network),
										Toast.LENGTH_LONG).show();
							}
						}

					} else {

						if (json.getString("message").equals("User not found")) {
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_email_incorrect),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						}
						else if (json.getString("message").equals(
								"Authentication failed")) {
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_fault_password_user),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						} else {
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_server_error), Toast.LENGTH_LONG)
									.show();
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
					SignInUser.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

	private class SignOutUser extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("appid", appid);
			listParam.add(p);
			String nounce = Utils.getCadenaAlfanumAleatoria(20);
			Credential credentials = new Credential(appid, nounce,
					"app/deregister");
			credentials.setSignature(signaturekey);
			String signature = credentials.getSignature();
			p = new Pair("nounce", nounce);

			listParam.add(p);
			p = new Pair("signature", signature);
			listParam.add(p);

			String result = Utils.doHttpRequest(3, listParam, params[0],
					"POST", false);
			return result;

		}

		protected void onPostExecute(String result) {

			if (!result.equals("")) {
				
				try {
					JSONObject json= new JSONObject(result);
			
				if(json.getString("status").equals("success"))
				{				
				String url = "https://service.mylogim.com/api1/app/register";
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					String[] args = new String[] { url, identifier, password };
					new RegisterApp().execute(args);
				} else {
					Toast.makeText(context,
							context.getString(R.string.toast_no_network),
							Toast.LENGTH_LONG).show();
				}
				}
				else
				{
					Toast.makeText(context,
							context.getString(R.string.toast_server_error),
							Toast.LENGTH_LONG).show();
					pDialog.cancel();
				}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(context,
						context.getString(R.string.toast_server_error),
						Toast.LENGTH_LONG).show();
				pDialog.cancel();
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					SignOutUser.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

}
