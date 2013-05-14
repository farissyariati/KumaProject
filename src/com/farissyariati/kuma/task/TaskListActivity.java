package com.farissyariati.kuma.task;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import com.farissyariati.kuma.R;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveNotificator;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;
import com.farissyariati.kuma.utility.FTimeUtility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskListActivity extends Activity {
	private JSONArray tasksArray;
	private ListView lv;
	private List<Tasks> list;
	private ProgressDialog pdDeleteTask;
	private ProgressDialog pdCloseTask;

	private String sessionID;
	private String deleteResult, closeResult;

	private Thread deleteTaskThread;
	private Thread closeTaskThread;

	private String passTaskName;
	private long passEndTask;
	private int passTaskID;
	private String passTaskDesc;
	private CollabtiveNotificator notificator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.sessionID = new FPreferencesManager(this).getSessionID();
		this.notificator = new CollabtiveNotificator(this);
		initParseTasksData();
		setListView();
	}
	
	
	private void initParseTasksData() {
		TasksParser parser = new TasksParser(this);
		try {
			tasksArray = new JSONArray(
					new FFileManager()
							.getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_TASK));
			System.out.println("ACTIVE TASK: array"+tasksArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		parser.parse(tasksArray);
		this.list = parser.getList();
		System.out.println("ACTIVE TASK: "+list.size());
	}

	private void setListView() {
		setContentView(R.layout.task_list);
		TasksArrayAdapter projectsArrayAdapter = new TasksArrayAdapter(
				getApplicationContext(), R.layout.task_list_row, list);
		this.lv = (ListView) findViewById(R.id.tasklistListView);
		lv.setAdapter(projectsArrayAdapter);
		setOnItemClickListener();
	}

	private void setOnItemClickListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				changeParamPassedData((int)id);
				StringBuilder sb = new StringBuilder();
				sb.append("Task Name: "+passTaskName+"\n");
				sb.append("Task Detail: "+passTaskDesc+"\n");
				sb.append("Task End at: "+new FTimeUtility().collabtiveDateFormat(passEndTask, "d/M/yyyy"));
				notificator.showAlert("Task Detail", sb.toString(),"OK");
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				changeParamPassedData((int) id);
				createMenuOnLongItemClicked(list.get((int) id).taskID);
				return false;
			}
		});
	}

	private void changeParamPassedData(int id) {
		this.passTaskID = list.get(id).taskID;
		this.passTaskName = list.get(id).taskName;
		this.passEndTask = list.get(id).taskEnd;
		this.passTaskDesc = list.get(id).taskDesc;
	}


	private void deleteTask(int tid) {
		final String taskID = tid + "";
		final int projectID = new FPreferencesManager(this).getTemporaryPassedPID();
		final int taskListID = new FPreferencesManager(this).getTemporaryPassedTLID();
		pdDeleteTask = new ProgressDialog(this);
		pdDeleteTask.setMessage("Deleting Task From Server..");
		pdDeleteTask.setCancelable(false);
		pdDeleteTask.show();
		this.deleteTaskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getDeleteTaskJSONObjects(taskID, sessionID);
				if (collManager.getDeleteTaskStatusCode() == 1) {
					collManager.getTasksJSONObjects(projectID+"", taskListID+"", sessionID);
					FFileManager fileManager = new FFileManager();
					fileManager.writeToFile(
							CollabtiveProfile.KUMA_FILE_JSON_TASK,
							collManager.getTasksJSONArray().toString());

					deleteResult = "1 Task Deleted";
					deleteDialogHandler.sendMessage(deleteDialogHandler
							.obtainMessage());
				} else {
					deleteResult = "Task Deletion Failed";
					deleteDialogHandler.sendMessage(deleteDialogHandler
							.obtainMessage());
				}

			}
		});
		deleteTaskThread.start();
	}

	private void closeTask(int tid) {
		final String taskID = tid + "";
		System.out.println("CLOSE TASK: TASK ID"+taskID);
		final int projectID = new FPreferencesManager(this).getTemporaryPassedPID();
		final int taskListID = new FPreferencesManager(this).getTemporaryPassedTLID();
		pdCloseTask = new ProgressDialog(this);
		pdCloseTask.setMessage("Closing Task From Server..");
		pdCloseTask.setCancelable(false);
		pdCloseTask.show();
		this.closeTaskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getCloseTaskJSONObject(taskID, sessionID);
				if (collManager.getCloseTaskStatusCode() == 1) {
					collManager.getTasksJSONObjects(projectID+"", taskListID+"", sessionID);
					FFileManager fileManager = new FFileManager();
					fileManager.writeToFile(
							CollabtiveProfile.KUMA_FILE_JSON_TASK,
							collManager.getTasksJSONArray().toString());

					closeResult = "1 Task Closed";
					closeDialogHandler.sendMessage(closeDialogHandler
							.obtainMessage());
				} else {
					closeResult = "Failed Closing Task";
					closeDialogHandler.sendMessage(closeDialogHandler
							.obtainMessage());
				}

			}
		});
		closeTaskThread.start();
	}
	

	@SuppressLint("HandlerLeak")
	private void createMenuOnLongItemClicked(final int taskID) {
		final CharSequence charSequence[] = { "Mark as Done", "Edit Task", "Delete Task"};
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Project Menu");
		adb.setItems(charSequence, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					closeTask(taskID);
					break;
				case 1:
					TaskListActivity.this.finish();
					goToEditTask();
					break;
				case 2:
					deleteTask(taskID);
					break;
				}
			}
		});
		adb.show();
	}

	Handler deleteDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdDeleteTask.dismiss();
			Toast.makeText(getBaseContext(), deleteResult, Toast.LENGTH_SHORT)
					.show();
			joinThreads();
			initParseTasksData();
			setListView();
		}
	};
	
	Handler closeDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdCloseTask.dismiss();
			Toast.makeText(getBaseContext(), closeResult, Toast.LENGTH_SHORT)
					.show();
			joinThreads();
			initParseTasksData();
			setListView();
		}
	};



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.task_list_menu, menu);
		
		MenuItem miAddProject = menu.findItem(R.id.menu_add_task);
		miAddProject.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent addTask = new Intent(getBaseContext(),
						AddTask.class);
				startActivity(addTask);
				finish();
				return false;
			}
		});
		
		
		return true;
	}

	private void joinThreads() {
		try {
			if (deleteTaskThread != null)
				deleteTaskThread.join();
			if(closeTaskThread != null)
				closeTaskThread.join();
			System.out.println("All thread join");
		} catch (Exception e) {
			System.out.println("Error Joining Thread");
			e.printStackTrace();
		}
	}


	private void goToEditTask() {
		Intent editActivity = new Intent(this, EditTask.class);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_TASK_NAME,
				passTaskName);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_TASK_DESC,
				passTaskDesc);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_TASK_END,
				passEndTask);
		editActivity.putExtra(CollabtiveProfile.COLL_TAG_TASK_TID,
				passTaskID);
		startActivity(editActivity);
	}

}
