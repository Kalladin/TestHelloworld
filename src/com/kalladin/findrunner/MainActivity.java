package com.kalladin.findrunner;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.kalladin.gcm.GCMAppServer;
import com.kalladin.gcm.GCMRegister;
import com.kalladin.navigation.LeaderNavigator;

public class MainActivity extends Activity {
	
	private static final String LOG_TAG = "MainActivity";

	private GCMRegister mGCMRegister;
	private GCMAppServer mGCMServer;
	private Config mConfig;
	
	private ImageView compassArrow;
	private float currentDegree = 0;
	private TextView longitudeTv;
	private TextView latitudeTv;
	private TextView compassDegreeTv;
	
	private LeaderNavigator mLeader;
	
	private Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {
        	
            Bundle temp = msg.getData();
            int command = msg.what;

            switch (command) {   
                 case Config.ROTATE_COMPASS:
                 {
                     RotateAnimation ra = new RotateAnimation(
                    		 currentDegree,
                    		 -msg.arg1,
                    		 Animation.RELATIVE_TO_SELF, 0.5f,
                    		 Animation.RELATIVE_TO_SELF, 0.5f);
                     ra.setDuration(210);
                     ra.setFillAfter(true);
                     compassArrow.startAnimation(ra);
                     currentDegree = -msg.arg1;
                     compassDegreeTv.setText(currentDegree + "deg");
                 }
                	 break;   
                 case Config.LOCATION_CHANGE:
                	 Location location = (Location)msg.obj;
                	 longitudeTv.setText(location.getLongitude() + "deg");
                	 latitudeTv.setText(location.getLatitude() + "deg");
                	 break;
                 default:
                 	break;
            }   
            super.handleMessage(msg);   
       }   
  }; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		setLayout();
		mLeader.start();
		mGCMRegister.registerGCM();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*
		if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onResume() {
		if(mLeader != null)
			mLeader.start();
		super.onResume();
	}
	@Override
	public void onPause() {
		if(mLeader != null)
			mLeader.stop();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void init() {
		mConfig = new Config(getApplicationContext());
		mGCMRegister = new GCMRegister(getApplicationContext());
		mGCMServer = new GCMAppServer();
		mLeader = new LeaderNavigator(getApplicationContext(),mHandler);
		mLeader.init();
	}
	
	private void setLayout() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		compassArrow = (ImageView) findViewById(R.id.compass_arrow);
		longitudeTv = (TextView) findViewById(R.id.longitudeTv);
		latitudeTv = (TextView) findViewById(R.id.latitudeTv);
		compassDegreeTv = (TextView) findViewById(R.id.compassDegreeTv);
	}
	
    private boolean checkPlayServices() {
        
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Config.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(LOG_TAG, "GooglePlayService is not supported on this device. ");
                finish();
            }
            return false;
        }
        return true;
    }
}
