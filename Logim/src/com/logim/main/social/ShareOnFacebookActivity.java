package com.logim.main.social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.*;
import com.logim.db.UserInfoDao;
import com.logim.main.R;
import com.logim.main.camera.Credential;
import com.logim.main.utils.Database;
import com.logim.main.utils.Pair;
import com.logim.main.utils.SysApplication;
import com.logim.main.utils.Utils;
import com.logim.view.ProgressView;
import com.logim.vo.UserInfoVo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShareOnFacebookActivity extends Activity implements OnClickListener{

	ProgressDialog pDialog;
	private EditText sendEdt;
	private Button sendButton;
	private LoginButton loginButton;
	private String messageToPost;
	String userid;
	String passwordUser;
	UserInfoVo userInfo;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private final String PENDING_ACTION_BUNDLE_KEY = "com.logim.main.social:PendingAction";
	private ProfilePictureView profilePictureView;
	private GraphUser user;
	private PendingAction pendingAction = PendingAction.NONE;

	private enum PendingAction {
		NONE, POST_STATUS_UPDATE
	}

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}
		setContentView(R.layout.activity_share);
		Intent intentReceived = getIntent();
		userid = intentReceived.getStringExtra("userid");
		passwordUser = intentReceived.getStringExtra("password");
		loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
		loginButton.setVisibility(ViewGroup.VISIBLE);
		loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						ShareOnFacebookActivity.this.user = user;
						updateUI();
						// It's possible that we were waiting for this.user to
						// be populated in order to post a
						// status update.
						handlePendingAction();
					}
				});
		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
		profilePictureView.setVisibility(ViewGroup.VISIBLE);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		SysApplication.getInstance().addActivity(this);
		sendButton = (Button) findViewById(R.id.share_button);
		sendButton.setOnClickListener(this);
		sendEdt = (EditText) findViewById(R.id.share_edit);
		
		UserInfoDao userInfoDao = new UserInfoDao(this);
		userInfo = userInfoDao.selectUserInfo(userid);
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (pendingAction != PendingAction.NONE
				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(ShareOnFacebookActivity.this)
					.setTitle(R.string.facebook_cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.facebook_ok, null).show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
			handlePendingAction();
		}
		updateUI();
	}

	private void updateUI() {
		Session session = Session.getActiveSession();
		boolean enableButtons = (session != null && session.isOpened());
		sendButton.setEnabled(enableButtons);
		if (enableButtons && user != null) {
			profilePictureView.setProfileId(user.getId());
			//profilePictureView.set
		} else {
			System.out.println("null");
			profilePictureView.setProfileId(null);
		}
	}

	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but
		// we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;
		switch (previouslyPendingAction) {
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		default:
			break;
		}
	}

	private void showPublishResult(String message, GraphObject result,
			FacebookRequestError error) {
		if (error == null) {
			//showToast("Message posted to your Weibo wall!");
			String url = "https://service.mylogim.com/api1/app/refer/social";
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				String[] params = new String[] { url };
				pDialog= new ProgressView(ShareOnFacebookActivity.this);
				new ConfirmSocial().execute(params);
			}
			else {
				Toast.makeText(ShareOnFacebookActivity.this,
						ShareOnFacebookActivity.this.getString(R.string.toast_no_network),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(ShareOnFacebookActivity.this, "Share fails!", Toast.LENGTH_SHORT).show();
		}

	}

	private class ConfirmSocial extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			ArrayList<Pair> listParam = new ArrayList<Pair>();
			Pair p = new Pair("appid", userInfo.getAppid());
			listParam.add(p);
			String nounce = Utils.getCadenaAlfanumAleatoria(20);
			Credential credentials = new Credential(userInfo.getAppid(), nounce,
					"app/refer/social");
			credentials.setSignature(userInfo.getSignaturekey());
			String signature = credentials.getSignature();
			p = new Pair("nounce", nounce);

			listParam.add(p);
			p = new Pair("signature", signature);
			listParam.add(p);

			p = new Pair("reftype", "facebook");
			listParam.add(p);

			String result = Utils.doHttpRequest(3, listParam, params[0],
					"POST", false);
			return result;

		}

		protected void onPostExecute(String result) {

			if (!result.equals("")) {
				JSONObject json = null;
				try {
					json = new JSONObject(result);

					
					if (json.getString("status").equals("success")) {
						if (userInfo.getReffacebook().equals("false")) {
							Database database = new Database(ShareOnFacebookActivity.this,
									"DBUserInfo", null, 1);
							SQLiteDatabase db = database.getWritableDatabase();
							int lim = Integer.parseInt(userInfo.getIdentitiesstoragelimit());
							lim = lim + 5;
							userInfo.setIdentitiesstoragelimit(Integer.toBinaryString(lim));
							
							db.execSQL("UPDATE UserInfo SET limite = '" + userInfo.getIdentitiesstoragelimit()
									+ "' WHERE userid ='" + userid + "'");
							db.execSQL("UPDATE UserInfo SET facebook = '"
									+ "true" + "' WHERE userid ='" + userid
									+ "'");
							db.close();
							database.close();

							Toast.makeText(
									ShareOnFacebookActivity.this,
									ShareOnFacebookActivity.this
											.getString(R.string.toast_success_facebook),
									Toast.LENGTH_LONG).show();
							
							pDialog.cancel();
							

						}
						
						Toast.makeText(
								ShareOnFacebookActivity.this,
								ShareOnFacebookActivity.this
										.getString(R.string.toast_success_facebook),
								Toast.LENGTH_LONG).show();
						goBack();
					}
					else
					{
						Toast.makeText(ShareOnFacebookActivity.this,
								ShareOnFacebookActivity.this.getString(
										R.string.toast_server_error), Toast.LENGTH_LONG)// Server
																			// Error
								.show();
						
						
					}
					pDialog.cancel();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			else{
			Toast.makeText(
					ShareOnFacebookActivity.this,
					ShareOnFacebookActivity.this.getString(
							R.string.toast_server_error), Toast.LENGTH_LONG)// Server
																// Error
					.show();
			pDialog.cancel();
			
			}
		}
		
		@Override
		protected void onPreExecute() {
			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					ConfirmSocial.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		
		}
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			goBack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void goBack() {
		this.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {

			goBack();
		}
		default:

			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public void onClick(View arg0) {
		if (checkParameters(sendEdt.getText().toString())) {
			onClickPostStatusUpdate();
		}	
	}

	private void onClickPostStatusUpdate() {
		performPublish(PendingAction.POST_STATUS_UPDATE);
	}

	private void postStatusUpdate() {
		if (user != null && hasPublishPermission()) {
            messageToPost=sendEdt.getText().toString();
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), messageToPost,
					new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							showPublishResult(messageToPost,
									response.getGraphObject(),
									response.getError());
						}
					});
			request.executeAsync();
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}
    private void performPublish(PendingAction action) {
        Session session = Session.getActiveSession();
        if (session != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSIONS));
            }
        }
    }

	private boolean checkParameters(String text) {
		boolean valid = true;
		if (text.equals("")) {
			Toast.makeText(this, this.getString(R.string.toast_fault_text),
					Toast.LENGTH_LONG).show();
			valid = false;
		}
		return valid;
	}

}
