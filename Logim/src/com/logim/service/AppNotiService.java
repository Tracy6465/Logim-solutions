package com.logim.service;

import com.logim.net.GetAppNoti;
import com.logim.vo.UserInfoVo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppNotiService extends Service {
	
	private UserInfoVo userInfo = new UserInfoVo();
	private boolean flag = true;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//TODO
		System.out.println("onstart");
		
		if (flag && intent.getParcelableExtra("userInfo") != null) {
			flag = false;
			userInfo = intent.getParcelableExtra("userInfo");
			new GetAppNoti(AppNotiService.this, userInfo).start();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("oncreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
