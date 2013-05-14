package com.farissyariati.kuma.projects;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

import com.farissyariati.kuma.ApplicationPreferencesActivity;
import com.farissyariati.kuma.LoginActivity;
import com.farissyariati.kuma.R;
import com.farissyariati.kuma.dashboard.DashboardActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;
import com.farissyariati.kuma.utility.FTimeUtility;
import com.farissyariati.kuma.utility.NotificationService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectListActivity extends Activity {
	private JSONArray projectsArray;
	private ListView lv;
	private List<Projects> list;
	private ProgressDialog pdDeleteProject;
	private ProgressDialog pdLogout;
	private ProgressDialog pdCloseProject;

	private String sessionID;
	private String deleteResult;
	private String logoutResult;
	private String closeResult;

	private Thread deleteProjectThread;
	private Thread logoutThread;
	private Thread closeProjectThread;

	private String passProjectName;
	private long passStartProject;
	private long passEndProject;
	private int passProjectBudget;
	private int passProjectID;
	private String passProjectDesc;
	private FTimeUtility timeUtility;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.sessionID = new FPreferencesManager(this).getSessionID();
		this.timeUtility = new FTimeUtility();
		initParseProjectsData();
		setListView();
		Intent startNotification = new Intent(this, NotificationService.class);
		startService(startNotification);
	}

	private void initParseProjectsData() {
		ProjectsParser parser = new ProjectsParser(this);
		try {
			projectsArray = new JSONArray(new FFileManager().getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_PROJECT));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		parser.parse(projectsArray);
		this.list = parser.getList();
	}

	private void setListView() {
		setContentView(R.layout.project_list);
		ProjectsArrayAdapter projectsArrayAdapter = new ProjectsArrayAdapter(getApplicationContext(),
				R.layout.project_list_row, list);
		this.lv = (ListView) findViewById(R.id.projectsListView);
		lv.setAdapter(projectsArrayAdapter);
		setOnItemClickListener();
	}

	private void setOnItemClickListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// calendar control
				changeParamPassedData((int) id);
				//
				if (!timeUtility.overdue(list.get((int) id).endTime * 1000, System.currentTimeMillis())) {
					Toast.makeText(getBaseContext(), "Project Dashboard", Toast.LENGTH_SHORT).show();
					FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
					fpm.setTemporaryPassedPID(list.get((int) id).projectID);
					goToProjectDashboard(list.get((int) id).projectID);
				} else {
					Toast.makeText(getBaseContext(), "Project is overdate, please consider to finish it soon", Toast.LENGTH_LONG).show();
					FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
					fpm.setTemporaryPassedPID(list.get((int) id).projectID);
					goToProjectDashboard(list.get((int) id).projectID);
				}

			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				changeParamPassedData((int) id);
				createMenuOnLongItemClicked(list.get((int) id).projectID);
				return false;
			}
		});
	}

	private void changeParamPassedData(int id) {
		this.passProjectID = list.get(id).projectID;
		this.passProjectName = list.get(id).projectName;
		this.passStartProject = list.get(id).startTime;
		this.passEndProject = list.get(id).endTime;
		this.passProjectDesc = list.get(id).projectDesc;
		this.passProjectBudget = list.get(id).projectBudget;
		FPreferencesManager fpm = new FPreferencesManager(this);
		fpm.setProjectControlStartTime(passStartProject);
		System.out.println("CALENDAR CONTROL: PASS PROJECT START = " + passStartProject);
		fpm.setProjectControlEndTime(passEndProject);
		System.out.println("CALENDAR CONTROL: PASS PROJECT START = " + passEndProject);
	}

	private void deleteProject(int pid) {
		final String projectID = pid + "";
		pdDeleteProject = new ProgressDialog(this);
		pdDeleteProject.setMessage("Deleting Project From Server..");
		pdDeleteProject.setCancelable(false);
		pdDeleteProject.show();
		this.deleteProjectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getDeleteProjectJSONObject(projectID, sessionID);
				if (collManager.getDeleteProjectsStatusCode() == 1) {
					collManager.getProjectsJSONObject(sessionID);
					FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
					fpm.setProjectCount(collManager.getProjectCount());
					FFileManager fileManager = new FFileManager();
					fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_PROJECT, collManager
							.getProjectsJSONArray().toString());

					deleteResult = "1 Project Deleted";
					deleteDialogHandler.sendMessage(deleteDialogHandler.obtainMessage());
				} else {
					deleteResult = "Project Deletion Failed";
					deleteDialogHandler.sendMessage(deleteDialogHandler.obtainMessage());
				}

			}
		});
		deleteProjectThread.start();
	}

	private void closeProject(int pid) {
		final String projectID = pid + "";
		pdCloseProject = new ProgressDialog(this);
		pdCloseProject.setCancelable(false);
		pdCloseProject.setMessage("Closing Project..");
		pdCloseProject.show();

		this.closeProjectThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getCloseProjectJSONObject(projectID, sessionID);
				if (collManager.getCloseProjectsStatusCode() == 1) {
					collManager.getProjectsJSONObject(sessionID);
					FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
					fpm.setProjectCount(collManager.getProjectCount());
					FFileManager fileManager = new FFileManager();
					fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_PROJECT, collManager
							.getProjectsJSONArray().toString());

					closeResult = "1 project has been finished";
					closeDialogHandler.sendMessage(closeDialogHandler.obtainMessage());
				} else {
					closeResult = "Failed close a project";
					closeDialogHandler.sendMessage(closeDialogHandler.obtainMessage());
				}
			}
		});
		closeProjectThread.start();

	}

	private void logout() {
		pdLogout = new ProgressDialog(this);
		pdLogout.setMessage("Logging out, please wait..");
		pdLogout.setCancelable(false);
		pdLogout.show();

		this.logoutThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// CollabtiveManager collManager = new
				// CollabtiveManager(CollabtiveManager.TEMPORARY_COLLABTIVE_URL_HOME);
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getLogoutJSONObject(sessionID);
				if (collManager.getLogoutStatusCode() == 1) {
					logoutResult = "Logut from Kuma Project";
					logoutDialogHandler.sendMessage(logoutDialogHandler.obtainMessage());
				} else {
					logoutResult = "Error while logging out";
					logoutDialogHandler.sendMessage(logoutDialogHandler.obtainMessage());
				}
			}
		});
		logoutThread.start();
	}

	private void goToProjectDashboard(int projectID) {
		Intent projectDashboard = new Intent(this, DashboardActivity.class);
		projectDashboard.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_POSTED_ID, projectID);
		projectDashboard.putExtra(CollabtiveProfile.KUMA_PASS_PROJECT_NAME, passProjectName);
		projectDashboard.putExtra(CollabtiveProfile.KUMA_PASS_PROJECT_DESC, passProjectDesc);
		projectDashboard.putExtra(CollabtiveProfile.KUMA_PASS_PROJECT_START, passStartProject);
		projectDashboard.putExtra(CollabtiveProfile.KUMA_PASS_PROJECT_END, passEndProject);
		startActivity(projectDashboard);
		overridePendingTransition(R.anim.main_fade_in, R.anim.splash_fade_out);
	}

	@SuppressLint("HandlerLeak")
	private void createMenuOnLongItemClicked(final int projectID) {
		final CharSequence charSequence[] = { "Mark as Done", "Edit Project","Delete Project" };
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Project Menu");
		adb.setItems(charSequence, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					closeProject(projectID);
					break;
				case 1:
					ProjectListActivity.this.finish();
					goToEditProject();
					break;
				case 2:
					deleteProject(projectID);
					break;
				}
			}
		});
		adb.show();
	}

	Handler deleteDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdDeleteProject.dismiss();
			Toast.makeText(getBaseContext(), deleteResult, Toast.LENGTH_SHORT).show();
			joinThreads();
			initParseProjectsData();
			setListView();
		}
	};

	Handler closeDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdCloseProject.dismiss();
			Toast.makeText(getBaseContext(), closeResult, Toast.LENGTH_LONG).show();
			joinThreads();
			initParseProjectsData();
			setListView();
		}
	};

	Handler logoutDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdLogout.dismiss();
			joinThreads();
			onLogoutSuccess();
			Toast.makeText(getBaseContext(), logoutResult, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.project_list_menu, menu);

		MenuItem miSettings = menu.findItem(R.id.menu_setting_system);
		miSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent preferencesIntent = new Intent(getBaseContext(), ApplicationPreferencesActivity.class);
				startActivity(preferencesIntent);
				return false;
			}
		});

		MenuItem miLogout = menu.findItem(R.id.menu_logout);
		miLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				logout();
				joinThreads();
				return false;
			}
		});

		MenuItem miAddProject = menu.findItem(R.id.menu_add_project);
		miAddProject.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent addProject = new Intent(getBaseContext(), AddProject.class);
				startActivity(addProject);
				finish();
				return false;
			}
		});

		return true;
	}

	private void joinThreads() {
		try {
			if (deleteProjectThread != null)
				deleteProjectThread.join();
			if (logoutThread != null)
				logoutThread.join();
			if (closeProjectThread != null)
				closeProjectThread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onLogoutSuccess() {
		FPreferencesManager fpm = new FPreferencesManager(this);
		fpm.setSessionID("");
		fpm.setUserID(0);
		ProjectListActivity.this.finish();
		fpm.setSavedEnabledNotificationState(fpm.getEnabledNotificationState());
		fpm.setEnabledNotificationState(false);
		Intent loginActivity = new Intent(this, LoginActivity.class);
		startActivity(loginActivity);
	}

	private void goToEditProject() {
		Intent editActivity = new Intent(this, EditProject.class);

		editActivity.putExtra(CollabtiveProfile.COLL_TAG_PROJECT_NAME, passProjectName);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_PROJECT_DESC, passProjectDesc);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_PROJECT_BUDGET, passProjectBudget);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_PROJECT_START, passStartProject);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_PROJECT_END, passEndProject);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_PROJECT_ID, passProjectID);
		startActivity(editActivity);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			FPreferencesManager fpm = new FPreferencesManager(getBaseContext());
			fpm.setSavedEnabledNotificationState(fpm.getEnabledNotificationState());
			fpm.setEnabledNotificationState(false);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
