package com.farissyariati.kuma.select.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import com.farissyariati.kuma.utility.CollabtiveProfile;

public class SelectUsersParser implements CollabtiveProfile {
	private final List<SelectUsers> selectUsersList;
	private final HashMap<String, String> map;

	Context context;

	public SelectUsersParser(Context context) {
		this.context = context;
		this.selectUsersList = new ArrayList<SelectUsers>();
		this.map = new HashMap<String, String>();
	}

	public void parse(JSONArray usersJSONArray) {
		System.out.println("JSONArray usersJSONArray: " + usersJSONArray);
		try {
			for (int i = 0; i < usersJSONArray.length(); i++) {
				JSONObject usr = usersJSONArray.getJSONObject(i);
				JSONObject userData = usr.getJSONObject(COLL_TAG_USER_USERDATA);
				int id = userData.getInt(COLL_TAG_USER_ID_2);
				String userName = userData.getString(COLL_TAG_USER_NAME);	
				String userEmail = userData.getString(COLL_TAG_USER_EMAIL);
				String phoneNumber = userData.getString(COLL_TAG_USER_PHONE_NUMBER_1);
				SelectUsers selectUsers = new SelectUsers(id, userName, userEmail, phoneNumber);
				this.selectUsersList.add(selectUsers);
				this.map.put(userName, userEmail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<SelectUsers> getList() {
		return this.selectUsersList;
	}
}
