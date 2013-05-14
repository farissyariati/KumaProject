package com.farissyariati.kuma.milestones;

import com.farissyariati.kuma.R;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "HandlerLeak" })
public class EditMilestone extends Activity {
	private RelativeLayout rlEndMilestone;
	private EditText etMilestoneName, etMilestoneDesc;
	private ProgressDialog pdEditMilestone;
	private CheckBox cbFinishMilestone;

	private int endDay, endMonth, endYear;
	private Thread jsonThread;
	private String resultText;

	private String sessionID;
	private CollabtiveManager collManager;

	static final int DATE_DIALOG_END = 1;

	private int passProjectID;
	private String passMilestoneName;
	private long passEndMilestone;
	private int passMilestoneID;
	private String passMilestoneDesc;

	private long endMilestone;
	private int milestoneStatus = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.milestone_edit_layout);
		getPassedParam();
		initComponents();
		setCallendar();
	}

	private void initComponents() {
		pdEditMilestone = new ProgressDialog(this);
		setInnerTypeFace();
		initclickAbleComponent();
		initEditAbleComponents();
	}

	private void setCallendar() {

		FTimeUtility ftu = new FTimeUtility(passEndMilestone);
		this.endDay = ftu.getDayFromMillis();
		this.endMonth = ftu.getMonthFromMillis();
		this.endYear = ftu.getYearFromMillis();

		endMilestone = new FTimeUtility().getUnixTimestamp(endDay, endMonth,
				endYear);
	}

	private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			endDay = dayOfMonth;
			endMonth = monthOfYear + 1;
			endYear = year;
			Toast.makeText(
					getBaseContext(),
					"Milestone ends at: " + endDay + "/" + endMonth + "/"
							+ endYear, Toast.LENGTH_LONG).show();
			endMilestone = new FTimeUtility().getUnixTimestamp(endDay,
					endMonth - 1, endYear);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
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
		TextView milestoneName = (TextView) findViewById(R.id.tv_edit_milestone_name);
		TextView milestoneDesc = (TextView) findViewById(R.id.tv_edit_milestone_description);
		TextView endmilestone = (TextView) findViewById(R.id.tv_edit_milestone_end);
		milestoneName.setTypeface(typeface);
		milestoneDesc.setTypeface(typeface);
		endmilestone.setTypeface(typeface);
	}

	private void initclickAbleComponent() {
		this.cbFinishMilestone = (CheckBox) findViewById(R.id.cb_end_milestone);
		cbFinishMilestone.setChecked(true);

		rlEndMilestone = (RelativeLayout) findViewById(R.id.rl_edit_milestone_end);
		rlEndMilestone.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_END);
			}
		});
	}

	private void initEditAbleComponents() {
		this.etMilestoneName = (EditText) findViewById(R.id.et_edit_milestone_name);
		this.etMilestoneName.setText(passMilestoneName);
		this.etMilestoneDesc = (EditText) findViewById(R.id.et_edit_milestone_description);
		this.etMilestoneDesc.setText(passMilestoneDesc);
	}

	private void getPassedParam() {
		this.passProjectID = new FPreferencesManager(this)
				.getTemporaryPassedPID();
		this.passMilestoneID = getIntent().getIntExtra(
				CollabtiveProfile.COLL_TAG_MILESTONE_MID, 0);
		this.passMilestoneDesc = getIntent().getStringExtra(
				CollabtiveProfile.COLL_TAG_MILESTONE_DESC);
		this.passMilestoneName = getIntent().getStringExtra(
				CollabtiveProfile.COLL_TAG_MILESTONE_NAME);
		this.passEndMilestone = getIntent().getLongExtra(
				CollabtiveProfile.COLL_TAG_MILESTONE_END, 0);
	}

	private void clearAllEditText() {
		etMilestoneName.setText(null);
		etMilestoneDesc.setText(null);
	}

	private void onEditMilestoneLoading() {
		pdEditMilestone.setMessage("Edit Milestone");
		pdEditMilestone.setCancelable(false);
		pdEditMilestone.show();
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

				if (cbFinishMilestone.isChecked())
					milestoneStatus = 0;
				System.out.println("Edit Milestone: milestoneStatus="+milestoneStatus);
				String milestoneName = etMilestoneName.getText().toString();
				String milestoneDesc = etMilestoneDesc.getText().toString();
				String end = endMilestone + "";
				try {
					collManager.getEditMilestoneJSONObject(
							passMilestoneID + "", milestoneName, milestoneDesc,
							end, milestoneStatus + "", sessionID);
					int statusCode = collManager.getEditMilestonesStatusCode();
					if (statusCode == 1) {
						resultText = "Milestone is edited successfully";
					} else {
						resultText = "Failed editing a Milesone";
					}
					editMilestoneHandler.sendMessage(editMilestoneHandler
							.obtainMessage());
					onUpdateExit();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		jsonThread.start();
	}

	Handler editMilestoneHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdEditMilestone.dismiss();
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

	private void onUpdateMilestone() {
		FFileManager ffm = new FFileManager();
		collManager.getMilestonesJSONObjects(passProjectID + "", sessionID);
		ffm.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_MILESTONES,
				collManager.getMilestonesJSONArray().toString());
	}

	private void onUpdateExit() {
		onUpdateMilestone();
		Intent startNewMilestone = new Intent(this,
				MilestonesListActivity.class);
		startActivity(startNewMilestone);
		EditMilestone.this.finish();
		jsonThreadDismiss();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			jsonThreadDismiss();
			finish();
			Intent returnMilestoneList = new Intent(this,
					MilestonesListActivity.class);
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
				onEditMilestoneLoading();
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

}
