package com.projectx;

import java.util.ArrayList;
import java.util.HashMap;

import android.provider.Settings.Secure;

public class SingleUserProfile {
	private static SingleUserProfile userProfile = null;
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

	public static void setUserProfile(SingleUserProfile profile) {
		userProfile = profile;
	}

	public static SingleUserProfile getUserProfile() {
		return userProfile;
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
