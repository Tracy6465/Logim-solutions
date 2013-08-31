package com.logim.activity;


import com.logim.main.R;
import com.logim.main.utils.ImageFragment;
import com.logim.main.utils.MyFragmentPagerAdapter;
import com.logim.main.utils.PageChangeListener;
import com.logim.main.utils.SysApplication;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

public class EntranceActivity extends FragmentActivity {
	
	private ViewPager pager;
	private PageIndicator mIndicator;
	private PageChangeListener pageChangeListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entrance);
		SysApplication.getInstance().addActivity(this); 
		
		initial();
	}
	
	private void initial() {
		pager = (ViewPager) findViewById(R.id.view_pager);
		MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager());

		ImageFragment fragmentImage = new ImageFragment(R.drawable.reg1, 
				this.getString(R.string.text_page1));
		ImageFragment fragmentImage2 = new ImageFragment(R.drawable.reg2, 
				this.getString(R.string.text_page2));
		ImageFragment fragmentImage3 = new ImageFragment(R.drawable.reg3, 
				this.getString(R.string.text_page3));
		ImageFragment fragmentImage4 = new ImageFragment(R.drawable.reg4, 
				this.getString(R.string.text_page4));

		adapter.addFragment(fragmentImage);
		adapter.addFragment(fragmentImage2);
		adapter.addFragment(fragmentImage3);
		adapter.addFragment(fragmentImage4);
       
		pageChangeListener = new PageChangeListener();
		pager.setOnPageChangeListener(pageChangeListener);
		pager.setAdapter(adapter);
		
		mIndicator = (CirclePageIndicator) findViewById(R.id.view_indicator);
		mIndicator.setViewPager(pager);
	}

	/**
	 * 
	 * goes to the sign in activity
	 */
	public void signIn(View v) {
		Intent intent = new Intent(this, SignInActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	 public boolean onKeyDown(int keyCode, KeyEvent event) {  
	        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){  
	        	SysApplication.getInstance().exit();
	            }  
	          
	        return super.onKeyDown(keyCode, event);  
	    }  	


	/**
	 * 
	 * goes to the create account activity
	 */
	public void createAccount(View v) {
		Intent intent = new Intent(this, CreateAccountActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

}
