package com.farissyariati.kuma.utility;



import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.*;
import android.widget.Toast;

public class SMSService extends Service {
	static final String TAG_MESSAGE_CONTENT = "message_content";
	static final String TAG_PHONE_NUMBER = "phone_number";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "Sending a Message..", Toast.LENGTH_SHORT).show();
		String message = intent.getExtras().getString(CollabtiveProfile.KUMA_TAG_SMS_CONTENT);
		String phoneNumber = intent.getExtras().getString(CollabtiveProfile.KUMA_TAG_PHONE_NUMBER);
		sendSMS(phoneNumber, message);
	}

	@Override
	public void onDestroy() {

	}

	private void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		// ketka sms dikirimkan

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "Report Sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic Failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No Service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio Off",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

			}
		}, new IntentFilter(SENT));

		// saat SMS diterima
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					//put your jetpack
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "Report Not Sent",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}
}
