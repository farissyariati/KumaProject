package com.farissyariati.kuma.utility;

import com.farissyariati.kuma.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

public class NotificationService extends Service {

	private int userID;
	private boolean ntfEnabled;
	private Thread projectNtfThread;
	private Thread emailNtfThread;
	private FPreferencesManager fpm;
	private CollabtiveNotificator collabtiveNotificator;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startID) {
		// this.intent = intent;
		this.fpm = new FPreferencesManager(getBaseContext());
		this.userID = fpm.getUserID();
		this.ntfEnabled = fpm.getEnabledNotificationState();
		this.collabtiveNotificator = new CollabtiveNotificator(new FPreferencesManager(this).getCollabtiveWebsite(),
				getBaseContext());
		createProjectNotificationService();
		createEmailNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		try {
			projectNtfThread.join();
		} catch (Exception e) {
			ntfEnabled = false;
		}
	}
	
	public void createProjectNotificationService() {
		this.projectNtfThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (ntfEnabled == true) {
					try {

						// get latest information about
						collabtiveNotificator.getCountLatestDataJSO(userID + "");
						int latestProjectCount = collabtiveNotificator.getNewProjectCount();
						int latestTaskCount = collabtiveNotificator.getNewTaskCount();

						System.out.println("Collabtive Notificator: Latest Project Count on Server: "
								+ latestProjectCount + " | On Application: " + fpm.getProjectCount());
						System.out.println("Collabtive Notificator: Latest Task Count on Server: " + latestTaskCount
								+ " | On Application: " + fpm.getTaskCount());

						// it means someone delete a task, and system need to
						// update it
						if (latestTaskCount < fpm.getTaskCount()) {
							System.out.println("Collabtive Notificator: Task Deletion On Server");
							fpm.setTaskCount(latestTaskCount);
						}

						// it means someone delete a project, and system need to
						// update it
						if (latestProjectCount < fpm.getProjectCount()) {
							fpm.setProjectCount(latestProjectCount);
							System.out.println("Collabtive Notificator: Project Deletion On Server");
						}

						// check whether sum of latest project is bigger
						if (latestProjectCount > fpm.getProjectCount()) {

							collabtiveNotificator.getLatestProjectDetailJSO(userID + "", "project");
							String projectName = collabtiveNotificator.getLatestProjectName();

							collabtiveNotificator.generateNotification("1 project added", "1 project assigned",
									projectName + " has been assigned to you", R.drawable.project, 1);
							collabtiveNotificator.createSounds(R.raw.sfxa);
							collabtiveNotificator.createVibra();

							fpm.setProjectCount(latestProjectCount);
							System.out.println("Collabtive Notificator: Someone adding a project on Server");
						}

						// check whether sum of latest task is bigger
						if (latestTaskCount > fpm.getTaskCount()) {
							collabtiveNotificator.getLatestProjectDetailJSO(userID + "", "task");
							String taskName = collabtiveNotificator.getLatestTaskName();
							int taskPID = collabtiveNotificator.getLatestTaskPID();
							fpm.setTemporaryPassedPID(taskPID);
							collabtiveNotificator.generateNotification("1 task added", "1 task assigned", taskName
									+ " has been assigned to you", R.drawable.task_notif, 2);
							collabtiveNotificator.createSounds(R.raw.sfxb);
							collabtiveNotificator.createVibra();
							fpm.setTaskCount(latestTaskCount);
							fpm.setNewTaskState(true);
							System.out.println("Collabtive Notificator: Someone adding a task on Server");
						}

						// check whether user change it's notification
						ntfEnabled = fpm.getEnabledNotificationState();
						Thread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
		projectNtfThread.start();
	}

	private void createEmailNotification() {
		this.emailNtfThread = new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (ntfEnabled == true) {
					try {
						if (isOnline() ==  true) {
							System.out.println("GMAIL READER: Kondisi Online");
							boolean found = false;
							int total = 0;
							javax.mail.Message unreadMessage[];
							GMailReader gmailReader = new GMailReader(fpm.getUserEmailAddress(),
									fpm.getUserEmailPassword());
							unreadMessage = gmailReader.getUnReadMessage();
							if (unreadMessage.length > 0) {
								for (int i = 0; i < unreadMessage.length; i++) {
									String content = gmailReader.getMessageContent(unreadMessage[i]);
									if (content.contains(CollabtiveProfile.KUMA_TAG_FOOTER_TAG)) {
										found = true;
										total++;
									}
								}
								if (found == true) {
									collabtiveNotificator.generateNotification("You've got " + total + " new message",
											"New Message", "Click to see upcoming message", R.drawable.email_notif, 3);
									collabtiveNotificator.createSounds(R.raw.sfxa);
									collabtiveNotificator.createVibra();
									System.out.println("Gmail Reader: Notifikasi Dibuat");
									found = false;
								}
								gmailReader.saveMessagesToFileText();
								System.out.println("Gmail Reader Save file text");
							}
						}else
							System.out.println("GMAIL READER: Suddenly Off Line Mode");
						emailNtfThread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		emailNtfThread.start();
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
