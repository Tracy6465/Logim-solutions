package com.logim.main.social;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.logim.db.DBValue;
import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.utils.Pair;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.view.ProgressView;
import com.logim.vo.UserInfoVo;

public class MailActivity extends Activity {

	ProgressDialog pDialog;
	UserInfoVo userInfo;
	String userid;
	String passwordUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail);
		SysApplication.getInstance().addActivity(this); 
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intentReceived = getIntent();
		userid = intentReceived.getStringExtra("userid");
		passwordUser = intentReceived.getStringExtra("password");
		
		UserInfoDao userInfoDao = new UserInfoDao(this);
		userInfo = userInfoDao.selectUserInfo(userid);
		
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
		}
		default:

			return super.onKeyUp(keyCode, event);
		}

	}

	private void goBack() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	public void sendMail(View v) {

		EditText desMail = (EditText) findViewById(R.id.send_email);
		EditText desText = (EditText) findViewById(R.id.send_text);
		boolean validParameters=checkParameters(desMail.getText().toString(),desText.getText().toString());
		if(validParameters)
		{
		String url = "https://service.mylogim.com/api1/app/refer/email";
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String[] params = new String[] { url, desMail.getText().toString(),desText.getText().toString() };
			pDialog = new ProgressView(MailActivity.this);
			new SendEmail().execute(params);

		} else {
			Toast.makeText(this.getBaseContext(),
					this.getBaseContext().getString(R.string.toast_no_network),
					Toast.LENGTH_SHORT).show();
		}
		}

	}
	
	public boolean checkParameters(String email,String text) {

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

		} else if(text.equals("")) {
			Toast.makeText(this, this.getString(R.string.toast_fault_text),
					Toast.LENGTH_LONG).show();
			valid = false;
		}
		return valid;

	}


	private class SendEmail extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("appid", userInfo.getAppid());
			listParam.add(p);
			String nounce = Utils.getCadenaAlfanumAleatoria(20);
			Credential credentials = new Credential(userInfo.getAppid(), nounce,
					"app/refer/email");
			credentials.setSignature(userInfo.getSignaturekey());
		
			String signature = credentials.getSignature();
			p = new Pair("nounce", nounce);

			listParam.add(p);
			p = new Pair("signature", signature);
			listParam.add(p);

			p = new Pair("demail", params[1]);
			listParam.add(p);
			
			p = new Pair("custommsg",params[2]);
			listParam.add(p);

			String result = Utils.doHttpRequest(3, listParam, params[0],
					"POST", false);
			return result;

		}

		protected void onPostExecute(String result) {

			if (!result.equals("")) {
				Log.i("praeda",result);
				JSONObject json = null;
				try {
					json = new JSONObject(result);

					if (json.getString("status").equals("success")) {
						if (userInfo.getRefemail().equals("false")) {

							int lim = Integer.parseInt(userInfo.getIdentitiesstoragelimit());
							lim = lim + 5;
							userInfo.setIdentitiesstoragelimit(Integer.toString(lim));
							UserInfoDao userInfoDao = new UserInfoDao(MailActivity.this);
							userInfoDao.updateSocial(userid, userInfo.getIdentitiesstoragelimit(), DBValue.Table_UserInfo.ISMAIL);

							TextView texto = (TextView) findViewById(R.id.textSendMail);
							texto.setText(MailActivity.this
									.getString(R.string.text_share_write_friend_no));
						}
						Toast.makeText(
								MailActivity.this,
								MailActivity.this
										.getString(R.string.toast_success_mail),
								Toast.LENGTH_LONG).show();
						pDialog.cancel();
						goBack();

					} else {
						if (json.getString("message")
								.equals("Already used destination email or max number of emails reached")) {
							Toast.makeText(
									MailActivity.this,
									MailActivity.this
											.getString(R.string.toast_already_used_email),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						} else if (json.getString("message")
								.equals("Origin and destination emails are the same")) {
							Toast.makeText(
									MailActivity.this,
									MailActivity.this
											.getString(R.string.toast_owned_email),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();
						}
						else if (json.getString("message")
								.equals("Invalid email address")) {
							Toast.makeText(
									MailActivity.this,
									MailActivity.this
											.getString(R.string.toast_email_incorrect),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();
						}
						else {
							Toast.makeText(
									MailActivity.this,
									MailActivity.this.getString(R.string.toast_server_error),
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			else
			{
				Toast.makeText(
				MailActivity.this,
				MailActivity.this.getString(R.string.toast_server_error),
				Toast.LENGTH_LONG).show();
				pDialog.cancel();
				
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					SendEmail.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

}
