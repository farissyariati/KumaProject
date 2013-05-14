package com.farissyariati.kuma.utility;

public interface CollabtiveProfile {
	// All URL Path
	static String TEMPORARY_COLLABTIVE_URL_HOME = "http://project.farissyariati.com";
	static String LOGIN_URL = "/Mobile/login/";
	static String LOGOUT_URL = "/Mobile/logout/";

	static String USERS_URL = "/Mobile/users/";
	static String ASSIGN_PROJECT_URL = "/Mobile/projects/assign/";
	static String LATEST_PROJECT_URL = "/Mobile/projects/get/latest.php";

	static String PROJECTS_GET_URL = "/Mobile/projects/get/";
	static String PROJECTS_ADD_URL = "/Mobile/projects/add/";
	static String PROJECTS_DELETE_URL = "/Mobile/projects/del/";
	static String PROJECTS_EDIT_URL = "/Mobile/projects/edit/";
	static String PROJECTS_CLOSE_URL = "/Mobile/projects/close/";

	static String MILESTONE_GET_URL = "/Mobile/projects/milestones/get/";
	static String MILESTONE_ADD_URL = "/Mobile/projects/milestones/add/";
	static String MILESTONE_DELETE_URL = "/Mobile/projects/milestones/del/";
	static String MILESTONE_EDIT_URL = "/Mobile/projects/milestones/edit/";
	static String MILESTONE_CLOSE_URL = "/Mobile/projects/milestones/close/";

	static String TASKLIST_GET_URL = "/Mobile/projects/tasklists/get/";
	static String TASKLIST_ADD_URL = "/Mobile/projects/tasklists/add/";
	static String TASKLIST_DELETE_URL = "/Mobile/projects/tasklists/del/";
	static String TASKLIST_EDIT_URL = "/Mobile/projects/tasklists/edit/";

	static String TASK_GET_URL = "/Mobile/projects/tasks/get/";
	static String TASK_ADD_URL = "/Mobile/projects/tasks/add/";
	static String TASK_DELETE_URL = "/Mobile/projects/tasks/del/";
	static String TASK_EDIT_URL = "/Mobile/projects/tasks/edit/";
	static String TASK_CLOSE_URL = "/Mobile/projects/tasks/close/";
	static String TASK_ASSIGNED_GET_URL  = "/Mobile/projects/tasks/get/task_user_assigned.php";

	static String FILES_GET_URL = "/Mobile/file/get/";
	static String FILES_CREATE_DIR = "/Mobile/file/create_dir/";
	static String FILES_CREATE_SUBFOLDER = "/Mobile/file/create_dir/subfolder/";
	static String FILES_GET_SUBFOLDERS = "/Mobile/file/scandir/";

	static String NOTIFY_COUNT_URL = "/Mobile/notification/count/";
	static String NOTIFY_GET_DATA = "/Mobile/notification/get/";

	// All Post Parameter
	static String PARAM_USERNAME = "username";
	static String PARAM_PASSWORD = "password";
	static String PARAM_SESSION_ID = "sid";

	// All JSON Object Tag
	static String COLL_TAG_RESPONSE = "response";
	static String COLL_TAG_STATUS = "status";
	static String COLL_TAG_STATUS_CODE = "statusCode";
	static String COLL_TAG_LOGIN = "login";
	static String COLL_TAG_SESSION_ID = "sessionid";
	static String COLL_TAG_USER_ID = "userid";

	// ALL JSON Array TAG
	static String COLL_ARRAY_TAG_PROJECTS = "projects";
	static String COLL_ARRAY_TAG_USERS = "users";
	static String COLL_ARRAY_TAG_MILESTONES = "milestones";
	static String COLL_ARRAY_TAG_TASKLIST = "tasklists";
	static String COLL_ARRAY_TAG_TASK = "tasks";
	static String COLL_ARRAY_TAG_FILES = "files";

	// All JSON TAG PROJECTS
	static String COLL_TAG_PROJECT_NAME = "name";
	static String COLL_TAG_PROJECT_START = "start";
	static String COLL_TAG_PROJECT_END = "end";
	static String COLL_TAG_PROJECT_ID = "id";
	static String COLL_TAG_PROJECT_DESC = "desc";
	static String COLL_TAG_PROJECT_ASSIGNME = "assignme";
	static String COLL_TAG_PROJECT_BUDGET = "budget";
	static String COLL_TAG_PROJECT_ID_PID = "pid";
	static String COLL_TAG_PROJECT_COUNT = "projectCount";
	static String COLL_TAG_PROJECT_STATUS = "status";

	// All JSON TAG Milestones
	static String COLL_TAG_MILESTONE_POSTED_ID = "pid";
	static String COLL_TAG_MILESTONE_ID = "id";
	static String COLL_TAG_MILESTONE_MID = "mid";
	static String COLL_TAG_MILESTONE_NAME = "name";
	static String COLL_TAG_MILESTONE_DESC = "desc";
	static String COLL_TAG_MILESTONE_START = "start";
	static String COLL_TAG_MILESTONE_END = "end";
	static String COLL_TAG_MILESTONE_STATUS = "status";

	// All JSON TAG Tasklist
	static String COLL_TAG_TASKLIST_POSTED_ID = "pid";
	static String COLL_TAG_TASKLIST_ID = "id";
	static String COLL_TAG_TASKLIST_PID = "pid";
	static String COLL_TAG_TASKLIST_MID = "mid";
	static String COLL_TAG_TASKLIST_TLID = "tlid";
	static String COLL_TAG_TASKLIST_MILESTONE_NAME = "milestonename";
	static String COLL_TAG_TASKLIST_NAME = "name";
	static String COLL_TAG_TASKLIST_DESC = "desc";
	static String COLL_TAG_TASKLIST_START = "start";
	static String COLL_TAG_TASKLIST_END = "end";
	static String COLL_TAG_TASKLIST_STATUS = "status";

	// All JSON TAG Task
	static String COLL_TAG_TASK_TID = "tid";
	static String COLL_TAG_TASK_ID = "id";
	static String COLL_TAG_TASK_PID = "pid";
	static String COLL_TAG_TASK_MID = "mid";
	static String COLL_TAG_TASK_TLID = "tlid";
	static String COLL_TAG_TASK_MILESTONE_NAME = "milestonename";
	static String COLL_TAG_TASK_NAME = "name";
	static String COLL_TAG_TASK_DESC = "desc";
	static String COLL_TAG_TASK_START = "start";
	static String COLL_TAG_TASK_END = "end";
	static String COLL_TAG_TASK_STATUS = "status";
	static String COLL_TAG_TASK_AUID = "assign_uid";
	static String COLL_TAG_TASK_COUNT = "taskCount";

	// All JSON TAG LATEST DATA
	static String COLL_TAG_LATEST_PROJECTS = "projects";
	static String COLL_TAG_LATEST_PROJECT_DETAIL = "projectDetail";
	static String COLL_TAG_LATEST_PROJECT_DETAIL_ID = "ID";

	// All JSON TAG USERS
	static String COLL_TAG_USER_NAME = "name";
	static String COLL_TAG_USER_EMAIL = "email";
	static String COLL_TAG_USER_USERDATA = "usersData";
	static String COLL_TAG_USER_ID_2 = "ID";
	static String COLL_TAG_USER_PHONE_NUMBER_1 = "tel1";

	// All JSON TAG ASSIGN
	static String COLL_TAG_ASSIGN_USERID = "uid";
	static String COLL_TAG_ASSIGN_PROJECTID = "pid";

	// All Default VALUE
	static String COLL_DEFAULT_SESSION_ID = "00xx";
	static int COLL_DEFAULT_USERID = 0;

	// etc
	static String KUMA_COLL_REMEMBERED_USER = "remember_user";
	static String KUMA_COLL_REMEMBERED_PASSWORD = "remember_password";
	static String KUMA_COLL_DEFAULT_REMEMBERED_USER = "";
	static String KUMA_COLL_DEFAULT_REMEMBERED_PASSWORD = "";
	static String KUMA_COLL_SELECTED_USERS = "selected_users";
	static String KUMA_COLL_TEMPORARY_PASSED_PROJECT_ID = "temp_pid";
	static String KUMA_COLL_TEMPORARY_PASSED_TASKLIST_ID = "temp_tlid";
	static String KUMA_COLL_FILE_LIST_ACTIVITY_START_STATE = "file_list_start_state";
	static String KUMA_COLL_FIRST_INSTALL_STATE = "first_install";
	static String KUMA_COLL_COLLABTIVE_WEBSITE = "collabtive_website";

	// file
	static String KUMA_FILE_JSON_PROJECT = "projects.json";
	static String KUMA_FILE_JSON_USERS = "users.json";
	static String KUMA_FILE_JSON_MILESTONES = "milestones.json";
	static String KUMA_FILE_JSON_TASKLIST = "tasklists.json";
	static String KUMA_FILE_JSON_TASK = "tasks.json";
	static String KUMA_FILE_JSON_TASK_ALL = "task_all.json";
	static String KUMA_FILE_JSON_TASK_ASSIGNED = "task_assigned.json";
	static String KUMA_FILE_JSON_FILES = "files.json";
	static String KUMA_FILE_DOWNLOAD_PATH = "/KumaProjectDownloaded";
	static String KUMA_FILE_RECEIVER_URL = "/Mobile/file/uploadfile/upload.php";

	// server file
	static String COLL_TAG_FILES_POSTED_PID = "pid";
	static String COLL_TAG_FILES_ID = "id";
	static String COLL_TAG_FILES_NAME = "name";
	static String COLL_TAG_FILES_DESC = "desc";
	static String COLL_TAG_FILES_PROJECT_ID = "project";
	static String COLL_TAG_FILES_MILESTONE_ID = "milestone";
	static String COLL_TAG_FILES_USER_ID = "userID";
	static String COLL_TAG_FILES_TAGS = "tags";
	static String COLL_TAG_FILES_ADDED = "added";
	static String COLL_TAG_FILES_FILE_URL = "file_url";
	static String COLL_TAG_FILES_TYPE = "type";
	static String COLL_TAG_FILES_TITLE = "title";
	static String COLL_TAG_FILES_FOLDER = "folder";
	static String COLL_TAG_FILES_VISIBLE = "visible";
	static String COLL_TAG_FILES_SUBFOLDERS = "sub_folders";

	// notification
	static String COLL_NOTIFY_PROJECT_COUNT_NEW = "project_count_new";
	static String COLL_NOTIFY_TASK_COUNT_NEW = "task_count_new";
	static String COLL_NOTIFY_PID = "pid";
	static String COLL_NOTIFY_TID = "tid";
	static String COLL_NOTIFY_NAME = "name";
	static String COLL_NOTIFY_UID = "uid";
	static String COLL_NOTIFT_INFO = "info";
	static String COLL_NOTIFY_CONDITION_NEW_TASK = "new_task_found";
	
	//notification preferences
	static String COLL_TAG_UPDATER = "notification_updater";
	static String COLL_PREF_ENABLE_NOTIFICATION = "enable_notification";
	static String COLL_PREF_TEMPORARY_ENABLE_NOTIFICATION = "temporary_enable_notification";
	static String COLL_PREF_USER_EMAIL = "user_email";
	static String COLL_PREF_USER_EMAIL_PASSWORD = "users_email_password";
	static String COLL_PREF_ON_LOGIN = "preferences_on_login";
	
	//email messaging
	static final String KUMA_TAG_CONTENT = "email_content";
	static final String KUMA_TAG_SUBJECT = "email_subject";
	static final String KUMA_TAG_RECEPIENT = "email_send_to";
	static final String KUMA_TAG_SENT_STATE = "sent_state";
	static final String KUMA_TAG_FOOTER_TAG = "Sent from KumaProject v.1.0";
	static final String KUMA_TAG_MESSAGING_TYPE = "messaging_type";
	static final String KUMA_TAG_EMAIL_JSON_ARRAY = "email_json_array.json";
	static final String KUMA_TAG_REPLY_STATE = "reply_state";
	static final String KUMA_TAG_REPLY_EMAIL = "email_reply";
	static final String KUMA_TAG_REPLY_SUBJECT = "subject_reply";
	static final String KUMA_TAG_REPLY_CONTENT = "content_reply";
	
	//sms messaging
	static final String KUMA_TAG_SMS_CONTENT = "message_content";
	static final String KUMA_TAG_PHONE_NUMBER = "phone_number";
	static final String KUMA_TAG_SMS_SUBJECT = "sms_subject";
	
	//calendar control
	static final String KUMA_CC_PROJECT_START = "calendar_control_project_start";
	static final String KUMA_CC_PROJECT_END = "calendar_control_project_end";
	static final String KUMA_CC_MILETOSNE_START = "calendar_control_milestone_start";
	static final String KUMA_CC_MILESTONE_END = "calendar_control_milestone_end";
	static final String KUMA_CC_TASKLIST_END = "calendar_control_tasklist_end";
	
	//pass data
	static final String KUMA_PASS_PROJECT_NAME = "passProjectName";
	static final String KUMA_PASS_PROJECT_DESC = "passProjectDesc";
	static final String KUMA_PASS_PROJECT_START = "passProjectStart";
	static final String KUMA_PASS_PROJECT_END = "passProjectEnd";

}
