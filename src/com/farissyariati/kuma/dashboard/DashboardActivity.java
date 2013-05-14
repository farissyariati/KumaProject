package com.farissyariati.kuma.dashboard;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.filelist.FileListActivity;
import com.farissyariati.kuma.messaging.AddMessageActivity;
import com.farissyariati.kuma.messaging.EmailFetcherActivity;
import com.farissyariati.kuma.milestones.MilestonesListActivity;
import com.farissyariati.kuma.tasklist.TasklistListActivity;
import com.farissyariati.kuma.timetracker.TimeTrackerActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveNotificator;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;
import com.farissyariati.kuma.utility.FTimeUtility;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class DashboardActivity extends Activity implements OnClickListener {

	private View btnMilestones;
	private Button btnTasklist;
	private View btnFiles;
	private View btnMessage;
	private View btnProjectDesc;
	private View btnAbout;
	private ProgressDialog fProgressDialog;
	private CollabtiveNotificator notificator;
	private String sessionID;
	private int projectID;

	private Thread fThread;
	private Thread msgThread;
	private String resultString;

	private String passProjectName;
	private String passProjectDesc;
	private long passProjectEnd, passProjectStart;

	private FPreferencesManager fpm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_layout);
		this.fpm = new FPreferencesManager(this);
		initViewComponents();
		initVar();
	}

	private void initViewComponents() {
		this.btnMilestones = (View) findViewById(R.id.btn_milestone);
		this.btnMilestones.setOnClickListener(this);
		this.btnTasklist = (Button) findViewById(R.id.btn_tasklist);
		this.btnTasklist.setOnClickListener(this);
		// check whether it found new task
		if (fpm.getNewTaskState() == true) {
			final Drawable drawableTop = getResources().getDrawable(R.drawable.btn_tasklist_new);
			btnTasklist.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
			fpm.setNewTaskState(false);
		}
		this.btnFiles = (View) findViewById(R.id.btn_files);
		this.btnFiles.setOnClickListener(this);
		this.btnMessage = (View) findViewById(R.id.btn_messages);
		this.btnMessage.setOnClickListener(this);
		this.btnProjectDesc = (View) findViewById(R.id.btn_project_detail);
		this.btnProjectDesc.setOnClickListener(this);
		this.btnAbout = (View) findViewById(R.id.btn_exit);
		this.btnAbout.setOnClickListener(this);
	}

	private void initVar() {
		this.sessionID = new FPreferencesManager(this).getSessionID();
		this.projectID = new FPreferencesManager(this).getTemporaryPassedPID();
		this.notificator = new CollabtiveNotificator(this);

		this.passProjectName = getIntent().getStringExtra(CollabtiveProfile.KUMA_PASS_PROJECT_NAME);
		this.passProjectDesc = getIntent().getStringExtra(CollabtiveProfile.KUMA_PASS_PROJECT_DESC);
		this.passProjectEnd = getIntent().getLongExtra(CollabtiveProfile.KUMA_PASS_PROJECT_END, 0);
		this.passProjectStart = getIntent().getLongExtra(CollabtiveProfile.KUMA_PASS_PROJECT_START, 0);
	}

	private void getMilestoneData() {
		this.fProgressDialog = new ProgressDialog(this);
		fProgressDialog.setMessage("Getting Project's Milestones..");
		fProgressDialog.setCancelable(false);
		fProgressDialog.show();

		this.fThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getMilestonesJSONObjects(projectID + "", sessionID);
				if (collManager.getMilestonesStatusCode() == 1) {
					FFileManager ffm = new FFileManager();
					ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_MILESTONES, collManager.getMilestonesJSONArray()
							.toString());
					resultString = "Successfully getting milestones data";
					fHandler.sendMessage(fHandler.obtainMessage());
					goToMilesoneList();
				} else {
					resultString = "Failed getting milestones data";
					fHandler.sendMessage(fHandler.obtainMessage());
				}
			}
		});
		fThread.start();
	}

	private void getTasklistData() {
		this.fProgressDialog = new ProgressDialog(this);
		fProgressDialog.setMessage("Getting Project's Tasklist");
		fProgressDialog.setCancelable(false);
		fProgressDialog.show();

		this.fThread = new Thread(new Runnable() {

			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getMilestonesJSONObjects(projectID + "", sessionID);
				collManager.getTasklistsJSONObjects(projectID + "", sessionID);
				if (collManager.getTasklistStatusCode() == 1) {
					FFileManager ffm = new FFileManager();
					ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASKLIST, collManager.getTasklistsJSONArray()
							.toString());
					ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_MILESTONES, collManager.getMilestonesJSONArray()
							.toString());
					resultString = "Successfully getting tasklists data";
					fHandler.sendMessage(fHandler.obtainMessage());
					goToTasklist();
				} else {
					resultString = "Failed getting tasklists data";
					fHandler.sendMessage(fHandler.obtainMessage());
				}
			}
		});
		fThread.start();
	}

	private void getTaskData() {
		this.fProgressDialog = new ProgressDialog(this);
		fProgressDialog.setMessage("Initializing Project Progress..");
		fProgressDialog.setCancelable(true);
		fProgressDialog.show();
		this.fThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("All Task Masuk Ke Thread");
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getAllTaskJSONObjects(projectID + "", sessionID);
				if (collManager.getAllTasksStatusCode() == 1) {
					collManager.getTaskUserAssigned(projectID + "");
					collManager.getUsersJSONObject();
					FFileManager fileManager = new FFileManager();
					fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASK_ALL, collManager
							.getAllTasksJSONArray().toString());
					fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASK_ASSIGNED, collManager
							.getTaskUserAssignedJSONArray().toString());
					fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_USERS, collManager.getUsersJSONArray()
							.toString());
					resultString = "Projetct Detail Done";
					fHandler.sendMessage(fHandler.obtainMessage());
					goToTimeTrackerActivity();
				} else {
					resultString = "Failed Initializing Project Detail";
					fHandler.sendMessage(fHandler.obtainMessage());
				}

			}
		});
		fThread.start();

	}

	private void getFilelistData() {
		this.fProgressDialog = new ProgressDialog(this);
		fProgressDialog.setMessage("Getting File List..");
		fProgressDialog.setCancelable(false);
		fProgressDialog.show();

		this.fThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// CollabtiveManager collManager = new CollabtiveManager(
				// CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME);
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getFilesJSONObjects(projectID + "");
				System.out.println("Collabtive File: " + collManager.fileJSONObjects);
				if (collManager.getFilesStatusCode() == 1) {
					FFileManager ffm = new FFileManager(getBaseContext());
					ffm.initDownloadDir(projectID + "");
					String subFolders = ffm.getProjectsDirSubfolders(projectID+"");
					ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_FILES, collManager.getFilesJSONArray().toString());
					resultString = "Successfully getting file data";
					fHandler.sendMessage(fHandler.obtainMessage());
					goToFilelist(subFolders);
				} else {
					resultString = "Failed getting file data";
					fHandler.sendMessage(fHandler.obtainMessage());
				}
			}
		});
		fThread.start();
	}

	Handler fHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			fProgressDialog.dismiss();
			Toast.makeText(getBaseContext(), resultString, Toast.LENGTH_LONG).show();
		}
	};

	private void getUsersData(final int messagingType) {
		this.fProgressDialog = new ProgressDialog(this);
		fProgressDialog.setMessage("Waiting Users List");
		fProgressDialog.setCancelable(false);
		fProgressDialog.show();
		final CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
		this.fThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					FFileManager ffm = new FFileManager();
					collManager.getUsersJSONObject();
					ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_USERS, collManager.getUsersJSONArray().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				selectUsersHandler.sendMessage(selectUsersHandler.obtainMessage());
				goToMessaging(messagingType);
			}
		});
		fThread.start();
	}

	Handler selectUsersHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			fProgressDialog.dismiss();
			Toast.makeText(getBaseContext(), "Get User's Data", Toast.LENGTH_LONG).show();
		}
	};

	private void goToMilesoneList() {
		Intent milestoneList = new Intent(this, MilestonesListActivity.class);
		milestoneList.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_POSTED_ID, projectID);
		startActivity(milestoneList);
	}

	private void goToTasklist() {
		Intent tasklistList = new Intent(this, TasklistListActivity.class);
		tasklistList.putExtra(CollabtiveProfile.COLL_TAG_TASKLIST_POSTED_ID, projectID);
		startActivity(tasklistList);
	}

	private void goToFilelist(String subFolders) {
		Intent fileList = new Intent(this, FileListActivity.class);
		fileList.putExtra(CollabtiveProfile.COLL_TAG_FILES_SUBFOLDERS, subFolders);
		startActivity(fileList);
	}

	private void goToMessaging(int messagingType) {
		Intent messaging = new Intent(this, AddMessageActivity.class);
		messaging.putExtra(CollabtiveProfile.KUMA_TAG_MESSAGING_TYPE, messagingType);
		startActivity(messaging);
	}

	private void goToTimeTrackerActivity() {

		Intent timeTracker = new Intent(this, TimeTrackerActivity.class);
		timeTracker.putExtra(CollabtiveProfile.KUMA_PASS_PROJECT_NAME, passProjectName);
		timeTracker.putExtra(CollabtiveProfile.KUMA_PASS_PROJECT_END, passProjectEnd);
		timeTracker.putExtra(CollabtiveProfile.KUMA_PASS_PROJECT_DESC, passProjectDesc);
		startActivity(timeTracker);
	}

	private void createMessagingMenu() {
		final CharSequence charSequence[] = { "Send Email", "Send SMS", "Show Latest Email" };
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Messaging Menu");
		adb.setItems(charSequence, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					getUsersData(1);
					break;
				case 1:
					getUsersData(2);
					break;
				case 2:
					Intent showMail = new Intent(getApplicationContext(), EmailFetcherActivity.class);
					startActivity(showMail);
					// retrieveMessage();
					break;
				}
			}
		});
		adb.show();
	}

	Handler joinThread = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				msgThread.join();
				System.out.println("RETRIEVE EMAIL: thread joined");
			} catch (Exception e) {
				System.out.println("RETRIEVE EMAIL: error join thread");
				e.printStackTrace();
			}
		}
	};

	void constructProjectDetail() {
		FTimeUtility ftu = new FTimeUtility();
		String title = "Project Detail";
		StringBuilder sb = new StringBuilder();
		sb.append("Project Name: " + passProjectName + "\n");
		sb.append("Project Description: " + passProjectDesc + "\n");
		sb.append("Project Duration: " + ftu.collabtiveDateFormat(passProjectStart, "d/M/yyyy") + " until "
				+ ftu.collabtiveDateFormat(passProjectEnd, "d/M/yyyy"));
		notificator.showAlert(title, sb.toString(), "Close");
	}

	private void constructDetail() {
		String title = "About KumaProject";
		String message = "KumaProject v.1.0\nProgram by: Faris Syariati\nDesign by: Arawinda\nNovember 2012";
		notificator.showAlert(title, message, "Close");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_milestone:
			getMilestoneData();
			break;
		case R.id.btn_tasklist:
			getTasklistData();
			break;
		case R.id.btn_files:
			getFilelistData();
			break;
		case R.id.btn_messages:
			createMessagingMenu();
			break;
		case R.id.btn_project_detail:
			getTaskData();
			// constructProjectDetail();
			break;
		case R.id.btn_exit:
			constructDetail();
			break;
		default:
			break;
		}
	}
}
