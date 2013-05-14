package com.farissyariati.kuma.utility;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class EmailService extends Service {
	private FPreferencesManager fpm;
	private int sentEmail = 0;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startID) {
		// Toast.makeText(getBaseContext(), "Sending Email..",
		// Toast.LENGTH_LONG).show();
		
		final String message = intent.getExtras().getString(CollabtiveProfile.KUMA_TAG_CONTENT);
		final String subject = intent.getExtras().getString(CollabtiveProfile.KUMA_TAG_SUBJECT);
		final String to = intent.getExtras().getString(CollabtiveProfile.KUMA_TAG_RECEPIENT);
		this.fpm = new FPreferencesManager(getBaseContext());
		
		Thread senderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				GMailSender sender = new GMailSender(fpm.getUserEmailAddress(), fpm.getUserEmailPassword());
				try {
					sentEmail = sender.sendMail(subject, message, fpm.getUserEmailAddress(), to);
					fpm.setSentState(sentEmail);
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), "System Cannot Send Your Email", Toast.LENGTH_LONG).show();
				}
			}
		});
		senderThread.start();
	}
}
