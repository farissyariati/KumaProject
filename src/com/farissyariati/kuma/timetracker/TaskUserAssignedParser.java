package com.farissyariati.kuma.timetracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.farissyariati.kuma.utility.CollabtiveProfile;

public class TaskUserAssignedParser implements CollabtiveProfile{
	
	private final List<TaskUserAssigned> taskUserAssignedList;
	private final HashMap<String, String> map;
	Context context;
	
	public TaskUserAssignedParser(Context context){
		this.context = context;
		this.taskUserAssignedList = new ArrayList<TaskUserAssigned>();
		this.map = new HashMap<String, String>();
	}
	
	public void parse(JSONArray taskUserAssignedArray){
		try{
			for(int i = 0; i< taskUserAssignedArray.length(); i++){
				JSONObject tua = taskUserAssignedArray.getJSONObject(i);
				JSONObject taskAssigned = tua.getJSONObject("taskAssigned");
				final int taskID = taskAssigned.getInt("ID");
				final int userID = taskAssigned.getInt("user");
				TaskUserAssigned taskUserAssigned = new TaskUserAssigned(taskID, userID);
				this.taskUserAssignedList.add(taskUserAssigned);
				this.map.put(taskID+"", userID+"");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<TaskUserAssigned> getList(){
		return this.taskUserAssignedList;
	}
}
