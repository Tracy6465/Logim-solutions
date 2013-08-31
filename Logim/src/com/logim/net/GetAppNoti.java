package com.logim.net;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.utils.Pair;
import com.logim.main.utils.Utils;
import com.logim.vo.UserInfoVo;

import android.content.Context;
import android.widget.Toast;

public class GetAppNoti extends Thread {
	
	private Context context;
	private UserInfoVo userInfo;
	String url = "https://service.mylogim.com/api1/app/notifications";
	String[] args = new String[] { url };
	
	public GetAppNoti(Context context, UserInfoVo userInfo) {
		super();
		
		//TODO
		System.out.println("GetAppNoti");
		
		this.context = context;
		this.userInfo = userInfo;
	}

	@Override
	public void run() {
		ArrayList<Pair> listParam = new ArrayList<Pair>();
		Pair p = new Pair("appid", userInfo.getAppid());
		listParam.add(p);
		
		String nounce = Utils.getCadenaAlfanumAleatoria(20);
		p = new Pair("nounce", nounce);
		listParam.add(p);
		
		Credential credentials = new Credential(userInfo.getAppid(), nounce,
				"app/notifications");
		credentials.setSignature(userInfo.getSignaturekey());
		String signature = credentials.getSignature();
		p = new Pair("signature", signature);
		listParam.add(p);
		
		String result = Utils.doHttpRequest(3, listParam, url,
				"POST", false);
		
		try {
			if (!result.equals("")) {
				//TODO
				System.out.println(result);
				JSONObject json = new JSONObject(result);
			
				if (json.getString("status").equals("success")) {
					String info = json.getString("info");
					JSONObject json2 = new JSONObject(info);
					
					if (json2.getString("userInfoChanged").equals("true")) {
						new GetAppInfoM(context, userInfo);
					}
				}
			}
			else {
				Toast.makeText(context,
						context.getString(R.string.toast_server_error),
						Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		new GetAppNoti(context, userInfo).start();
	}
}
