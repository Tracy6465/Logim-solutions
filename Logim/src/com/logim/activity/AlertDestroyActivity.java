package com.logim.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.logim.db.UserIdentityDao;
import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.service.AppNotiService;
import com.logim.utils.AccountPref;
import com.logim.utils.Pref;

public class AlertDestroyActivity extends Activity{
	
	Context context;
	TextView text;
	Button accept;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		
		context = this;
		
		UserInfoDao userInfoDao = new UserInfoDao(context);
		userInfoDao.deleteUserInfo();
		
		UserIdentityDao userIdentityDao = new UserIdentityDao(context);
		userIdentityDao.deleteIdentities();
		
		new Pref(context).setScanStart(false);
		
		AccountPref accountPref = new AccountPref(context);
		accountPref.clear();
		
		SharedPreferences pre=getSharedPreferences("WeiboAuth", MODE_PRIVATE);
		Editor editor = pre.edit();
		editor.clear();
		editor.commit();
		
		pre=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = pre.edit();
		editor.clear();
		editor.commit();
		
		text = (TextView)findViewById(R.id.alert_text);
		text.setText(getString(R.string.dialog_destroy_text));
		accept = (Button)findViewById(R.id.alert_button);
		accept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AppNotiService.class);
			    stopService(intent);
			    
				intent = new Intent(context, WelcomeActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
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
}
