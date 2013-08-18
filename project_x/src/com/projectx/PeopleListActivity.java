package com.projectx;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PeopleListActivity extends ListActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_people_list);
			String personId;
      ListView listView = getListView();
      listView.setTextFilterEnabled(true);
      Intent i = getIntent();
      if ((i != null) && (i.getStringExtra(Constants.PERSON_ID) != null)) {
        personId = i.getStringExtra(Constants.PERSON_ID);
      } else {
				personId = Constants.TEST;
        Log.e(Constants.TAG, "PeopleListActivity called with incorrect intent. Something is wrong here");
      }
      ArrayList<SingleChatRecord> chatRecords = ChatRecords.getAllRecordsForThePerson(personId);
      Log.d(Constants.TAG, "Hello world");
      // For now - FIXME(ashishb).
      ArrayList<String> messages = new ArrayList<String>();
      for (SingleChatRecord s : chatRecords) {
        messages.add(s.getMessage());
				Log.d(Constants.TAG, s.getMessage());
      }      
      if (chatRecords != null) {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, messages);
				listView.setAdapter(listAdapter);
			}
  }
}
