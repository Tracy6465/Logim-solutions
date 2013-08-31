package com.logim.activity;

import com.logim.main.R;
import com.logim.service.AppNotiService;
import com.logim.utils.AccountPref;
import com.logim.utils.Pref;

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

public class AlertBlockedActivity extends Activity {
	
	Context context;
	TextView text;
	Button accept;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		
		context = this;
		
		Pref pref = new Pref(context);
		pref.setBlocked(true);
		pref.setScanStart(false);
		
		text = (TextView)findViewById(R.id.alert_text);
		text.setText(getString(R.string.dialog_blocked_text));
		accept = (Button)findViewById(R.id.alert_button);
		accept.setVisibility(View.GONE);
		
		Intent intent = new Intent(context, AppNotiService.class);
	    stopService(intent);
//		accept.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(context, AppNotiService.class);
//			    stopService(intent);
//			    
//				finish();
//				System.exit(0);
//			}
//		});
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
