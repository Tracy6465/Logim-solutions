package com.logim.activity;


import javax.crypto.SecretKey;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.logim.main.R;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Base64DecoderException;
import com.logim.main.utils.Database;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;

/**
 * 
 * @author David Prieto Rivera Information about the identity
 * 
 */
public class IdentityInfo extends Activity {

	// ATRIBUTES
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	Database database;
	String message;
	String nombre;
	String count;
	String userid;
	String passwordUser;
	String salt;
	//int identityCounter;
	String url;
	
	private EditText textPassword;
//	private EditText pinEt;
//	private Builder pinBuilder;
//	private Dialog pinDialog;
	// ACTIVITY LIFECYCLE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identity_form);
		SysApplication.getInstance().addActivity(this); 
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Extraemos parametro del intent
		Intent intent = getIntent();
		message = intent.getStringExtra("id");
		userid= intent.getStringExtra("userid");
		passwordUser= intent.getStringExtra("password");
		salt= intent.getStringExtra("salt");

		// Escribimos los parametros de las identidades en los textview,
		// leyendolos de la base de datos
		count="0";
		actualizar();

		int cod = Integer.parseInt(count);
		cod++;
		count = Integer.toString(cod);

		EditText textName = (EditText) findViewById(R.id.edit_message);
		textName.setSelected(true);
		EditText textMail = (EditText) findViewById(R.id.edit_email);
		textMail.setSelected(true);
		textPassword = (EditText) findViewById(R.id.edit_password);
		textPassword.setSelected(true);
		EditText textUrl = (EditText) findViewById(R.id.edit_url);
		textUrl.setSelected(true);
		
//		pinEt = new EditText(this);
//		pinEt.setSingleLine(true);
//		pinEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
//		pinEt.setInputType(InputType.TYPE_CLASS_NUMBER);
//		
//		pinBuilder = new Builder(this);
//		pinBuilder.setView(pinEt)
//		.setTitle("Input Pin")
//		.setPositiveButton("Accept", new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (pinCheck(pinEt.getText().toString())) {
//					textPassword.setTransformationMethod(null);
//				}
//				pinEt.setText("");
//			}
//		})
//		.setNegativeButton("Cancel", null);
//		pinDialog = pinBuilder.create();
	}
	
//	String pin = "";
//	boolean UsePin = false;
//	
//	private boolean pinCheck(String pinInput) {
//		if (!pinInput.equals("") && pinInput.equals(pin)) {
//			return true;
//		}
//		
//		return false;
//	}
	
//	private boolean ifPin() {
//		Database database = new Database(this, "DBUserPin", null, 1);
//		SQLiteDatabase db = database.getReadableDatabase();
//		String[] args = { userid };
//		Cursor c = db.rawQuery(
//				" SELECT pin, hasPin, scanStart FROM UserPin WHERE userid=?", args);
//		if (c.moveToFirst()) {
//			// Travel the cursor until no more records
//			do {
//
//				pin = c.getString(0);
//				if (c.getString(1).equals("true")) {
//					UsePin = true;
//					try {
//						pin = Security.decrypt(Base64.decodeWebSafe(salt), pin,
//								passwordUser + Secure.ANDROID_ID);
//					} catch (Base64DecoderException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} else {
//					UsePin = false;
//				}
//
//			} while (c.moveToNext());
//		}
//		db.close();
//		
//		return UsePin;
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_identity_info, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
        	goBack();
        	break;
        case R.id.action_showpwd:
    		if(textPassword.getTransformationMethod()!=null ) {
//    			if (ifPin()) {
//    				pinDialog.show();
//    			}
//    			else {
    				textPassword.setTransformationMethod(null);
//    			}
    		}
    		else {
    			textPassword.setTransformationMethod(new PasswordTransformationMethod());
    		}
    		textPassword.setSelected(true);
    		
            break;
//        case R.id.action_webpage:
//        	Intent intent = new Intent("android.intent.action.VIEW",
//    				Uri.parse("http://" + url));
//    		startActivity(intent);
//    		
//            break;
        case R.id.action_delete:
			Dialog d = crearDialogoConfirmacion(message);
			d.setCanceledOnTouchOutside(true);
			d.show();
        	
        	break;
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
	
	private void goBack() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	private Dialog crearDialogoConfirmacion(final String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(this.getString(R.string.dialog_eliminar));
		builder.setMessage(this.getString(R.string.dialog_eliminar_pregunta));
		builder.setPositiveButton(this.getString(R.string.button_accept),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					
						String[] args = new String[] {message, userid };
						SQLiteDatabase db = database.getWritableDatabase();
						db.execSQL(
								"DELETE FROM Identities WHERE codigo=? AND userid=?",
								args);
						db.close();
						dialog.cancel();
						finish();
					}
				});
		builder.setNegativeButton(this.getString(R.string.button_cancel),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		return builder.create();
	}
	
	public void onStart() {
		super.onStart();
		actualizar();
		String[] args = new String[] { message,userid };
		SQLiteDatabase db = database.getWritableDatabase();
		if (db != null) {
			db.execSQL(" UPDATE Identities SET count ='" + count
					+ "' WHERE codigo=? AND userid=?", args);
			db.execSQL(
					" UPDATE Identities SET visited ='" + Utils.getDatePhone()
							+ "' WHERE codigo=? AND userid=?", args);
		}
		db.close();
		
	}

	// ACTIVITY METHODS
	/**
	 * Refreshes the info about the identity
	 */
	public void actualizar()
	{
		SecretKey key = null;
		try {
			key=Security.getExistingKey(passwordUser+Secure.ANDROID_ID, Base64.decodeWebSafe(salt));
		} catch (Base64DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		String[] args = new String[] { message,userid };
		database = new Database(this, "DBIdentities", null, 1);
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor c = db
				.rawQuery(
						" SELECT nombre, email, password, url, count FROM Identities WHERE codigo=? AND userid=?",
						args);
		
		if (c.moveToFirst()) {
			// Recorremos el cursor hasta que no haya más registros
			do {

				nombre = c.getString(0);

				String email = c.getString(1);
				String password = c.getString(2);
				try {
					password= Security.decrypt(Base64.decodeWebSafe(salt),password, passwordUser+Secure.ANDROID_ID);
					email= Security.decrypt(Base64.decodeWebSafe(salt),email,passwordUser+Secure.ANDROID_ID);
				} catch (Base64DecoderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
				url = c.getString(3);
				count = c.getString(4);

				EditText editText = (EditText) findViewById(R.id.edit_message);
				editText.setText(nombre);
				EditText editText2 = (EditText) findViewById(R.id.edit_email);
				editText2.setText(email);
				EditText editText3 = (EditText) findViewById(R.id.edit_password);
				editText3.setText(password);
				EditText editText4 = (EditText) findViewById(R.id.edit_url);
				editText4.setText(url);

			} while (c.moveToNext());
		}
		db.close();
		
	}
	
	/** Called when the user clicks the Send button */
	public void passParameters(View view) {

		boolean validParameters = false;

		EditText editText = (EditText) findViewById(R.id.edit_message);
		String nombre = editText.getText().toString();
		EditText editText2 = (EditText) findViewById(R.id.edit_email);
		String email = editText2.getText().toString();
		EditText editText3 = (EditText) findViewById(R.id.edit_password);
		String password = editText3.getText().toString();
		EditText editText4 = (EditText) findViewById(R.id.edit_url);
		String url = editText4.getText().toString();
		
		validParameters = checkParameters(nombre, email, password, url);

		if (validParameters) {
			Database database = new Database(this, "DBIdentities", null, 1);

			SQLiteDatabase db = database.getWritableDatabase();
			// Si hemos abierto correctamente la base de datos
			if (db != null) {
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

				db.execSQL("UPDATE Identities SET nombre = '" + nombre
						+ "' WHERE codigo ='" + message + "' AND userid ='"+ userid +"'");
				db.execSQL("UPDATE Identities SET email = '" + mailEncripted
						+ "' WHERE codigo ='" + message + "'AND userid ='"+ userid +"'");
				db.execSQL("UPDATE Identities SET password = '" + passwordEncripted
						+ "' WHERE codigo ='" + message + "'AND userid ='"+ userid +"'");
				db.execSQL("UPDATE Identities SET url = '" + url
						+ "' WHERE codigo ='" + message + "'AND userid ='"+ userid +"'");

			}
			db.close();
			finish();
		}
	}

	// Comprueba parametros del formulario
	public boolean checkParameters(String nombre, String email,
			String password, String url) {
		Boolean valid = true;
		TextView textView;
		textView = (TextView) findViewById(R.id.edit_message);
		textView.setTextColor(Color.BLACK);
		textView = (TextView) findViewById(R.id.edit_email);
		textView.setTextColor(Color.BLACK);
		textView = (TextView) findViewById(R.id.edit_password);
		textView.setTextColor(Color.BLACK);
		textView = (TextView) findViewById(R.id.edit_url);
		textView.setTextColor(Color.BLACK);
		
		if (nombre.equals("")) {
			textView = (TextView) findViewById(R.id.edit_message);
			textView.setTextColor(getResources().getColor(R.color.red));
			Toast.makeText(textView.getContext(),
					(textView.getContext()).getString(R.string.toast_fault_name),
					Toast.LENGTH_SHORT).show();
			valid = false;

		}
		if (url.equals("")) {
			textView = (TextView) findViewById(R.id.edit_url);
			textView.setTextColor(getResources().getColor(R.color.red));
			Toast.makeText(textView.getContext(),
					(textView.getContext()).getString(R.string.toast_fault_url),
					Toast.LENGTH_SHORT).show();
			valid = false;
		}
		return valid;

	}
	
}
