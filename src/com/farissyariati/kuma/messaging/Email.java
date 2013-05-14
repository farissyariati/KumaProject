package com.farissyariati.kuma.messaging;

import org.json.JSONObject;

public class Email {
	public String sender, sendDate, content, subject;

	public Email(String sender, String sendDate, String content, String subject) {
		this.sender = sender;
		this.sendDate = sendDate;
		this.content = content;
		this.subject = subject;
	}

	public JSONObject getJSONObject() {
		JSONObject object = new JSONObject();
		try {
			object.put("emailSender", sender);
			object.put("emailSendDate", sendDate);
			object.put("emailContent", content);
			object.put("emailSubject", subject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	public Email() {

	}

}
