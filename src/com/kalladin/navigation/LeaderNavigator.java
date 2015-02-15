package com.kalladin.navigation;

import java.util.ArrayList;

import com.kalladin.findrunner.Config;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LeaderNavigator implements SensorEventListener, LocationListener {
	private SensorManager mSensorManager;
	private LocationManager mLocationManager;
	private Context mContext;
	private Handler mUIHandler;
	private String provider;
	private final String LOG_TAG = "LeaderNavigator";
	private float[] accelerometerValues = new float[3]; 
	private float[] magneticFieldValues = new float[3];
	
	private boolean bMagnetValueGet = false;
	private boolean bGravityValueGet = false;
	
	public LeaderNavigator (Context context, Handler handler) {
		this.mContext = context;
		this.mUIHandler = handler;
	}
	
	public void init() {
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		mLocationManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void start() {
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
 
        // Creating an empty criteria object
        Criteria criteria = new Criteria();
 
        // Getting the name of the provider that meets the criteria
        provider = mLocationManager.getBestProvider(criteria, false);
 
        if(provider!=null && !provider.equals("")){
 
            // Get the location from the given provider
            Location location = mLocationManager.getLastKnownLocation(provider);
 
            mLocationManager.requestLocationUpdates(provider, 20000, 1, this);
 
            if(location!=null)
                onLocationChanged(location);
        }else{
            Log.d(LOG_TAG, "No Provider Found");
        }
    }
 		
	public void stop() {
		mSensorManager.unregisterListener(this);
		mLocationManager.removeUpdates(this);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			bMagnetValueGet = true;
			magneticFieldValues = event.values;
		}
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			bGravityValueGet = true;
			accelerometerValues = event.values;
		}
		
		if(bMagnetValueGet && bGravityValueGet) {
			float [] values = new float[3];
			float [] R = new float[9];
			float [] I = new float[9];
			ArrayList<Float> msgData = new ArrayList<Float>();
			
			SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticFieldValues);
			SensorManager.getOrientation(R, values);
			float inclineAngle = (float) Math.toDegrees	(SensorManager.getInclination(I));
			float azimuth = (float) Math.toDegrees(values[0]);
			float pitch = (float) Math.toDegrees(values[1]);
			float roll = (float) Math.toDegrees(values[2]);
			Log.d(LOG_TAG, "A:" + azimuth);
			Log.d(LOG_TAG, "P:" + pitch);
			Log.d(LOG_TAG, "R:" + roll);
			Log.d(LOG_TAG, "I:" + inclineAngle);
			msgData.add(azimuth);
			msgData.add(pitch);
			msgData.add(roll);
			msgData.add(inclineAngle);
			Message msg = new Message();
			msg.what = Config.ROTATE_COMPASS;
			msg.obj = msgData;
			// Device is vertical 
			if(pitch < -5) {
				msg.arg1 = Math.round(roll);
				Log.d(LOG_TAG,"NORMAL VERTICAL MODE:" +msg.arg1);
			} else if (pitch > 5) {
				msg.arg1 = -1 * Math.round(roll);
				Log.d(LOG_TAG,"UPSIDEDOWN VERTICAL MODE:" + msg.arg1);
			} else {
				msg.arg1 = Math.round(azimuth);
				Log.d(LOG_TAG,"HORIZONTAL MODE" +msg.arg1);
			}
			mUIHandler.sendMessage(msg);

		}
		// send event to ui handler
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLocationChanged(Location location) {
		Message msg = new Message();
		msg.what = Config.LOCATION_CHANGE;
		msg.obj  = location;
		mUIHandler.sendMessage(msg);
				  
        Log.d(LOG_TAG, "Longitude:" + location.getLongitude());
        Log.d(LOG_TAG, "Latitude:" + location.getLatitude() );		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
}
