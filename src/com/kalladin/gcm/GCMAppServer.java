package com.kalladin.gcm;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.kalladin.findrunner.Config;

import android.os.AsyncTask;
import android.util.Log;

public class GCMAppServer {
	
	private String LOG_TAG = "GCMAppServer";
		
	public void shareRegIdWithAppServer(final String workId, final String regId, final String serverAddress) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String result = "";
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("id", workId);
				paramsMap.put("os", Config.SYSTEM);
				paramsMap.put("token", regId);
		
				try {
					
					URL serverUrl = null;
					String serverUrl_str = serverAddress;
					if(serverUrl_str == "")
						return "No GCM Server Address";
					StringBuilder postBody = new StringBuilder();
					Iterator<Entry<String, String>> iterator = paramsMap.entrySet()
							.iterator();
		
					while (iterator.hasNext()) {
						Entry<String, String> param = iterator.next();
						postBody.append(param.getKey()).append('=')
								.append(param.getValue());
						if (iterator.hasNext()) {
							postBody.append('&');
						}
					}
					serverUrl_str = serverUrl_str + "?" + postBody.toString();
		
					try {
						//sample to register sip vccloud server  http://sip.vccloud.quantatw.com/ios/reg.php?id=99042624&os=android&token=12323424342342
						serverUrl = new URL(serverUrl_str);
					} catch (MalformedURLException e) {
						Log.e(LOG_TAG, "URL Connection Error: "
								+ serverAddress, e);
						result = "Invalid URL: " + serverAddress;
					}
					HttpURLConnection httpCon = null;
					try {
						httpCon = (HttpURLConnection) serverUrl.openConnection();
						httpCon.setReadTimeout(5000);
            			httpCon.setConnectTimeout(3000);
						httpCon.setRequestMethod("GET");
		
						int status = httpCon.getResponseCode();
						if (status == 200) {
							result = "RegId shared with Application Server. RegId: "
									+ regId +"   WorkId:" + workId + "   OS:" + Config.SYSTEM;
							Log.d(LOG_TAG, result);
						} else {
							result = "Get Failure." + " Status: " + status;
						}
					} finally {
						if (httpCon != null) {
							httpCon.disconnect();
						}
					}
		
				} catch (IOException e) {
					result = "Post Failure. Error in sharing with App Server.";
					Log.e(LOG_TAG, "Error in sharing with App Server: " + e);
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				Log.d(LOG_TAG,"shareRegIdWithAppServer(" + workId +"," + regId +") - " + result);
			}	
		}.execute(null,null,null);
	}
}
