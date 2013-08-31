package com.logim.net;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.logim.activity.PinActivity;
import com.logim.db.UserIdentityDao;
import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Utils;
import com.logim.vo.UserInfoVo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

public class GetAppInfo extends AsyncTask<String, Integer, String> {

	private Activity activity;
	private Context context;
	private UserInfoVo userInfo;
	private String password;
	
	private UserInfoDao userInfoDao;
	
	public GetAppInfo(Activity activity, Context context, UserInfoVo userInfo, String password) {
		this.activity = activity;
		this.context = context;
		this.userInfo = userInfo;
		this.password = password;
		
		userInfoDao = new UserInfoDao(context);
		
		String url = "https://service.mylogim.com/api1/app/info";
		String[] args = new String[] { url };
		this.execute(args);
	}
			
	@Override
	protected String doInBackground(String... params) {
		ArrayList<Pair> listParam = new ArrayList<Pair>();
		Pair p = new Pair("appid", userInfo.getAppid());
		listParam.add(p);
		
		String nounce = Utils.getCadenaAlfanumAleatoria(20);
		p = new Pair("nounce", nounce);
		listParam.add(p);
		
		Credential credentials = new Credential(userInfo.getAppid(), nounce,
				"app/info");
		credentials.setSignature(userInfo.getSignaturekey());
		String signature = credentials.getSignature();
		p = new Pair("signature", signature);
		listParam.add(p);
		
		p = new Pair("osversion", Build.VERSION.RELEASE);
		listParam.add(p);
		
		UserIdentityDao indentityDao = new UserIdentityDao(context);
		int count = indentityDao.selectIdentitiesCount(userInfo.getUserid());
		p = new Pair("usedids", count);
		listParam.add(p);
		
		String result = Utils.doHttpRequest(5, listParam, params[0],
				"POST", false);
		return result;
	}
	
	protected void onPostExecute(String result) {
		try {
			if (!result.equals("")) {
				JSONObject json = new JSONObject(result);
			
				if (json.getString("status").equals("success")) {
					String info = json.getString("info");
					JSONObject json2 = new JSONObject(info);
					
					if (json2.getString("appstatus").equals("blocked")) {
						Intent i = new Intent("com.logim.blocked");
						context.sendBroadcast(i);
					}
					else {
						userInfo.setUserid(json2.getString("userid"));
						userInfo.setType(json2.getString("type"));
						userInfo.setStatus(json2.getString("status"));
						userInfo.setIdentitiesstoragelimit(json2.getString("identitiesstoragelimit"));
						userInfo.setRefemail(json2.getString("refemail"));
						userInfo.setReffacebook(json2.getString("reffacebook"));
						userInfo.setReftwitter(json2.getString("reftwitter"));
						userInfo.setRefweibo(json2.getString("refweibo"));
						userInfo.setPremexp(json2.getString("premexp"));
						
						userInfoDao.updateUserInfo(userInfo);
						
						Database database3 = new Database(context, "DBUserSalt", null, 1);
						SQLiteDatabase db = database3.getReadableDatabase();
						String salt = "";
						
						if (db != null) {
							String[] args2 = {userInfo.getUserid()};
							Cursor c = db.rawQuery("SELECT salt FROM UserSalt WHERE userid=?", args2);
							if (c.moveToFirst()) {
								salt = c.getString(0);
								
							}
						}
						db.close();
						
						Intent intent = new Intent(context,
								PinActivity.class);
						intent.putExtra("userid", userInfo.getUserid());
						intent.putExtra("tipo", "main");
						intent.putExtra("password", password);
						intent.putExtra("salt",salt);
						activity.startActivity(intent);
						activity.finish();
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
					
				}
				//TODO
				else {
					Toast.makeText(
							context,
							context.getString(
									R.string.toast_server_error), Toast.LENGTH_LONG)
							.show();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
