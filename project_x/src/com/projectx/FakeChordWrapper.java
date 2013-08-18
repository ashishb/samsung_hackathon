package com.projectx;

import java.util.HashMap;

import android.util.Log;

public class FakeChordWrapper {

	public static HashMap<String, String> getPersonIdAndUsernames() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id1", "john smith");
		map.put("id2", "john sid");
		map.put("TEST", "test entry");
		return map;
	}

	public static void sendMessage(String personId, String message) {
		Log.i(Constants.TAG, "Sending message faked, personId: " + personId + " message:" + message);
	}
}
