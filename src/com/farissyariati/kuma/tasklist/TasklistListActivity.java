package com.farissyariati.kuma.tasklist;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.task.TaskListActivity;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveNotificator;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;

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

public class TasklistListActivity extends Activity {
	private JSONArray tasklistArray;
	private ListView lv;
	private List<TaskLists> list;

	private ProgressDialog pdDeleteTasklist;
	private ProgressDialog pdTaskData;
	private Thread deleteTasklistThread;
	private Thread taskThread;

	private String sessionID;

	private int passProjectID;
	private String deleteResult;
	private String taskResult;

	private int passMilestoneID;
	private int passTasklistID;
	private String passTasklistName;
	private String passTasklistDesc;
	
	private CollabtiveNotificator notificator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initVar();
		initParseTasklistData();
		setListView();
	}

	private void initVar() {
		this.notificator = new CollabtiveNotificator(this);
		this.sessionID = new FPreferencesManager(this).getSessionID();
		this.passProjectID = new FPreferencesManager(this).getTemporaryPassedPID();
	}

	private void initParseTasklistData() {
		TaskListsParser parser = new TaskListsParser(this);
		try {
			this.tasklistArray = new JSONArray(
					new FFileManager().getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_TASKLIST));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		parser.parse(tasklistArray);
		this.list = parser.getList();
	}

	private void setListView() {
		setContentView(R.layout.tasklist_list);
		TasklistsArrayAdapter tasklistsArrayAdapter = new TasklistsArrayAdapter(getApplicationContext(),
				R.layout.tasklist_list_row, list);
		this.lv = (ListView) findViewById(R.id.tasklistListView);
		lv.setAdapter(tasklistsArrayAdapter);
		setOnItemClickListener();
	}

	private void setOnItemClickListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				changeParamPassedData((int) id);
				getTaskData(list.get((int) id).taskListID);
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				changeParamPassedData((int) id);
				createMenuOnLongItemClicked(list.get((int) id).taskListID);
				return false;
			}
		});
	}

	private void deleteTasklist(int tlistID) {
		final String tasklistID = tlistID + "";
		pdDeleteTasklist = new ProgressDialog(this);
		pdDeleteTasklist.setMessage("Deleting Tasklist From Server..");
		pdDeleteTasklist.setCancelable(false);
		pdDeleteTasklist.show();
		this.deleteTasklistThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getDeleteTasklistJSONObjects(tasklistID, sessionID);
				if (collManager.getDeleteTasklistStatusCode() == 1) {
					collManager.getTasklistsJSONObjects(passProjectID + "", sessionID);
					FFileManager fileManager = new FFileManager();
					fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASKLIST, collManager
							.getTasklistsJSONArray().toString());
					deleteResult = "1 Tasklist Deleted";
					deleteDialogHandler.sendMessage(deleteDialogHandler.obtainMessage());
				} else {
					deleteResult = "Tasklist Deletion Failed";
					deleteDialogHandler.sendMessage(deleteDialogHandler.obtainMessage());
				}

			}
		});
		deleteTasklistThread.start();
	}

	private void getTaskData(int tlid) {
		final FPreferencesManager fpm = new FPreferencesManager(this);
		fpm.setTemporaryPassedTLID(tlid);
		final String tasklistID = tlid + "";
		this.pdTaskData = new ProgressDialog(this);
		pdTaskData.setMessage("Getting Task Data..");
		pdTaskData.setCancelable(false);
		pdTaskData.show();

		this.taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				try {
					collManager.getTasksJSONObjects(passProjectID + "", tasklistID + "", sessionID);
					if (collManager.getTasksStatusCode() == 1) {
						long milestoneEnd = collManager.getSpecifiedMilestoneEndTime(passMilestoneID + "", sessionID);
						fpm.setMilestoneControlEndTime(milestoneEnd);
						System.out.println("CALENDAR CONTROL: MILESTONE END AT: "+milestoneEnd);
						FFileManager fileManager = new FFileManager();
						fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASK, collManager.getTasksJSONArray()
								.toString());
						taskResult = "Success getting task data..";
						taskHandler.sendMessage(taskHandler.obtainMessage());
						goToTaskList();
					} else {
						taskResult = "Failed getting task data";
						taskHandler.sendMessage(taskHandler.obtainMessage());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		taskThread.start();
	}

	Handler taskHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdTaskData.dismiss();
			Toast.makeText(getBaseContext(), taskResult, Toast.LENGTH_LONG).show();
		}
	};

	private void changeParamPassedData(int id) {
		this.passMilestoneID = list.get(id).milestoneID;
		this.passTasklistName = list.get(id).taskListName;
		this.passTasklistDesc = list.get(id).taskListDesc;
		this.passTasklistID = list.get(id).taskListID;
	}

	Handler deleteDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdDeleteTasklist.dismiss();
			Toast.makeText(getBaseContext(), deleteResult, Toast.LENGTH_SHORT).show();
			joinThreads();
			initParseTasklistData();
			setListView();
		}
	};

	private void joinThreads() {
		try {
			if (deleteTasklistThread != null)
				deleteTasklistThread.join();
			System.out.println("All thread join");
		} catch (Exception e) {
			System.out.println("Error Joining Thread");
			e.printStackTrace();
		}
	}

	private void goToEditTasklist() {
		Intent editTasklist = new Intent(this, EditTasklist.class);
		editTasklist.putExtra(CollabtiveProfile.COLL_TAG_TASKLIST_MID, passMilestoneID);
		editTasklist.putExtra(CollabtiveProfile.COLL_TAG_TASKLIST_NAME, passTasklistName);
		editTasklist.putExtra(CollabtiveProfile.COLL_TAG_TASKLIST_DESC, passTasklistDesc);
		editTasklist.putExtra(CollabtiveProfile.COLL_TAG_TASKLIST_TLID, passTasklistID);
		startActivity(editTasklist);
		TasklistListActivity.this.finish();
	}

	private void goToTaskList() {
		Intent goTaskList = new Intent(this, TaskListActivity.class);
		// goTaskList.putExtra(CollabtiveProfile.COLL_TAG_TASK_TLID,
		// taskListID);
		startActivity(goTaskList);
	}

	@SuppressLint("HandlerLeak")
	private void createMenuOnLongItemClicked(final int tasklistID) {
		final CharSequence charSequence[] = { "Edit Tasklist", "Delete Tasklist", "Tasklist Detail" };
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Tasklist Menu");
		adb.setItems(charSequence, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					goToEditTasklist();
					break;
				case 1:
					deleteTasklist(tasklistID);
					break;
				case 2:
					showTasklistDetail();
					break;
				}
			}
		});
		adb.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tasklist_list_menu, menu);
		MenuItem miAddProject = menu.findItem(R.id.menu_add_tasklist);
		miAddProject.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent addTasklist = new Intent(getBaseContext(), AddTaskList.class);
				addTasklist.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_POSTED_ID, passProjectID);
				startActivity(addTasklist);
				finish();
				return false;
			}
		});
		return true;
	}
	
	private void showTasklistDetail(){
		StringBuilder sb = new StringBuilder();
		sb.append("Tasklist Name: "+passTasklistName+"\n");
		sb.append("Tasklist Description: "+passTasklistDesc+"\n");
		notificator.showAlert("Tasklist Detail", sb.toString(), "OK");
	}

}
