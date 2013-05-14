package com.farissyariati.kuma.messaging;

import org.json.JSONArray;
import org.json.JSONObject;
import com.farissyariati.kuma.R;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmailFetcherActivity extends Activity {
	private TextView tvEmailContent;
	private FFileManager fileManager;
	private RelativeLayout rlReplyEmail;
	private AutoCompleteTextView actEmailAddress;
	private Button btPassReply;

	private String autoComplete[];
	private JSONArray emailList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_read_layout);
		initComponent();
		this.fileManager = new FFileManager();
		setEmailContent();
		initAutoComplete();
	}

	private void initComponent() {
		this.tvEmailContent = (TextView) findViewById(R.id.tv_read_email_content);
		this.rlReplyEmail = (RelativeLayout) findViewById(R.id.rl_reply_email);
		rlReplyEmail.setVisibility(View.GONE);
		this.btPassReply = (Button) findViewById(R.id.bt_reply_email);
		btPassReply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!actEmailAddress.getText().toString().equals("")) {
					Intent startReply = new Intent(getBaseContext(), AddMessageActivity.class);
					String replySubjectEmail[] = actEmailAddress.getText().toString().split(" - ");
					String passSubject = replySubjectEmail[0];
					String passEmail = replySubjectEmail[1];
					String passContent = searchEmailContent(passSubject);
					startReply.putExtra(CollabtiveProfile.KUMA_TAG_REPLY_SUBJECT, passSubject);
					startReply.putExtra(CollabtiveProfile.KUMA_TAG_REPLY_EMAIL, passEmail);
					startReply.putExtra(CollabtiveProfile.KUMA_TAG_REPLY_CONTENT, passContent);
					startReply.putExtra(CollabtiveProfile.KUMA_TAG_REPLY_STATE, 1);
					startActivity(startReply);
				}
			}
		});
	}

	private String searchEmailContent(String subjectKey) {
		String result = "";
		try {
			for (int i = 0; i < emailList.length(); i++) {
				JSONObject emailObject = emailList.getJSONObject(i);
				String content = emailObject.getString("emailContent");
				String subject = emailObject.getString("emailSubject");
				if (subject.contains(subjectKey)) {
					result = content;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void setEmailContent() {
		StringBuilder sb = new StringBuilder();
		try {
			this.emailList = new JSONArray(fileManager.getJSONContent(CollabtiveProfile.KUMA_TAG_EMAIL_JSON_ARRAY)
					.toString());
			for (int i = 0; i < emailList.length(); i++) {
				JSONObject emailObject = emailList.getJSONObject(i);
				String from = emailObject.getString("emailSender");
				String sendDate = emailObject.getString("emailSendDate");
				String content = emailObject.getString("emailContent");
				String subject = emailObject.getString("emailSubject");
				String format = "From: " + from + "\nEmail Date: " + sendDate + "\n\nSubject: " + subject
						+ "\n\nMessage:\n" + content;
				sb.append(format + "\n");
				sb.append("<-------------------------------------------------------------------------------->\n\n");
			}
			tvEmailContent.setText(sb.toString());

		} catch (Exception e) {
			tvEmailContent.setText("You have no mail..");
			e.printStackTrace();
		}
	}

	private void initAutoComplete() {
		try {
			this.actEmailAddress = (AutoCompleteTextView) findViewById(R.id.act_reply);
			actEmailAddress.setText("type email subject");
			this.autoComplete = new String[emailList.length()];
			for (int i = 0; i < emailList.length(); i++) {
				JSONObject emailObject = emailList.getJSONObject(i);
				autoComplete[i] = emailObject.getString("emailSubject") + " - " + emailObject.getString("emailSender");
			}
		} catch (Exception e) {
			this.autoComplete = new String[0];
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
				autoComplete);
		actEmailAddress.setThreshold(1);
		actEmailAddress.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Clear Email").setIcon(R.drawable.next);
		menu.add(0, 1, 0, "Reply");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			fileManager.writeToFile(CollabtiveProfile.KUMA_TAG_EMAIL_JSON_ARRAY, null);
			setEmailContent();
			break;
		case 1:
			rlReplyEmail.setVisibility(View.VISIBLE);
			initAutoComplete();
			break;
		default:
			break;
		}
		return true;
	}

}
