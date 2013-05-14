package com.farissyariati.kuma.filelist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FileListActivity extends Activity {
	private JSONArray filesArray;
	private ListView lv;
	private List<Files> list;

	private ProgressDialog pdDownload;
	private ProgressDialog pdCreateFolder;
	private String rootDir;
	private String fileURL;
	private String fileName;
	private String downloadPath;
	private int passProjectID;
	
	private String subFoldesRaw;

	private Thread refreshThread;
	private ProgressDialog pdRefresh;
	
	private RelativeLayout rlCreateSubfolder;
	private EditText etNewFolder;
	private Button btCreateSubfolder;

	private static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private int startState;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initVars();
		if(startState == 1)
			checkRefreshState();
		else{
			checkDownloadPath(CollabtiveProfile.KUMA_FILE_DOWNLOAD_PATH);
			initParseFilesData();
			setListView();
		}
		
	}

	private void checkRefreshState() {
		if (startState == 1) {
			System.out.println("File List Activity: startState =" + startState);
			this.pdRefresh = new ProgressDialog(this);
			pdRefresh.setCancelable(false);
			pdRefresh.setMessage("Refresh file data..");
			pdRefresh.show();

			this.refreshThread = new Thread(new Runnable() {
				@Override
				public void run() {
					CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
					try {
						collManager.getFilesJSONObjects(passProjectID + "");
						if (collManager.getFilesStatusCode() == 1) {
							FFileManager ffm = new FFileManager();
							ffm.writeToFile(
									CollabtiveProfile.KUMA_FILE_JSON_FILES,
									collManager.getFilesJSONArray().toString());
						}
						refreshHandler.sendMessage(refreshHandler
								.obtainMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			refreshThread.start();
		}
	}

	Handler refreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdRefresh.dismiss();
			checkDownloadPath(CollabtiveProfile.KUMA_FILE_DOWNLOAD_PATH);
			initParseFilesData();
			setListView();
			FPreferencesManager manager = new FPreferencesManager(getBaseContext());
			manager.setFileListActivityStartState(0);
		}
	};

	private void initVars() {
		this.subFoldesRaw = getIntent().getExtras().getString(CollabtiveProfile.COLL_TAG_FILES_SUBFOLDERS);
		this.startState = new FPreferencesManager(this)
		.getFileListActivityStartState();
		this.rootDir = Environment.getExternalStorageDirectory().toString();
		this.passProjectID = new FPreferencesManager(this)
				.getTemporaryPassedPID();
	}

	private void checkDownloadPath(String path) {
		File downDir = new File(rootDir + path);
		this.downloadPath = downDir.toString() + "/";
		if (!downDir.exists()) {
			downDir.mkdirs();
		}

	}

	private void initParseFilesData() {
		FilesParser parser = new FilesParser(this);
		try {
			filesArray = new JSONArray(
					new FFileManager()
							.getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_FILES));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		parser.parse(filesArray);
		this.list = parser.getList();
	}
	
	private void initCreateSubfolderComponents(){
		this.rlCreateSubfolder = (RelativeLayout)findViewById(R.id.rl_create_subfolder);
		this.etNewFolder = (EditText)findViewById(R.id.et_subfolder);
		etNewFolder.setText("New Folder Name");
		this.btCreateSubfolder = (Button)findViewById(R.id.bt_create_subfolder);
		rlCreateSubfolder.setVisibility(View.GONE);
		
		btCreateSubfolder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createSubFolder();
				//Toast.makeText(getBaseContext(), "Halo", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void createSubFolder(){
		pdCreateFolder = new ProgressDialog(this);
		pdCreateFolder.setCancelable(false);
		pdCreateFolder.setMessage("Creating Subfolder");
		pdCreateFolder.show();
		Thread createFolder = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Create Subfolder: Masuk Thread");
				FFileManager createDir = new FFileManager(getBaseContext());
				if(createDir.initSubfolderDownloadDir(passProjectID+"", etNewFolder.getText().toString()) == 1){
					subFoldesRaw = new FFileManager(getBaseContext()).getProjectsDirSubfolders(passProjectID+"");
					createFolderHandler.sendMessage(Message.obtain(createFolderHandler, 1));
				}
					//System.out.println("Create Subfolder: Sukses");
				else
					//System.out.println("Create Subfolder: Gagal");
					createFolderHandler.sendMessage(Message.obtain(createFolderHandler, 0));
			}
		});
		createFolder.start();
	}
	
	Handler createFolderHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 0:
				Toast.makeText(getBaseContext(), "Failed Creating a Folder", Toast.LENGTH_LONG).show();
				pdCreateFolder.dismiss();
				break;
			case 1:
				Toast.makeText(getBaseContext(), "Folder Created Successfully", Toast.LENGTH_LONG).show();
				pdCreateFolder.dismiss();
				rlCreateSubfolder.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};

	private void setListView() {
		setContentView(R.layout.file_list);
		initCreateSubfolderComponents();
		FilesArrayAdapter filesArrayAdapter = new FilesArrayAdapter(
				getApplicationContext(), R.layout.file_list_row, list);
		this.lv = (ListView) findViewById(R.id.filesListView);
		lv.setAdapter(filesArrayAdapter);
		setOnItemClickListener();
	}

	private void startDownloadFile(String url) {
		new DownloadFile().execute(url);
	}

	private void goToUploadFile() {
		Intent goToUploadFile = new Intent(this, UploadFileActivity.class);
		goToUploadFile.putExtra(CollabtiveProfile.COLL_TAG_FILES_SUBFOLDERS, subFoldesRaw);
		startActivity(goToUploadFile);
		FileListActivity.this.finish();
	}

	private void setOnItemClickListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				fileURL = CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME + "/"
//						+ list.get((int) id).fileURL;
				FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
				fileURL = fpm.getCollabtiveWebsite() +  "/"
						+ list.get((int) id).fileURL;
				String urlSplit[] = fileURL.split("/");
				int length = urlSplit.length;
				fileName = urlSplit[length - 1];
				createMenuOnLongItemClicked(list.get((int) id).id);
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {

				return false;
			}
		});
	}

	private void createMenuOnLongItemClicked(final int fileID) {
		final CharSequence charSequence[] = { "File Detail",
				"Download this file" };
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("File Menu");
		adb.setItems(charSequence, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					break;
				case 1:
					startDownloadFile(fileURL);
					break;
				}
			}
		});
		adb.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_list_menu, menu);
		MenuItem miLogout = menu.findItem(R.id.menu_add_file);
		miLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				goToUploadFile();
				return false;
			}
		});
		
		MenuItem miAddFolder = menu.findItem(R.id.menu_add_folder);
		miAddFolder.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				rlCreateSubfolder.setVisibility(View.VISIBLE);
				return false;
			}
		});
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			pdDownload = new ProgressDialog(this);
			pdDownload.setMessage("Downloading file..");
			pdDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pdDownload.setCancelable(false);
			pdDownload.show();
			return pdDownload;
		default:
			return null;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			FPreferencesManager manager = new FPreferencesManager(getBaseContext());
			manager.setFileListActivityStartState(0);
			System.out.println("File List Activity: start State "+manager.getFileListActivityStartState());
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class DownloadFile extends AsyncTask<String, String, String> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;

			try {
				URL url = new URL(aurl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();

				int fileLength = connection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(downloadPath
						+ fileName);
				byte data[] = new byte[1024];
				long total = 0;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
			}
			return null;

		}

		protected void onProgressUpdate(String... progress) {
			pdDownload.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			Toast.makeText(getBaseContext(),
					fileName + " has been saved in " + downloadPath,
					Toast.LENGTH_LONG).show();
		}
	}

}
