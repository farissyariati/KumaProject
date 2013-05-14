package com.farissyariati.kuma.milestones;

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

public class MilestonesListActivity extends Activity {
	private JSONArray milestoneArray;
	private ListView lv;
	private List<Milestones> list;

	private ProgressDialog pdDeleteMilestone;
	private ProgressDialog pdDoneMilestone;
	private Thread deleteMilestoneThread;
	private Thread doneMilestoneThread;

	private String sessionID;
	private int passProjectID;
	private String deleteResult;
	private String closeResult;

	private int passMilestoneID;
	private String passMilestoneName;
	private String passMilestoneDesc;
	private long passMilestoneEnd;

	private CollabtiveNotificator notificator;
	private FTimeUtility timeUtility;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.milestone_list);
		initVar();
		initParseMilestoneData();
		setListView();
	}

	private void initVar() {
		this.notificator = new CollabtiveNotificator(this);
		this.timeUtility = new FTimeUtility();
		this.sessionID = new FPreferencesManager(this).getSessionID();
		this.passProjectID = new FPreferencesManager(this).getTemporaryPassedPID();
	}

	private void initParseMilestoneData() {
		MilestonesParser parser = new MilestonesParser(this);
		try {
			this.milestoneArray = new JSONArray(
					new FFileManager().getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_MILESTONES));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		parser.parse(milestoneArray);
		this.list = parser.getList();
	}

	private void setListView() {
		setContentView(R.layout.milestone_list);
		MilestonesArrayAdapter milestonesArrayAdapter = new MilestonesArrayAdapter(getApplicationContext(),
				R.layout.milestone_list_row, list);
		this.lv = (ListView) findViewById(R.id.milestonesListView);
		lv.setAdapter(milestonesArrayAdapter);
		setOnItemClickListener();
	}

	private void setOnItemClickListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				changeParamPassedData((int) id);
				StringBuilder sb = new StringBuilder();
				sb.append("Milestone Name: " + passMilestoneName + "\n");
				sb.append("Milestone Desc: " + passMilestoneDesc + "\n");
				sb.append("Milestone End at: " + timeUtility.collabtiveDateFormat(passMilestoneEnd, "d/M/yyy"));
				notificator.showAlert("Milestone Detail", sb.toString(), "OK");
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				changeParamPassedData((int) id);
				createMenuOnLongItemClicked(list.get((int) id).mlstID);
				return false;
			}
		});
	}

	private void deleteMilestone(int mlstID) {
		final String milestoneID = mlstID + "";
		pdDeleteMilestone = new ProgressDialog(this);
		pdDeleteMilestone.setMessage("Deleting Milestone From Server..");
		pdDeleteMilestone.setCancelable(false);
		pdDeleteMilestone.show();
		this.deleteMilestoneThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
				collManager.getDeleteMilestoneJSONObjects(milestoneID, sessionID);
				if (collManager.getDeleteMilestonesStatusCode() == 1) {
					collManager.getMilestonesJSONObjects(passProjectID + "", sessionID);
					FFileManager fileManager = new FFileManager();
					fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_MILESTONES, collManager
							.getMilestonesJSONArray().toString());

					deleteResult = "1 Milestone Deleted";
					deleteDialogHandler.sendMessage(deleteDialogHandler.obtainMessage());
				} else {
					deleteResult = "Milestone Deletion Failed";
					deleteDialogHandler.sendMessage(deleteDialogHandler.obtainMessage());
				}

			}
		});
		deleteMilestoneThread.start();
	}

	private void doneMilestone(int mid) {
		final String mlstID = mid + "";
		pdDoneMilestone = new ProgressDialog(this);
		pdDoneMilestone.setCancelable(false);
		pdDoneMilestone.setMessage("Closing Milesone and It's Tasks");
		pdDoneMilestone.show();

		doneMilestoneThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					CollabtiveManager collManager = new CollabtiveManager(getBaseContext());
					collManager.getCloseMilestoneJSONObject(mlstID, sessionID);
					if (collManager.getCloseMilestoneStatusCode() == 1) {
						collManager.getMilestonesJSONObjects(passProjectID + "", sessionID);
						FFileManager fileManager = new FFileManager();
						fileManager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_MILESTONES, collManager
								.getMilestonesJSONArray().toString());
						closeResult = "Milestone Closed";
						closeMilestoneHandler.sendMessage(Message.obtain(closeMilestoneHandler, 1));
					} else {
						closeResult = "Failed Closing Milestone";
						closeMilestoneHandler.sendMessage(Message.obtain(closeMilestoneHandler, 1));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		doneMilestoneThread.start();
	}

	Handler closeMilestoneHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					if (doneMilestoneThread != null)
						doneMilestoneThread.join();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 1:
				pdDoneMilestone.dismiss();
				initParseMilestoneData();
				setListView();
				joinThreads();
				Toast.makeText(getBaseContext(), closeResult, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	private void changeParamPassedData(int id) {
		this.passMilestoneID = list.get(id).mlstID;
		this.passMilestoneName = list.get(id).mlstName;
		this.passMilestoneDesc = list.get(id).mlstDesc;
		this.passMilestoneEnd = list.get(id).mlstEnd;
	}

	Handler deleteDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pdDeleteMilestone.dismiss();
			Toast.makeText(getBaseContext(), deleteResult, Toast.LENGTH_SHORT).show();
			joinThreads();
			initParseMilestoneData();
			setListView();
		}
	};

	private void joinThreads() {
		try {
			if (deleteMilestoneThread != null)
				deleteMilestoneThread.join();
			if (doneMilestoneThread != null)
				doneMilestoneThread.join();
			System.out.println("All thread join");
		} catch (Exception e) {
			System.out.println("Error Joining Thread");
			e.printStackTrace();
		}
	}

	private void goToEditMilestone() {
		Intent editMilestone = new Intent(this, EditMilestone.class);
		editMilestone.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_MID, passMilestoneID);
		editMilestone.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_NAME, passMilestoneName);
		editMilestone.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_DESC, passMilestoneDesc);
		editMilestone.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_END, passMilestoneEnd);
		startActivity(editMilestone);
		MilestonesListActivity.this.finish();
	}

	@SuppressLint("HandlerLeak")
	private void createMenuOnLongItemClicked(final int milestoneID) {
		final CharSequence charSequence[] = {"Mark as Done", "Edit Milestone", "Delete Milestone" };
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Milestone Menu");
		adb.setItems(charSequence, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					doneMilestone(milestoneID);
					break;
				case 1:
					goToEditMilestone();
					break;
				case 2:
					deleteMilestone(milestoneID);
					break;
				}
			}
		});
		adb.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.milestone_list_menu, menu);
		MenuItem miAddProject = menu.findItem(R.id.menu_add_milestone);
		miAddProject.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent addMilestone = new Intent(getBaseContext(), AddMilestone.class);
				addMilestone.putExtra(CollabtiveProfile.COLL_TAG_MILESTONE_POSTED_ID, passProjectID);
				startActivity(addMilestone);
				finish();
				return false;
			}
		});
		return true;
	}
}
