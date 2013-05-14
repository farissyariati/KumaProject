package com.farissyariati.kuma.filelist;

public class Files {
	public int id, projectID, milestoneID, userID, folder;
	public String fileName, fileDesc, tags, fileURL, type, title;
	public long addedDate;

	public Files(int id, String fileName, String fileDesc, int projectID,
			int milestoneID, int userID, String tags, long addedDate,
			String fileURL, String type, String title, int folder) {
		this.id = id;
		this.projectID = projectID;
		this.milestoneID = milestoneID;
		this.userID = userID;
		this.folder = folder;
		this.fileName = fileName;
		this.fileDesc = fileDesc;
		this.tags = tags;
		this.fileURL = fileURL;
		this.type = type;
		this.title = title;
		this.addedDate = addedDate;

	}

	public Files() {

	}

}
