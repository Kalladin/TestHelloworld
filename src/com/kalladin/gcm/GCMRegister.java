package com.kalladin.gcm;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kalladin.findrunner.Config;

public class GCMRegister {

	GoogleCloudMessaging gcm;
	Context mContext;
	String regId;
	
	static final String LOG_TAG = "GCMRegister";
	
	public GCMRegister(Context context) {
		this.mContext = context;
	}
	
	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(mContext);
		regId = Config.getRegistrationId();

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.i(LOG_TAG, "registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Log.i(LOG_TAG, "registerGCM - stored in shared preference - regId: " + regId);
		}
		return regId;
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(mContext);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.i(LOG_TAG, "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					Config.storeRegistrationId(regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.e(LOG_TAG, "Error: " + msg);
				}
				Log.i(LOG_TAG, "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.d(LOG_TAG, "Registered with GCM Server." + msg);
			}
		}.execute(null, null, null);
	}

}
