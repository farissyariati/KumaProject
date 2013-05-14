package com.farissyariati.kuma.filelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import com.farissyariati.kuma.utility.CollabtiveProfile;

public class FilesParser implements CollabtiveProfile {

	private final List<Files> filesList;
	private final HashMap<String, String> map;

	Context context;

	public FilesParser(Context context) {
		this.context = context;
		this.filesList = new ArrayList<Files>();
		this.map = new HashMap<String, String>();
	}

	public void parse(JSONArray filesArray) {
		try {
			for (int i = 0; i < filesArray.length(); i++) {
				JSONObject f = filesArray.getJSONObject(i);
				final int fileID = f.getInt(COLL_TAG_FILES_ID);
				final String fileName = f.getString(COLL_TAG_FILES_NAME);
				final String fileDesc = f.getString(COLL_TAG_FILES_DESC);
				final int projectID = f.getInt(COLL_TAG_FILES_PROJECT_ID);
				final int milestoneID = f.getInt(COLL_TAG_FILES_MILESTONE_ID);
				final int userID = f.getInt(COLL_TAG_FILES_USER_ID);
				final String tags = f.getString(COLL_TAG_FILES_TAGS);
				final String sAddedDate = f.getString(COLL_TAG_FILES_ADDED);
				final String fileURL = f.getString(COLL_TAG_FILES_FILE_URL);
				final String type = f.getString(COLL_TAG_FILES_TYPE);
				final String fileTitle = f.getString(COLL_TAG_FILES_TITLE);
				final int folder = f.getInt(COLL_TAG_FILES_FOLDER);

				long addedDate;
				if (sAddedDate.equals(""))
					addedDate = 0;
				else {
					addedDate = Long.parseLong(sAddedDate);
				}

				Files files = new Files(fileID, fileName, fileDesc, projectID,
						milestoneID, userID, tags, addedDate, fileURL, type,
						fileTitle, folder);
				this.filesList.add(files);
				this.map.put(fileName, fileDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Files> getList() {
		return this.filesList;
	}
}
