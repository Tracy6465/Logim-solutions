package com.logim.activity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logim.main.R;
import com.logim.main.utils.Pair;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.view.ProgressView;

/**
 * 
 * @author David Prieto Rivera Create a user account of the Logim app
 * 
 */
public class CreateAccountActivity extends FileActivity implements TextWatcher {

	// ATRIBUTES
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	EditText editText;
	String password;
	ProgressDialog pDialog;
	ProgressBar pbar;
    Context context;
	// ACTIVITY LIFECYCLE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		context = CreateAccountActivity.this;
		SysApplication.getInstance().addActivity(this); 
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		EditText editText2 = (EditText) findViewById(R.id.create_password);
		editText2.addTextChangedListener(this);

		// Initiate the progress bar
		pbar = (ProgressBar) findViewById(R.id.progressBarCreate);
		pbar.setMax(10);
		pbar.setProgress(0);
	}

	/**
	 * 
	 * Sends to the server the email and password of the user and receives a
	 * userid for authentification
	 */
	public void CreateAccount(View v) {

		boolean validParameters = false;

		// get Parameters
		editText = (EditText) findViewById(R.id.create_email);
		String email = editText.getText().toString();
		EditText editText2 = (EditText) findViewById(R.id.create_password);
		password = editText2.getText().toString();
		EditText editText3 = (EditText) findViewById(R.id.confirm_password);
		String password2 = editText3.getText().toString();
		String url = "https://service.mylogim.com/api1/user/register";
		// check if parameters are valid
		validParameters = checkParameters(email, password, password2);
		String[] args = new String[] { url, email, password };
		if (validParameters) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				pDialog = new ProgressView(this);
				new CreateUserAccount().execute(args);
			} else {
				Toast.makeText(context, context.getString(R.string.toast_no_network),
						Toast.LENGTH_LONG).show();
			}
		}

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

	/**
	 * Check the form parameters
	 * 
	 * @param nombre
	 * @param email
	 * @param password
	 * @param url
	 * @return
	 */
	public boolean checkParameters(String email, String password1,
			String password2) {
		Boolean valid = true;

		if (email.equals("")) {
			Toast.makeText(context, context.getString(R.string.toast_fault_email),
					Toast.LENGTH_LONG).show();
			valid = false;

		} else if (!email.contains("@")) {

			Toast.makeText(context, context.getString(R.string.toast_email_incorrect),
					Toast.LENGTH_LONG).show();
			valid = false;

		}
		if (password1.equals("")) {
			Toast.makeText(context, context.getString(R.string.toast_fault_password2_empty),
					Toast.LENGTH_LONG).show();
			valid = false;

		}else if(password1.length()<6)
		{
			Toast.makeText(context, context.getString(R.string.toast_fault_password_length),
					Toast.LENGTH_LONG).show();
			valid = false;
			
		}
		if (password2.equals("")) {
			Toast.makeText(context, context.getString(R.string.toast_fault_password_empty),
					Toast.LENGTH_LONG).show();
			valid = false;

		} else if (!password2.equals(password1)) {
			Toast.makeText(context,context.getString(R.string.toast_fault_Password_different),
					Toast.LENGTH_LONG).show();
			valid = false;

		}
		return valid;

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

	/**
	 * 
	 * Return to the previous View
	 */
	void goBack() {
		Intent intent = new Intent(context, EntranceActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/**
	 * 
	 * @author David Prieto Rivera Class to do the HTTP Request in a separate
	 *         thread from the UI.
	 * 
	 */
	private class CreateUserAccount extends AsyncTask<String, Integer, String> {

		/**
		 * Do the HTTP Request PUT
		 */
		String name;
		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("email", params[1]);
			listParam.add(p);
			name=params[1];
			p = new Pair("password", params[2]);
			listParam.add(p);
			String result = Utils.doHttpRequest(2, listParam, params[0],
					"POST", false);
			return result;

		}

		/**
		 * Parsing the JSON Response for extracting the userid and processin
		 * error messages
		 */
		protected void onPostExecute(String result) {
			try {
				if (!result.equals("")) {
					JSONObject json = new JSONObject(result);
				
					if (json.getString("status").equals("success")) {
						String identifier = json.getString("info");
						Intent intent = new Intent(context,
								ActivationActivity.class);
							
						intent.putExtra("password", password);
						intent.putExtra("identifier", identifier);
						intent.putExtra("name", name);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						
					} else {

						if (json.getString("message").equals(
								"User is already registered")) {
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_already_registered),// Email
																		// is
																		// already
																		// registered
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						}
						if (json.getString("message").equals(
								"Invalid parameters")) {
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_email_incorrect),// Email is
																		// not
																		// valid
									Toast.LENGTH_LONG).show();
							pDialog.cancel();

						} else {
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_email_incorrect), Toast.LENGTH_LONG)// Server
																				// Error
									.show();
							pDialog.cancel();
						}

					}
				} else {
					Toast.makeText(context,
							context.getString(R.string.toast_server_error),// HTTP
																			// Error
							Toast.LENGTH_LONG).show();
					pDialog.cancel();
				}
			} catch (JSONException e) { // TODO Auto-generated catch block
				
				e.printStackTrace();
			}

		}

		/**
		 * Shows the processing dialog
		 */
		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					CreateUserAccount.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	/**
	 * Calculate the security of the password
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		int puntuacion = 0;
		String text = s.toString();
		pbar.setProgress(puntuacion);
		// When the password's length is more than 8, the security is +40%
		if (text.length() < 4) {
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
		String release=Build.VERSION.RELEASE;
		// Changing the color of the security bar
		if (puntuacion <= 4) {
			/*Drawable dr = getResources()
					.getDrawable(R.drawable.progressbar);
			pbar.setProgressDrawable(dr);*/
			if (Build.VERSION.SDK_INT > 10) {
				Drawable dr = getResources()
						.getDrawable(R.drawable.progressbar_easy);
				pbar.setProgressDrawable(dr);
			} else {

				Drawable drawable = pbar.getProgressDrawable();
				drawable.setColorFilter(new LightingColorFilter(0xFF000000,
						0xeec8102e));
			}

		} else if (puntuacion <= 6) {
			if (Build.VERSION.SDK_INT > 10) {
				Drawable dr = getResources().getDrawable(
						R.drawable.progressbar_normal);
				pbar.setProgressDrawable(dr);
			} else {
				Drawable drawable = pbar.getProgressDrawable();
				drawable.setColorFilter(new LightingColorFilter(0xFF000000,
						0xaac8102e));

			}
		} else {
			if (Build.VERSION.SDK_INT > 10) {
				Drawable dr = getResources().getDrawable(
						R.drawable.progressbar_hard);
				pbar.setProgressDrawable(dr);
			} else {
				 Drawable drawable = pbar.getProgressDrawable();
				 drawable.setColorFilter(new LightingColorFilter(0xFF000000,
				 0x66c8102e));
			}
		}
			
		pbar.incrementProgressBy(puntuacion);
		

	}
}