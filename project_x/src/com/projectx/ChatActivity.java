package com.projectx;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends ListActivity {

	String personId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);
      ListView listView = getListView();
      listView.setTextFilterEnabled(true);
      Intent i = getIntent();
      if ((i != null) && (i.getStringExtra(Constants.PERSON_ID) != null)) {
        personId = i.getStringExtra(Constants.PERSON_ID);
      } else {
				personId = Constants.TEST;
        Log.e(Constants.TAG, "ChatActivity called with incorrect intent. Something is wrong here");
      }
      ArrayList<SingleChatRecord> chatRecords = ChatRecords.getAllRecordsForThePerson(personId);
      Log.d(Constants.TAG, "Hello world");
      // For now - FIXME(ashishb).
      ArrayList<String> messages = new ArrayList<String>();
      for (SingleChatRecord s : chatRecords) {
        messages.add(s.getMessage());
				Log.d(Constants.TAG, s.getMessage());
      }      
      if (messages != null) {
      	Log.d(Constants.TAG, "Hello world 2");
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, messages);
				listView.setAdapter(listAdapter);
			}
  }

	public void sendMessage() {
		EditText chatbox = (EditText) findViewById(R.id.chatbox);
		String message = chatbox.getText().toString();
		FakeChordWrapper.sendMessage(personId, message);
	}

 @Override
 public void onListItemClick(ListView l, View v, int position, long id) {
	 // Do nothing.
 }

}
