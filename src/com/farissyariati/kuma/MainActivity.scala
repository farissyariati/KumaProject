package com.farissyariati.kuma

import android.app.Activity
import android.os.Bundle
import com.farissyariati.kuma.utility.FPreferencesManager
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.CollabtiveManager;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.projects.ProjectListActivity;
import android.content.Intent;

class MainActivity extends Activity {
  /*try using scala*/
  final var SPLASH_LOAD_MILIS = 3000;
  var splashThread: Thread = null;
  var fpm: FPreferencesManager = null;

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.splash_screen)
    initVariable
    onCheckRootDir
    splashLoad
  }

  private def initVariable() {
    this.fpm = new FPreferencesManager(this)
  }

  private def onCheckRootDir() {
    var fFileManager = new FFileManager
    fFileManager.checkRootDirectory
  }

  private def splashLoad() {
    splashThread = new Thread(new Runnable() {
      override def run() {
        try {
          Thread.sleep(SPLASH_LOAD_MILIS)
          var firstInstalationState: Int = fpm.getOnFirstInstall
          if (firstInstalationState == 1) {
            var actionPreferences: Intent = new Intent(getBaseContext,
              classOf[ApplicationPreferencesActivity])
            startActivity(actionPreferences)
            finish
          }
          else{
            retrievedSavedPreferences
            checkSessionIdAvailable
           
          }
        } catch {
          case e: Exception => startLoginActivity
        }
      }
    });
    splashThread.start
  }
  
  private def retrievedSavedPreferences(){
    fpm.setEnabledNotificationState(fpm.getSavedEnabledNotificationState)
  }
  
  private def checkSessionIdAvailable(){
	  fpm.setFileListActivityStartState(0)
	  var collManager:CollabtiveManager = new CollabtiveManager(getBaseContext)
	  var manager:FFileManager = new FFileManager
	  if(!fpm.getSessionID.equals("")){
	    collManager.getProjectsJSONObject(fpm.getSessionID)
	    fpm.setProjectCount(collManager.getProjectCount)
	    if(collManager.getProjectsStatusCode == 1){
	      try{
	       manager.writeToFile(CollabtiveProfile.KUMA_FILE_JSON_PROJECT, collManager.getProjectsJSONArray().toString())
	       startProjectListActivity
	      }
	      catch{
	        case e:Exception=> startLoginActivity
	      }
	    }
	  }else
	    startLoginActivity
  }
  
  private def startLoginActivity(){
    var startLogin:Intent = new Intent(getBaseContext, classOf[LoginActivity]);
    startActivity(startLogin);
    finish;
  }
  
  private def startProjectListActivity(){
    var startProjectList:Intent = new Intent(getBaseContext, classOf[ProjectListActivity]);
    startActivity(startProjectList);
    finish;
  }
}
