package com.farissyariati.kuma.timetracker;

import java.util.List;

import org.json.JSONArray;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.select.users.SelectUsers;
import com.farissyariati.kuma.select.users.SelectUsersParser;
import com.farissyariati.kuma.task.Tasks;
import com.farissyariati.kuma.task.TasksParser;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FTimeUtility;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

public class TimeTrackerActivity extends Activity {
	private List<Tasks> list;
	private List<TaskUserAssigned> taskUsersList;
	private List<SelectUsers> usersList;

	private int progressPercentage;
	private JSONArray taskJSONArray;
	private JSONArray taskUsersArray;
	private JSONArray usersArray;
	private Thread progressAnimationThread;
	private Thread progressCounterThread;
	private TextView tvProgressPercentage, tvProjectDetail;
	private ImageView ivProgressAnimation;

	private boolean counterDone, progressDone;

	private int counter;
	private String passProjectName, passProjectDesc;
	private long passProjectEnd;

	private FTimeUtility ftu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_detail_layout);
		initComponents();
		initParseTaskData();
		this.progressPercentage = getProjectProgressPercentage();
		tvProgressPercentage.setText(progressPercentage + "%");
		progressAnimate(progressPercentage);
		progressCounter(progressPercentage);
		initPassVariable();
		constructStringDetail();
	}

	private void initComponents() {
		this.tvProgressPercentage = (TextView) findViewById(R.id.tv_project_progress);
		this.tvProjectDetail = (TextView) findViewById(R.id.tv_project_detail);
		this.ivProgressAnimation = (ImageView) findViewById(R.id.iv_project_progress_animation);
		ivProgressAnimation.setImageResource(R.drawable.percent_zero);
	}

	private void initPassVariable() {
		this.passProjectName = getIntent().getStringExtra(CollabtiveProfile.KUMA_PASS_PROJECT_NAME);
		this.passProjectDesc = getIntent().getStringExtra(CollabtiveProfile.KUMA_PASS_PROJECT_DESC);
		this.passProjectEnd = getIntent().getLongExtra(CollabtiveProfile.KUMA_PASS_PROJECT_END, 0);
	}

	private void initParseTaskData() {
		TasksParser parser = new TasksParser(this);
		try {
			taskJSONArray = new JSONArray(new FFileManager().getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_TASK_ALL));
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser.parseAll(taskJSONArray);
		this.list = parser.getList();

		TaskUserAssignedParser tuaParser = new TaskUserAssignedParser(this);
		try {
			taskUsersArray = new JSONArray(
					new FFileManager().getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_TASK_ASSIGNED));
			System.out.println("Task User Assigned: " + taskUsersArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tuaParser.parse(taskUsersArray);
		this.taskUsersList = tuaParser.getList();

		SelectUsersParser userParser = new SelectUsersParser(this);
		try {
			usersArray = new JSONArray(new FFileManager().getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_USERS));
			System.out.println("Task User Assigned, User Info: "+usersArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		userParser.parse(usersArray);
		this.usersList =  userParser.getList();
	}

	private int getProjectProgressPercentage() {
		int result = 0;
		int doneTask = 0;
		try {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).taskStatus == 0) {
					doneTask++;
				}
			}
			System.out.println("TASK ALL: Done Task Count 1: " + doneTask);
			double percent = ((double) doneTask / (double) list.size()) * 100;
			if (percent - (int) percent >= 0.5)
				result = (int) percent + 1;
			else
				result = (int) percent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String getUsersFormatted(int taskID){
		String usersFormatted = "";
		StringBuilder sb = new StringBuilder();
		try{
			
			for(int i = 0; i < taskUsersList.size(); i++){
				if(taskUsersList.get(i).taskID == taskID){
					sb.append(taskUsersList.get(i).userID+"-");
				}
			}
			String userIDFormatted = sb.toString();
			String[] sIDs = userIDFormatted.split("-");
			
			sb = new StringBuilder();
			for(int i = 0; i < sIDs.length; i++){
				for(int j = 0;j < usersList.size();j++){
					if(Integer.parseInt(sIDs[i]) == usersList.get(j).id)
						sb.append(usersList.get(j).username+" ");
				}
			}
			usersFormatted = sb.toString();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return usersFormatted;
	}

	private void progressAnimate(final int projectProgress) {
		this.progressDone = false;
		this.progressAnimationThread = new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					progressAnimationThread.sleep(500);
					for (int i = 0; i < projectProgress / 10; i++) {
						animationHandler.sendMessage(Message.obtain(animationHandler, i + 1));
						progressAnimationThread.sleep(200);
					}
					progressDone = true;
					if (counterDone == true && progressDone == true) {
						animationHandler.sendMessage(Message.obtain(animationHandler, 12));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		progressAnimationThread.start();
	}

	private void progressCounter(final int projectProgress) {
		this.counterDone = false;
		this.counter = 0;
		this.progressCounterThread = new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					for (int i = 0; i < projectProgress; i++) {
						progressCounterThread.sleep(50);
						counter++;
						animationHandler.sendMessage(Message.obtain(animationHandler, 11));
					}
					counterDone = true;
					if (counterDone == true && progressDone == true) {
						animationHandler.sendMessage(Message.obtain(animationHandler, 12));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		progressCounterThread.start();
	}

	Handler animationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ivProgressAnimation.setImageResource(R.drawable.percent_zero);
				break;
			case 1:
				ivProgressAnimation.setImageResource(R.drawable.percent_ten);
				break;
			case 2:
				ivProgressAnimation.setImageResource(R.drawable.percent_twenty);
				break;
			case 3:
				ivProgressAnimation.setImageResource(R.drawable.percent_thirty);
				break;
			case 4:
				ivProgressAnimation.setImageResource(R.drawable.percent_fourty);
				break;
			case 5:
				ivProgressAnimation.setImageResource(R.drawable.percent_fifty);
				break;
			case 6:
				ivProgressAnimation.setImageResource(R.drawable.percent_sixty);
				break;
			case 7:
				ivProgressAnimation.setImageResource(R.drawable.percent_seventy);
				break;
			case 8:
				ivProgressAnimation.setImageResource(R.drawable.percent_eighty);
				break;
			case 9:
				ivProgressAnimation.setImageResource(R.drawable.percent_ninety);
				break;
			case 10:
				ivProgressAnimation.setImageResource(R.drawable.percent_hundred);
				break;
			case 11:
				tvProgressPercentage.setText(counter + "%");
				break;
			case 12:
				try {
					if (progressAnimationThread != null) {
						progressAnimationThread.join();
						System.out.println("Animation: progressAnimationThread Join");
					}

					if (progressCounterThread != null) {
						progressCounterThread.join();
						System.out.println("Animation: progressCounterThread Join");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	};

	private void constructStringDetail() {
		StringBuilder sb = new StringBuilder();
		ftu = new FTimeUtility(passProjectEnd);
		sb.append("<Project Detail>\n\n");
		sb.append("Name: " + passProjectName + "\n");
		sb.append("Description: " + passProjectDesc + "\n");
		sb.append("End Date: " + ftu.getDay() + "/" + (ftu.getMonth() + 1) + "/" + ftu.getYear());
		if (ftu.overdue(passProjectEnd * 1000, System.currentTimeMillis()))
			sb.append(" (Your project is "
					+ Math.abs(ftu.getDayDiff(System.currentTimeMillis(), passProjectEnd * 1000) - 1) + " days late)");
		else
			sb.append(" (You still have " + Math.abs(ftu.getDayDiff(System.currentTimeMillis(), passProjectEnd * 1000))
					+ " days left to finish this project)");

		sb.append("\n\n\n<Unfinished Project Task>\n\n");

		int counter = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).taskStatus == 1) {
				counter++;
				ftu = new FTimeUtility(list.get(i).taskEnd);
				sb.append(counter + ". " + list.get(i).taskName +" by: "+getUsersFormatted(list.get(i).taskID)+"\n");
				sb.append("    Description: " + list.get(i).taskDesc + "\n");
				sb.append("    Time Limit: " + ftu.getDay() + "/" + (ftu.getMonth() + 1) + "/" + ftu.getYear());
				if (ftu.overdue(list.get(i).taskEnd * 1000, System.currentTimeMillis())) {
					sb.append(" (this task is "
							+ Math.abs(ftu.getDayDiff(System.currentTimeMillis(), list.get(i).taskEnd * 1000) - 1)
							+ " days late)" + "\n\n");
				} else
					sb.append("\n\n");
			}
		}

		if (counter == 0)
			sb.append("   No active task\n\n");

		sb.append("\n<Finished Project Task>\n\n");
		counter = 1;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).taskStatus == 0) {
				// ftu = new FTimeUtility(list.get(i).taskEnd);
				String description = list.get(i).taskDesc;
				if (description.equals(""))
					description = "No description available";
				sb.append(counter + ". " + list.get(i).taskName + " by: "+getUsersFormatted(list.get(i).taskID)+"\n");
				sb.append("    Description: " + description + "\n\n");
				// sb.append("    Time Limit: "+ftu.getDay()+"/"+(ftu.getMonth()+1)+"/"+ftu.getYear()+"\n\n");
				counter++;
			}
		}

		String content = sb.toString().replace("<br />", "");
		tvProjectDetail.setText(content);
	}

}
