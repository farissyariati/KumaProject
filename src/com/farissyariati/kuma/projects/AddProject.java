package com.farissyariati.kuma.projects;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.select.users.SelectUsersListActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;
import com.farissyariati.kuma.utility.FTimeUtility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "HandlerLeak" })
public class AddProject extends Activity {
	private Button btSelectUsers;
	private RelativeLayout rlStartProject;
	private RelativeLayout rlEndProject;
	private EditText etProjectName, etProjectDesc, etProjectBudget;
	private ProgressDialog pdAddProject;

	private int startDay, startMonth, startYear;
	private int endDay, endMonth, endYear;
	private Thread jsonThread;
	private Thread jsonUsersThread;
	private String resultText;
	private int projectLatestID;

	private String sessionID;
	private CollabtiveManager collManager;

	static final int DATE_DIALOG_START = 0;
	static final int DATE_DIALOG_END = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int orientation = getResources().getConfiguration().orientation;
		if(orientation == 1)
			setContentView(R.layout.project_add_layout);
		else
			setContentView(R.layout.project_add_layout_lanscape);
		initComponents();
		setCallendar();
	}

	private void initComponents() {
		pdAddProject = new ProgressDialog(this);
		setInnerTypeFace();
		initclickAbleComponent();
		initEditAbleComponents();
	}

	private void setCallendar() {
		FTimeUtility ftu = new FTimeUtility();
		this.startDay = ftu.getDay();
		this.startMonth = ftu.getMonth();
		this.startYear = ftu.getYear();
		// end project time. set now
		this.endDay = ftu.getDay();
		this.endMonth = ftu.getMonth();
		this.endYear = ftu.getYear();
	}

	private DatePickerDialog.OnDateSetListener startProjectDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			startDay = dayOfMonth;
			startMonth = monthOfYear + 1;
			startYear = year;
			Toast.makeText(
					getBaseContext(),
					"Project starts at: " + startDay + "/" + startMonth + "/"
							+ startYear, Toast.LENGTH_LONG).show();
		}
	};

	private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			endDay = dayOfMonth;
			endMonth = monthOfYear;
			endYear = year;
			Toast.makeText(
					getBaseContext(),
					"Project ends at: " + endDay + "/" + endMonth + "/"
							+ endYear, Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_START:
			return new DatePickerDialog(this, startProjectDateListener,
					startYear, startMonth, startDay);
		case DATE_DIALOG_END:
			return new DatePickerDialog(this, endDateListener, endYear,
					endMonth, endDay);
		default:
			break;
		}
		return null;
	}

	private void setInnerTypeFace() {
		Typeface typeface = Typeface.createFromAsset(this.getAssets(),
				"Sketch_Block.ttf");
		TextView projectName = (TextView) findViewById(R.id.tv_add_project_name);
		TextView projectDesc = (TextView) findViewById(R.id.tv_add_project_description);
		TextView projectBudget = (TextView) findViewById(R.id.tv_add_project_budget);
		TextView startProject = (TextView) findViewById(R.id.tv_add_project_start);
		TextView endProject = (TextView) findViewById(R.id.tv_add_project_end);
		
		this.btSelectUsers = (Button) findViewById(R.id.bt_select_user);
		btSelectUsers.setTypeface(typeface);
		
		projectName.setTypeface(typeface);
		projectDesc.setTypeface(typeface);
		projectBudget.setTypeface(typeface);
		startProject.setTypeface(typeface);
		endProject.setTypeface(typeface);
	}

	private void initclickAbleComponent() {

		btSelectUsers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoadUsersToSelect();
			}
		});

		rlStartProject = (RelativeLayout) findViewById(R.id.rl_add_project_start);
		rlStartProject.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_START);
			}
		});
		rlEndProject = (RelativeLayout) findViewById(R.id.rl_add_project_end);
		rlEndProject.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_END);
			}
		});
	}

	private void initEditAbleComponents() {
		this.etProjectName = (EditText) findViewById(R.id.et_add_project_name);
		this.etProjectDesc = (EditText) findViewById(R.id.et_add_project_description);
		this.etProjectBudget = (EditText) findViewById(R.id.et_add_project_budget);
	}

	private void clearAllEditText() {
		etProjectName.setText(null);
		etProjectDesc.setText(null);
		etProjectBudget.setText(null);
	}

	private void onAddProjectLoading() {
		pdAddProject.setMessage("Adding project to server..");
		pdAddProject.setCancelable(false);
		pdAddProject.show();
		this.jsonThread = new Thread(new Runnable() {
			@Override
			public void run() {
				collManager = new CollabtiveManager(getBaseContext());
				FPreferencesManager fpm = new FPreferencesManager(
						getBaseContext());
				sessionID = fpm.getSessionID();
				String projectName = etProjectName.getText().toString();
				String projectDesc = etProjectDesc.getText().toString();
				String projectBudget = etProjectBudget.getText().toString();
				long endProject = new FTimeUtility().getUnixTimestamp(endDay,
						endMonth, endYear);
				String end = endProject + "";
				try {
					collManager.getAddProjectResult(sessionID, projectName,
							projectDesc, end, projectBudget, 0 + "");
					int statusCode = collManager.getAddProjectStatusCode();
					if (statusCode == 1) {
						collManager.getLatestProjectJSONObjects();
						projectLatestID = collManager.getLatestProjectID();
						assignAllSelectedUsers();
						resultText = "Project is added successfully";
					} else {
						resultText = "Failed adding a project";
					}
					addProjectHandler.sendMessage(addProjectHandler
							.obtainMessage());
					jsonThreadDismiss();
				} catch (Exception e) {
					System.out.println("Kok Error");
					e.printStackTrace();
				}
			}
		});
		jsonThread.start();
	}

	private void assignAllSelectedUsers() {
		FPreferencesManager fpm = new FPreferencesManager(this);
		String selectedIDChain = fpm.getSelectedIDChain();
		String id[] = selectedIDChain.split(";");
		for (int i = 0; i < id.length; i++) {
			if (!id[i].equals("0") && !id[i].equals("")){
				collManager.getAssignProjectJSONObjects(id[i], sessionID,
						projectLatestID + "");
			}
		}
	}

	private void onLoadUsersToSelect() {
		pdAddProject.setMessage("Waiting Users List");
		pdAddProject.setCancelable(false);
		pdAddProject.show();
		collManager = new CollabtiveManager(getBaseContext());
		this.jsonUsersThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					FFileManager ffm = new FFileManager();
					collManager.getUsersJSONObject();
					ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_USERS,
							collManager.getUsersJSONArray().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				selectUsersHandler.sendMessage(selectUsersHandler
						.obtainMessage());
				goSelectUsers();
			}
		});
		jsonUsersThread.start();
	}

	Handler addProjectHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdAddProject.dismiss();
			Toast.makeText(getBaseContext(), resultText, Toast.LENGTH_LONG)
					.show();
		}
	};

	Handler selectUsersHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdAddProject.dismiss();
			Toast.makeText(getBaseContext(), "Select User/Users",
					Toast.LENGTH_LONG).show();
		}
	};

	private void jsonThreadDismiss() {
		try {
			// jsonThread.join();
			onUpdateExit();
		} catch (Exception e) {
			System.out.println("error start activity");
			e.printStackTrace();
		}
	}

	private void onUpdateProject() {
		FFileManager ffm = new FFileManager();
		collManager.getProjectsJSONObject(sessionID);
		ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_PROJECT, collManager
				.getProjectsJSONArray().toString());
	}

	private void onUpdateExit() {
		onUpdateProject();
		Intent startNewProjectList = new Intent(this, ProjectListActivity.class);
		startActivity(startNewProjectList);
		this.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			Intent returnProjectList = new Intent(this,
					ProjectListActivity.class);
			startActivity(returnProjectList);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void goSelectUsers() {
		Intent selectUsers = new Intent(this, SelectUsersListActivity.class);
		startActivity(selectUsers);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.project_add_menu, menu);
		MenuItem miDone = menu.findItem(R.id.done_add_menu);
		miDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				onAddProjectLoading();
				return false;
			}
		});
		
		MenuItem miClear = menu.findItem(R.id.clear_add_menu);
		miClear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				clearAllEditText();
				return false;
			}
		});
		return true;
	}

}
