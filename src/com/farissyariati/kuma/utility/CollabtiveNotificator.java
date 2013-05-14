package com.farissyariati.kuma.utility;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.farissyariati.kuma.NotificationReceiverActivity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;

public class CollabtiveNotificator implements CollabtiveProfile {
	private JSONParser jsonParser;
	private String COLLABTIVE_URL;

	private JSONObject countLatestDataJSO;
	private JSONObject latestProjectDetail;
	private JSONObject latestTaskDetail;

	private NotificationManager notificationManager;
	private FPreferencesManager fpm;
	Context context;

	static final String TAG_UPDATER = "updater";
	
	public CollabtiveNotificator(Context context){
		this.context = context;
	}
	
	public CollabtiveNotificator(String collabtiveURL, Context context) {
		this.jsonParser = new JSONParser();
		this.COLLABTIVE_URL = collabtiveURL;
		this.context = context;
		this.fpm = new FPreferencesManager(context);
	}

	public void getCountLatestDataJSO(String userID) {
		String NOTIFY_COUNT_URL_POSTED = COLLABTIVE_URL + NOTIFY_COUNT_URL;
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(COLL_NOTIFY_UID, userID));
		try {
			countLatestDataJSO = jsonParser.postJSONFromUrl(nameValuePair, NOTIFY_COUNT_URL_POSTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNewProjectCount() {
		int result;
		try {
			result = countLatestDataJSO.getInt(COLL_NOTIFY_PROJECT_COUNT_NEW);
		} catch (Exception e) {
			result = 0;
			e.printStackTrace();
		}
		return result;
	}

	public int getNewTaskCount() {
		int result;
		try {
			result = countLatestDataJSO.getInt(COLL_NOTIFY_TASK_COUNT_NEW);
		} catch (Exception e) {
			result = 0;
			e.printStackTrace();
		}
		return result;
	}

	/* NOTIFICATOR GET LATEST PROJECT DETAIL */

	public void getLatestProjectDetailJSO(String userID, String info) {
		String NOTIFY_GET_URL = COLLABTIVE_URL + NOTIFY_GET_DATA;
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_NOTIFY_UID, userID));
		nameValuePair.add(new BasicNameValuePair(COLL_NOTIFT_INFO, info));

		if (info.equals("project")) {
			latestProjectDetail = jsonParser.postJSONFromUrl(nameValuePair, NOTIFY_GET_URL);
		}

		if (info.equals("task")) {
			latestTaskDetail = jsonParser.postJSONFromUrl(nameValuePair, NOTIFY_GET_URL);
		}
		

	}

	public int getLatestProjectID() {
		int result = 0;
		try {
			result = latestProjectDetail.getInt(COLL_NOTIFY_PID);
			System.out.println("Notification System: Latest PID " + result);
		} catch (Exception e) {
			System.out.println("Notification System: " + e.toString());
		}
		return result;
	}

	public String getLatestProjectName() {
		String result = "none";
		try {
			result = latestProjectDetail.getString(COLL_NOTIFY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getLatestTaskID() {
		int result = 0;
		try {
			result = latestTaskDetail.getInt(COLL_NOTIFY_TID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getLatestTaskName() {
		String result = "none";
		try {
			result = latestTaskDetail.getString(COLL_NOTIFY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getLatestTaskPID(){
		int result = 0;
		try{
			result = latestTaskDetail.getInt(COLL_NOTIFY_PID);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/* NOTIFICATION */
	@SuppressWarnings("deprecation")
	public void generateNotification(CharSequence ticker, CharSequence title, CharSequence message, int icon, int state) {
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		CharSequence notificationTicker = ticker;
		CharSequence notificationTitle = title;
		CharSequence notificationMessage = message;
		long now = System.currentTimeMillis();

		Notification notification = new Notification(icon, notificationTicker, now);
		fpm.setNotifcatorUpdaterState(state);
		Intent intent = new Intent(context, NotificationReceiverActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

		notification.setLatestEventInfo(context, notificationTitle, notificationMessage, contentIntent);
		notificationManager.notify(1, notification);

	}

	public void createVibra() {
		Vibrator vibra = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		int dot = 200; // Length of Gap Between Words
		long[] pattern = { 0, // Start immediately
				dot };
		vibra.vibrate(pattern, -1);
	}

	public void createSounds(int sound) {
		MediaPlayer player = MediaPlayer.create(context, sound);
		player.start();
	}
	
	public void showAlert(String title, String message, String buttonMessage) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton(buttonMessage, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// do Nothing
					}
				}).show();
	}

}
