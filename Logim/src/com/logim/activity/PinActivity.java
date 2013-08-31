package com.logim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.logim.db.UserPinDao;
import com.logim.main.R;
import com.logim.main.camera.CameraActivity;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Base64DecoderException;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.utils.Pref;
import com.logim.vo.UserPinVo;

/**
 * 
 * @author David Prieto Rivera Activity of the optional security title screen
 * 
 */
public class PinActivity extends FileActivity {

	// ATRIBUTES
	UserPinVo userPin;
	Pref pref;
	Intent intent;
	String userid;
	String password;

	// ACTIVITY LIFECYLE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin);
		SysApplication.getInstance().addActivity(this); 
		
		pref = new Pref(this);
		// Check if Pin security option is activated and if it is, get the pin
		Intent intentReceived = getIntent();
		userid = intentReceived.getStringExtra("userid");
		String tipo = intentReceived.getStringExtra("tipo");
		password = intentReceived.getStringExtra("password");
		String salt = intentReceived.getStringExtra("salt");
		
		UserPinDao userPinDao = new UserPinDao(this);
		userPin = userPinDao.selectUserPinForCheck(userid);
		userPinDao.close();
		
		intent = new Intent(this, MainActivity.class);
		
		if (userPin == null || !userPin.isHasPin()) {
			start();
		}
		else if (userPin != null) {
			if (pref.isScanStart()) {
				intent = new Intent(this, CameraActivity.class);
			}
			else if (tipo.equals("main")) {
				intent = new Intent(this, MainActivity.class);
			}
			
			if (userPin.isHasPin()) {
				try {
					userPin.setPin(Security.decrypt(Base64.decodeWebSafe(salt), userPin.getPin(),
							password + Secure.ANDROID_ID));
				} catch (Base64DecoderException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void start() {
		intent.putExtra("userid", userid);
		intent.putExtra("password", password);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	/**
	 * 
	 * Checks if the pin is correct
	 */
	public void Access(View v) {
		if (userPin.isHasPin()) {
			EditText editText1 = (EditText) findViewById(R.id.number1);
			String nombre = editText1.getText().toString();
			if (nombre.equals(userPin.getPin())) {
				start();
			} else {
				Toast.makeText(
						editText1.getContext(),
						(editText1.getContext())
								.getString(R.string.toast_fault_pin),
						Toast.LENGTH_SHORT).show();

			}

		}
	}

}
