package com.farissyariati.kuma;

import com.farissyariati.kuma.utility.FPreferencesManager;
import com.farissyariati.kuma.utility.NotificationService;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class ApplicationPreferencesActivity extends PreferenceActivity {
	private FPreferencesManager fpm;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
		this.fpm = new FPreferencesManager(this);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			//try
			if(fpm.getPreferencesLoginState() ==  1){
				System.out.println("PREFERENCES LOGIN: ACCESS FROM LOGIN");
				fpm.setPreferencesLoginState(0);
				finish();
			}else{
				Intent notificationService = new Intent(this,
						NotificationService.class);
				startService(notificationService);
				finish();
			}
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Next").setIcon(R.drawable.next);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			int firstInstallState = fpm.getOnFirstInstall();
			if(firstInstallState == 1){
				fpm.setOnFirstInstall(0);
				Intent goToLoginPage = new Intent(this, LoginActivity.class);
				startActivity(goToLoginPage);
				finish();
			}
			break;
		default:
			break;
		}
		return true;
	}
}
