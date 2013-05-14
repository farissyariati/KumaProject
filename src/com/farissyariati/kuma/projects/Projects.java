package com.farissyariati.kuma.projects;

import com.farissyariati.kuma.utility.FTimeUtility;

public class Projects {
	private static final String timeFormat = "d/M/yyyy";

	public String projectName;
	public long startTime;
	public long endTime;
	public int projectID;
	public String durationFormat;
	public String projectDesc;
	public int projectBudget;
	public int projectStatus;
	
	public Projects() {

	}

	public Projects(int projectID, String projectName, long startTime,
			long endTime, String projectDesc, int projectBudget, int status) {
		this.projectID = projectID;
		this.projectName = projectName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.durationFormat = durationFormatter();
		this.projectDesc = projectDesc;
		this.projectBudget = projectBudget;
		this.projectStatus = status;
	}

	private String durationFormatter() {
		FTimeUtility ftu = new FTimeUtility();
		String sStartTime, sEndTime;
		sStartTime = ftu.collabtiveDateFormat(startTime, timeFormat);
		if (endTime == 0)
			sEndTime = "Never Due";
		else
			sEndTime = ftu.collabtiveDateFormat(endTime, timeFormat);
		
		String daysLeft = "";
		if(ftu.overdue(endTime*1000, System.currentTimeMillis()))
			daysLeft = "("+Math.abs(ftu.getDayDiff(System.currentTimeMillis(), endTime*1000)-1)+" days late)";
		else
			daysLeft = "("+ftu.getDayDiff(System.currentTimeMillis(), endTime*1000)+" days left)";
		return "Duration: " + sStartTime + " - " + sEndTime+"   "+daysLeft;
	}

}
