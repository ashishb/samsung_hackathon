///**
// * 
// */
//package com.projectx;
//
///**
// * @author adisomani
// *
// */
//
//import com.samsung.chord.ChordManager;
//import com.samsung.chord.IChordChannel;
//import com.samsung.chord.IChordChannelListener;
//import com.samsung.chord.IChordManagerListener;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.PowerManager;
//import android.util.Log;
//import com.samsung.chord.ChordManager.INetworkListener;
//
//
//public class AppStartActivity  extends Activity {
//
//	public static String PUBLIC_LISTENER_CHANNEL = "com.project.x";
//  
//	private ChordManager mChord = null;
//
//  private IChordChannelListener mListener = null;
//
//  private PowerManager.WakeLock mWakeLock = null;
//  
//  public static final String chordFilePath = Environment.getExternalStorageDirectory()
//      .getAbsolutePath() + "/Chord";
//  
//  private static final String TAG = "[ProjectX]";
//  private static final String TAGClass = "AppStartActivity : ";
//
////for notifying event to Activity
//  public interface IChordServiceListener {
//      void onReceiveMessage(String node, String channel, String message);
//
//      public static final int SENT = 0;
//
//      public static final int RECEIVED = 1;
//
//      public static final int CANCELLED = 2;
//
//      public static final int REJECTED = 3;
//
//      public static final int FAILED = 4;
//
//      void onUpdateNodeInfo(String nodeName, String ipAddress);
//  }
//  
//  // Initialize chord
//  public void initializeChord(IChordServiceListener listener) throws Exception {
//      if (mChord != null) {
//          return;
//      }
//
//      // #1. GetInstance
//      mChord = ChordManager.getInstance(this);
//      Log.d(TAG, TAGClass + "[Initialize] Chord Initialized");
//      
//      mListener = listener;
//
//      // #2. set some values before start
//      mChord.setTempDirectory(chordFilePath);
//      mChord.setHandleEventLooper(getMainLooper());
//  }
//  
////Start chord
//  public int start(int interfaceType) {
//  	      // #3. set some values before start
//      return mChord.start(interfaceType, new IChordManagerListener() {
//          @Override
//          public void onStarted(String name, int reason) {
//              Log.d(TAG, TAGClass + "onStarted chord");
//
//              if (null != mListener)
//                  mListener.onUpdateNodeInfo(name, mChord.getIp());
//
//              if (STARTED_BY_RECONNECTION == reason) {
//                  Log.e(TAG, TAGClass + "STARTED_BY_RECONNECTION");
//                  return;
//              }
//              // if(STARTED_BY_USER == reason) : Returns result of calling
//              // start
//
//              // #4.(optional) listen for public channel
//              IChordChannel channelInst = mChord.joinChannel("PROJECTX", mListener);
//
//
//              if (null == channel) {
//                  Log.e(TAG, TAGClass + "fail to join public");
//              }
//          }
//
//          @Override
//          public void onNetworkDisconnected() {
//              Log.e(TAG, TAGClass + "onNetworkDisconnected()");
//              if (null != mListener)
//                  mListener.onNetworkDisconnected();
//          }
//
//          @Override
//          public void onError(int error) {
//              // TODO Auto-generated method stub
//
//          }
//
//      });
//  }
// 	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		initializeChord();
//		joinPublicChannel();
//	}
//
//}
