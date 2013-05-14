package com.farissyariati.kuma;

import com.farissyariati.kuma.projects.ProjectListActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

public class SplashScreenActivity extends Activity {
	private static final int MILIS_SPLASH_LOAD = 3000;
	private Thread splashThread;
	private FPreferencesManager fpm;
	/*
	private Thread timeCounterThread;
	private boolean counterOn = true;
	private int counter;
*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int orientation = getResources().getConfiguration().orientation;
		
		if(orientation == 1)
			setContentView(R.layout.splash_screen);
		else
			setContentView(R.layout.splash_screen_lanscape);
		
		this.fpm = new FPreferencesManager(this);
		onCheckRootDir();
		splashLoad();
	}

	private void onCheckRootDir() {
		FFileManager fFileManager = new FFileManager();
		fFileManager.checkRootDirectory();
	}

	private void splashLoad() {
		splashThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					synchronized (this) {
						wait(MILIS_SPLASH_LOAD);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					try {
						int firstInstallationSate = fpm.getOnFirstInstall();
						if (firstInstallationSate == 1) {
							Intent actionPreferences = new Intent(getBaseContext(),
									ApplicationPreferencesActivity.class);
							startActivity(actionPreferences);
							finish();
						} else {
							retrievedSavedPreferences();
							checkSessionIDAvailable();
						}
					} catch (Exception e) {
						startLoginActivity();
					}
				}
			}
		});
		splashThread.start();
	}
	

	private void startLoginActivity() {
		Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
		startActivity(loginIntent);
		SplashScreenActivity.this.finish();
		overridePendingTransition(R.anim.main_fade_in, R.anim.splash_fade_out);
		joinSplashThread();
	}

	private void startProjectList() {
		Intent projectList = new Intent(this, ProjectListActivity.class);
		startActivity(projectList);
		SplashScreenActivity.this.finish();
		overridePendingTransition(R.anim.main_fade_in, R.anim.splash_fade_out);
		joinSplashThread();
	}

	private void joinSplashThread() {
		try {
			splashThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void retrievedSavedPreferences() {
		fpm.setEnabledNotificationState(fpm.getSavedEnabledNotificationState());
	}

	private void checkSessionIDAvailable() {
		fpm.setFileListActivityStartState(0);
		CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
		FFileManager manager = new FFileManager();
		if (!fpm.getSessionID().equals("")) {
			collManager.getProjectsJSONObject(fpm.getSessionID());
			fpm.setProjectCount(collManager.getProjectCount());
			if (collManager.getProjectsStatusCode() == 1) {
				try {
					manager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_PROJECT, collManager.getProjectsJSONArray()
							.toString());
					startProjectList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(this, "Your Session has expired, Please Login", Toast.LENGTH_LONG).show();
				startLoginActivity();
			}

		} else {
			startLoginActivity();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfiguration){
		if(newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE)
			setContentView(R.layout.splash_screen);
		if(newConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT)
			setContentView(R.layout.splash_screen_lanscape);
	}
}
