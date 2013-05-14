package com.farissyariati.kuma.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.farissyariati.kuma.utility.CollabtiveProfile;

public class TasksParser implements CollabtiveProfile {
	private final List<Tasks> tasksList;
	private final HashMap<String, String> map;

	Context context;

	public TasksParser(Context context) {
		this.context = context;
		this.tasksList = new ArrayList<Tasks>();
		this.map = new HashMap<String, String>();
	}

	public void parse(JSONArray tasksArray) {
		try {
			for (int i = 0; i < tasksArray.length(); i++) {
				JSONObject t = tasksArray.getJSONObject(i);
				final int taskID = t.getInt(COLL_TAG_TASK_ID);
				final int projectID = t.getInt(COLL_TAG_TASK_PID);
				final int tlistID = t.getInt(COLL_TAG_TASK_TLID);
				final String taskName = t.getString(COLL_TAG_TASK_NAME);
				final String taskDesc = t.getString(COLL_TAG_TASK_DESC);
				final long taskStart = t.getLong(COLL_TAG_TASK_START);
				final int taskStatus = t.getInt(COLL_TAG_STATUS);
				// saved projectEnd as String. Handling, IF it's null
				final String sStartEnd = t.getString(COLL_TAG_TASK_END);
				long taskEnd;

				if (sStartEnd.equals(""))
					taskEnd = 0;
				else {
					taskEnd = Long.parseLong(sStartEnd);
				}

				if (taskStatus == 1) {
					Tasks task = new Tasks(taskID, projectID, tlistID, taskName, taskDesc, taskStart, taskEnd,
							taskStatus);
					this.tasksList.add(task);
				}
				this.map.put(taskName, taskDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parseAll(JSONArray tasksArray) {
		try {
			for (int i = 0; i < tasksArray.length(); i++) {
				JSONObject t = tasksArray.getJSONObject(i);
				final int taskID = t.getInt(COLL_TAG_TASK_ID);
				final int projectID = t.getInt(COLL_TAG_TASK_PID);
				final int tlistID = t.getInt(COLL_TAG_TASK_TLID);
				final String taskName = t.getString(COLL_TAG_TASK_NAME);
				final String taskDesc = t.getString(COLL_TAG_TASK_DESC);
				final long taskStart = t.getLong(COLL_TAG_TASK_START);
				final int taskStatus = t.getInt(COLL_TAG_STATUS);
				// saved projectEnd as String. Handling, IF it's null
				final String sStartEnd = t.getString(COLL_TAG_TASK_END);
				long taskEnd;

				if (sStartEnd.equals(""))
					taskEnd = 0;
				else {
					taskEnd = Long.parseLong(sStartEnd);
				}

				Tasks task = new Tasks(taskID, projectID, tlistID, taskName, taskDesc, taskStart, taskEnd, taskStatus);
				this.tasksList.add(task);

				this.map.put(taskName, taskDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Tasks> getList() {
		return this.tasksList;
	}
}
