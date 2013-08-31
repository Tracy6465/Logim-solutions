package com.logim.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.logim.main.R;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.net.GetUserInfo;
import com.logim.utils.AccountPref;
import com.logim.utils.Pref;
import com.logim.view.ProgressView;

/**
 * 
 * @author David Prieto Rivera 
 * Activity of the user account activation.
 * the user receives an email with a code and he must write it on this view
 * 
 */
public class ActivationActivity extends FileActivity {

	// ATRIBUTES
	public String pin;
	String password;
	ProgressDialog pDialog;
	EditText editText;
	String identifier;
	String name;
	String encsalt;
	Context context;
	
	AccountPref accountPref;

	// ACTIVITY LIFECYLE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activation);
		context=ActivationActivity.this;
		
		accountPref = new AccountPref(context);
		
		SysApplication.getInstance().addActivity(this); 

		//Receives the user password as a parameter
		Intent intent = getIntent();
		password = intent.getStringExtra("password");
		identifier=intent.getStringExtra("identifier");
		name=intent.getStringExtra("name");
		
		TextView texto = (TextView) findViewById(R.id.textActivation);
		texto.setTypeface(null, Typeface.BOLD);
	}

	// ACTIVITY METHODS

	/**
	 * 
	 * Checks if the code is correct
	 */
	public void ActivateUser(View v) {
		
	    editText = (EditText) findViewById(R.id.introActivation);
		String pin = editText.getText().toString();
		String url = "https://service.mylogim.com/api1/user/activate";
		
		
		if (!pin.equals("")) {
			
			//We check if a network connection is available
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			
			if (networkInfo != null && networkInfo.isConnected()) {
				
				//If a network connection is available we pass the necessary parameters (url, userid, password and code)		
				String[] args = new String[] { url, identifier, password, pin };
				
				//Creation of a dialog
				pDialog = new ProgressView(context);
				 
				//Excute the HTTP method in a new thread
				new ActivateUserAccount().execute(args);
			} else {
				Toast.makeText(context,context.getString(R.string.toast_no_network),//ERROR No network connection available
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context,
					(context).getString(R.string.toast_fault_pin_empty),//Code field is empty
					Toast.LENGTH_SHORT).show();

		}

	}
	
	private class RegisterApp extends AsyncTask<String, Integer, String> {

		String signaturek;
		String identifier;
	

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			identifier = params[1];
			Pair p = new Pair("userid", params[1]);
			listParam.add(p);
			p = new Pair("password", params[2]);
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
						accountPref.setEmail(name);
						accountPref.setPassword(password);
						
						String appid = json.getString("info");
						
						Intent intent = new Intent(context,
								TutorialsActivity.class);
						intent.putExtra("userid", identifier);
						intent.putExtra("password", password);
						
						new GetUserInfo(ActivationActivity.this, context, name, password, appid, signaturek, intent);
					} else {
						Toast.makeText(context,
								context.getString(R.string.toast_server_error),//Another type of JSON Error Response
								Toast.LENGTH_SHORT).show();
						pDialog.cancel();
					}
				} else {
					Toast.makeText(context,
							context.getString(R.string.toast_server_error),//Another type of JSON Error Response
							Toast.LENGTH_SHORT).show();
					pDialog.cancel();
					
				}
			} catch (JSONException e) { // TODO Auto-generated catch block
			
				e.printStackTrace();
			}

		}
	}
	
	// MENU
	/**
	 * Overrides the Back Button's function of the Phone
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
	/**
	 * 
	 *Go to the previous View
	 */
	private void goBack()
	{
		Intent intent = new Intent(context, CreateAccountActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 
	 * @author David Prieto Riversa
	 * Class for doing the Activation User Account in a new thread separated from the UI
	 *
	 */
	private class ActivateUserAccount extends AsyncTask<String, Integer, String> {

		
		@Override
		/**
		 * Does the HTTP Request and returns the response 
		 */
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("identifier", params[1]);
			listParam.add(p);
			
			p = new Pair("password", params[2]);
			listParam.add(p);
	
			p = new Pair("actcode", params[3]);
			listParam.add(p);
			
			String result = Utils.doHttpRequest(3, listParam,
					params[0],
					"POST",false);
			return result;

		}

		/**
		 * Checks the JSON Response
		 */
		protected void onPostExecute(String result) {
			try {
				
				if (!result.equals("")) {
					JSONObject json = new JSONObject(result);
			
					if (json.getString("status").equals("success")) {//Successful, we insert the user data in database and we register the app				
						//if (!RegisterApp(identifier, password)) {
							Security.getKey(password+Secure.ANDROID_ID);//Seguir orden apto para 4.0
							byte[] salt=Security.salt;
							encsalt=Base64.encodeWebSafe(salt, true);
							Database database = new Database(context, "DBUserSalt",
									null, 1);
							SQLiteDatabase db = database.getWritableDatabase();
									
							// Si hemos abierto correctamente la base de datos
							if (db != null) {
			
								db.execSQL("INSERT INTO UserSalt (userid, salt,email) "
										+ "VALUES ('"
										+ identifier
										+ "', '"
										+ encsalt + "', '" +  name +"')");

							}
							db.close();

							String url = "https://service.mylogim.com/api1/app/register";
							ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
							NetworkInfo networkInfo = connMgr
									.getActiveNetworkInfo();
							if (networkInfo != null
									&& networkInfo.isConnected()) {
								String[] args = new String[] { url, identifier,
										password, encsalt };
								pDialog = new ProgressView(getBaseContext());
								new RegisterApp().execute(args);
							} else {
								Toast.makeText(context,
										context.getString(R.string.toast_no_network),
										Toast.LENGTH_SHORT).show();
							}
					} else {
						if (json.getString("message").equals("Invalid request")) {//Parameters not valid
							Toast.makeText(context,
									context.getString(R.string.toast_fault_pin),
									Toast.LENGTH_SHORT).show();
							pDialog.cancel();

						} else {
							Toast.makeText(context,
									context.getString(R.string.toast_server_error),//Another type of JSON Error Response
									Toast.LENGTH_SHORT).show();
							pDialog.cancel();
						}

					}
				} else {
					Toast.makeText(context, context.getString(R.string.toast_server_error),
							Toast.LENGTH_SHORT).show();//HTTP ERROR
					pDialog.cancel();
				}
			} catch (JSONException e) { // TODO Auto-generated catch block
				
				e.printStackTrace();
			}

		}

		/**
		 * We show the processing dialog when the request begin
		 */
		@Override
		protected void onPreExecute() {

			
			  pDialog.setOnCancelListener(new OnCancelListener() {
			 
			 @Override public void onCancel(DialogInterface dialog) {
			ActivateUserAccount.this.cancel(true); } });
			 

			pDialog.setProgress(0);
			pDialog.show();
		}

	}
}
