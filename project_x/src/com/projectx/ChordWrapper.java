package com.projectx;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;

public class ChordWrapper {

	private static final String TAG = "[ProjectX]";
	private static final String TAGClass = "ChordWrapper : ";
	private static IChordChannelListener mListener = null;
	public static final String chordFilePath = Environment
	    .getExternalStorageDirectory().getAbsolutePath() + "/Chord";

	private static ChordManager mChord = null;

	public static void initializeChord(IChordChannelListener listener,
	    Context context) throws Exception {
		if (mChord != null) {
			return;
		}

		// #1. GetInstance
		mChord = ChordManager.getInstance(context);
		Log.d(TAG, TAGClass + "[Initialize] Chord Initialized");

		mListener = listener;

		// #2. set some values before start
		mChord.setTempDirectory(chordFilePath);
		mChord.setHandleEventLooper(context.getMainLooper());
	}

	public static int start(int interfaceType) {
		// #3. set some values before start
		return mChord.start(interfaceType, new IChordManagerListener() {
			@Override
			public void onStarted(String name, int reason) {
				Log.d(TAG, TAGClass + "onStarted chord");

				if (STARTED_BY_RECONNECTION == reason) {
					Log.e(TAG, TAGClass + "STARTED_BY_RECONNECTION");
					return;
				}
				// if(STARTED_BY_USER == reason) : Returns result of calling
				// start

				// #4.(optional) listen for public channel
				IChordChannel channel = mChord.joinChannel("PROJECTX", mListener);

				if (null == channel) {
					Log.e(TAG, TAGClass + "fail to join public");
				}
			}

			@Override
			public void onError(int error) {
				// TODO Auto-generated method stub
				Log.e(TAG, TAGClass + "TODO onError");

			}

			@Override
			public void onNetworkDisconnected() {
				// TODO Auto-generated method stub
				Log.e(TAG, TAGClass + "TODO onNetworkDisconnected");

			}

		});
	}

}
