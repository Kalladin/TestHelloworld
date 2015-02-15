package com.kalladin.findrunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Config {
	
	private static Context mContext;
	private static final String LOG_TAG = "Config";
	/* **************** GCM Variables **************** */
	public static final String GCM_FILE_NAME = "GCM_preference";
	public static final String REG_ID = "regToken";
	public static final String APP_VERSION = "appVer";
	public static final String GOOGLE_PROJECT_ID = "546256119190";
	public static final String SYSTEM = "Android";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	/* **************** MainActivity Commands **************** */
	public static final int ROTATE_COMPASS  = 0;
	public static final int LOCATION_CHANGE = 1;
		

	public Config(Context appContext) {
		this.mContext = appContext;
	}
	
    public static String getRegistrationId() {
		final SharedPreferences prefs = mContext.getSharedPreferences(
				GCM_FILE_NAME, Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(LOG_TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppPackageVersion();
		if (registeredVersion != currentVersion) {
			Log.i(LOG_TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}
    
	public static int getAppPackageVersion() {
		try {
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d(LOG_TAG,
					"getAppPackageVersion() - Context is corrupted!!" + e);
			throw new RuntimeException(e);
		}
	}
    
	public static void storeRegistrationId(String regId) {
		final SharedPreferences prefs = mContext.getSharedPreferences(
				GCM_FILE_NAME, Context.MODE_PRIVATE);
		int appVersion = getAppPackageVersion();
		Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
}
