package com.farissyariati.kuma.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import com.farissyariati.kuma.utility.CollabtiveProfile;

public class ProjectsParser implements CollabtiveProfile {

	private final List<Projects> projectsList;
	private final HashMap<String, String> map;

	Context context;

	public ProjectsParser(Context context) {
		this.context = context;
		this.projectsList = new ArrayList<Projects>();
		this.map = new HashMap<String, String>();
	}

	public void parse(JSONArray projectsArray) {
		try {
			for (int i = 0; i < projectsArray.length(); i++) {
				JSONObject p = projectsArray.getJSONObject(i);
				final String projectName = p.getString(COLL_TAG_PROJECT_NAME);
				final String projectDesc = p.getString(COLL_TAG_PROJECT_DESC);
				final int projectID = p.getInt(COLL_TAG_PROJECT_ID);
				final long projectStart = p.getLong(COLL_TAG_PROJECT_START);
				final int projectBudget = (int) p.getDouble(COLL_TAG_PROJECT_BUDGET);
				final int projectStatus = (int)p.getInt(COLL_TAG_PROJECT_STATUS);
				// saved projectEnd as String. Handling, IF it's null
				final String sProjectEnd = p.getString(COLL_TAG_PROJECT_END);
				long projectEnd;

				if (sProjectEnd.equals(""))
					projectEnd = 0;
				else {
					projectEnd = Long.parseLong(sProjectEnd);
				}
				
				if(projectStatus == 1){
					Projects project = new Projects(projectID, projectName, projectStart, projectEnd, projectDesc,
							projectBudget, projectStatus);
					this.projectsList.add(project);
				}
				this.map.put(projectName, projectDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Projects> getList() {
		return this.projectsList;
	}
}
