package com.farissyariati.kuma.filelist;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import com.farissyariati.kuma.R;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FPreferencesManager;
import com.farissyariati.kuma.utility.FUploader;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class UploadFileActivity extends Activity implements AdapterView.OnItemSelectedListener {
	private static final int REQUEST_PICK_FILE = 1;
	static final String UTF_ENCODE = "UTF-8";

	private TextView mFilePathTextView;
	private Button mStartActivityButton;
	TextView progressTextView;
	private EditText edFileName, edFileDesc;
	private Spinner spinnerSubFolders;

	private String filePath;
	private int passProjectID;
	private String uploadedPath;
	private String subFoldersRaw;
	private ArrayAdapter<String> spinnerAdapter;
	private String[] subFoldersPath;
	private String uploadReceiverURL;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initStringAdapter();
		initComponents();
		initVar();
	}

	private void initComponents() {
		edFileName = (EditText) findViewById(R.id.ed_upload_file_title);
		edFileDesc = (EditText) findViewById(R.id.ed_upload_file_desc);
		mFilePathTextView = (TextView) findViewById(R.id.file_path_text_view);
		progressTextView = (TextView) findViewById(R.id.tv_progree_upload);
		mStartActivityButton = (Button) findViewById(R.id.start_file_picker_button);
		mStartActivityButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), FilePickerActivity.class);
				startActivityForResult(intent, REQUEST_PICK_FILE);
			}
		});
		spinnerSubFolders = (Spinner) findViewById(R.id.spinner_target_path);
		spinnerSubFolders.setOnItemSelectedListener(this);
		this.spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subFoldersPath);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSubFolders.setAdapter(spinnerAdapter);
	}

	private void initStringAdapter() {
		this.passProjectID = new FPreferencesManager(this).getTemporaryPassedPID();
		String separator = "/standard";
		this.subFoldersRaw = getIntent().getExtras().getString(CollabtiveProfile.COLL_TAG_FILES_SUBFOLDERS);
		subFoldersPath = subFoldersRaw.split("\n");
		for (int i = 1; i < subFoldersPath.length; i++) {
			subFoldersPath[i] = subFoldersPath[i].split(separator)[1];
		}

		separator = "/" + passProjectID;
		System.out.println("SubFolder Separator now: " + separator);
		for (int i = 2; i < subFoldersPath.length; i++) {
			subFoldersPath[i] = subFoldersPath[i].split("/" + passProjectID)[1];
			System.out.println("SubFolder Split " + subFoldersPath[i]);
		}
		subFoldersPath[1] = "/";
		subFoldersPath[0] = "Choose Upload File Target";
	}

	private void initVar() {
		try {
			FPreferencesManager fpm = new FPreferencesManager(this);
			this.uploadReceiverURL = fpm.getCollabtiveWebsite() + CollabtiveProfile.KUMA_FILE_RECEIVER_URL;
			fpm.setFileListActivityStartState(0);
			int pid = new FPreferencesManager(this).getTemporaryPassedPID();
			this.uploadedPath = "../../../files/standard/" + passProjectID + "/";
			uploadedPath = URLEncoder.encode(uploadedPath, UTF_ENCODE);
			String project = URLEncoder.encode(pid + "", UTF_ENCODE);
			String userID = URLEncoder.encode(new FPreferencesManager(this).getUserID() + "", UTF_ENCODE);

			this.uploadReceiverURL += "?uploaded_path=" + uploadedPath;
			uploadReceiverURL += "&pid=" + project;
			uploadReceiverURL += "&uid=" + userID;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void changePath(String pathRaw) {
		try {
			FPreferencesManager fpm = new FPreferencesManager(this);
			this.uploadReceiverURL = fpm.getCollabtiveWebsite() + CollabtiveProfile.KUMA_FILE_RECEIVER_URL;
			fpm.setFileListActivityStartState(0);
			int pid = new FPreferencesManager(this).getTemporaryPassedPID();
			this.uploadedPath = "../../../files/standard/" + passProjectID + "/"+pathRaw+"/";
			uploadedPath = URLEncoder.encode(uploadedPath, UTF_ENCODE);
			String project = URLEncoder.encode(pid + "", UTF_ENCODE);
			String userID = URLEncoder.encode(new FPreferencesManager(this).getUserID() + "", UTF_ENCODE);

			this.uploadReceiverURL += "?uploaded_path=" + uploadedPath;
			uploadReceiverURL += "&pid=" + project;
			uploadReceiverURL += "&uid=" + userID;
			System.out.println("Upload Receiver URL: "+uploadReceiverURL);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_PICK_FILE:
				if (data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					File f = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
					mFilePathTextView.setText(f.getPath());
					this.filePath = f.getPath();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.upload_menu, menu);
		MenuItem miUploadFile = menu.findItem(R.id.menu_done);
		miUploadFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				try {
					String title = edFileName.getText().toString();
					String description = edFileDesc.getText().toString();
					title = URLEncoder.encode(title, UTF_ENCODE);
					description = URLEncoder.encode(description, UTF_ENCODE);
					uploadReceiverURL += "&title=" + title;
					uploadReceiverURL += "&desc=" + description;
					FUploader uploader = new FUploader(UploadFileActivity.this, getBaseContext());
					uploader.setUrlAndFile(uploadReceiverURL, filePath, progressTextView, subFoldersRaw);
					uploader.execute();
				} catch (Exception e) {
					mFilePathTextView.setText(e.toString());
				}
				return false;
			}
		});
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			Intent returnFilelist = new Intent(this, FileListActivity.class);
			returnFilelist.putExtra(CollabtiveProfile.COLL_TAG_FILES_SUBFOLDERS, subFoldersRaw);
			startActivity(returnFilelist);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (position > 1) {
			//Toast.makeText(getBaseContext(), "|" + subFoldersPath[position] + "|", Toast.LENGTH_LONG).show();
			changePath(subFoldersPath[position]);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
