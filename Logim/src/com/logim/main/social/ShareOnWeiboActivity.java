package com.logim.main.social;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;


import com.logim.db.DBValue;
import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.utils.Pair;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.view.ProgressView;
import com.logim.vo.UserInfoVo;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareOnWeiboActivity extends Activity implements OnClickListener, RequestListener {

	ProgressDialog pDialog;
	Button sendButton;
	EditText sendMessageEdt;
	UserInfoVo userInfo;
	String userid;
	String passwordUser;
	private String messageToPost;

	Weibo mWeibo;
	String token;
	String expires_in;
	Oauth2AccessToken access_token;
	StatusesAPI api;

	final String CONSUMER_KEY = "3327642206";
	final String REDIRECT_URL = "http://www.sina.com";
	SharedPreferences pre;
	final String PRE_FILENAME="WeiboAuth";
	Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		SysApplication.getInstance().addActivity(this);
		
		mWeibo=Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		Intent intentReceived = getIntent();
		userid = intentReceived.getStringExtra("userid");
		passwordUser = intentReceived.getStringExtra("password");
		initView();
		
		UserInfoDao userInfoDao = new UserInfoDao(this);
		userInfo = userInfoDao.selectUserInfo(userid);
	}
	
	private void initView() {
		sendButton = (Button) findViewById(R.id.share_button);
		sendButton.setOnClickListener(this);
		sendMessageEdt = (EditText) findViewById(R.id.share_edit);
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

	private void goBack() {
		this.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private Boolean checkAuth() {
		pre = getSharedPreferences(PRE_FILENAME, MODE_PRIVATE);
		token = pre.getString("token", "");
		expires_in = pre.getString("expires_in", "0");
		access_token = new Oauth2AccessToken(token, expires_in);
		return access_token.isSessionValid();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_button:
			  if (checkAuth()) {
				  messageToPost = sendMessageEdt.getText().toString();
				if (checkParameters(messageToPost))
				{
					access_token = new Oauth2AccessToken(pre.getString("token", ""), pre.getString("expires_in", "0"));
					api = new StatusesAPI(access_token);
					try {
					  api.update(messageToPost, "0", "0", this);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}
			else {
				  mWeibo.authorize(ShareOnWeiboActivity.this, new AuthDialogListener());
				  
			}
			break;
		}
	}

	private boolean checkParameters(String text) {
		boolean valid= true;
		if(text.equals("")) {
			Toast.makeText(this, this.getString(R.string.toast_fault_text),
					Toast.LENGTH_LONG).show();
			valid = false;
		}
		return valid;
	}

	class AuthDialogListener implements WeiboAuthListener{

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel : ",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onComplete(Bundle values) {
			token = values.getString("access_token");
			expires_in = values.getString("expires_in");
			editor = pre.edit();
			editor.putString("token", token);
			editor.putString("expires_in", expires_in);
			editor.commit();
			Toast.makeText(getApplicationContext(), "Successful authorization", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}
		
	}

	@Override
	public void onComplete(String arg0) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//showToast("Message posted to your Weibo wall!");
				String url = "https://service.mylogim.com/api1/app/refer/social";
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					String[] params = new String[] { url };
					pDialog= new ProgressView(ShareOnWeiboActivity.this);
					new ConfirmSocial().execute(params);
				}
				else {
					Toast.makeText(ShareOnWeiboActivity.this,
							ShareOnWeiboActivity.this.getString(R.string.toast_no_network),
							Toast.LENGTH_SHORT).show();
				}
				}
				
			
		});
	}

	@Override
	public void onError(WeiboException arg0) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ShareOnWeiboActivity.this, "Share fails!", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void onIOException(IOException arg0) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ShareOnWeiboActivity.this, "Share fails!", Toast.LENGTH_SHORT).show();
			}
		});
	
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

			p = new Pair("reftype", "weibo");
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
						if (userInfo.getRefweibo().equals("false")) {
							int lim = Integer.parseInt(userInfo.getIdentitiesstoragelimit());
							lim = lim + 5;
							userInfo.setIdentitiesstoragelimit(Integer.toString(lim));
							UserInfoDao userInfoDao = new UserInfoDao(ShareOnWeiboActivity.this);
							userInfoDao.updateSocial(userid, userInfo.getIdentitiesstoragelimit(), DBValue.Table_UserInfo.ISWEIBO);

							Toast.makeText(
									ShareOnWeiboActivity.this,
									ShareOnWeiboActivity.this
											.getString(R.string.toast_success_weibo),
									Toast.LENGTH_LONG).show();
							
							pDialog.cancel();
							

						}
						
						Toast.makeText(
								ShareOnWeiboActivity.this,
								ShareOnWeiboActivity.this
										.getString(R.string.toast_success_weibo),
								Toast.LENGTH_LONG).show();
						goBack();
					}
					else
					{
						Toast.makeText(ShareOnWeiboActivity.this,
								ShareOnWeiboActivity.this.getString(
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
					ShareOnWeiboActivity.this,
					ShareOnWeiboActivity.this.getString(
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
}
