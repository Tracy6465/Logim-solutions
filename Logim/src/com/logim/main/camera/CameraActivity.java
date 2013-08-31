package com.logim.main.camera;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.crypto.SecretKey;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.Toast;

import com.logim.activity.MainActivity;
import com.logim.activity.TutorialsCameraActivity;
import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.utils.Base64;
import com.logim.main.utils.Base64DecoderException;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Security;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.service.AppNotiService;
import com.logim.utils.Pref;
import com.logim.view.ProgressView;
import com.logim.vo.UserInfoVo;

/**
 * 
 * @author David Prieto Rivera Activity of the Scanner View
 * 
 */
public class CameraActivity extends Activity {
	// ATRIBUTES
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	Context context;
	UserInfoVo userInfo;
	String messageToParse;
	String passwordUser;
	String salt;
	ProgressDialog pDialog;
	LinkedList<Identity> idList;
	Database idDatabase;
	boolean unique;
	String userid;

	ImageScanner scanner;

//	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");// Load Library
	}

	// ACTIVITY LIFECYCLE

	/**
	 * Create the Scanner View
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		context=CameraActivity.this;
		SysApplication.getInstance().addActivity(this); 
		/* Center the title */

		Intent intentReceived = getIntent();
		userid= intentReceived.getStringExtra("userid");
		passwordUser= intentReceived.getStringExtra("password");
		
		Database database3 = new Database(this, "DBUserSalt", null, 1);
		SQLiteDatabase db = database3.getReadableDatabase();

		if (db != null) {
			String[] args2 = { userid };
			Cursor c = db.rawQuery("SELECT salt FROM UserSalt WHERE userid=?", args2);
			if (c.moveToFirst()) {
				salt = c.getString(0);
				
			}
		}
		db.close();
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		autoFocusHandler = new Handler();
		mCamera = getCameraInstance();
		unique = false;
		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
		
		ImageView scanArea = new ImageView(this);
		scanArea.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		scanArea.setImageResource(R.drawable.scan_frame);
		scanArea.setScaleType(ScaleType.CENTER_INSIDE);
		
		FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(mPreview);
		preview.addView(scanArea);
		
		UserInfoDao userInfoDao  = new UserInfoDao(context);
		userInfo = userInfoDao.selectUserInfo(userid);

		idDatabase = new Database(this, "DBIdentities", null, 1);
		
		Pref pref = new Pref(this);
		if (pref.getCameraTutorials()) {
			toTutorials();
		}

	}
	
	 private long exitTime = 0; 
	 
	 @Override 
	 public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){ 
			if((System.currentTimeMillis()-exitTime) > 2000){ 
				Toast.makeText(getApplicationContext(), "Press again to exit!", Toast.LENGTH_SHORT).show(); 
				exitTime = System.currentTimeMillis();
            } 
			else { 
				Intent intent = new Intent(this, AppNotiService.class);
			    stopService(intent);
			    finish(); 
				System.exit(0);
			} 
			return true; 
		} 
		return super.onKeyDown(keyCode, event); 
	}    
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_camera, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_identity:
        	toIdentity();
        	break;
        case R.id.action_again:
//        	barcodeScanned = false;
			mCamera.setPreviewCallback(previewCb);
			mCamera.startPreview();
			previewing = true;
			mCamera.autoFocus(autoFocusCB);
			
        	break;
        }
        
		return super.onOptionsItemSelected(item);
    }
	
	private void toIdentity() {
		Intent intent = new Intent(this, MainActivity.class);
    	intent.putExtra("userid", userid);
		intent.putExtra("password", passwordUser);
		
		startActivity(intent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		
		finish();
	}
	
	private void toTutorials() {
		Intent intent = new Intent(this, TutorialsCameraActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, 0);
	}
	
	// ACTIVITY METHODS
	/**
	 * Pause the Scanner
	 */
	public void onPause() {
		super.onPause();
		//releaseCamera();
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		releaseCamera();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {

		public void ProcessQR(String messageToParse) {
			//if (!unique) {
				unique = true;
				boolean hasList = true;
				StringTokenizer st = new StringTokenizer(messageToParse);
				
				int nDatos = st.countTokens();
			

				if (nDatos != 0) {
					String method = st.nextToken();
					
					if (method.equals("storeidentity")) {
						hasList = false;
					}
					String requestId = st.nextToken();
					

					idList = new LinkedList<Identity>();
					Credential credentials;
					if (hasList) {
						SQLiteDatabase db = idDatabase.getReadableDatabase();
						// String[] args = { st.nextToken() };
						/*
						 * Cursor c = db .rawQuery(
						 * " SELECT codigo, nombre, email, password, url  FROM Identities WHERE url=?"
						 * , args);
						 */
						String[] args={userid};
						Cursor c = db
								.rawQuery(
										" SELECT codigo, nombre, email, password, url  FROM Identities WHERE userid=?",
										args);
						if (c.moveToFirst()) {
							// Recorremos el cursor hasta que no haya más
							// registros
							do {
								String identifier = c.getString(0);
								String name = c.getString(1);
								String login = c.getString(2);
								String password = c.getString(3);
								try {
									password= Security.decrypt(Base64.decodeWebSafe(salt),password, passwordUser+Secure.ANDROID_ID);
									login= Security.decrypt(Base64.decodeWebSafe(salt),login,passwordUser+Secure.ANDROID_ID);
								} catch (Base64DecoderException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								String url = c.getString(4);
								Identity id = new Identity(identifier, name,
										url, login, password);
								idList.add(id);
							} while (c.moveToNext());
						}
						db.close();
						credentials = new Credential(userInfo.getAppid(),
								Utils.getCadenaAlfanumAleatoria(20),
								"app/response/identity/set");
						credentials.setSignature(userInfo.getSignaturekey());
					} else {

						credentials = new Credential(userInfo.getAppid(),
								Utils.getCadenaAlfanumAleatoria(20),
								"app/response/identity/store");
						credentials.setSignature(userInfo.getSignaturekey());

					}

					Argument arg = new Argument(requestId, "accepted",
							credentials, idList);
					/*
					 * scanText.setText("parsing result " +
					 * arg.generateJson(hasList).toString());
					 */

					/*
					 * Intent intent = new Intent(context,
					 * Activation.class); intent.putExtra(EXTRA_MESSAGE,
					 * password); startActivity(intent); finish();
					 */

					ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()) {
						JSONObject[] params = new JSONObject[] { arg
								.generateJson(hasList) };
						pDialog = new ProgressView(context);
						if (hasList) {
							new RequestIdentity().execute(params);
						} else {
							new StoreIdentity().execute(params);
						}
					} else {
						Toast.makeText(
								context,
								context.getString(
										R.string.toast_no_network), Toast.LENGTH_LONG)
								.show();
					}

				}

			}
		

		class RequestIdentity extends AsyncTask<JSONObject, Integer, String> {

			/**
			 * Do the HTTP Request PUT
			 */
			@Override
			protected String doInBackground(JSONObject... params) {

				
				ArrayList<Pair> listParam = new ArrayList<Pair>();
				Pair p = new Pair("argument", params[0].toString());
				listParam.add(p);
				String result = Utils
						.doHttpRequest(
								1,
								listParam,
								"https://service.mylogim.com/api1/app/response/identity/set",
								"POST", true);
			
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

							/*Intent intent = new Intent(context,
									MainActivity.class);
							intent.putExtra("userid",userid);
							intent.putExtra("password", passwordUser);
							overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
							startActivity(intent);
							finish();*/
							Toast.makeText(
									context,
									context.getString(
											R.string.toast_identities_loaded),// Email
																		// is
																		// already
																		// registered
									Toast.LENGTH_LONG).show();
							pDialog.cancel();
							
						} else {

							
								Toast.makeText(
										context,
										context.getString(
												R.string.toast_server_error),
										Toast.LENGTH_LONG)// Server Error
										.show();
								pDialog.cancel();
							

						}
					} else {
						Toast.makeText(
								context,
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
						RequestIdentity.this.cancel(true);
					}
				});

				pDialog.setProgress(0);
				pDialog.show();
			}

		}

		class StoreIdentity extends AsyncTask<JSONObject, Integer, String> {

			/**
			 * Do the HTTP Request PUT
			 */
			@Override
			protected String doInBackground(JSONObject... params) {

			
				ArrayList<Pair> listParam = new ArrayList<Pair>();
				Pair p = new Pair("argument", params[0].toString());
				listParam.add(p);
				String result = Utils
						.doHttpRequest(
								1,
								listParam,
								"https://service.mylogim.com/api1/app/response/identity/store",
								"POST", true);
				
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
						
							String info = json.getString("info");
							JSONObject jsonInfo = new JSONObject(info);
							
							String id = jsonInfo.getString("identifier");
							String name = jsonInfo.getString("name");
							name=name.toLowerCase();
							String login = jsonInfo.getString("username");
							String url = jsonInfo.getString("originUrl");
							String password = jsonInfo.getString("password");
							Database database = new Database(
									context, "DBIdentities",
									null, 1);
							SQLiteDatabase db = database.getReadableDatabase();
							String[] args={userid};
							Cursor c = db.rawQuery(
									"SELECT COUNT(*) FROM Identities WHERE userid=?",args);
							int number = 0;
							if (c.moveToFirst()) {
								do {
									number = c.getInt(0);
								} while (c.moveToNext());
							}
							db.close();
							
							UserInfoDao userInfoDao = new UserInfoDao(context);
							int limit = Integer.parseInt(userInfoDao.selectLimit(userid));
							
							if (number < limit) {
								
								SecretKey key = null;
								try {
									key=Security.getExistingKey(passwordUser+Secure.ANDROID_ID, Base64.decodeWebSafe(salt));
								} catch (Base64DecoderException e) {
									e.printStackTrace();
								}
								String passwordEncripted = Security.cipher(password, key);
								byte[] iv= Security.iv;
								passwordEncripted=Base64.encodeWebSafe(iv, true)+"]"+passwordEncripted;
								String loginEncripted = Security.cipher(login, key);
								iv= Security.iv;
								loginEncripted=Base64.encodeWebSafe(iv, true)+"]"+loginEncripted;
								
								db = database.getWritableDatabase();
								db.execSQL("INSERT INTO Identities (codigo, nombre, email, password, url, creation, visited, count, userid, user) "
										+ "VALUES ('"
										+ Utils.getCadenaAlfanumAleatoria(20)
										+ "', '"
										+ name
										+ "', '"
										+ loginEncripted
										+ "', '"
										+ passwordEncripted
										+ "', '"
										+ url
										+ "', '"
										+ Utils.getDatePhone()
										+ "', '"
										+ Utils.getDatePhone()
										+ "', '" + "0" + "', '"+ userid + "', '"+ userInfo.getUseremail() +"')");
								db.close();
								unique = false;

								toIdentity();
							} else {
								Toast.makeText(
										context,
										context.getString(
												R.string.toast_limit_identities),
										Toast.LENGTH_LONG).show();
								pDialog.cancel();
								unique = false;

							}
						} else {

							
								Toast.makeText(
										context,
										context.getString(
												R.string.toast_server_error),
										Toast.LENGTH_LONG)// Server Error
										.show();
								pDialog.cancel();
								unique = false;
							

						}
					} else {
						Toast.makeText(
								context,
								context.getString(R.string.toast_server_error),// HTTP
																				// Error
								Toast.LENGTH_LONG).show();
						pDialog.cancel();
						unique = false;
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
						StoreIdentity.this.cancel(true);
					}
				});

				pDialog.setProgress(0);
				pDialog.show();
			}

		}

		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				Object[] objs = syms.toArray();
				Symbol sym = (Symbol) objs[0];
				// for (Symbol sym : syms) {
				// scanText.setText("barcode result " + sym.getData());
//				barcodeScanned = true;
				String message = sym.getData();
			
				messageToParse = " ";
				boolean exit = false;
				int index1 = 0, index2 = 0;

				do {
					// Mirar manera de saber que el codigo es de Logim
					index1 = message.indexOf("=", index2);
					System.out.println(index1);
					if (!message.contains("mylogim.com")) {
						index1 = -1;
					}
					if (index1 != -1) {
						index2 = message.indexOf("%", index1);
						if (index2 != -1) {
							messageToParse = messageToParse
									+ message.substring(index1 + 1, index2)
									+ " ";
						} else {
							index2 = message.length();
							messageToParse = messageToParse
									+ message.substring(index1 + 1, index2)
									+ " ";
							exit = true;
						}
					} else {
						Toast.makeText(
								context,
								context.getString(R.string.toast_fault_qr),//Qr incorrecto

								Toast.LENGTH_LONG).show();
						exit = true;
					}

				} while (!exit);
				// scanText.setText("parsing result " + messageToParse);

				ProcessQR(messageToParse);

				// }
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

}
