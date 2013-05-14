package com.farissyariati.kuma.task;

import com.farissyariati.kuma.utility.FTimeUtility;

public class Tasks {

	private static final String timeFormat = "d/M/yyyy";

	public int taskID, projectID, tasklistID, taskStatus;
	public long taskStart, taskEnd;
	public String taskName, taskDesc;
	public String durationFormatter;

	public Tasks() {
	}

	public Tasks(int taskID, int projectID, int tasklistID, String taskName,
			String taskDesc, long taskStart, long taskEnd, int status) {
		this.taskID = taskID;
		this.projectID = projectID;
		this.tasklistID = tasklistID;
		this.taskName = taskName;
		this.taskDesc = taskDesc;
		this.taskStart = taskStart;
		this.taskEnd = taskEnd;
		this.taskStatus = status;
		this.durationFormatter = durationFormatter();
	}

	private String durationFormatter() {
		FTimeUtility ftu = new FTimeUtility();
		String sEndTime;
		//sStartTime = ftu.collabtiveDateFormat(taskStart, timeFormat);
		if (taskEnd == 0)
			sEndTime = "Never Due";
		else
			sEndTime = ftu.collabtiveDateFormat(taskEnd, timeFormat);
		return "End Task: " + sEndTime;
	}

}
