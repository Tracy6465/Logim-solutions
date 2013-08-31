package com.logim.activity;

import com.logim.main.R;
import com.logim.utils.Pref;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TutorialsActivity extends Activity {
	
	Intent intent;
	Pref pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorials);
		
		pref = new Pref(this);
		pref.setScanStart(false);
		
		intent = getIntent();
	}
	
	public void toVideo(View view) {
		pref.setMainTutorials(true);
		pref.setCameraTutorials(true);
		
		Intent intent = new Intent(TutorialsActivity.this, TutorialsVideoActivity.class); 
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	public void toMain(View view) {
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
