package com.farissyariati.kuma;

import com.farissyariati.kuma.dashboard.DashboardActivity;
import com.farissyariati.kuma.messaging.EmailFetcherActivity;
import com.farissyariati.kuma.projects.ProjectListActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NotificationReceiverActivity extends Activity {
	private int updaterState;
	private CollabtiveManager collManager;
	private FFileManager fileManager;
	private ProgressDialog pdNotificator;
	private Thread projectUpdater;
	private FPreferencesManager fpm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_receiver);
		this.fpm = new FPreferencesManager(this);
		this.fileManager = new FFileManager();
		this.updaterState = fpm.getNotificationUpdaterValue();
		initUpdater();
	}

	private void initUpdater() {
		runUpdate();
	}

	private void runUpdate() {
		this.pdNotificator = new ProgressDialog(this);
		pdNotificator.setCancelable(false);
		pdNotificator.setMessage("Updating..");
		pdNotificator.show();

		this.projectUpdater = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
//					collManager = new CollabtiveManager(CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME);
					collManager = new CollabtiveManager(getBaseContext());
					collManager.getProjectsJSONObject(fpm.getSessionID());
					System.out.println("Notification Receiver: " + collManager.projectsJSONObjects);
					if (collManager.getProjectsStatusCode() == 1) {
						System.out.println("Notification Receiver: " + collManager.getProjectsJSONArray().toString());
						fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_PROJECT, collManager
								.getProjectsJSONArray().toString());
						System.out.println("Notification Receiver: Finish Writing Files");
						pdDismissHandler.sendMessage(pdDismissHandler.obtainMessage());

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		projectUpdater.start();
	}

	private void startNewProjectListActivity() {
		Intent projectList = new Intent(this, ProjectListActivity.class);
		projectList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(projectList);
	}

	Handler pdDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			System.out.println("Notification Receiver, Ready for Dismissing");
			pdNotificator.dismiss();
			if (updaterState == 1)
				startNewProjectListActivity();
			else if (updaterState == 2) {
				finish();
				goToProjectDashboard(fpm.getTemporaryPassedPID());
			}
			else if(updaterState == 3){
				finish();
				Intent showNewMail = new Intent(getApplicationContext(), EmailFetcherActivity.class);
				startActivity(showNewMail);
			}

		}
	};

	private void goToProjectDashboard(int projectID) {
		Intent projectDashboard = new Intent(this, DashboardActivity.class);
		projectDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		projectDashboard.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_POSTED_ID, projectID);
		startActivity(projectDashboard);
	}

}
