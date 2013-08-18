package com.projectx;

import com.projectx.AppStartActivity.IChordServiceListener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;

public class NodeActivity extends Activity implements IChordServiceListener {
	
	private static final String TAG = "[ProjectX]";
  private static final String TAGClass = "NodeActivity : ";

	public void onReceiveMessage(String node, String channel, String message) {
		Log.d(TAG, TAGClass + "received message: [" + message + "] from [" + node
		    + "] on channel " + channel);
     //mDataTestFragment.onMessageReceived(node, channel, message);
		 // update chat records
 }
	 @Override
   public void onUpdateNodeInfo(String nodeName, String ipAddress) {
		 Log.d(TAG, TAGClass + "updated nodeName to " + nodeName);
		 // my name changed, do nothing.
   }
}
