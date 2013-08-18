package com.projectx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.samsung.chord.samples.apidemo.R;

public class PeopleListActivity extends ListActivity {

	ArrayList<String> usernames, personIds;
	HashMap<String, String> personIdAndUsernames;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_people_list);
			String personId;
      ListView listView = getListView();
      listView.setTextFilterEnabled(true);
			if (SingleUserProfile.getUserProfile() == null) {
				Log.e(Constants.TAG, "Single user profile does not exist.");
				return;
			}

			personIdAndUsernames = FakeChordWrapper.getPersonIdAndUsernames();
			usernames = new ArrayList<String>();
			personIds = new ArrayList<String>();
			for (Entry<String, String> entry : personIdAndUsernames.entrySet()) {
				personIds.add(entry.getKey());
				usernames.add(entry.getValue());
			}
      if (usernames != null) {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, usernames);
				listView.setAdapter(listAdapter);
			}
  }

	@Override
  public void onListItemClick(ListView l, View v, int position, long id) {
		String personId = personIds.get(position);
		String username = usernames.get(position);
		Log.d(Constants.TAG, String.format("Position: %d, username:%s, personId:%s", position, username, personId));
    Intent i = new Intent(getApplication(), ChatActivity.class);
		i.putExtra(Constants.PERSON_ID, personId);
    startActivity(i);
  }
}
