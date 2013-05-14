package com.farissyariati.kuma.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Environment;

public class FFileManager {
	static final String saveFileDir = "/KumaProject/";
	private File sdCardPath;
	private FPreferencesManager fpm;

	public FFileManager() {
		this.sdCardPath = Environment.getExternalStorageDirectory();
	}
	
	public FFileManager(Context context){
		this.fpm = new FPreferencesManager(context);
		this.sdCardPath = Environment.getExternalStorageDirectory();
	}

	public void writeToFile(String file, String JSONData) {
		try {
			File aFile = new File(Environment.getExternalStorageDirectory()+saveFileDir + file);
			
			if (aFile.exists())
				aFile.delete();

			aFile.createNewFile();
			System.out.println("FILE MANAGER File Created");
			FileWriter writer = new FileWriter(aFile);
			writer.write(JSONData);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkRootDirectory() {
		File kumaProjectFolder = new File(sdCardPath + saveFileDir);
		if (!kumaProjectFolder.exists())
			kumaProjectFolder.mkdir();
	}
	
	public String getJSONContent(String fileName){
		String result = null;
		try{
			String line;
			StringBuilder sb = new StringBuilder();
			File aFile = new File(this.sdCardPath+saveFileDir+fileName);
			BufferedReader reader = new BufferedReader(new FileReader(aFile));
			
			while((line = reader.readLine()) != null){
				sb.append(line+"\n");
			}
			result = sb.toString();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public String getFileTextContent(String fileName){
		String result = null;
		try{
			String line;
			StringBuilder sb = new StringBuilder();
			File aFile = new File(this.sdCardPath+saveFileDir+fileName);
			BufferedReader reader = new BufferedReader(new FileReader(aFile));
			
			while((line = reader.readLine()) != null){
				sb.append(line+"\n");
			}
			result = sb.toString();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public void initDownloadDir(String projectID) {
		System.out.println("Check Download Dir: Masuk");
		//String url = CollabtiveProfile.TEMPORARY_COLLABTIVE_URL_HOME+CollabtiveProfile.FILES_CREATE_DIR;
		String url = fpm.getCollabtiveWebsite()+CollabtiveProfile.FILES_CREATE_DIR;
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(
				CollabtiveProfile.COLL_TAG_PROJECT_ID_PID, projectID));
		System.out.println("Check Download Dir: "+nameValuePair.toString());
		System.out.println("Check Download Dir: URL= "+url);
		try{
			
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse response = client.execute(httpPost);

			BufferedReader input = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = input.readLine()) != null) {
				sb.append(line + "\n");
			}
			System.out.println("Check Download Dir: "+sb.toString());
		}
		catch(Exception e){
			System.out.println("Check Download Dir: Error");
			e.printStackTrace();
		}
	}
	
	public int initSubfolderDownloadDir(String projectID, String newFolder) {
		int result = 0;
		try{
			newFolder = URLEncoder.encode(newFolder, "UTF_ENCODE");
			System.out.println("Create Folder: New Folder "+newFolder);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		String url = fpm.getCollabtiveWebsite()+CollabtiveProfile.FILES_CREATE_SUBFOLDER;
		System.out.println("Create Folder: URL "+url);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair(
				CollabtiveProfile.COLL_TAG_PROJECT_ID_PID, projectID));
		nameValuePair.add(new BasicNameValuePair("folder_name", newFolder));
		try{
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse response = client.execute(httpPost);

			BufferedReader input = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = input.readLine()) != null) {
				sb.append(line);
			}
			result = Integer.parseInt(sb.toString());
			System.out.println("Check Download Dir: "+sb.toString());
		}
		catch(Exception e){
			System.out.println("Check Download Dir: Error");
			e.printStackTrace();
		}
		return result;
	}
	
	
	public String getProjectsDirSubfolders(String projectID) {
		String result = "/n";
		String url = fpm.getCollabtiveWebsite()+CollabtiveProfile.FILES_GET_SUBFOLDERS;
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(
				CollabtiveProfile.COLL_TAG_PROJECT_ID_PID, projectID));
		try{
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse response = client.execute(httpPost);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = input.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			System.out.println("Check Subfolders: "+sb.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
