package com.kalladin.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

	public static final String LOG_TAG = "GCMNotificationIntentService";

	private NotificationManager mNotificationManager;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		boolean screenState = pm.isScreenOn();
		
		/* Keep cpu wake up & screen light up */

        mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				Log.e(LOG_TAG,"Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				Log.d(LOG_TAG,"Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				/*
	        	WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"GCM_WAKE_LOCK");
	        	wl.acquire(5000);
				showGCMNotificationText("caller", extras.get(GlobalStorage.MESSAGE_KEY).toString());
				Log.i(LOG_TAG, "screenState: " + screenState);

				if(!screenState || GlobalStorage.bGCMDialogOnScreen)
					startGCMDialogActivity(extras.get(GlobalStorage.MESSAGE_KEY).toString());
				Log.i(LOG_TAG, "Received: " + extras.toString());
				*/
			}
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);        
	}
}
