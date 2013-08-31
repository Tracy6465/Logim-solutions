package com.logim.activity;

import java.util.Locale;


import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.utils.AccountPref;
import com.logim.utils.Pref;
import com.logim.vo.UserInfoVo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

public class WelcomeActivity extends Activity{
	
	Context context;
	UserInfoVo userInfo;
	boolean logout;
	
	Intent intent = new Intent();
	UserInfoDao userInfoDao;
	String password;
	String email;
	
	AccountPref accountPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		logout = false;
				
		accountPref = new AccountPref(context);
		Pref pref = new Pref(context);
		
		Resources res = getResources();
		Configuration config = res.getConfiguration();
		config.locale = new Locale(pref.getLanguage());
		res.updateConfiguration(config, null);
		
		setContentView(R.layout.activity_welcome);
		
		new Thread(){
			public void run(){
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				signIn();
			}
		}.start();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			return true;
		}
		default:
			return super.onKeyUp(keyCode, event);
		}

	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler =new Handler(){ 
		@Override 
		public void handleMessage(Message msg){ 
			super.handleMessage(msg); 
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} 
		 
	}; 
	
	void signIn() {
		email = accountPref.getEmail();
		password = accountPref.getPassword();
		
		userInfoDao = new UserInfoDao(this);
		userInfo =  userInfoDao.selectUser();
		
		if (!password.equals("") && !email.equals("") && userInfo != null && userInfo.getUseremail().equals(email)) {
			logout = true;
			new com.logim.net.GetAppInfo(this, context, userInfo, password);
		}
		else {
			intent.setClass(this, EntranceActivity.class);
			handler.sendEmptyMessage(0);
		}
	}
}
