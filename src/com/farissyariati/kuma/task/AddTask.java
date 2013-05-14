package com.farissyariati.kuma.task;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.select.users.SelectUsersListActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveNotificator;
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
public class AddTask extends Activity {
	private Button btSelectUsers;
	private RelativeLayout rlEndTask;
	private EditText etTaskName, etTaskDesc;
	private ProgressDialog pdAddTask;
	private TextView tvMiletstoneEndControl;

	private int endDay, endMonth, endYear;
	private Thread jsonThread;
	private Thread jsonUsersThread;
	private String resultText;

	private int projectID;
	private int tasklistID;
	private String sessionID;
	private CollabtiveManager collManager;
	private long passEndMilestoneControl;
	FPreferencesManager fpm;
	private FTimeUtility timeUtility;
	private CollabtiveNotificator notificator;

	static final int DATE_DIALOG_START = 0;
	static final int DATE_DIALOG_END = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_add_layout);
		initVars();
		initComponents();
		setCallendar();
	}

	private void initVars() {
		FPreferencesManager fpm = new FPreferencesManager(this);
		this.sessionID = fpm.getSessionID();
		this.projectID = fpm.getTemporaryPassedPID();
		this.tasklistID = fpm.getTemporaryPassedTLID();
		this.notificator = new CollabtiveNotificator(this);
		this.fpm = new FPreferencesManager(this);
		this.timeUtility = new FTimeUtility();
		this.notificator = new CollabtiveNotificator(this);
		this.passEndMilestoneControl = fpm.getMilestoneControlEndTime();
	}

	private void initComponents() {
		this.pdAddTask = new ProgressDialog(this);
		this.tvMiletstoneEndControl = (TextView) findViewById(R.id.tv_milestone_end_control);
		tvMiletstoneEndControl.setText("Task limit date: "
				+ timeUtility.collabtiveDateFormat(passEndMilestoneControl, "d/M/yyyy"));
		setInnerTypeFace();
		initclickAbleComponent();
		initEditAbleComponents();
	}

	private void setCallendar() {
		FTimeUtility ftu = new FTimeUtility();
		// end project time. set now
		this.endDay = ftu.getDay();
		this.endMonth = ftu.getMonth();
		this.endYear = ftu.getYear();
	}

	private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			endDay = dayOfMonth;
			endMonth = monthOfYear + 1;
			endYear = year;
			Toast.makeText(getBaseContext(), "Task ends at: " + endDay + "/" + endMonth + "/" + endYear,
					Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_END:
			return new DatePickerDialog(this, endDateListener, endYear, endMonth, endDay);
		default:
			break;
		}
		return null;
	}

	private void setInnerTypeFace() {
		Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Sketch_Block.ttf");
		TextView projectName = (TextView) findViewById(R.id.tv_add_task_name);
		TextView projectDesc = (TextView) findViewById(R.id.tv_add_task_description);
		TextView endProject = (TextView) findViewById(R.id.tv_add_task_end);

		this.btSelectUsers = (Button) findViewById(R.id.bt_select_user_task);
		btSelectUsers.setTypeface(typeface);

		projectName.setTypeface(typeface);
		projectDesc.setTypeface(typeface);
		endProject.setTypeface(typeface);
	}

	private void initclickAbleComponent() {

		btSelectUsers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoadUsersToSelect();
			}
		});

		rlEndTask = (RelativeLayout) findViewById(R.id.rl_add_task_end);
		rlEndTask.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_END);
			}
		});
	}

	private void initEditAbleComponents() {
		this.etTaskName = (EditText) findViewById(R.id.et_add_task_name);
		this.etTaskDesc = (EditText) findViewById(R.id.et_add_task_description);

	}

	private void clearAllEditText() {
		etTaskName.setText(null);
		etTaskDesc.setText(null);
	}

	private void onAddTaskLoading() {
		pdAddTask.setMessage("Adding task to server..");
		pdAddTask.setCancelable(false);
		pdAddTask.show();
		this.jsonThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// collManager = new CollabtiveManager(
				// CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME);
				collManager = new CollabtiveManager(getBaseContext());

				String taskName = etTaskName.getText().toString();
				String taskDesc = etTaskDesc.getText().toString();
				long endTask = new FTimeUtility().getUnixTimestamp(endDay, endMonth - 1, endYear);
				String end = endTask + "";
				String assignedUserIDs = new FPreferencesManager(getBaseContext()).getSelectedIDChain();
				try {
					collManager.getAddTaskJSONObject(projectID + "", tasklistID + "", taskName, taskDesc, end,
							assignedUserIDs, sessionID);
					int statusCode = collManager.getAddTaskStatusCode();
					if (statusCode == 1) {
						resultText = "task is added successfully";
					} else {
						resultText = "Failed adding a task";
					}
					addTaskHandler.sendMessage(addTaskHandler.obtainMessage());
					jsonThreadDismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		jsonThread.start();
	}

	private void onLoadUsersToSelect() {
		pdAddTask.setMessage("Waiting Users List");
		pdAddTask.setCancelable(false);
		pdAddTask.show();
		// collManager = new CollabtiveManager(
		// CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME);
		collManager = new CollabtiveManager(getBaseContext());
		this.jsonUsersThread = new Thread(new Runnable() {
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
				goSelectUsers();
			}
		});
		jsonUsersThread.start();
	}

	Handler addTaskHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdAddTask.dismiss();
			Toast.makeText(getBaseContext(), resultText, Toast.LENGTH_LONG).show();
		}
	};

	Handler selectUsersHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdAddTask.dismiss();
			Toast.makeText(getBaseContext(), "Select User/Users", Toast.LENGTH_LONG).show();
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

	private void onUpdateTask() {
		FFileManager ffm = new FFileManager();
		collManager.getTasksJSONObjects(projectID + "", tasklistID + "", sessionID);
		ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASK, collManager.getTasksJSONArray().toString());
	}

	private void onUpdateExit() {
		onUpdateTask();
		Intent startNewTaskList = new Intent(this, TaskListActivity.class);
		startActivity(startNewTaskList);
		this.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			Intent returnTaskList = new Intent(this, TaskListActivity.class);
			startActivity(returnTaskList);
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
		getMenuInflater().inflate(R.menu.task_add_menu, menu);
		MenuItem miDone = menu.findItem(R.id.done_add_menu);
		miDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				long endTask = new FTimeUtility().getUnixTimestamp(endDay, endMonth - 1, endYear);
				if (timeUtility.onTimeRange(passEndMilestoneControl, endTask))
					onAddTaskLoading();
				else
					notificator.showAlert("Failed Adding Task", "Calendar Out of Milestone Range", "OK");
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
