package com.farissyariati.kuma.messaging;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.select.users.SelectUsers;
import com.farissyariati.kuma.select.users.SelectUsersParser;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.EmailService;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;
import com.farissyariati.kuma.utility.SMSService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class AddMessageActivity extends Activity {
	private AutoCompleteTextView actMessageSubject;
	private EditText etSubject, etMessageContent;
	private int messagingType;
	// this is for email & sms
	private List<SelectUsers> usersList;
	private String[] autoCompleteText;
	private JSONArray usersJSONArray;

	private FPreferencesManager fpm;
	private ProgressDialog pdMessagingService;
	private int sentEmail = 0;
	private String recepient;
	static final String TWO_ENTER = "\n\n";
	boolean serviceStopped = false;
	
	private int replyState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_add_layout);
		this.messagingType = getIntent().getIntExtra(CollabtiveProfile.KUMA_TAG_MESSAGING_TYPE, 1);
		this.fpm = new FPreferencesManager(this);
		pdMessagingService = new ProgressDialog(this);
		initParseSelectUsersData();
		initAutoComplete();
		initEditText();
		
		this.replyState = getIntent().getIntExtra(CollabtiveProfile.KUMA_TAG_REPLY_STATE, 0);
		if(replyState == 1){
			String replyEmail = getIntent().getStringExtra(CollabtiveProfile.KUMA_TAG_REPLY_EMAIL);
			String replySubject = getIntent().getStringExtra(CollabtiveProfile.KUMA_TAG_REPLY_SUBJECT);
			String replyContent = getIntent().getStringExtra(CollabtiveProfile.KUMA_TAG_REPLY_CONTENT);
			
			
			String replyEmailString = matchContact(replyEmail);
			String replySubjectString = "Re: "+replySubject;
			String replyContentString = "-------------------------------\n"+replyContent;
			
			actMessageSubject.setText(replyEmailString);
			etSubject.setText(replySubjectString);
			etMessageContent.setText(replyContentString);
		}
	}

	private void initAutoComplete() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
				autoCompleteText);
		this.actMessageSubject = (AutoCompleteTextView) findViewById(R.id.act_message_contact);
		actMessageSubject.setThreshold(1);
		actMessageSubject.setAdapter(adapter);
	}

	private void initEditText() {
		this.etSubject = (EditText) findViewById(R.id.et_message_subject);
		this.etMessageContent = (EditText) findViewById(R.id.et_message_content);
	}

	private void initParseSelectUsersData() {
		SelectUsersParser parser = new SelectUsersParser(this);
		try {
			usersJSONArray = new JSONArray(new FFileManager().getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_USERS));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		parser.parse(usersJSONArray);
		this.usersList = parser.getList();
		this.autoCompleteText = new String[usersList.size()];
		for (int i = 0; i < usersList.size(); i++) {
			if (messagingType == 1)
				this.autoCompleteText[i] = usersList.get(i).username + " - " + usersList.get(i).userEmail;
			else
				this.autoCompleteText[i] = usersList.get(i).username + " - " + usersList.get(i).phoneNumber;
		}
	}

	private void clearInput() {
		etSubject.setText("");
		etMessageContent.setText("");
		actMessageSubject.setText("");
	}

	private void startEmailService() {
		String splitUserInfo[] = actMessageSubject.getText().toString().split(" - ");
		this.recepient = splitUserInfo[1];
		Intent emailService = new Intent(this, EmailService.class);
		emailService.putExtra(CollabtiveProfile.KUMA_TAG_SUBJECT, etSubject.getText().toString());
		emailService.putExtra(CollabtiveProfile.KUMA_TAG_CONTENT, etMessageContent.getText().toString() + TWO_ENTER
				+ CollabtiveProfile.KUMA_TAG_FOOTER_TAG);
		emailService.putExtra(CollabtiveProfile.KUMA_TAG_RECEPIENT, recepient);
		startService(emailService);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.task_add_menu, menu);
		MenuItem miDone = menu.findItem(R.id.done_add_menu);
		miDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (messagingType == 1) {
					fpm.setSentState(0);
					initEmailSentStateListener();
					startEmailService();
				} else
					sendSMS();
				return false;
			}
		});

		MenuItem miClear = menu.findItem(R.id.clear_add_menu);
		miClear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				clearInput();
				return false;
			}
		});
		return true;
	}

	private void initEmailSentStateListener() {
		pdMessagingService.setCancelable(true);
		pdMessagingService.setMessage("Sending Email..");
		pdMessagingService.show();
		Thread listenState = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					sentEmail = fpm.getSentState();
					if (sentEmail == 1) {
						emailHandler.sendMessage(emailHandler.obtainMessage());
						break;
					}
				}
			}
		});
		listenState.start();
	}

	Handler emailHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (fpm.getSentState() == 1) {
				if (sentEmail == 1) {
					fpm.setSentState(0);
					pdMessagingService.dismiss();
					Toast.makeText(getBaseContext(), "Email sent to: " + recepient, Toast.LENGTH_LONG).show();
					finish();
					stopService(new Intent(getApplicationContext(), EmailService.class));
				}

			}
		}
	};

	private void sendSMS() {
		String splitUserInfo[] = actMessageSubject.getText().toString().split(" - ");
		this.recepient = splitUserInfo[1];
		String messageContent = "Subject: " + etSubject.getText().toString() + "\n"
				+ etMessageContent.getText().toString() + TWO_ENTER + CollabtiveProfile.KUMA_TAG_FOOTER_TAG;
		Intent smsService = new Intent(this, SMSService.class);
		smsService.putExtra(CollabtiveProfile.KUMA_TAG_SMS_CONTENT, messageContent);
		smsService.putExtra(CollabtiveProfile.KUMA_TAG_PHONE_NUMBER, recepient);
		startService(smsService);
	}
	
	private String matchContact(String email){
		String result = "";
		for(int i = 0; i < autoCompleteText.length; i++){
			if(autoCompleteText[i].contains(email)){
				result = autoCompleteText[i];
				break;
			}
		}
		return result;
	}
}
