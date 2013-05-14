package com.farissyariati.kuma.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

import org.json.JSONArray;

import com.farissyariati.kuma.messaging.Email;

public class GMailReader {
	private Message unreadMessages[];
	private Folder inbox;

	private String emailAddress;
	private String password;

	// private List<Email> emailList;
	private JSONArray emailJSONArray;
	static final String HOST = "imap.gmail.com";
	static final String TAG_ERROR = "Error while getting the message/messages";

	public GMailReader(String emailAddress, String password) {
		this.emailAddress = emailAddress;
		this.password = password;
		this.emailJSONArray = new JSONArray();
		// this.emailList = new ArrayList<Email>();
		initUnreadMessages();
	}

	private void initUnreadMessages() {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect(HOST, emailAddress, password);
			this.inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			this.unreadMessages = inbox.search(ft);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message[] getUnReadMessage() {
		return this.unreadMessages;
	}

	public int getUnreadMessageCount() {
		return unreadMessages.length;
	}

	public String getMessageContent(Message message) {
		String messageContent = "";
		try {
			Object messageObject = message.getContent();
			if (messageObject instanceof Multipart) {
				messageContent = "Cannot Read Message";
			} else {
				InputStream is = message.getInputStream();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = "";
				while ((line = input.readLine()) != null) {
					sb.append(line + "\n");
				}
				messageContent = sb.toString();
			}
		} catch (Exception e) {
			messageContent = TAG_ERROR;
			e.printStackTrace();
		}
		return messageContent;
	}

	@SuppressWarnings("unused")
	public void saveMessagesToFileText() {
		System.out.println("Gmail Reader Entering savedMessages");
		FFileManager fileManager = new FFileManager();
		StringBuilder sb = new StringBuilder();
		try {
			if (unreadMessages.length > 0) {
				for (int i = 0; i < unreadMessages.length; i++) {
					Message message = unreadMessages[i];
					String content = getMessageContent(unreadMessages[i]);
					String subject = message.getSubject();
					@SuppressWarnings("deprecation")
					String sentdate = message.getSentDate().toGMTString();
					String sender = InternetAddress.toString(message.getFrom());
					String line = sender + "#" + sentdate + "#" + subject + "#" + getMessageContent(message);
					if (content.contains(CollabtiveProfile.KUMA_TAG_FOOTER_TAG)) {
						sb.append(line + ";");
						// try
						Email email = new Email(sender, sentdate, content, subject);
						this.emailJSONArray.put(email.getJSONObject());
					}
				}

				// simpan message
				if (!sb.toString().equals("")) {
					JSONArray savedEmailJSONArray = new JSONArray();
					JSONArray newJSONArray = new JSONArray();
					// temporary saved file
					try {
						savedEmailJSONArray = new JSONArray(
								fileManager.getJSONContent(CollabtiveProfile.KUMA_TAG_EMAIL_JSON_ARRAY));
					} catch (Exception e) {
						fileManager.writeToFile(CollabtiveProfile.KUMA_TAG_EMAIL_JSON_ARRAY, emailJSONArray.toString());
						e.printStackTrace();
					}
					if (savedEmailJSONArray != null) {
						JSONParser parser = new JSONParser();
						newJSONArray = parser.combineJSONArray(savedEmailJSONArray, emailJSONArray);
						fileManager.writeToFile(CollabtiveProfile.KUMA_TAG_EMAIL_JSON_ARRAY, newJSONArray.toString());
					} else {
						fileManager.writeToFile(CollabtiveProfile.KUMA_TAG_EMAIL_JSON_ARRAY, emailJSONArray.toString());
					}
					System.out.println("EMAIL JSON CONTENT: "+fileManager.getJSONContent(CollabtiveProfile.KUMA_TAG_EMAIL_JSON_ARRAY));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
