package com.farissyariati.kuma.milestones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.farissyariati.kuma.utility.CollabtiveProfile;

public class MilestonesParser implements CollabtiveProfile {
	private final List<Milestones> milestonesList;
	private final HashMap<String, String> map;

	Context context;

	public MilestonesParser(Context context) {
		this.context = context;
		this.milestonesList = new ArrayList<Milestones>();
		this.map = new HashMap<String, String>();
	}

	public void parse(JSONArray milestonesArray) {
		try {
			for (int i = 0; i < milestonesArray.length(); i++) {
				JSONObject m = milestonesArray.getJSONObject(i);
				final int mlstID = m.getInt(COLL_TAG_MILESTONE_ID);
				final String mlstName = m.getString(COLL_TAG_MILESTONE_NAME);
				final String mlstDesc = m.getString(COLL_TAG_MILESTONE_DESC);
				final long mlstStart = m.getLong(COLL_TAG_MILESTONE_START);
				final int mlstStatus = m.getInt(COLL_TAG_MILESTONE_STATUS);

				final String sMlstEnd = m.getString(COLL_TAG_MILESTONE_END);
				long mlstEnd;

				if (sMlstEnd.equals(""))
					mlstEnd = 0;
				else {
					mlstEnd = Long.parseLong(sMlstEnd);
				}
				Milestones milestone = new Milestones(mlstID, mlstName,
						mlstDesc, mlstStart, mlstEnd, mlstStatus);
				this.milestonesList.add(milestone);
				this.map.put(mlstName, mlstDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Milestones> getList(){
		return this.milestonesList;
	}
}
