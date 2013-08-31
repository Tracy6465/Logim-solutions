package com.logim.net;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Utils;
import com.logim.vo.UserInfoVo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetUserInfo extends AsyncTask<String, Integer, String> {
	
	private Activity activity;
	private Context context;
	private String email;
	private String appid;
	private String signaturekey;
	private Intent intent;
	private UserInfoVo userInfo;
	
	public GetUserInfo(Activity activity, Context context, String email, String password, String appid, String signaturekey, Intent intent) {
		super();
		this.activity = activity;
		this.context = context;
		this.email = email;
		this.appid = appid;
		this.signaturekey = signaturekey;
		this.intent = intent;
		
		userInfo = new UserInfoVo();
		
		String url = "https://service.mylogim.com/api1/user/info?email="
				+ email + "&password=" + password;
		String[] params = new String[] { url };
		this.execute(params);
	}
	
	public UserInfoVo getUserInfo() {
		return userInfo;
	}

	@Override
	protected String doInBackground(String... params) {
		ArrayList<Pair> listParam = new ArrayList<Pair>();
		String result = Utils.doHttpRequest(2, listParam, params[0], "GET",
				false);
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		try {

			if (!result.equals("")) {
				JSONObject json = new JSONObject(result);
				
				if (json.getString("status").equals("success")) {
					String info = json.getString("info");
					JSONObject subJson = new JSONObject(info);
					userInfo.setUseremail(email);
					userInfo.setUserid(subJson.getString("userid"));
					userInfo.setAppid(appid);
					userInfo.setType(subJson.getString("type"));
					userInfo.setStatus(subJson.getString("status"));
					userInfo.setIdentitiesstoragelimit(subJson.getString("identitiesstoragelimit"));
					userInfo.setSignaturekey(signaturekey);
					userInfo.setRefemail(subJson.getString("refemail"));
					userInfo.setReffacebook(subJson.getString("reffacebook"));
					userInfo.setReftwitter(subJson.getString("reftwitter"));
					userInfo.setRefweibo(subJson.getString("refweibo"));
					userInfo.setPremexp(subJson.getString("premexp"));
					
					UserInfoDao userInfoDao = new UserInfoDao(context);
					userInfoDao.insertUserInfo(userInfo);
					userInfoDao.close();
					
					activity.startActivity(intent);
					activity.finish();
					activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
				else {

					if (json.getString("message").equals("User not found")) {
						Toast.makeText(
								context,
								context.getString(
										R.string.toast_email_incorrect),
								Toast.LENGTH_LONG).show();

					}
					else if (json.getString("message").equals(
							"Authentication failed")) {
						Toast.makeText(
								context,
								context.getString(
										R.string.toast_fault_password_user),
								Toast.LENGTH_LONG).show();

					} else {
						Toast.makeText(
								context,
								context.getString(
										R.string.toast_server_error), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
			else {
				Toast.makeText(context,
						context.getString(R.string.toast_server_error),
						Toast.LENGTH_LONG).show();
			}
		}
		catch (JSONException e) {
			e.printStackTrace ();
		}
		
		this.cancel(true);
	}

}
