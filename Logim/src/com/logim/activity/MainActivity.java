package com.logim.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.camera.CameraActivity;
import com.logim.main.social.SocialActivity;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.SysApplication;
import com.logim.service.AppNotiService;
import com.logim.utils.Pref;
import com.logim.vo.UserInfoVo;

/**
 * 
 * @author David Prieto Rivera Activity that shows the identity ListView
 * 
 */
public class MainActivity extends Activity implements TextWatcher, OnNavigationListener {

	// ATRIBUTES
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	public final static ArrayList<Pair> listItems = new ArrayList<Pair>();// lista
																			// identidades
	Database database;// identity database
	MySimpleAdapter adapter;
	int identityCounter = 0;// counter identities
	int listLength;
	String criterio = "codigo";//criterio means criterion,codigo means code
	Intent intent;// intent
	boolean searching;
	String searchText;
	int seleccion;
	String messageEliminar;
	String userid;
	String salt;
	String password;
	
	UserInfoVo userInfo = new UserInfoVo();
	
	ActionBar actionBar;
	// ACTIVITY LIFECYCLE
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);// Asignamos layout
		SysApplication.getInstance().addActivity(this); 
		
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		Intent intentReceived = getIntent();
		userid = intentReceived.getStringExtra("userid");
		password = intentReceived.getStringExtra("password");
		
		ArrayAdapter<String> list = new ArrayAdapter<String>(actionBar.getThemedContext(),
				R.layout.item_spinner,
				R.id.spinner_text, new String[] {
						getString(R.string.option_name),
						getString(R.string.option_creation),
						getString(R.string.option_last), 
						getString(R.string.option_often)});
		list.setDropDownViewResource(R.layout.item_spinner);
		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
				// Specify a SpinnerAdapter to populate the dropdown list.
				list, this);
		// inicializamos atributos

		intent = new Intent(this, IdentityInfo.class);
		database = new Database(this, "DBIdentities", null, 1);
		adapter = new MySimpleAdapter(this,
				android.R.layout.simple_list_item_1, listItems);

		searching = false;
		seleccion = -1;
		
		// build listview
		ListView lv = (ListView) findViewById(R.id.identities_list);
		lv.setTextFilterEnabled(true);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When we click, open view of identity information
				String message = ((TextView) view
						.findViewById(android.R.id.text2)).getText().toString();
				intent.putExtra("id", message);
				intent.putExtra("userid", userid);
				intent.putExtra("show", false);
				intent.putExtra("salt", salt);
				intent.putExtra("password", password);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String message = ((TextView) arg1
						.findViewById(android.R.id.text2)).getText().toString();
				Dialog d = crearDialogoConfirmacion(message);
				d.setCanceledOnTouchOutside(true);
				d.show();

				return true;
			}
		});

		listItems.clear();
		
		SQLiteDatabase db = database.getReadableDatabase();
		String[] args = { userid };
		Cursor c = db.rawQuery(
				" SELECT codigo, nombre FROM Identities WHERE userid=? ORDER BY "
						+ criterio, args);

		identityCounter = 0;
		if (c.moveToFirst()) {
			// Recorremos el cursor hasta que no haya más registros
			do {
				String code = c.getString(0);
				String nombre = c.getString(1);
				Pair p = new Pair(code, nombre);
				listItems.add(p);
				identityCounter++;
			} while (c.moveToNext());

		}
		db.close();

		Database database3 = new Database(this, "DBUserSalt", null, 1);
		db = database3.getReadableDatabase();

		// Si hemos abierto correctamente la base de datos
		if (db != null) {
			String[] args2 = { userid };
			c = db.rawQuery("SELECT salt FROM UserSalt WHERE userid=?", args2);
			if (c.moveToFirst()) {
				salt = c.getString(0);
				
			}
		}
		db.close();
		adapter.notifyDataSetChanged();
		
		Pref pref = new Pref(this);
		if (pref.getMainTutorials()) {
			toTutorials();
		}
		
	}
	
	@Override
	protected void onResume() {// Actualiza lista cada vez que se inicia la
		// actividad
		super.onResume();
		actualizar();
	}
	
    private long exitTime = 0; 
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){ 
		    if((System.currentTimeMillis()-exitTime) > 2000){ 
			    Toast.makeText(getApplicationContext(), getString(R.string.toast_exit), Toast.LENGTH_SHORT).show(); 
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
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		seleccion = itemPosition;
		switch (itemPosition) {
		case 0:
			criterio = "nombre";
			break;
		case 1:
			criterio = "creation";
			break;
		case 2:
			criterio = "visited";
			break;
		case 3:
			criterio = "count";
			break;
		}
		
		actualizar();
		
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch(item.getItemId()) {
        case R.id.action_scan:
        	toCamera();
            return true;
        case R.id.action_search:
        	AutoCompleteTextView auto = (AutoCompleteTextView) item.getActionView().findViewById(R.id.search_text);
    		auto.addTextChangedListener(this);
    		auto.setFocusable(true);
    		auto.setFocusableInTouchMode(true);
    		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    		auto.requestFocus();
    		
        	return true;
        case R.id.action_add:
        	intent = addItems();
        	break;
        case R.id.action_settings:
        	toSetting();
        	return true;
        }
		intent.putExtra("userid", userid);
		intent.putExtra("identities", identityCounter);
		intent.putExtra("password", password);
		intent.putExtra("salt", salt);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		
		return super.onOptionsItemSelected(item);
	}
	
	private void toCamera() {
		Intent intent = new Intent(this, CameraActivity.class);
		intent.putExtra("userid", userid);
		intent.putExtra("password", password);
		
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		
		finish();
	}
	
	private Intent addItems() {
		Intent intent = new Intent(this, IdentityForm.class);
		intent.putExtra("mail", userInfo.getUseremail());
		
		return intent;
	}
	
	private void toSetting() {
		Intent intent = new Intent(this, SettingsActivity.class);
		intent.putExtra("userid", userid);
		intent.putExtra("identities", identityCounter);
		intent.putExtra("password", password);
		intent.putExtra("salt", salt);
		
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		
		finish();
	}
	
	public void toSocial(View v) {
		Intent intent2 = new Intent(this, SocialActivity.class);
		intent2.putExtra("userid", userid);
		intent2.putExtra("password", password);
		startActivity(intent2);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void toTutorials() {
		Intent intent = new Intent(this, TutorialsMainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, 0);
	}

	private Dialog crearDialogoConfirmacion(String message) {
		messageEliminar = message;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(this.getString(R.string.dialog_eliminar));
		builder.setMessage(this.getString(R.string.dialog_eliminar_pregunta));
		builder.setPositiveButton(this.getString(R.string.button_accept),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					
						identityCounter = identityCounter - 1;
						String[] args = new String[] { messageEliminar, userid };
						SQLiteDatabase db = database.getWritableDatabase();
						db.execSQL(
								"DELETE FROM Identities WHERE codigo=? AND userid=?",
								args);
						db.close();

						actualizar();//update list
						dialog.cancel();
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

	/**
	 * Refresh the listview of identities
	 */
	public void actualizar() {

		
		String[] args = { userid };
		listItems.clear();
		
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor c2 = db.rawQuery(
				" SELECT count(*) from Identities WHERE userid=?", args);
		Cursor c;
		boolean desc = true;
		
		//by hongtao
//		if (criterio.equals("nombre") && ) {
//
//			desc = false;
//		} else 
		if (criterio.equals("creation")) {
			desc = false;
		}
		
		String  sql = " SELECT codigo, nombre FROM Identities WHERE ";
		if (!searching) {
			sql += "userid=? ORDER BY " + criterio;
		} else {
			sql += "nombre LIKE '%" + searchText + "%' AND userid= ? ORDER BY " + criterio;
		}
		
		sql = desc ? sql + " DESC" : sql;
		c = db.rawQuery(sql, args);

		if (c2.moveToFirst()) {
			identityCounter = c2.getInt(0);
		}
		
		if (c.moveToFirst()) {
			// Recorremos el cursor hasta que no haya más registros
			do {

				String code = c.getString(0);
				String nombre = c.getString(1);
				Pair p = new Pair(code, nombre);
				listItems.add(p);
			} while (c.moveToNext());

		}
		db.close();
		adapter.notifyDataSetChanged();

 		UserInfoDao userInfoDao = new UserInfoDao(this);
		userInfo = userInfoDao.selectUserInfo(userid);
		userInfoDao.close();
		
		TextView textView = (TextView) findViewById(R.id.identities);
		String iden = getString(R.string.text_storage_1) + 
				identityCounter + 
				getString(R.string.text_storage_2);
		if (userInfo.getType().equals("premium")) {
			iden += getString(R.string.text_storage_infinity);
		}
		else {
			iden += userInfo.getIdentitiesstoragelimit();
		}
		textView.setText(iden);
		
		Intent intent = new Intent(this, AppNotiService.class);
		intent.putExtra("userInfo", userInfo);
		this.startService(intent);

	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		searchText = s.toString();
		searching = true;
		actualizar();
	}

	// Class to make custom adapter
	/**
	 * 
	 * Custom adapter with two elements
	 * 
	 */
	public class MySimpleAdapter extends ArrayAdapter {

		private ArrayList<Pair> results;

		public MySimpleAdapter(Context context, int resource,
				ArrayList<Pair> data) {
			super(context, resource, data);
			this.results = data;
		}

		public View getView(int position, View view, ViewGroup parent) {

			// Typeface localTypeface1 = Typeface.createFromAsset(getAssets(),
			// "fonts/256BYTES.TTF");
			View v = view;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.text_list, null);
			}

			// name
			TextView tt = (TextView) v.findViewById(android.R.id.text1);
			tt.setText(results.get(position).getNombre());

			// Identificador
			TextView tt2 = (TextView) v.findViewById(android.R.id.text2);
			String aux = String.valueOf(results.get(position).getCodigo());
			tt2.setText(aux);
			return v;
		}

	}

}
