package com.farissyariati.kuma.tasklist;

import com.farissyariati.kuma.utility.FTimeUtility;

public class TaskLists {
	private static final String timeFormat = "d/M/yyyy";

	public int taskListID;
	public int projectID;
	public int milestoneID;
	public String milestoneName;
	public String taskListName;
	public String taskListDesc;
	public long tasklistStart;
	public int status;

	public String startFormatter;

	public TaskLists() {
	}

	public TaskLists(int tlsID, int projectID, int mlstID, String milestoneName, String taskListName,
			String taskListDesc, long start, int status) {
		this.taskListID = tlsID;
		this.projectID = projectID;
		this.milestoneID = mlstID;
		this.milestoneName = milestoneName;
		this.taskListName = taskListName;
		this.taskListDesc = taskListDesc;
		this.tasklistStart = start;
		this.status = status;
		this.startFormatter = startFormatter();
	}

	private String startFormatter() {
		FTimeUtility ftu = new FTimeUtility();
		String sStartTime;
		sStartTime = ftu.collabtiveDateFormat(tasklistStart, timeFormat);
		return "Added at: " + sStartTime;
	}

}
