package com.logim.utils;

import com.logim.activity.AlertDestroyActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DestroyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent it = new Intent(context, AlertDestroyActivity.class);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
	}

}
