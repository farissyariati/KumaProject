package com.farissyariati.kuma.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class FPreferencesManager implements CollabtiveProfile {
	private Context fContext;
	private SharedPreferences sharedPreferences;
	private Editor editor;

	public FPreferencesManager(Context context) {
		this.fContext = context;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fContext);
		editor = sharedPreferences.edit();
	}

	public void setSessionID(String sessionID) {
		editor.putString(COLL_TAG_SESSION_ID, sessionID);
		editor.commit();
	}

	public void setUserID(int userID) {
		editor.putInt(COLL_TAG_USER_ID, userID);
		editor.commit();
	}

	public void setRememberedUsername(String username) {
		editor.putString(KUMA_COLL_REMEMBERED_USER, username);
		editor.commit();
	}

	public void setRememberedPassword(String password) {
		editor.putString(KUMA_COLL_REMEMBERED_PASSWORD, password);
		editor.commit();
	}

	public void setSelectedIDChain(String userIDChain) {
		editor.putString(KUMA_COLL_SELECTED_USERS, userIDChain);
		editor.commit();
	}

	public void setTemporaryPassedPID(int projectID) {
		editor.putInt(KUMA_COLL_TEMPORARY_PASSED_PROJECT_ID, projectID);
		editor.commit();
	}

	public void setTemporaryPassedTLID(int tasklistID) {
		editor.putInt(KUMA_COLL_TEMPORARY_PASSED_TASKLIST_ID, tasklistID);
		editor.commit();
	}

	public void setFileListActivityStartState(int startState) {
		editor.putInt(KUMA_COLL_FILE_LIST_ACTIVITY_START_STATE, startState);
		editor.commit();
	}

	public void setEnabledNotificationState(boolean enabled) {
		editor.putBoolean(COLL_PREF_ENABLE_NOTIFICATION, enabled);
		editor.commit();
	}

	public void setSavedEnabledNotificationState(boolean enabled) {
		editor.putBoolean(COLL_PREF_TEMPORARY_ENABLE_NOTIFICATION, enabled);
		editor.commit();
	}

	public void setProjectCount(int projectCount) {
		editor.putInt(COLL_TAG_PROJECT_COUNT, projectCount);
		editor.commit();
	}

	public void setTaskCount(int taskCount) {
		editor.putInt(COLL_TAG_TASK_COUNT, taskCount);
		editor.commit();
	}

	public void setNotifcatorUpdaterState(int state) {
		editor.putInt(COLL_TAG_UPDATER, state);
		editor.commit();
	}

	public void setOnFirstInstall(int state) {
		editor.putInt(KUMA_COLL_FIRST_INSTALL_STATE, state);
		editor.commit();
	}
	
	public void setPreferencesLoginState(int state){
		editor.putInt(COLL_PREF_ON_LOGIN, state);
		editor.commit();
	}
	
	public void setSentState(int state){
		editor.putInt(KUMA_TAG_SENT_STATE, state);
		editor.commit();
	}
	
	public void setProjectControlStartTime(long projectStart){
		editor.putLong(KUMA_CC_PROJECT_START, projectStart);
		editor.commit();
	}
	
	public void setProjectControlEndTime(long projectEnd){
		editor.putLong(KUMA_CC_PROJECT_END, projectEnd);
		editor.commit();
	}
	
	public void setMilestoneControlEndTime(long milestoneEnd){
		editor.putLong(KUMA_CC_MILESTONE_END, milestoneEnd);
		editor.commit();
	}
	
	public void setNewTaskState(boolean found){
		editor.putBoolean(COLL_NOTIFY_CONDITION_NEW_TASK, found);
		editor.commit();
	}
	
	public boolean getNewTaskState(){
		return sharedPreferences.getBoolean(COLL_NOTIFY_CONDITION_NEW_TASK, false);
	}
	
	public long getMilestoneControlEndTime(){
		return sharedPreferences.getLong(KUMA_CC_MILESTONE_END, 0);
	}
	
	
	public long getProjectControlStartTime(){
		return sharedPreferences.getLong(KUMA_CC_PROJECT_START, 0);
	}
	
	public long getProjectControlEndTime(){
		return sharedPreferences.getLong(KUMA_CC_PROJECT_END, 0);
	}
	
	
	public int getSentState(){
		return sharedPreferences.getInt(KUMA_TAG_SENT_STATE, 0);
	}

	public int getOnFirstInstall() {
		return sharedPreferences.getInt(KUMA_COLL_FIRST_INSTALL_STATE, 1);
	}
	
	public int getPreferencesLoginState(){
		return sharedPreferences.getInt(COLL_PREF_ON_LOGIN, 0);
	}

	public String getSessionID() {
		return sharedPreferences.getString(COLL_TAG_SESSION_ID, COLL_DEFAULT_SESSION_ID);
	}

	public int getUserID() {
		return sharedPreferences.getInt(COLL_TAG_USER_ID, COLL_DEFAULT_USERID);
	}

	public String getRememberedUsername() {
		return sharedPreferences.getString(KUMA_COLL_REMEMBERED_USER, KUMA_COLL_DEFAULT_REMEMBERED_USER);
	}

	public String getRemeberedPassword() {
		return sharedPreferences.getString(KUMA_COLL_REMEMBERED_PASSWORD, KUMA_COLL_DEFAULT_REMEMBERED_PASSWORD);
	}

	public String getSelectedIDChain() {
		return sharedPreferences.getString(KUMA_COLL_SELECTED_USERS, "0;0");
	}

	public int getTemporaryPassedPID() {
		return sharedPreferences.getInt(KUMA_COLL_TEMPORARY_PASSED_PROJECT_ID, 0);
	}

	public int getTemporaryPassedTLID() {
		return sharedPreferences.getInt(KUMA_COLL_TEMPORARY_PASSED_TASKLIST_ID, 0);
	}

	public int getFileListActivityStartState() {
		return sharedPreferences.getInt(KUMA_COLL_FILE_LIST_ACTIVITY_START_STATE, 0);
	}

	public boolean getEnabledNotificationState() {
		return sharedPreferences.getBoolean(COLL_PREF_ENABLE_NOTIFICATION, true);
	}

	public int getProjectCount() {
		return sharedPreferences.getInt(COLL_TAG_PROJECT_COUNT, 0);
	}

	public int getTaskCount() {
		return sharedPreferences.getInt(COLL_TAG_TASK_COUNT, 0);
	}

	public int getNotificationUpdaterValue() {
		return sharedPreferences.getInt(COLL_TAG_UPDATER, 0);
	}

	public boolean getSavedEnabledNotificationState() {
		return sharedPreferences.getBoolean(COLL_PREF_TEMPORARY_ENABLE_NOTIFICATION, false);
	}

	public String getCollabtiveWebsite() {
		return sharedPreferences.getString(KUMA_COLL_COLLABTIVE_WEBSITE, "http://project.farissyariati.com/");
	}
	
	public String getUserEmailAddress(){
		return sharedPreferences.getString(COLL_PREF_USER_EMAIL, "@gmail.com");
	}
	
	public String getUserEmailPassword(){
		return sharedPreferences.getString(COLL_PREF_USER_EMAIL_PASSWORD, "password");
	}

}
