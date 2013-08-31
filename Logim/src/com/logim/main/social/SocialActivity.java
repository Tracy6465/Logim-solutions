package com.logim.main.social;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.logim.adapter.SocialAdapter;
import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.utils.SysApplication;
import com.logim.vo.SocialVo;

public class SocialActivity extends Activity {
	
	private ListView listView;
	
	ProgressDialog pDialog;
	String appid;
	String signaturekey;
	String userId;
	String password;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social);
		SysApplication.getInstance().addActivity(this); 
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intentReceived = getIntent();
		userId = intentReceived.getStringExtra("userid");
		password= intentReceived.getStringExtra("password");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		UserInfoDao userInfoDao = new UserInfoDao(this);
		String[] social = userInfoDao.selectSocial(userId);
		
		ArrayList<SocialVo> list = new ArrayList<SocialVo>();
		SocialVo item = new SocialVo(R.drawable.logo_mail, 
				getString(R.string.text_share_title_email), 
				getString(R.string.text_share_summary_email), 
				social[0]);
		list.add(item);
		item = new SocialVo(R.drawable.logo_facebook, 
				getString(R.string.text_share_title_facebook), 
				getString(R.string.text_share_summary_facebook), 
				social[1]);
		list.add(item);
		item = new SocialVo(R.drawable.logo_twitter, 
				getString(R.string.text_share_title_twitter), 
				getString(R.string.text_share_summary_twitter), 
				social[2]);
		list.add(item);
		item = new SocialVo(R.drawable.logo_weibo, 
				getString(R.string.text_share_title_weibo), 
				getString(R.string.text_share_summary_weibo), 
				social[3]);
		list.add(item);
		
		SocialAdapter socialAdapter = new SocialAdapter(this, list);
		listView = (ListView)findViewById(R.id.social);
		listView.setAdapter(socialAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					goMail();
					break;
				case 1:
					goFace();
					break;
				case 2:
					goTwitter();
					break;
				case 3:
					goWeibo();
					break;
				}
			}
		});
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
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	void goMail() {
		Intent intent = new Intent(SocialActivity.this, MailActivity.class);
		intent.putExtra("userid", userId);	
		intent.putExtra("password", password);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		
	}
	
	void goFace() {
		Intent postOnFacebookWallIntent = new Intent(this, ShareOnFacebookActivity.class);
		postOnFacebookWallIntent.putExtra("userid", userId);	
		postOnFacebookWallIntent.putExtra("password", password);
		startActivity(postOnFacebookWallIntent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	void goWeibo() {
		Intent postOnWeiboWallIntent=new Intent(this, ShareOnWeiboActivity.class);
		postOnWeiboWallIntent.putExtra("userid", userId);	
		postOnWeiboWallIntent.putExtra("password", password);
		startActivity(postOnWeiboWallIntent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	void goTwitter() {
		Intent intent = new Intent(this, ShareOnTwitterActivity.class);
		intent.putExtra("userid", userId);	
		intent.putExtra("password", password);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

}
