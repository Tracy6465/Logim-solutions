package com.logim.activity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings.Secure;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.logim.db.UserIdentityDao;
import com.logim.db.UserInfoDao;
import com.logim.db.UserPinDao;
import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.camera.Identity;
import com.logim.main.social.SocialActivity;
import com.logim.main.utils.AES128;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Base64DecoderException;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.service.AppNotiService;
import com.logim.utils.AccountPref;
import com.logim.utils.Pref;
import com.logim.view.ProgressView;
import com.logim.vo.UserInfoVo;
import com.logim.vo.UserPinVo;

/**
 * 
 * @author David Prieto Rivera Activity for Settings screen
 * 
 */
public class SettingsActivity extends PreferenceActivity implements
		CompoundButton.OnCheckedChangeListener {

	// ATRIBUTES
	boolean active = false;
	ProgressDialog pDialog;
	String user;
	String userId;
	String appid;
	String signaturekey;
	String limit;
	String type;
	String expires;
	int seleccion;
	int identityCounter;
	String passwordUser;
	String salt;
	UserInfoVo userInfo;
	UserPinVo userPin;
	Context context;
	private Preference userPre;
	private Preference typePre;
	private Preference expiresPre;
	private Preference identityPre;
	
	private Builder logoutBuilder;
	private Dialog logoutDialog;
//	private Preference logoutPre;
	private Preference changePre;
	
	private Preference invitationPre;
	private String backupTime;
	private Preference lastPre;
	private Preference backupPre;
	private Preference restorePre;
	
	private ListPreference languagePre;
	private EditText pinEt;
	private Builder pinBuilder;
	private Dialog pinDialog;
	private CheckBoxPreference pinCb;
	private final int SHOW_INPUT_METHOD = 1;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
                switch (msg.what) {
                case SHOW_INPUT_METHOD:
                        InputMethodManager imm = (InputMethodManager) SettingsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        break;
                }
        }

	};

	private CheckBoxPreference scanCb;
	
	private Preference tutorialsPre;
	private Preference aboutPre;
	
	SharedPreferences pre;
	final String PRE_FILENAME="WeiboAuth";
	Editor editor;
	
	Pref pref;

	// ACTIVITY LIFECYCLE
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		this.addPreferencesFromResource(R.xml.config_layout);
		
		// Center the title
		SysApplication.getInstance().addActivity(this); 
		context = SettingsActivity.this;
		
		Intent intentReceived = getIntent();
		userId = intentReceived.getStringExtra("userid");
		identityCounter = intentReceived.getIntExtra("identities", 0);
		passwordUser = intentReceived.getStringExtra("password");
		salt = intentReceived.getStringExtra("salt");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		backupTime = getString(R.string.pref_summary_last);
				 
		getUserInfo();
		
		userPre = (Preference) findPreference("config_user");
		userPre.setSummary(userInfo.getUseremail());
		
		typePre = (Preference) findPreference("config_type");
		typePre.setSummary(userInfo.getType());
		
		expiresPre = (Preference) findPreference("config_expires");
		expiresPre.setSummary(getTime(Long.parseLong(userInfo.getPremexp())));
		
		identityPre = (Preference) findPreference("config_max");
		String iden = getString(R.string.text_storage_1) + 
				identityCounter + 
				getString(R.string.text_storage_2);
		if (userInfo.getType().equals("premium")) {
			iden += getString(R.string.text_storage_infinity);
		}
		else {
			iden += userInfo.getIdentitiesstoragelimit();
		}
		identityPre.setSummary(iden);
		
		changePre = (Preference) findPreference("config_change");
		changePre.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				changePassword();
				return false;
			}
		});
		
		logoutBuilder = new Builder(this);
		logoutBuilder.setTitle(getString(R.string.text_warring));
		logoutBuilder.setMessage(getString(R.string.text_sign_out));
		logoutBuilder.setPositiveButton(getString(R.string.button_sign_out), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				signOut();
			}
		})
		.setNegativeButton(getString(R.string.button_cancel), null);
		logoutDialog = logoutBuilder.create();
		
//		logoutPre = (Preference) findPreference("config_signout");
//		logoutPre.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				logoutDialog.show();
//				return false;
//			}
//		});
		
		invitationPre = (Preference) findPreference("config_invitation");
		invitationPre.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				toSocial();
				return false;
			}
		});
		
		new Thread() {

			@Override
			public void run() {
				super.run();
				checkBackUp();
				handler.sendEmptyMessage(0);
			}
			
		}.start();
		lastPre = (Preference) findPreference("config_last");
		
		backupPre = (Preference) findPreference("config_backup");
		backupPre.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				doBackUp();
				return false;
			}
		});
		
		restorePre = (Preference) findPreference("config_restore");
		restorePre.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				doRestore();
				return false;
			}
		});
		
		getUserPin();
		
		pref = new Pref(context);
		scanCb = (CheckBoxPreference) findPreference("config_scan");
		scanCb.setChecked(pref.isScanStart());
		scanCb.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				pref.setScanStart((Boolean)newValue);
				
				return true;
			}
		});
		
		languagePre = (ListPreference) findPreference("config_language");
		languagePre.setValue(pref.getLanguage());
		languagePre.setSummary(languagePre.getEntry());
		languagePre.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String language = (String)newValue;
				pref.setLanguage(language);
				
				Resources res = context.getResources();
				Configuration config = res.getConfiguration();
				DisplayMetrics dm = res.getDisplayMetrics();
				config.locale = new Locale(language);
				res.updateConfiguration(config, dm);
				
				languagePre.setValue(language);
				languagePre.setSummary(languagePre.getEntry());
				
				Intent i = new Intent("com.logim.language");
				sendBroadcast(i);
			    SettingsActivity.this.finish();
			    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				
				return true;
			}
		});
		
		pinEt = new EditText(this);
		pinEt.setSingleLine(true);
		pinEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
		pinEt.setInputType(InputType.TYPE_CLASS_NUMBER);
		pinEt.setHint(R.string.dialog_pin_hint);
		
		pinBuilder = new Builder(this);
		pinBuilder.setView(pinEt)
		.setTitle(getString(R.string.dialog_pin_title))
		.setPositiveButton(getString(R.string.button_save), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!userPin.getPin().equals("null")) {
					pinEt.setText(userPin.getPin());
				}
				
				save();
			}
		})
		.setNegativeButton(getString(R.string.button_cancel), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pinCb.setChecked(false);
				pinEt.setText("");
			}
		});
		pinDialog = pinBuilder.create();
		
		pinCb = (CheckBoxPreference) findPreference("config_pin");
		if (userPin.getHasPin().equals("true")) {
			pinCb.setChecked(true);
			if (!userPin.getPin().equals("null")) {
				try {
					userPin.setPin(
							Security.decrypt(Base64.decodeWebSafe(salt), userPin.getPin(),
							passwordUser + Secure.ANDROID_ID));
				} catch (Base64DecoderException e) {
					e.printStackTrace();
				}
				pinEt.setText(userPin.getPin());
			}
		} else {
			pinCb.setChecked(false);
		}
		pinCb.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				active = (Boolean)newValue;
				if (active) {
					pinDialog.show();
					mHandler.sendEmptyMessage(SHOW_INPUT_METHOD);
				}
				else {
					save();
				}
				
				return true;
			}
		});
		
		tutorialsPre = (Preference) findPreference("config_tutorials");
		tutorialsPre.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsActivity.this, TutorialsVideoActivity.class); 
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				return false;
			}
		});
		
		aboutPre = (Preference) findPreference("config_about");
		aboutPre.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsActivity.this, AboutActivity.class); 
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				return false;
			}
		});
	}

	private void getUserInfo() {
		UserInfoDao userInfoDao = new UserInfoDao(this);
		userInfo = userInfoDao.selectUserInfo(userId);
		user = userInfo.getUseremail();
		appid = userInfo.getAppid();
		type = userInfo.getType();
		limit = userInfo.getIdentitiesstoragelimit();
		signaturekey = userInfo.getSignaturekey();
		expires = userInfo.getPremexp();
		userInfoDao.close();
	}
	
	private void getUserPin() {
		UserPinDao userPinDao = new UserPinDao(this);
		userPin = userPinDao.selectUserPinForSet(userId, userInfo.getUseremail());
		userPinDao.close();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
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
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("userid", userId);
		intent.putExtra("password", passwordUser);
		
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		
	}
	
	// ACTIVITY METHODS

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

	}

	/**
	 * 
	 * Save the changes in Settings
	 */
	private void save() {
		String code = pinEt.getText().toString();
		UserPinDao userPinDao = new UserPinDao(context);
		
		if (active == true) {
			if (code.equals("")) {

				Toast.makeText(pinEt.getContext(),
						(pinEt.getContext()).getString(R.string.toast_fault_pin_empty),
						Toast.LENGTH_LONG).show();
			} else if (code.length() != 4) {
				Toast.makeText(pinEt.getContext(),
						(pinEt.getContext()).getString(R.string.toast_fault_pin_4),
						Toast.LENGTH_LONG).show();
			} else {
				// Encriptar code
				SecretKey key = null;
				try {
					key = Security.getExistingKey(passwordUser
							+ Secure.ANDROID_ID, Base64.decodeWebSafe(salt));
				} catch (Base64DecoderException e) {
					e.printStackTrace();
				}
				String codeEncripted = Security.cipher(code, key);
				byte[] iv = Security.iv;
				codeEncripted = Base64.encodeWebSafe(iv, true) + "]"
						+ codeEncripted;
				
				userPin.setPin(codeEncripted);
				userPin.setHasPin("true");
				userPinDao.updateUserPin(userPin);
				userPinDao.close();
				
				userPin.setPin(pinEt.getText().toString());
				
				Toast.makeText(
						pinEt.getContext(),
						(pinEt.getContext())
								.getString(R.string.toast_pin_saved),
						Toast.LENGTH_LONG).show();
			}
		} else {
			userPin.setPin("null");
			userPin.setHasPin("false");
			userPinDao.updateUserPin(userPin);
			userPinDao.close();
			
			Toast.makeText(pinEt.getContext(),
					(pinEt.getContext()).getString(R.string.toast_pin_saved),
					Toast.LENGTH_LONG).show();
		}
		userPinDao.close();
	}
	
	private void toSocial() {
		Intent intent = new Intent(SettingsActivity.this, SocialActivity.class);
		intent.putExtra("userid", userId);
		intent.putExtra("password", passwordUser);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler =new Handler(){ 
		@Override 
		public void handleMessage(Message msg){ 
			super.handleMessage(msg); 
			if (!backupTime.equals("") && !backupTime.equals(null)) {
				lastPre.setSummary(backupTime);
			}
		} 
		 
	}; 
		
	@SuppressLint("SimpleDateFormat")
	private String checkBackUp() {
		ArrayList<Pair> listParam = new ArrayList<Pair>();
		Pair p = new Pair("appid", userInfo.getAppid());
		listParam.add(p);
		String nounce = Utils.getCadenaAlfanumAleatoria(20);
		Credential credentials = new Credential(userInfo.getAppid(), nounce,
				"app/backupudatainfo");
		credentials.setSignature(userInfo.getSignaturekey());
		String signature = credentials.getSignature();
		p = new Pair("nounce", nounce);

		listParam.add(p);
		p = new Pair("signature", signature);
		listParam.add(p);
		String url = "https://service.mylogim.com/api1/app/backupudatainfo"
				+ "?appid=" + userInfo.getAppid() + "&nounce=" + nounce
				+ "&signature=" + signature;
		String result = Utils
				.doHttpRequest(3, listParam, url, "GET", false);
		
		if (!result.equals("")) {
			try {
				JSONObject json = new JSONObject(result);
				Long timem = json.getLong("info");
				backupTime =  getTime(timem);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getTime(Long timem) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timem);
		
		return formatter.format(calendar.getTime());
	}
	
	private void doBackUp() {
		LinkedList<Identity> idList = new LinkedList<Identity>();

		Database idDatabase = new Database(this, "DBIdentities", null, 1);
		SQLiteDatabase db = idDatabase.getReadableDatabase();
		String[] args = { userId };
		Cursor c = db
				.rawQuery(
						" SELECT codigo, nombre, email, password, url, creation, visited, count  FROM Identities WHERE userid=?",
						args);
		if (c.moveToFirst()) {
			// Recorremos el cursor hasta que no haya más
			// registros
			do {
				String identifier = c.getString(0);
				String name = c.getString(1);
				String login = c.getString(2);
				String password = c.getString(3);
				try {
					password = Security.decrypt(Base64.decodeWebSafe(salt),
							password, passwordUser + Secure.ANDROID_ID);
					login = Security.decrypt(Base64.decodeWebSafe(salt), login,
							passwordUser + Secure.ANDROID_ID);
				} catch (Base64DecoderException e) {
					e.printStackTrace();
				}
				
				String url = c.getString(4);
				String creation = c.getString(5);
				String lastUse = c.getString(6);
				String countUse = c.getString(7);
				Identity id = new Identity(identifier, name, url, login,
						password, creation, lastUse, countUse);
				idList.add(id);
			} while (c.moveToNext());
		}
		db.close();
		String nounce = Utils.getCadenaAlfanumAleatoria(20);
		Credential credentials = new Credential(userInfo.getAppid(), nounce,
				"app/backupudata");
		credentials.setSignature(userInfo.getSignaturekey());
//		String signature = credentials.getSignature();
		JSONObject json = new JSONObject();
		JSONArray jsonlist = new JSONArray();

		for (int i = 0; i < idList.size(); i++) {
			jsonlist.put(idList.get(i).generateJsonComplete());
		}

		try {
			json.put("identities", jsonlist);
			json.put("hasPin", userPin.getHasPin());
			json.put("pin", userPin.getPin());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String data = json.toString();
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String[] params = new String[] { data };
			pDialog = new ProgressView(context);
			new BackUp().execute(params);

		} else {
			Toast.makeText(context, context.getString(R.string.toast_no_network),
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 
	 */
	private void doRestore() {
		// String url = "https://service.mylogim.com/api1/app/restoreudata";
		String url = "https://service.mylogim.com/api1/app/backupudatainfo";
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String[] params = new String[] { url };
			pDialog = new ProgressView(context);
			// new Restore().execute(params);
			new CheckBackUp().execute(params);

		} else {
			Toast.makeText(context, context.getString(R.string.toast_no_network),
					Toast.LENGTH_LONG).show();
		}

	}
	
	private void changePassword() {
		Intent intent = new Intent(this, ChangePassword.class);
		intent.putExtra("email", userInfo.getUseremail());
		intent.putExtra("password", passwordUser);
		intent.putExtra("userid", userId);
		intent.putExtra("identities", identityCounter);
		intent.putExtra("salt", salt);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

	}
	
	public void signOut(View v) {
		logoutDialog.show();
	}
	
	/**
	 * 
	 * The user signs out of his/her account and returns to the Welcome View
	 */
	private void signOut() {
		UserInfoDao userInfoDao = new UserInfoDao(this);
		userInfoDao.deleteUserInfo(userId);
		
		UserIdentityDao userIdentityDao = new UserIdentityDao(this);
		userIdentityDao.deleteIdentities(userId);
		
		pref.setScanStart(false);
		
		String url = "https://service.mylogim.com/api1/app/deregister";
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String[] args = new String[] { url };
			pDialog = new ProgressView(this);
			new SignOutUser().execute(args);
		} else {
			Toast.makeText(this, this.getString(R.string.toast_no_network),
					Toast.LENGTH_LONG).show();
		}

	}

	private class SignOutUser extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("appid", userInfo.getAppid());
			listParam.add(p);
			String nounce = Utils.getCadenaAlfanumAleatoria(20);
			Credential credentials = new Credential(userInfo.getAppid(), nounce,
					"app/deregister");
			credentials.setSignature(userInfo.getSignaturekey());
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
						AccountPref accountPref = new AccountPref(context);
						accountPref.clear();
						
						pre=getSharedPreferences(PRE_FILENAME, MODE_PRIVATE);
						Editor editor = pre.edit();
						editor.clear();
						editor.commit();
						
						pre=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						editor = pre.edit();
						editor.clear();
						editor.commit();
						
						pDialog.cancel();
						
						Intent intent = new Intent(context, AppNotiService.class);
					    stopService(intent);
					    
						intent = new Intent(context, WelcomeActivity.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
					else
					{
						Toast.makeText(context, context.getString(R.string.toast_server_error),
								Toast.LENGTH_LONG).show();
						pDialog.cancel();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				

			} else {
				Toast.makeText(context, context.getString(R.string.toast_server_error),
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

	private class BackUp extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("appid", appid);
			listParam.add(p);
			String nounce = Utils.getCadenaAlfanumAleatoria(20);
			Credential credentials = new Credential(appid, nounce,
					"app/backupudata");
			credentials.setSignature(signaturekey);
			String signature = credentials.getSignature();
			p = new Pair("nounce", nounce);

			listParam.add(p);
			p = new Pair("signature", signature);
			listParam.add(p);

			AES128 aes = new AES128();
			// String key = Utils.getCadenaAlfanumAleatoria(10);
			SecretKey key = null;
			String encsalt = "";

			key = Security.getKey(passwordUser);
			byte[] salt = Security.salt;
			encsalt = Base64.encodeWebSafe(salt, true);

			String data = "";
			String initialVector = "";
			try {
				data = aes.encrypt(params[0], key);
				byte[] iv = AES128.iv;
				initialVector = Base64.encodeWebSafe(iv, true);
				

			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}

			p = new Pair("data", data);
			listParam.add(p);
			p = new Pair("enciv", initialVector);
			listParam.add(p);
			p = new Pair("encsalt", encsalt);
			listParam.add(p);
			p = new Pair("version", "1");
			listParam.add(p);

			String result = Utils.doHttpRequest(7, listParam,
					"https://service.mylogim.com/api1/app/backupudata", "POST",
					false);
			return result;

		}

		protected void onPostExecute(String result) {

			if (!result.equals("")) {
				JSONObject json;
				try {
					json = new JSONObject(result);
					
					if (json.getString("status").equals("success")) {
						Toast.makeText(context,
								context.getString(R.string.toast_success_backup),
								Toast.LENGTH_LONG).show();
						pDialog.cancel();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(context, context.getString(R.string.toast_server_error),
						Toast.LENGTH_LONG).show();
				pDialog.cancel();
			}
			
			new Thread() {

				@Override
				public void run() {
					super.run();
					checkBackUp();
					handler.sendEmptyMessage(0);
				}
				
			}.start();

		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					BackUp.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

	private class CheckBackUp extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {

			return checkBackUp();

		}

		protected void onPostExecute(String result) {

			pDialog.cancel();
			if (!result.equals("")) {
				JSONObject json;
				try {
					json = new JSONObject(result);
				
					if (json.getString("status").equals("success")) {
						String info = json.getString("info");
						if (!info.equals("")) {
							String url = "https://service.mylogim.com/api1/app/restoreudata";

							ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
							NetworkInfo networkInfo = connMgr
									.getActiveNetworkInfo();
							
							if (networkInfo != null
									&& networkInfo.isConnected()) {
								String[] params = new String[] { url };
								pDialog = new ProgressView(context);
								new Restore().execute(params);
							} else {
								Toast.makeText(context,
										context.getString(R.string.toast_no_network),
										Toast.LENGTH_LONG).show();
							}

						} else {
							Toast.makeText(context,
									context.getString(R.string.toast_no_backup),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(context, context.getString(R.string.toast_server_error),
						Toast.LENGTH_LONG).show();
				pDialog.cancel();
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					CheckBackUp.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

	private class Restore extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("appid", appid);
			listParam.add(p);
			String nounce = Utils.getCadenaAlfanumAleatoria(20);
			Credential credentials = new Credential(appid, nounce,
					"app/restoreudata");
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
				JSONObject json;
				try {

					json = new JSONObject(result);

					

					if (json.getString("status").equals("success")) {
						String info = json.getString("info");
						JSONObject jsoninfo = new JSONObject(info);
						String data = jsoninfo.getString("data");
						String encsalt = jsoninfo.getString("encsalt");
						String enciv = jsoninfo.getString("enciv");
						try {
							AES128 aes = new AES128();
							byte[] iv = Base64.decodeWebSafe(enciv);
							byte[] saltenc = Base64.decodeWebSafe(encsalt);
							byte[] datos = Base64.decodeWebSafe(data);
							SecretKey key = Security.getExistingKey(
									passwordUser, saltenc);
							String original = aes.decrypt(datos, key, iv);
							
							JSONObject jsonOriginal2 = new JSONObject(original);
							
							String hasPinRestore= jsonOriginal2.getString("hasPin");
							String pinRestore= jsonOriginal2.getString("pin");
							Database database = new Database(
									SettingsActivity.this, "DBUserPin", null, 1);
							SQLiteDatabase db = database.getWritableDatabase();
							// Si hemos abierto correctamente la base de datos

							// Encriptar code
							SecretKey key2 = null;
							try {
								key2 = Security.getExistingKey(passwordUser
										+ Secure.ANDROID_ID,
										Base64.decodeWebSafe(salt));
							} catch (Base64DecoderException e) {
								e.printStackTrace();
							}
							String codeEncripted = Security.cipher(pinRestore,
									key2);
							byte[] iv2 = Security.iv;
							codeEncripted = Base64.encodeWebSafe(iv2, true)
									+ "]" + codeEncripted;

						
							if (db != null) {

								if (hasPinRestore.equals("true")) {
									
									db.execSQL("UPDATE UserPin SET pin = '"
											+ codeEncripted
											+ "' WHERE userid ='" + userId
											+ "'");
									db.execSQL("UPDATE UserPin SET hasPin = '"
											+ "true" + "' WHERE userid ='"
											+ userId + "'");

								} else {
									
									db.execSQL("UPDATE UserPin SET pin = '"
											+ "null" + "' WHERE userid ='"
											+ userId + "'");
									db.execSQL("UPDATE UserPin SET hasPin = '"
											+ "false" + "' WHERE userid ='"
											+ userId + "'");

								}
							}
							db.close();
							pDialog.cancel();
							
							JSONArray jsonOriginal = jsonOriginal2
									.getJSONArray("identities");
							int lim = Integer.parseInt(limit);
							if (jsonOriginal.length() + identityCounter <= lim) {
								Dialog d = crearDialogoSeleccion(jsonOriginal);
								
								d.show();
							} else {
								Dialog d = crearDialogoConfirmacion(jsonOriginal);
							
								d.show();
							}

						} catch (KeyException e) {
							e.printStackTrace();
						} catch (InvalidAlgorithmParameterException e) {
							e.printStackTrace();
						} catch (IllegalBlockSizeException e) {
							e.printStackTrace();
						} catch (BadPaddingException e) {
							e.printStackTrace();
						} catch (GeneralSecurityException e) {
							e.printStackTrace();
						} catch (Base64DecoderException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Toast.makeText(context,
								context.getString(R.string.toast_success_restore),
								Toast.LENGTH_LONG).show();
						pDialog.cancel();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(context, context.getString(R.string.toast_server_error),
						Toast.LENGTH_LONG).show();
				pDialog.cancel();
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					Restore.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

	private Dialog crearDialogoSeleccion(final JSONArray jsonOriginal) {
		final String[] items = { context.getString(R.string.option_delete_existing),
				context.getString(R.string.option_mantener_existentes) };

		//pDialog.cancel();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(this.getString(R.string.dialog_order));
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						
//						if (item == 0) {
//							seleccion = 0;
//							restoreIdentities(seleccion, jsonOriginal);
//						} else if (item == 1) {
//							seleccion=1;
//							restoreIdentities(seleccion, jsonOriginal);
//						}
						seleccion = item;
						restoreIdentities(seleccion, jsonOriginal);
						// actualizar();
					}
				});

		return builder.create();
	}

	private Dialog crearDialogoConfirmacion(final JSONArray jsonOriginal) {
		// messageEliminar=message;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(context.getString(R.string.dialog_restore_continue));
		builder.setMessage(context.getString(R.string.dialog_restore_warning));
		builder.setPositiveButton(context.getString(R.string.button_accept),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						restoreIdentities(0, jsonOriginal);
						dialog.cancel();
					}
				});
		builder.setNegativeButton(context.getString(R.string.button_cancel),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		return builder.create();
	}

	private void restoreIdentities(int erase, JSONArray jsonOriginal) {
		Database database = new Database(context, "DBIdentities", null, 1);
		SQLiteDatabase db = database.getWritableDatabase();

		String[] args = { userId };
		if (erase == 0) {
			db.execSQL("DELETE FROM Identities WHERE userid=?", args);
		}
		db.close();
		for (int i = 0; i < jsonOriginal.length(); i++) {
			SQLiteDatabase db2 = database.getReadableDatabase();
			SQLiteDatabase db3 = database.getReadableDatabase();
			JSONObject identity;
			try {
				identity = jsonOriginal.getJSONObject(i);
				String name = identity.getString("name");
				String login = identity.getString("username");
				String password = identity.getString("password");
				String url = identity.getString("originUrl");
				String id = identity.getString("identifier");
				String creation = identity.getString("creation");
				String lastUse = identity.getString("lastUse");
				String countUse = identity.getString("countUse");
				String[] args2 = { id, userId };
				Cursor c = db2.rawQuery(
						"SELECT * FROM Identities WHERE codigo=? AND userid=?",
						args2);
				if (!c.moveToFirst()) {
					
					SecretKey key = null;
					try {
						key = Security
								.getExistingKey(passwordUser
										+ Secure.ANDROID_ID,
										Base64.decodeWebSafe(salt));
					} catch (Base64DecoderException e) {
						e.printStackTrace();
					}
					String passwordEncripted = Security.cipher(password, key);
					byte[] iv = Security.iv;
					passwordEncripted = Base64.encodeWebSafe(iv, true) + "]"
							+ passwordEncripted;
					db3 = database.getWritableDatabase();
					String loginEncripted = Security.cipher(login, key);
					iv = Security.iv;
					loginEncripted = Base64.encodeWebSafe(iv, true) + "]"
							+ loginEncripted;
					db3.execSQL("INSERT INTO Identities (codigo, nombre, email, password, url, creation, visited, count, userid,user) "
							+ "VALUES ('"
							+ id
							+ "', '"
							+ name
							+ "', '"
							+ loginEncripted
							+ "', '"
							+ passwordEncripted
							+ "', '"
							+ url
							+ "', '"
							+ creation
							+ "', '"
							+ lastUse
							+ "', '"
							+ countUse
							+ "', '"
							+ userId
							+ "', '"
							+ user
							+ "')");
					db3.close();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			db2.close();
		}
		// db2.close();
		// db.close();
		goBack();

	}

}
