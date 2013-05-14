package com.farissyariati.kuma.utility;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.PasswordAuthentication;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class GMailSender extends javax.mail.Authenticator {
	private static final String mailhost = "smtp.gmail.com";
	private String user;
	private String password;
	private Session session;

	static {
		Security.addProvider(new MyJSSEProvider());
	}

	public GMailSender(String username, String password) {
		this.user = username;
		this.password = password;
		setProperties();

	}

	private void setProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.host", mailhost);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
		properties.setProperty("mail.smtp.quitwait", "false");
		//session = Session.getDefaultInstance(properties, this);
		session = Session.getInstance(properties, this);
	}
	
	@Override
    protected PasswordAuthentication getPasswordAuthentication(){
    	return new PasswordAuthentication(user, password);
    }
    

	public synchronized int sendMail(String subject, String body,
			String sender, String recepients) {
		int sent = 0;
		try{
			MimeMessage message = new MimeMessage(session);
			DataHandler dHandler = new DataHandler(new ByteArrayDataSource(
					body.getBytes(), "text/plain"));
			message.setSender(new InternetAddress(sender));
			message.setSubject(subject);
			message.setDataHandler(dHandler);

			if (recepients.indexOf(',') > 0)
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recepients));
			else
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(
						recepients));
			Transport.send(message);
			sent = 1;
		}
		catch(Exception e){
			System.out.println("Collabtive Email Ervice: Error at "+"\n"+e.toString());
		}
		return sent;
	}
  
	public class ByteArrayDataSource implements DataSource {

		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String getContentType() {
			if (type == null) {
				return "application/octet-stream";
			} else
				return type;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			// TODO Auto-generated method stub
			return new ByteArrayInputStream(data);
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "ByteArrayDataSource";
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			// TODO Auto-generated method stub
			throw new IOException("Not Supported");
		}

	}

}
