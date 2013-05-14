package com.farissyariati.kuma;

import com.farissyariati.kuma.projects.ProjectListActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity {

	private ProgressDialog loginProgressDialog;
	private TextView tvUsername;
	private TextView tvPassword;
	private Button loginButton;
	private CheckBox cbRemember;

	private Thread loginThread;
	private boolean authenticated;
	private CollabtiveManager collManager;

	private String sessionID;
	private String rememberedUsername;
	private String rememberedPassword;
	private String result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int orientation = getResources().getConfiguration().orientation;
		if(orientation == 1)
			setContentView(R.layout.login_page);
		else
			setContentView(R.layout.login_page_lanscape);
		initComponents();
		setRemeberedUsernamePassword();
		initOnLoginButtonClicked();
	}

	private void initComponents() {
		loginProgressDialog = new ProgressDialog(this);
		tvUsername = (TextView) findViewById(R.id.login_edit_text);
		tvPassword = (TextView) findViewById(R.id.password_edit_text);
		loginButton = (Button) findViewById(R.id.login_button);
		cbRemember = (CheckBox) findViewById(R.id.cb_remember);
	}

	private void initOnLoginButtonClicked() {
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cbRemember.isChecked()) {
					FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
					fpm.setRememberedUsername(tvUsername.getText().toString());
					fpm.setRememberedPassword(tvPassword.getText().toString());
				}
				onCheckData();
			}
		});
	}

	private void onLoading() {
		loginProgressDialog.setMessage("Connecting to Collabtive Site..");
		loginProgressDialog.setCancelable(false);
		loginProgressDialog.show();
	}

	@SuppressLint("HandlerLeak")
	private void connecting() {
		//collManager = new CollabtiveManager(collabtivePath);
		collManager = new CollabtiveManager(getBaseContext());
		onLoading();
		this.loginThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				authenticated = collManager.authenticate(tvUsername.getText().toString(), tvPassword.getText()
						.toString());
				if (authenticated) {
					FFileManager manager = new FFileManager();
					sessionID = collManager.getSessionID();
					int userID = collManager.getUserID();
					FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
					fpm.setUserID(userID);
					collManager.getProjectsJSONObject(sessionID);
					fpm.setProjectCount(collManager.getProjectCount());
					if (collManager.getProjectsStatusCode() == 1) {
						try {
							manager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_PROJECT, collManager
									.getProjectsJSONArray().toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						result = "Connection success..";
						setSessionID();
						goToProjectList();
					} else {
						result = "This account does not have a permission to edit a project";
					}
				} else
					result = "Password doesn't match";
				loginHandler.sendMessage(loginHandler.obtainMessage());
				disconnecting();
			}
		});
		loginThread.start();
	}

	Handler loginHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			loginProgressDialog.dismiss();
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
		}
	};

	private void disconnecting() {
		try {
			loginThread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void goToProjectList() {
		Intent projectList = new Intent(getBaseContext(), ProjectListActivity.class);
		startActivity(projectList);
		LoginActivity.this.finish();
	}

	private void setSessionID() {
		FPreferencesManager fpManager = new FPreferencesManager(this);
		fpManager.setSessionID(sessionID);
	}

	private void onCheckData() {
		if (tvUsername.getText().toString().equals("") || tvPassword.getText().toString().equals(""))
			Toast.makeText(getBaseContext(), "Username & Password Required", Toast.LENGTH_LONG).show();
		else
			// goToProjectList();
			connecting();
	}

	private void setRemeberedUsernamePassword() {
		this.rememberedUsername = new FPreferencesManager(getBaseContext()).getRememberedUsername();
		this.rememberedPassword = new FPreferencesManager(getBaseContext()).getRemeberedPassword();
		System.out.println("Remembered Password is: " + rememberedPassword);
		tvUsername.setText(rememberedUsername);
		tvPassword.setText(rememberedPassword);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Settings").setIcon(R.drawable.next);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
			fpm.setPreferencesLoginState(1);
			Intent preferencesActivity = new Intent(getBaseContext(), ApplicationPreferencesActivity.class);
			startActivity(preferencesActivity);
			break;
		default:
			break;
		}
		return true;
	}

}
