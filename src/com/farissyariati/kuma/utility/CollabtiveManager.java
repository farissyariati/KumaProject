package com.farissyariati.kuma.utility;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class CollabtiveManager implements CollabtiveProfile {

	private String COLLABTIVE_URL;
	private String POSTED_LOGIN_URL;
	private JSONParser jsonParser;

	public JSONObject loginJSONObjects;
	public JSONObject logoutJSONObjects;

	public JSONObject usersJSONObjects;
	public JSONObject assignProjectJSONObjects;

	public JSONObject projectsJSONObjects;
	public JSONObject addProjectResult;
	public JSONObject latestProjectJSONObjects;
	public JSONObject deleteProjectJSONObjects;
	public JSONObject editProjectJSONObjects;
	public JSONObject closeProjectJSONObjects;

	public JSONObject milestoneJSONObjects;
	public JSONObject addMilestoneJSONObjects;
	public JSONObject deleteMilestoneJSONObjects;
	public JSONObject editMilestoneJSONObjects;
	public JSONObject closeMilestoneJSONObjects;

	public JSONObject tasklistJSONObject;
	public JSONObject addTasklistJSONObject;
	public JSONObject deleteTasklistJSONObject;
	public JSONObject editTasklistJSONObject;

	public JSONObject taskJSONObjects;
	public JSONObject taskAllJSONObjects;
	public JSONObject addTaskJSONObjects;
	public JSONObject deleteTaskJSONObjects;
	public JSONObject editTaskJSONObjects;
	public JSONObject closeTaskJSONObjects;
	public JSONObject userAssignedTaskJSONObjects;

	public JSONObject fileJSONObjects;

	private boolean authenticated = false;

	// public CollabtiveManager(String collabtivePath) {
	// this.COLLABTIVE_URL = collabtivePath;
	// this.jsonParser = new JSONParser();
	//
	// }
	public CollabtiveManager(Context context) {
		this.COLLABTIVE_URL = new FPreferencesManager(context).getCollabtiveWebsite();
		System.out.println("Collabtive Website: " + COLLABTIVE_URL);
		this.jsonParser = new JSONParser();
	}

	/* LOGIN ACTIVITY */
	public boolean authenticate(String username, String password) {
		this.POSTED_LOGIN_URL = this.COLLABTIVE_URL + LOGIN_URL;

		try {
			this.loginJSONObjects = getCollabtiveLoginJSONObjects(username, password);
			JSONObject response = loginJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			int statusCode = Integer.parseInt(status.getString(COLL_TAG_STATUS_CODE));
			if (statusCode == 1)
				this.authenticated = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authenticated;
	}

	public String getSessionID() {
		String sessionID = "00xxx";
		if (this.authenticated == true) {
			try {
				JSONObject response = loginJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
				JSONObject login = response.getJSONObject(COLL_TAG_LOGIN);
				sessionID = login.getString(COLL_TAG_SESSION_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sessionID;
	}

	public int getUserID() {
		int userID = 0;
		if (this.authenticated == true) {
			try {
				JSONObject response = loginJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
				JSONObject login = response.getJSONObject(COLL_TAG_LOGIN);
				userID = login.getInt(COLL_TAG_USER_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userID;
	}

	private JSONObject getCollabtiveLoginJSONObjects(String username, String password) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(PARAM_USERNAME, username));
		nameValuePair.add(new BasicNameValuePair(PARAM_PASSWORD, password));
		JSONObject loginJSONObjects = null;
		try {
			loginJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_LOGIN_URL);
			System.out.println("Login JSON Object: " + loginJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginJSONObjects;
	}

	/* END OF LOGIN ACTIVITY */

	/* LOGOUT USER */
	public void getLogoutJSONObject(String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String POSTED_LOGOUT_USER = this.COLLABTIVE_URL + LOGOUT_URL;
		try {
			this.logoutJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_LOGOUT_USER);
			System.out.println("Logout JSON Object: " + logoutJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getLogoutStatusCode() {
		int result = 0;
		try {
			JSONObject response = logoutJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
			System.out.println("Logout Status Code: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF LOGIN USER */

	/* PROJECTS-GET ACTIVITY */

	public void getProjectsJSONObject(String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String POSTED_PROJECTS_GET = this.COLLABTIVE_URL + PROJECTS_GET_URL;
		try {
			this.projectsJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_PROJECTS_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public JSONArray getProjectsJSONArray() {
		JSONArray projects = null;
		try {
			JSONObject response = projectsJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			projects = response.getJSONArray(COLL_ARRAY_TAG_PROJECTS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projects;
	}

	public int getProjectsStatusCode() {
		int result = 0;
		try {
			JSONObject response = projectsJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getProjectCount() {
		int result = 0;
		try {
			JSONObject response = projectsJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			result = response.getInt(COLL_TAG_PROJECT_COUNT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String parseProjectsArray(String sessionID) {
		String result = "";
		try {
			// JSONArray projectArray = getProjectsJSONArray(sessionID);
			JSONArray projectArray = getProjectsJSONArray();
			for (int i = 0; i < projectArray.length(); i++) {
				JSONObject p = projectArray.getJSONObject(i);
				String name = p.getString(COLL_TAG_PROJECT_NAME);
				String start = p.getString(COLL_TAG_PROJECT_START);
				String end = p.getString(COLL_TAG_PROJECT_END);

				result += "Data ke " + (i + 1) + "\nNama: " + name + "\nStart: " + start + "\nEnd: " + end + "\n\n";
				System.out.println("Result: " + result);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return result;
	}

	/* END OF PROJECTS GET */

	/* PROJECT-ADD */
	// end in long
	// budget in int
	// assignme in int
	public void getAddProjectResult(String sessionID, String name, String desc, String end, String budget,
			String assignMe) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_NAME, name));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_DESC, desc));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_END, end));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_BUDGET, budget));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_ASSIGNME, assignMe));
		String POSTED_PROJECT_ADD_URL = this.COLLABTIVE_URL + PROJECTS_ADD_URL;

		try {
			this.addProjectResult = jsonParser.postJSONFromUrl(nameValuePair, POSTED_PROJECT_ADD_URL);
			System.out.println("Collabtive Manager: " + addProjectResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getAddProjectStatusCode() {
		int result = 0;
		try {
			JSONObject response = addProjectResult.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END PROJECT ADD */

	/* PROJECT EDIT */
	public void getEditProjectJSONObject(String pid, String sessionID, String projectName, String projectDesc,
			String endProject, String budget) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_ID_PID, pid));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_NAME, projectName));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_DESC, projectDesc));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_END, endProject));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_BUDGET, budget));

		String POSTED_EDIT_PROJECT_URL = this.COLLABTIVE_URL + PROJECTS_EDIT_URL;
		try {
			this.editProjectJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_EDIT_PROJECT_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getEditProjectStatusCode() {
		int result = 0;
		try {
			JSONObject response = editProjectJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF PROJECT EDIT */

	/* USERS GET */
	public void getUsersJSONObject() {
		String USERS_URL_ONGET = this.COLLABTIVE_URL + USERS_URL;
		try {
			this.usersJSONObjects = jsonParser.getJSONFromUrl(USERS_URL_ONGET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray getUsersJSONArray() {
		JSONArray usersData = null;
		try {
			usersData = this.usersJSONObjects.getJSONArray(COLL_ARRAY_TAG_USERS);
		} catch (Exception e) {

		}
		return usersData;
	}

	public void getLatestProjectJSONObjects() {
		String LATEST_PROJECT_URL_GET = this.COLLABTIVE_URL + LATEST_PROJECT_URL;
		try {
			this.latestProjectJSONObjects = jsonParser.getJSONFromUrl(LATEST_PROJECT_URL_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getLatestProjectID() {
		int latestID = 0;
		try {
			JSONArray projects = latestProjectJSONObjects.getJSONArray(COLL_TAG_LATEST_PROJECTS);
			JSONObject prj = projects.getJSONObject(0);
			JSONObject projectDetail = prj.getJSONObject(COLL_TAG_LATEST_PROJECT_DETAIL);
			latestID = projectDetail.getInt(COLL_TAG_LATEST_PROJECT_DETAIL_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return latestID;
	}

	/* END USERS GET */

	/* ASSIGN USER TO PROJECT */
	public void getAssignProjectJSONObjects(String userID, String sessionID, String projectID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_ASSIGN_USERID, userID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_ASSIGN_PROJECTID, projectID));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String ASSIGN_USER_TO_PROJECT_URL = this.COLLABTIVE_URL + ASSIGN_PROJECT_URL;
		try {
			this.assignProjectJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, ASSIGN_USER_TO_PROJECT_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* END ASSIGN USER TO PROJECT */

	/* DELETE PROJECT */
	public void getDeleteProjectJSONObject(String projectID, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_ID_PID, projectID));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String COMPLETE_DELETE_PROJECT_URL = this.COLLABTIVE_URL + PROJECTS_DELETE_URL;
		try {
			this.deleteProjectJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, COMPLETE_DELETE_PROJECT_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDeleteProjectsStatusCode() {
		int result = 0;
		try {
			JSONObject response = deleteProjectJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END DELETE PROJECT */
	
	/*PROJECT CLOSE*/
	public void getCloseProjectJSONObject(String pid, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_ID_PID, pid));
		String POSTED_PROJECTS_CLOSE = this.COLLABTIVE_URL + PROJECTS_CLOSE_URL;
		System.out.println("PROJECT CLOSE: WEB: "+POSTED_PROJECTS_CLOSE);
		try {
			this.closeProjectJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_PROJECTS_CLOSE);
			System.out.println("PROJECT CLOSED: "+closeProjectJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getCloseProjectsStatusCode() {
		int result = 0;
		try {
			JSONObject response = closeProjectJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
			System.out.println("PROJECT CLOSED: "+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/*END OF PROJECT CLOSE*/

	/*---------------MILESTONE PROCEDURE AND FUNCTION ----------------------------------*/
	/* MILESTONE GET */

	public void getMilestonesJSONObjects(String pid, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_POSTED_ID, pid));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String POSTED_MILESTONES_GET = this.COLLABTIVE_URL + MILESTONE_GET_URL;
		try {
			this.milestoneJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_MILESTONES_GET);
			System.out.println("Milestone JSON Object: " + milestoneJSONObjects);
		} catch (Exception e) {
			System.out.println("Error Getting Milestone JSONObjects");
			e.printStackTrace();
		}

	}

	public long getSpecifiedMilestoneEndTime(String mlstID, String sessionID) {
		long result = 0;
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_MID, mlstID));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String POSTED_MILESTONE_GET = this.COLLABTIVE_URL + MILESTONE_GET_URL;
		try {
			JSONObject milestsoneJSONObject = jsonParser.postJSONFromUrl(nameValuePair, POSTED_MILESTONE_GET);
			JSONObject response = milestsoneJSONObject.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject milestone = response.getJSONObject("milestone");
			result = milestone.getLong(COLL_TAG_MILESTONE_END);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getMilestonesStatusCode() {
		int result = 0;
		try {
			JSONObject response = milestoneJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray getMilestonesJSONArray() {
		JSONArray projects = null;
		try {
			JSONObject response = milestoneJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			projects = response.getJSONArray(COLL_ARRAY_TAG_MILESTONES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projects;
	}

	/* END OF MILESTONE GET */

	/* ADD MILESTONE */
	public void getAddMilestoneJSONObjects(String pid, String sessionID, String mlstName, String mlstDesc, String end,
			String status) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_POSTED_ID, pid));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_NAME, mlstName));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_DESC, mlstDesc));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_END, end));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_STATUS, status));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));

		final String POSTED_MILESTONE_ADD_URL = this.COLLABTIVE_URL + MILESTONE_ADD_URL;
		try {
			this.addMilestoneJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_MILESTONE_ADD_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getAddMilestonesStatusCode() {
		int result = 0;
		try {
			JSONObject response = addMilestoneJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF ADD MILESTONE */

	/* DELETE MILESTONE */
	public void getDeleteMilestoneJSONObjects(String mlstID, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_MID, mlstID));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));

		final String POSTED_DELETE_MILESTONE_URL = this.COLLABTIVE_URL + MILESTONE_DELETE_URL;
		try {
			this.deleteMilestoneJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_DELETE_MILESTONE_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDeleteMilestonesStatusCode() {
		int result = 0;
		try {
			JSONObject response = deleteMilestoneJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF DELETE MILESTONE */

	/* EDIT MILESTONE */
	public void getEditMilestoneJSONObject(String mlstID, String mlstName, String mlstDesc, String mlstEnd,
			String mlstStatus, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_MID, mlstID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_NAME, mlstName));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_DESC, mlstDesc));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_END, mlstEnd));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_STATUS, mlstStatus));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));

		String POSTED_MILESTONE_EDIT_URL = this.COLLABTIVE_URL + MILESTONE_EDIT_URL;
		try {
			this.editMilestoneJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_MILESTONE_EDIT_URL);
			System.out.println("editMilestoneJSONObject: " + editMilestoneJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getEditMilestonesStatusCode() {
		int result = 0;
		try {
			JSONObject response = editMilestoneJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF EDIT MILESTONE */
	
	/*MILESTONE CLOSE*/
	public void getCloseMilestoneJSONObject(String mid, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_MILESTONE_MID, mid));
		String POSTED_MILESTONE_CLOSE = this.COLLABTIVE_URL + PROJECTS_CLOSE_URL;
		System.out.println("PROJECT CLOSE: WEB: "+POSTED_MILESTONE_CLOSE);
		try {
			this.closeMilestoneJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_MILESTONE_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getCloseMilestoneStatusCode() {
		int result = 0;
		try {
			JSONObject response = closeMilestoneJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
			System.out.println("PROJECT CLOSED: "+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/*END OF MILESTONE CLOSE*/

	/*---------------TASKLIST PROCEDURE AND FUNCTION ----------------------------------*/
	/* TASKLIST GET */
	public void getTasklistsJSONObjects(String pid, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_POSTED_ID, pid));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String POSTED_TASKLISTS_GET = this.COLLABTIVE_URL + TASKLIST_GET_URL;
		System.out.println("Tasklist, pid: " + pid + ", sessionID: " + sessionID);
		System.out.println("Tasklist POSTE_TASKLIST_GET: " + POSTED_TASKLISTS_GET);
		try {
			this.tasklistJSONObject = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASKLISTS_GET);
			System.out.println("Tasklist: " + tasklistJSONObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getTasklistStatusCode() {
		int result = 0;
		try {
			JSONObject response = tasklistJSONObject.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray getTasklistsJSONArray() {
		JSONArray tasklists = null;
		try {
			JSONObject response = tasklistJSONObject.getJSONObject(COLL_TAG_RESPONSE);
			tasklists = response.getJSONArray(COLL_ARRAY_TAG_TASKLIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tasklists;
	}

	/* END OF TASKLIST GET */
	/* TASKLIST ADD */
	public void getAddTasklistJSONObject(String projectID, String milestoneID, String name, String desc,
			String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_NAME, name));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_DESC, desc));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_PID, projectID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_MID, milestoneID));

		String POSTED_TASKLIST_ADD_URL = this.COLLABTIVE_URL + TASKLIST_ADD_URL;

		try {
			this.addTasklistJSONObject = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASKLIST_ADD_URL);
			System.out.println("Add Tasklist JSONObject: " + addTasklistJSONObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getAddTasklistStatusCode() {
		int result = 0;
		try {
			JSONObject response = addTasklistJSONObject.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF TASKLIST ADD */

	/* DELETE TASKLIS */
	public void getDeleteTasklistJSONObjects(String tlistID, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_TLID, tlistID));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));

		final String POSTED_DELETE_TASKLIST_URL = this.COLLABTIVE_URL + TASKLIST_DELETE_URL;
		try {
			this.deleteTasklistJSONObject = jsonParser.postJSONFromUrl(nameValuePair, POSTED_DELETE_TASKLIST_URL);
			System.out.println("Delete Tasklist: JSON Object: " + deleteTasklistJSONObject);
		} catch (Exception e) {
			System.out.println("Error Deleting Tasklist");
			e.printStackTrace();
		}
	}

	public int getDeleteTasklistStatusCode() {
		int result = 0;
		try {
			JSONObject response = deleteTasklistJSONObject.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF DELETE TASKLIST */

	/* EDIT TASKLIST */
	public void getEditTasklistJSONObject(String tasklistID, String mlstID, String tasklistName, String tasklistDesc,
			String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_TLID, tasklistID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_MID, mlstID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_NAME, tasklistName));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASKLIST_DESC, tasklistDesc));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));

		String POSTED_TASKLILST_EDIT_URL = this.COLLABTIVE_URL + TASKLIST_EDIT_URL;
		try {
			this.editTasklistJSONObject = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASKLILST_EDIT_URL);
			System.out.println("EditTasklist: " + editTasklistJSONObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getEditTasklistStatusCode() {
		int result = 0;
		try {
			JSONObject response = editTasklistJSONObject.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF EDIT TASKLIST */

	/*---------------TASK PROCEDURE AND FUNCTION ----------------------------------*/
	/* GET TASK */
	public void getTasksJSONObjects(String projectID, String tasklistID, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_PID, projectID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_TLID, tasklistID));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		String POSTED_TASKS_GET = this.COLLABTIVE_URL + TASK_GET_URL;
		try {
			this.taskJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASKS_GET);
			System.out.println("Task JSON Object: " + taskJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public int getTasksStatusCode() {
		int result = 0;
		try {
			JSONObject response = taskJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray getTasksJSONArray() {
		JSONArray tasklists = null;
		try {
			JSONObject response = taskJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			tasklists = response.getJSONArray(COLL_ARRAY_TAG_TASK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tasklists;
	}
	
	public void getAllTaskJSONObjects(String pid, String sessionID){
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_PID, pid));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair("all", "1"));
		String POSTED_TASKS_GET = this.COLLABTIVE_URL + TASK_GET_URL;
		try {
			this.taskAllJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASKS_GET);
			System.out.println("ALL TASK: "+taskAllJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getAllTasksStatusCode() {
		int result = 0;
		try {
			JSONObject response = taskAllJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getAllTaskCount(){
		int result = 0;
		try{
			JSONObject response = taskAllJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			result = response.getInt(COLL_TAG_TASK_COUNT);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public JSONArray getAllTasksJSONArray() {
		JSONArray tasklists = null;
		try {
			JSONObject response = taskAllJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			tasklists = response.getJSONArray(COLL_ARRAY_TAG_TASK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tasklists;
	}

	/* END GET TASK */

	/* TASK ADD */
	public void getAddTaskJSONObject(String projectID, String tasklistID, String name, String desc, String end,
			String assignedUserIDs, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_NAME, name));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_DESC, desc));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_END, end));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_PID, projectID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_TLID, tasklistID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_AUID, assignedUserIDs));

		String POSTED_TASK_ADD_URL = this.COLLABTIVE_URL + TASK_ADD_URL;

		try {
			this.addTaskJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASK_ADD_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getAddTaskStatusCode() {
		int result = 0;
		try {
			JSONObject response = addTaskJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF TASK ADD */

	/* DELETE TASK */
	public void getDeleteTaskJSONObjects(String taskID, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_TID, taskID));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));

		final String POSTED_DELETE_TASK_URL = this.COLLABTIVE_URL + TASK_DELETE_URL;
		try {
			this.deleteTaskJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_DELETE_TASK_URL);
			System.out.println("Delete Task: JSON Object: " + deleteTaskJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDeleteTaskStatusCode() {
		int result = 0;
		try {
			JSONObject response = deleteTaskJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF DELETE TASK */

	/* EDIT TASK */
	public void getEditTaskJSONObject(String taskID, String tasklistID, String tasktName, String taskDesc,
			String taskEnd, String selectedIDs, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		System.out.println("EditTask: TaskID=" + taskID + ", TaskListID=" + tasklistID + ", TaskName=" + tasktName
				+ ", TaskDesc=" + taskDesc + ", TaskEnd: " + taskEnd);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_TID, taskID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_TLID, tasklistID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_NAME, tasktName));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_DESC, taskDesc));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_AUID, selectedIDs));
		nameValuePair.add(new BasicNameValuePair("end", taskEnd));
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		System.out.println("EditTask: " + nameValuePair.toString());
		String POSTED_TASK_EDIT_URL = this.COLLABTIVE_URL + TASK_EDIT_URL;
		try {
			this.editTaskJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASK_EDIT_URL);
			System.out.println("EditTask: " + taskEnd);
			System.out.println("EditTask: " + editTaskJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getEditTaskStatusCode() {
		int result = 0;
		try {
			JSONObject response = editTaskJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/* END OF EDIT TASK */
	
	/*TASK CLOSE*/
	public void getCloseTaskJSONObject(String tid, String sessionID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair(PARAM_SESSION_ID, sessionID));
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_TASK_TID, tid));
		String POSTED_TASK_CLOSE = this.COLLABTIVE_URL + TASK_CLOSE_URL;
		try {
			this.closeTaskJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_TASK_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getCloseTaskStatusCode() {
		int result = 0;
		try {
			JSONObject response = closeTaskJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/*END OF TASK CLOSE*/
	
	/*TASK USER ASSIGNED GET*/
	public void getTaskUserAssigned(String pid){
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_PROJECT_ID_PID, pid));
		String GET_USER_ASSIGNED_DATA = this.COLLABTIVE_URL + TASK_ASSIGNED_GET_URL;
		try{
			this.userAssignedTaskJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, GET_USER_ASSIGNED_DATA);
			System.out.println("TASK USER ASSIGNED: "+userAssignedTaskJSONObjects);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public JSONArray getTaskUserAssignedJSONArray(){
		JSONArray taskUserAssignedJSONArray = null;
		try{
			taskUserAssignedJSONArray = userAssignedTaskJSONObjects.getJSONArray("usersTask");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return taskUserAssignedJSONArray;
	}
	/*END OF TASK USER ASSIGNED GET*/

	/*---------------FILES PROCEDURE AND FUNCTION ----------------------------------*/
	/* GET FILE */
	public void getFilesJSONObjects(String projectID) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(COLL_TAG_FILES_POSTED_PID, projectID));
		String POSTED_FILES_GET = this.COLLABTIVE_URL + FILES_GET_URL;
		try {
			this.fileJSONObjects = jsonParser.postJSONFromUrl(nameValuePair, POSTED_FILES_GET);
			System.out.println("Files JSON Object: " + fileJSONObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getFilesStatusCode() {
		int result = 0;
		try {
			JSONObject response = fileJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			JSONObject status = response.getJSONObject(COLL_TAG_STATUS);
			result = status.getInt(COLL_TAG_STATUS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray getFilesJSONArray() {
		JSONArray tasklists = null;
		try {
			JSONObject response = fileJSONObjects.getJSONObject(COLL_TAG_RESPONSE);
			tasklists = response.getJSONArray(COLL_ARRAY_TAG_FILES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tasklists;
	}

	/* END GET TASK */

}
