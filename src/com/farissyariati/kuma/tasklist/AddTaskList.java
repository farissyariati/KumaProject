package com.farissyariati.kuma.tasklist;

import java.util.List;

import org.json.JSONArray;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.milestones.Milestones;
import com.farissyariati.kuma.milestones.MilestonesParser;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddTaskList extends Activity implements
		AdapterView.OnItemSelectedListener {
	private int passProjectID;
	private int selectedMilestoneID;
	private EditText etTasklistName, etTasklistDescription;
	private Spinner spMilestoneName;

	private CollabtiveManager collManager;
	private String sessionID;
	private ProgressDialog pdAddTasklist;
	private Thread jsonThread;
	private ArrayAdapter<String> spinnerAdapter;

	private String[] milestoneName;
	private int[] milestoneID;
	private JSONArray milestonesArray;
	private List<Milestones> milestones;
	
	private String resultText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist_add_layout);
		initVar();
		initParseMilestoneData();
		initComponents();
	}

	private void initVar() {
		this.passProjectID = new FPreferencesManager(this)
				.getTemporaryPassedPID();
	}

	private void initComponents() {
		this.etTasklistName = (EditText) findViewById(R.id.et_add_tasklist_name);
		this.etTasklistDescription = (EditText) findViewById(R.id.et_add_tasklist_description);
		this.spMilestoneName = (Spinner) findViewById(R.id.sp_tasklist_milestone);
		spMilestoneName.setOnItemSelectedListener(this);
		this.spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, milestoneName);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMilestoneName.setAdapter(spinnerAdapter);
		setInnerTypeFace();
	}

	private void setInnerTypeFace() {
		Typeface typeface = Typeface.createFromAsset(this.getAssets(),
				"Sketch_Block.ttf");
		TextView tasklistName = (TextView) findViewById(R.id.tv_add_tasklist_name);
		TextView tasklistDesc = (TextView) findViewById(R.id.tv_add_tasklist_description);
		TextView tasklistAscMilestone = (TextView) findViewById(R.id.tv_add_tasklist_associated_milestone);

		tasklistName.setTypeface(typeface);
		tasklistDesc.setTypeface(typeface);
		tasklistAscMilestone.setTypeface(typeface);
	}

	private void initParseMilestoneData() {
		MilestonesParser parser = new MilestonesParser(this);
		try {
			this.milestonesArray = new JSONArray(
					new FFileManager()
							.getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_MILESTONES));
			System.out.println("Tasklist MilestoneJSONArray: "
					+ milestonesArray);
			parser.parse(milestonesArray);
			this.milestones = parser.getList();
			int length = milestones.size();
			this.milestoneID = new int[length];
			this.milestoneName = new String[length];
			for (int i = 0; i < length; i++) {
				this.milestoneID[i] = milestones.get(i).mlstID;
				this.milestoneName[i] = milestones.get(i).mlstName;
				System.out.println("Tasklist Milestone's Name: "
						+ milestoneName[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onAddTasklistLoading() {
		pdAddTasklist = new ProgressDialog(this);
		pdAddTasklist.setMessage("Adding tasklist to server..");
		pdAddTasklist.setCancelable(false);
		pdAddTasklist.show();
		System.out.println("Add Tasklist: Selected MilestoneID "+selectedMilestoneID);
		this.jsonThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Add Tasklist: Masuk Ke Thread");
				// TODO Auto-generated method stub
//				collManager = new CollabtiveManager(
//						CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME);
				collManager = new CollabtiveManager(getBaseContext());
				FPreferencesManager fpm = new FPreferencesManager(
						getBaseContext());
				sessionID = fpm.getSessionID();
				String tasklistName = etTasklistName.getText().toString();
				String tasklistDesc = etTasklistDescription.getText()
						.toString();
				try {
					collManager.getAddTasklistJSONObject(passProjectID + "",
							selectedMilestoneID + "", tasklistName, tasklistDesc,
							sessionID);
					int statusCode = collManager.getAddTasklistStatusCode();
					if (statusCode == 1) {
						resultText = "Tasklist is added successfully";
					} else {
						resultText = "Failed adding a tasklist";
					}
					addTasklistHandler.sendMessage(addTasklistHandler
							.obtainMessage());
					jsonThreadDismiss();
				} catch (Exception e) {
					System.out
							.println("Error dalam Thread Penambahan Tasklist");
					e.printStackTrace();
				}
			}
		});
		jsonThread.start();
	}
	
	Handler addTasklistHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdAddTasklist.dismiss();
			Toast.makeText(getBaseContext(), resultText, Toast.LENGTH_LONG)
					.show();
		}
	};
	
	private void jsonThreadDismiss() {
		try {
			//jsonThread.join();
			onUpdateExit();
		} catch (Exception e) {
			System.out.println("error start activity");
			e.printStackTrace();
		}
	}
	
	private void onUpdateExit() {
		onUpdateTasklist();
		Intent startNewTasklistList = new Intent(this,
				TasklistListActivity.class);
		startActivity(startNewTasklistList);
		this.finish();
	}
	
	private void onUpdateTasklist() {
		FFileManager ffm = new FFileManager();
		collManager.getTasklistsJSONObjects(passProjectID+"", sessionID);
		ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASKLIST,
				collManager.getTasklistsJSONArray().toString());
	}

	private void clearAllEditText() {
		etTasklistName.setText(null);
		etTasklistDescription.setText(null);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		this.selectedMilestoneID = milestoneID[position];
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		this.selectedMilestoneID = milestoneID[0];
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			Intent returnTasklistList = new Intent(this,
					TasklistListActivity.class);
			startActivity(returnTasklistList);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tasklist_add_menu, menu);
		MenuItem miDone = menu.findItem(R.id.done_add_tasklist_menu);
		miDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				//Toast.makeText(getBaseContext(), "Something: "+selectedMilestoneID, Toast.LENGTH_SHORT).show();
				onAddTasklistLoading();
				return false;
			}
		});

		MenuItem miClear = menu.findItem(R.id.clear_add_tasklist_menu);
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
