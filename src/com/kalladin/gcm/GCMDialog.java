package com.kalladin.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GCMDialog extends Activity {
	public static final String LOG_TAG = "GCMDialog";
	AlertDialog alert;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	super.onCreate(savedInstanceState);

        Bundle msg = getIntent().getExtras();
        if(msg == null) {
        	Log.e(LOG_TAG,"GCMDialog onCreate() with no message!");
        	finish();
        }
        
        //if(alert != null)
        	//alert.dismiss();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
          
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.ic_launcher);
	    builder.setTitle("123");
	    builder.setMessage(msg.getString("MSG"));
	    builder.setCancelable(false);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            finish();
	        }
	    });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
                finish();
	        }
	    });
	    alert = builder.create();
	    alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	    alert.show();
	    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	    
    }

}
