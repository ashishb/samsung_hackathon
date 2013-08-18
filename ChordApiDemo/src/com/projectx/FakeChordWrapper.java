package com.projectx;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;

public class FakeChordWrapper {

	public static HashMap<String, String> getPersonIdAndUsernames() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adi", "adi");
		map.put("joe", "joe");
		map.put("TEST", "test entry");
		return map;
	}

	public static void sendMessage(Context context, String personId, String message) {
		Log.i(Constants.TAG, "Sending message, personId: " + personId + " message:" + message);
		// ChordApiDemoService mChordService = ChordApiDemoActivity.mChordService;
		// if (mChordService == null) {
		// 	Log.i(Constants.TAG, "mChordService is null.");
		// } else {
		ChordManager mChord = ChordManager.getInstance(context);
		List<IChordChannel> channelList = mChord.getJoinedChannelList();
		for (IChordChannel channel : channelList) {
			Log.d(Constants.TAG, "" + channel);
			byte[][] data = new byte[1][];
			data[0] = (personId + ":" + message).getBytes();
			channel.sendDataToAll("CHAT", data);
		}
		ChatRecords.addOutgoingMessage(personId, message);
	}
}
