package com.farissyariati.kuma.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(String url) {
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new HttpPost(url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;
	}

	public JSONObject postJSONFromUrl(List<NameValuePair> nameValuePair,
			String url) {
		try {
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
			json = sb.toString();
			jObj = new JSONObject(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jObj;
	}
	
	public JSONArray combineJSONArray(JSONArray a, JSONArray b){
		JSONArray jsonArray = new JSONArray();
		try{
			for(int i = 0; i < b.length(); i++){
				JSONObject jsonObject = b.getJSONObject(i);
				jsonArray.put(jsonObject);
			}
			for(int i = 0; i < a.length(); i++){
				JSONObject jsonObject = a.getJSONObject(i);
				jsonArray.put(jsonObject);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return jsonArray;
	}
}