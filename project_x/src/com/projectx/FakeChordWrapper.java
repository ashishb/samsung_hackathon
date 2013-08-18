package com.projectx;

import java.util.HashMap;

public class FakeChordWrapper {

	public static HashMap<String, String> getPersonIdAndUsernames() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id1", "john smith");
		map.put("id2", "john sid");
		map.put("TEST", "test entry");
		return map;
	}
}
