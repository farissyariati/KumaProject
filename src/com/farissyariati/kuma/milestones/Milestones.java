package com.farissyariati.kuma.milestones;

import com.farissyariati.kuma.utility.FTimeUtility;

public class Milestones {
	private static final String timeFormat = "d/M/yyyy";
	public int mlstID, mlstStatus;
	public String mlstName, mlstDesc;
	public long mlstStart, mlstEnd;
	public String durationFormat;

	public Milestones() {

	}

	public Milestones(int mlstID, String mlstName, String mlstDesc,
			long mlstStart, long mlstEnd, int mlstStatus) {
			this.mlstID = mlstID;
			this.mlstName = mlstName;
			this.mlstDesc = mlstDesc;
			this.mlstStart = mlstStart;
			this.mlstEnd = mlstEnd;
			this.mlstStatus = mlstStatus;
			this.durationFormat = durationFormatter();
	}

	private String durationFormatter() {
		FTimeUtility ftu = new FTimeUtility();
		String sEndTime;
		//sStartTime = ftu.collabtiveDateFormat(mlstStart, timeFormat);
		if (mlstEnd == 0)
			sEndTime = "Never Due";
		else
			sEndTime = ftu.collabtiveDateFormat(mlstEnd, timeFormat);
		return "End Milestone: " + sEndTime;
	}
}
