package com.logim.main.social;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.view.ProgressView;
import com.logim.vo.UserInfoVo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShareOnTwitterActivity extends Activity implements OnClickListener {
	// Constants
	/**
	 * Register your here app https://dev.twitter.com/apps/new and get your
	 * consumer key and secret
	 * */
	private SharedPreferences prefs;
	ProgressDialog pDialog;
	Button sendButton;
	EditText sendMessageEdt;
	String userid;
	String passwordUser;
	UserInfoVo userInfo;
	
	private final Handler mTwitterHandler = new Handler();
	final Runnable mUpdateTwitterNotification = new Runnable() {
		public void run() {
			String url = "https://service.mylogim.com/api1/app/refer/social";
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				String[] params = new String[] { url };
				pDialog= new ProgressView(ShareOnTwitterActivity.this);
				new ConfirmSocial().execute(params);
			}
			else {
				Toast.makeText(ShareOnTwitterActivity.this,
						ShareOnTwitterActivity.this.getString(R.string.toast_no_network),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		SysApplication.getInstance().addActivity(this);
		Intent intentReceived = getIntent();
		userid = intentReceived.getStringExtra("userid");
		Constants.setUserid(userid);
		passwordUser = intentReceived.getStringExtra("password");
		Constants.setPassword(passwordUser);
		initView();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		UserInfoDao userInfoDao = new UserInfoDao(this);
		userInfo = userInfoDao.selectUserInfo(userid);

	}
	private class ConfirmSocial extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("appid", userInfo.getAppid());
			listParam.add(p);
			String nounce = Utils.getCadenaAlfanumAleatoria(20);
			Credential credentials = new Credential(userInfo.getAppid(), nounce,
					"app/refer/social");
			credentials.setSignature(userInfo.getSignaturekey());
			String signature = credentials.getSignature();
			p = new Pair("nounce", nounce);

			listParam.add(p);
			p = new Pair("signature", signature);
			listParam.add(p);

			p = new Pair("reftype", "twitter");
			listParam.add(p);

			String result = Utils.doHttpRequest(3, listParam, params[0],
					"POST", false);
			return result;

		}

		protected void onPostExecute(String result) {

			if (!result.equals("")) {
				JSONObject json = null;
				try {
					json = new JSONObject(result);

					
					if (json.getString("status").equals("success")) {
						if (userInfo.getReftwitter().equals("false")) {
							Database database = new Database(ShareOnTwitterActivity.this,
									"DBUserInfo", null, 1);
							SQLiteDatabase db = database.getWritableDatabase();
							int lim = Integer.parseInt(userInfo.getIdentitiesstoragelimit());
							lim = lim + 5;
							userInfo.setIdentitiesstoragelimit(Integer.toString(lim));
							db.execSQL("UPDATE UserInfo SET limite = '" + userInfo.getIdentitiesstoragelimit()
									+ "' WHERE userid ='" + userid + "'");
							db.execSQL("UPDATE UserInfo SET twitter = '"
									+ "true" + "' WHERE userid ='" + userid
									+ "'");
							db.close();
							database.close();

							Toast.makeText(
									ShareOnTwitterActivity.this,
									ShareOnTwitterActivity.this
											.getString(R.string.toast_success_twitter),
									Toast.LENGTH_LONG).show();
							
							pDialog.cancel();
							

						}
						
						Toast.makeText(
								ShareOnTwitterActivity.this,
								ShareOnTwitterActivity.this
										.getString(R.string.toast_success_twitter),
								Toast.LENGTH_LONG).show();
						goBack();
					}
					else
					{
						Toast.makeText(ShareOnTwitterActivity.this,
								ShareOnTwitterActivity.this.getString(
										R.string.toast_server_error), Toast.LENGTH_LONG)// Server
																			// Error
								.show();
						
						
					}
					pDialog.cancel();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			else{
			Toast.makeText(
					ShareOnTwitterActivity.this,
					ShareOnTwitterActivity.this.getString(
							R.string.toast_server_error), Toast.LENGTH_LONG)// Server
																// Error
					.show();
			pDialog.cancel();
			
			}
		}
		
		@Override
		protected void onPreExecute() {
			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					ConfirmSocial.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		
		}
	}
	private void initView() {
		sendButton = (Button) findViewById(R.id.share_button);
		sendButton.setOnClickListener(this);
		sendMessageEdt = (EditText) findViewById(R.id.share_edit);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
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

	private void goBack() {
		this.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private boolean checkParameters(String string) {
		boolean valid = true;
		if (string.equals("")) {
			Toast.makeText(this, this.getString(R.string.toast_fault_text),
					Toast.LENGTH_LONG).show();
			valid = false;
		}
		return valid;
	}

	@Override
	public void onClick(View arg0) {
		if (isAuth()) {
			if (checkParameters(sendMessageEdt.getText().toString())) {
				  sendTweet();
			}
                  
		} else {
			Intent i = new Intent(getApplicationContext(),
					PrepareRequestTokenActivity.class);
			i.putExtra("tweet_msg", getTweetMsg());
			startActivity(i);
		}

	}

	private boolean isAuth() {
		return prefs.getBoolean("isAuth", false);
	}

	private String getTweetMsg() {
		return sendMessageEdt.getText().toString();
	}

	private void sendTweet() {
		Thread t = new Thread() {
			public void run() {

				try {
					String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
					String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
					
					AccessToken a = new AccessToken(token,secret);
					Twitter twitter = new TwitterFactory().getInstance();
					twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
					twitter.setOAuthAccessToken(a);
			        twitter.updateStatus(getTweetMsg());
					mTwitterHandler.post(mUpdateTwitterNotification);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		};
		t.start();
	}
	
}
