package com.projectx;

import java.util.ArrayList;
import java.util.HashMap;

import android.provider.Settings.Secure;

public class SingleUserProfile {
	private String androidId;
	private String firstName;
	private String lastName;
	private String profilePicturePath;
	
	public SingleUserProfile(String androidId, String firstName, String lastName, String profilePicturePath) {
		this.androidId = androidId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.profilePicturePath = profilePicturePath;
	}

	public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getProfilePicturePath() {
		return profilePicturePath;
	}

	public void setProfilePicturePath(String profilePicturePath) {
		this.profilePicturePath = profilePicturePath;
	}
	
}

// Represents a collection of chat records with everyone else.
final class UserProfiles {
	// Maps unique id of the person (we are talking to)
	// to list of chat records.
	private static HashMap<String, ArrayList<SingleUserProfile>> userProfiles = new HashMap<String, ArrayList<SingleUserProfile>>();

	private UserProfiles() {
	}

	public static void addUserProfile(String androidId, String firstName, String lastName, String profilePicturePath) {
		SingleUserProfile user = new SingleUserProfile(androidId, firstName, lastName, profilePicturePath);
		if (!userProfiles.containsKey(androidId)) {
			userProfiles.put(androidId, new ArrayList<SingleUserProfile>());
		}
		userProfiles.get(androidId).add(user);
	}

}
