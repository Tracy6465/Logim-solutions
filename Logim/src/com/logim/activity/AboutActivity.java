package com.logim.activity;

import com.logim.main.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		this.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	/**
	 * 
	 * Show the terms of the contract
	 */
	public void ShowTerms(View v) {
		Intent intent = new Intent(this, WebViewActivity.class); 
		intent.putExtra("url", getString(R.string.url_term));
		intent.putExtra("title", getString(R.string.title_activity_term));
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 
	 * Show the Policy
	 */
	public void ShowPolicy(View v) {
		Intent intent = new Intent(this, WebViewActivity.class); 
		intent.putExtra("url", getString(R.string.url_policy));
		intent.putExtra("title", getString(R.string.title_activity_policy));
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
