package com.logim.activity;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Base64DecoderException;
import com.logim.main.utils.Database;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;

/**
 * 
 * @author David Prieto Rivera Form to add an identity
 * 
 */
public class IdentityForm extends Activity implements TextWatcher {

	// ATRIBUTES
	String code;
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	ProgressBar pbar;
	String userid;
	int identityCounter;
	String salt;
	String passwordUser;
    String user;
	// ACTIVITY LIFECYCLE
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identity_form);
		SysApplication.getInstance().addActivity(this); 

		// Initiate the progress bar
		
		Intent intent = getIntent();
		userid= intent.getStringExtra("userid");
		identityCounter= intent.getIntExtra("identities", 0);
		passwordUser = intent.getStringExtra("password");
		salt= intent.getStringExtra("salt");
		user= intent.getStringExtra("mail");

		pbar = (ProgressBar) findViewById(R.id.progressBar1);
		pbar.setVisibility(View.VISIBLE);
		pbar.setMax(10);
		pbar.setProgress(0);
		
		EditText editText2 = (EditText) findViewById(R.id.edit_password);
		editText2.addTextChangedListener(this);
		
	}

	// ACTIVITY METHODS

	/**
	 * Called when the user clicks the Send button
	 * 
	 * @throws FileNotFoundException
	 */
	public void passParameters(View view) throws FileNotFoundException {

		boolean validParameters = false;

		// get Parameters
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String nombre = editText.getText().toString();
		nombre=nombre.toLowerCase();
		EditText editText2 = (EditText) findViewById(R.id.edit_email);
		String email = editText2.getText().toString();
		EditText editText3 = (EditText) findViewById(R.id.edit_password);
		String password = editText3.getText().toString();
		EditText editText4 = (EditText) findViewById(R.id.edit_url);
		String url = editText4.getText().toString();

		// check if parameters are valid
		validParameters = checkParameters(nombre, email, password, url);

		if (validParameters) {
			Database database = new Database(this, "DBIdentities", null, 1);
			
			UserInfoDao userInfoDao = new UserInfoDao(this);
			int limit = Integer.parseInt(userInfoDao.selectLimit(userid));
			
			SQLiteDatabase db = database.getWritableDatabase();
			// Si hemos abierto correctamente la base de datos
			if (db != null) {

				if (identityCounter < limit) {
				
					SecretKey key = null;
					try {
						key=Security.getExistingKey(passwordUser+Secure.ANDROID_ID, Base64.decodeWebSafe(salt));
					} catch (Base64DecoderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String passwordEncripted = Security.cipher(password, key);
					byte[] iv= Security.iv;
					passwordEncripted=Base64.encodeWebSafe(iv, true)+"]"+passwordEncripted;
					
					String mailEncripted = Security.cipher(email, key);
					iv= Security.iv;
					mailEncripted=Base64.encodeWebSafe(iv, true)+"]"+mailEncripted;
					
					code=Utils.getCadenaAlfanumAleatoria(20);
					// Insertamos los datos en la tabla de Identidades
					db.execSQL("INSERT INTO Identities (codigo, nombre, email, password, url, creation, visited, count,userid,user) "
							+ "VALUES ('"
							+ code
							+ "', '"
							+ nombre
							+ "', '"
							+ mailEncripted
							+ "', '"
							+ passwordEncripted
							+ "', '"
							+ url
							+ "', '"
							+ Utils.getDatePhone()
							+ "', '"
							+ Utils.getDatePhone() + "', '" + "0" + "', '"+ userid + "', '"+ user +"')");

					
				
					finish();

				}
				else
				{
					Toast.makeText(
							this,
							this.getString(
									R.string.toast_limit_identities),
							Toast.LENGTH_SHORT).show();
				}
				db.close();

			}
			
		}
	}

	/**
	 * Check the form parameters
	 * 
	 * @param nombre
	 * @param email
	 * @param password
	 * @param url
	 * @return
	 */
	public boolean checkParameters(String nombre, String email,
			String password, String url) {
		Boolean valid = true;
		TextView textView;
		textView = (TextView) findViewById(R.id.name);
		textView.setTextColor(Color.BLACK);
		textView = (TextView) findViewById(R.id.email);
		textView.setTextColor(Color.BLACK);
		textView = (TextView) findViewById(R.id.password);
		textView.setTextColor(Color.BLACK);
		textView = (TextView) findViewById(R.id.urlView);
		textView.setTextColor(Color.BLACK);

		if (nombre.equals("")) {
			textView = (TextView) findViewById(R.id.name);
			textView.setTextColor(getResources().getColor(R.color.red));
			Toast.makeText(textView.getContext(),
					(textView.getContext()).getString(R.string.toast_fault_name),
					Toast.LENGTH_SHORT).show();
			valid = false;

		}
		if (email.equals("")) {
			textView = (TextView) findViewById(R.id.email);
			textView.setTextColor(getResources().getColor(R.color.red));
			Toast.makeText(textView.getContext(),
					(textView.getContext()).getString(R.string.toast_fault_email),
					Toast.LENGTH_SHORT).show();
			valid = false;

		} 
		if (password.equals("")) {
			textView = (TextView) findViewById(R.id.password);
			textView.setTextColor(getResources().getColor(R.color.red));
			Toast.makeText(textView.getContext(),
					(textView.getContext()).getString(R.string.toast_fault_password2_empty),
					Toast.LENGTH_SHORT).show();
			valid = false;

		}
		if (url.equals("")) {
			textView = (TextView) findViewById(R.id.urlView);
			textView.setTextColor(getResources().getColor(R.color.red));
			Toast.makeText(textView.getContext(),
					(textView.getContext()).getString(R.string.toast_fault_url),
					Toast.LENGTH_SHORT).show();
			valid = false;
		}
		return valid;

	}
	
	private void goBack()
	{
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

//			NavUtils.navigateUpFromSameTask(this);
			goBack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
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

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int puntuacion = 0;
		String text = s.toString();
		pbar.setProgress(puntuacion);
		// When the password's length is more than 8, the security is +40%
		if (text.length() < 1) {
			puntuacion = 0;
		} else if (text.length() < 8) {
			puntuacion += 2;
		} else {
			puntuacion += 4;
		}
		Pattern p = Pattern.compile("[A-Z]");// the password contains capital
												// letters +20%
		Matcher m = p.matcher(text);
		if (m.find()) {
			puntuacion += 2;
		}
		p = Pattern.compile("[0-9]");// the password contain numbers +20%
		m = p.matcher(text);
		if (m.find()) {
			puntuacion += 2;
		}
		p = Pattern.compile("[^a-zA-Z_0-9]");// the password contains special
												// characters +20%
		m = p.matcher(text);
		if (m.find()) {
			puntuacion += 2;
		}

		// Changing the color of the security bar
		if (puntuacion < 4) {
			
			if (Build.VERSION.SDK_INT > 10) {
				Drawable dr = getResources()
						.getDrawable(R.drawable.progressbar_easy);
				pbar.setProgressDrawable(dr);
			} else {

				Drawable drawable = pbar.getProgressDrawable();

				drawable.setColorFilter(new LightingColorFilter(0xFF000000,
						0xFF0000));
			}

		} else if (puntuacion < 7) {
		
			if (Build.VERSION.SDK_INT > 10) {
				Drawable dr = getResources().getDrawable(
						R.drawable.progressbar_normal);
				pbar.setProgressDrawable(dr);
			} else {
				Drawable drawable = pbar.getProgressDrawable();
				drawable.setColorFilter(new LightingColorFilter(0xFF000000,
						0xFFFF00));

			}
		} else {
			
			if (Build.VERSION.SDK_INT > 10) {
			Drawable dr = getResources().getDrawable(
					R.drawable.progressbar_hard);
			pbar.setProgressDrawable(dr);
			}else
			{
				 Drawable drawable = pbar.getProgressDrawable();
				 drawable.setColorFilter(new LightingColorFilter(0xFF000000,
				 0x32CD32));
			}
		}
			
		// pbar.setProgress(puntuacion);
		pbar.incrementProgressBy(puntuacion);
	
	}

}
