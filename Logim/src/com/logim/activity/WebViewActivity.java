package com.logim.activity;

import com.logim.main.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
	
	private WebView webview;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intentReceived = getIntent();
		String url = intentReceived.getStringExtra("url");
		String title = intentReceived.getStringExtra("title");
		
		setTitle(title);
		
		webview = (WebView) findViewById(R.id.webview); 
        webview.getSettings().setJavaScriptEnabled(true); 
        webview.loadUrl(url); 
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

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

}
