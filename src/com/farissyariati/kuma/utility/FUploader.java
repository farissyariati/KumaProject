package com.farissyariati.kuma.utility;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.farissyariati.kuma.filelist.FileListActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

public class FUploader extends AsyncTask<Object, String, Object> {
	URL connectURL;
	String params;
	String responseString;
	String fileName;
	byte[] dataToServer;
	FileInputStream fileInputStream;
	TextView info;
	private Activity parentActivity;
	private Context context;
	private String subFolders;

	public FUploader(Activity parentActivity, Context context) {
		this.parentActivity = parentActivity;
		this.context = context;
	}

	public void setUrlAndFile(String urlString, String fileName, TextView info, String subFoldersRaw) {
		this.info = info;
		this.subFolders = subFoldersRaw;
		try {
			fileInputStream = new FileInputStream(fileName);
			connectURL = new URL(urlString);
		} catch (Exception e) {
			publishProgress(e.toString());
		}
		this.fileName = fileName;
	}

	synchronized void doUpload() {
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try {
			publishProgress("Uploading, please wait...");
			HttpURLConnection conn = (HttpURLConnection) connectURL
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
					+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			fileInputStream.close();
			dos.flush();

			InputStream is = conn.getInputStream();
			int ch;

			StringBuffer buff = new StringBuffer();
			while ((ch = is.read()) != -1) {
				buff.append((char) ch);
			}
			publishProgress(buff.toString());
			dos.close();
		} catch (Exception e) {
			publishProgress(e.toString());
		}
	}

	@Override
	protected Object doInBackground(Object... arg0) {
		doUpload();
		return null;
	}

	protected void onProgressUpdate(String... progress) {
		this.info.setText(progress[0]);
		if (progress[0].contains("successfully")) {
			FPreferencesManager fpm = new FPreferencesManager(context);
			fpm.setFileListActivityStartState(1);
			Intent refreshFileList = new Intent(context, FileListActivity.class);
			refreshFileList.putExtra(CollabtiveProfile.COLL_TAG_FILES_SUBFOLDERS, subFolders);
			parentActivity.startActivity(refreshFileList);
			parentActivity.finish();
		}
	}

}
