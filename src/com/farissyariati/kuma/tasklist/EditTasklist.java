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

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "HandlerLeak" })
public class EditTasklist extends Activity implements
		AdapterView.OnItemSelectedListener {
	private EditText etTasklistName, etTasklistDesc;
	private ProgressDialog pdEditTasklist;
	private Spinner spMilestoneName;
	private ArrayAdapter<String> spinnerAdapter;

	private Thread jsonThread;
	private String resultText;

	private String sessionID;
	private CollabtiveManager collManager;

	private int passTasklistID;
	private String passTasklistName;
	private String passTasklistDesc;
	private int selectedMilestoneID;
	private int passMilestoneID;

	private String[] milestoneName;
	private int[] milestoneID;
	private JSONArray milestonesArray;
	private List<Milestones> milestones;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist_edit_layout);
		getPassedParam();
		initParseMilestoneData();
		initComponents();
	}

	private void initComponents() {
		pdEditTasklist = new ProgressDialog(this);
		this.spMilestoneName = (Spinner) findViewById(R.id.sp_edit_tasklist_milestone);
		spMilestoneName.setOnItemSelectedListener(this);
		this.spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, milestoneName);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMilestoneName.setAdapter(spinnerAdapter);
		int searchedIndex = 0;
		for(int i = 0; i < milestones.size(); i++){
			if(milestoneID[i] == passMilestoneID){
				searchedIndex = i;
				break;
			}
		}
		spMilestoneName.setSelection(searchedIndex);
		setInnerTypeFace();
		initEditAbleComponents();
	}

	private void setInnerTypeFace() {
		Typeface typeface = Typeface.createFromAsset(this.getAssets(),
				"Sketch_Block.ttf");
		TextView tasklistName = (TextView) findViewById(R.id.tv_edit_tasklist_name);
		TextView tasklistDesc = (TextView) findViewById(R.id.tv_edit_tasklist_description);
		TextView tasklistAscMilestone = (TextView) findViewById(R.id.tv_edit_tasklist_associated_milestone);
		tasklistName.setTypeface(typeface);
		tasklistDesc.setTypeface(typeface);
		tasklistAscMilestone.setTypeface(typeface);
	}

	private void initEditAbleComponents() {
		this.etTasklistName = (EditText) findViewById(R.id.et_edit_tasklist_name);
		this.etTasklistName.setText(passTasklistName);
		this.etTasklistDesc = (EditText) findViewById(R.id.et_edit_tasklist_description);
		this.etTasklistDesc.setText(passTasklistDesc);
	}

	private void getPassedParam() {
		this.passTasklistID = getIntent().getIntExtra(
				CollabtiveProfile.COLL_TAG_TASKLIST_TLID, 0);
		this.passTasklistDesc = getIntent().getStringExtra(
				CollabtiveProfile.COLL_TAG_TASKLIST_DESC);
		this.passTasklistName = getIntent().getStringExtra(
				CollabtiveProfile.COLL_TAG_TASKLIST_NAME);
		this.passMilestoneID = getIntent().getIntExtra(
				CollabtiveProfile.COLL_TAG_TASKLIST_MID, 0);
	}

	private void clearAllEditText() {
		etTasklistName.setText(null);
		etTasklistDesc.setText(null);
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

	private void onEditTasklistLoading() {
		pdEditTasklist.setMessage("Edit Tasklist");
		pdEditTasklist.setCancelable(false);
		pdEditTasklist.show();
		this.jsonThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				collManager = new CollabtiveManager(
//						CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME);
				collManager = new CollabtiveManager(getBaseContext());
				FPreferencesManager fpm = new FPreferencesManager(
						getBaseContext());
				sessionID = fpm.getSessionID();
				String tasklistName = etTasklistName.getText().toString();
				String tasklistDesc = etTasklistDesc.getText().toString();
				try {
					collManager.getEditTasklistJSONObject(passTasklistID + "",
							selectedMilestoneID + "", tasklistName,
							tasklistDesc, sessionID);
					int statusCode = collManager.getEditTasklistStatusCode();

					if (statusCode == 1) {
						resultText = "Tasklist is edited successfully";
					} else {
						resultText = "Failed editing a Tasklist";
					}
					editTasklistHandler.sendMessage(editTasklistHandler
							.obtainMessage());
					onUpdateExit();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		jsonThread.start();
	}

	Handler editTasklistHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdEditTasklist.dismiss();
			Toast.makeText(getBaseContext(), resultText, Toast.LENGTH_LONG)
					.show();
		}
	};

	private void jsonThreadDismiss() {
		try {
			if (jsonThread != null) {
				jsonThread.join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onUpdateTasklist() {
		FFileManager ffm = new FFileManager();
		collManager.getTasklistsJSONObjects(
				new FPreferencesManager(this).getTemporaryPassedPID() + "",
				sessionID);
		ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_TASKLIST, collManager
				.getTasklistsJSONArray().toString());
	}

	private void onUpdateExit() {
		onUpdateTasklist();
		Intent startNewTasklist = new Intent(this, TasklistListActivity.class);
		startActivity(startNewTasklist);
		EditTasklist.this.finish();
		jsonThreadDismiss();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			jsonThreadDismiss();
			finish();
			Intent returnMilestoneList = new Intent(this,
					TasklistListActivity.class);
			startActivity(returnMilestoneList);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.milestone_edit_menu, menu);
		MenuItem miDone = menu.findItem(R.id.done_edit);
		miDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				onEditTasklistLoading();
				return false;
			}
		});

		MenuItem miClear = menu.findItem(R.id.clear_edit);
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		this.selectedMilestoneID = milestoneID[arg2];
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
