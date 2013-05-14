package com.farissyariati.kuma.tasklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.farissyariati.kuma.utility.CollabtiveProfile;

public class TaskListsParser implements CollabtiveProfile {
	private final List<TaskLists> tasklistsList;
	private final HashMap<String, String> map;

	Context context;

	public TaskListsParser(Context context) {
		this.context = context;
		this.tasklistsList = new ArrayList<TaskLists>();
		this.map = new HashMap<String, String>();
	}

	public void parse(JSONArray tasklistsArray) {
		try {
			for (int i = 0; i < tasklistsArray.length(); i++) {
				JSONObject t = tasklistsArray.getJSONObject(i);

				final int tlID = t.getInt(COLL_TAG_TASKLIST_ID);
				final int projectID = t.getInt(COLL_TAG_TASKLIST_PID);
				final int mlstID = t.getInt(COLL_TAG_TASKLIST_MID);
				final String milestoneName = t
						.getString(COLL_TAG_TASKLIST_MILESTONE_NAME);
				final String tasklistName = t.getString(COLL_TAG_TASKLIST_NAME);
				final String taskListDesc = t.getString(COLL_TAG_TASKLIST_DESC);
				final long tasklistStart = t.getLong(COLL_TAG_TASKLIST_START);
				final int tasklistStatus = t.getInt(COLL_TAG_TASKLIST_STATUS);

				TaskLists tasklist = new TaskLists(tlID, projectID, mlstID,
						milestoneName, tasklistName, taskListDesc,
						tasklistStart, tasklistStatus);
				this.tasklistsList.add(tasklist);
				this.map.put(tasklistName, taskListDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<TaskLists> getList() {
		return this.tasklistsList;
	}
}
