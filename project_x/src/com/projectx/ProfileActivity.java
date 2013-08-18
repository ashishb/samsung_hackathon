package com.projectx;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class ProfileActivity extends Activity {

	private static int RESULT_LOAD_IMAGE = 1;
	private String profilePicturePath = null;
	private String firstName = null;
	private String lastName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		EditText fname = (EditText) findViewById(R.id.editFirstName);
		firstName = fname.getText().toString();
		EditText lname = (EditText) findViewById(R.id.editLastName);
		lastName = lname.getText().toString();
		
		final Button buttonLoadImage = (Button) findViewById(R.id.buttonPhotoPicker);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(Intent.ACTION_PICK,
				    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		final Button buttonProfileCreator = (Button) findViewById(R.id.buttonProfileCreator);
		buttonProfileCreator.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				String androidId = Secure.ANDROID_ID;
				UserProfiles.addUserProfile(androidId, firstName, lastName, profilePicturePath);
				Intent intent = new Intent(getApplication(), PeopleListActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
		    && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
			    null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			profilePicturePath = cursor.getString(columnIndex);
			cursor.close();
			
			ImageView imageView = (ImageView) findViewById(R.id.imageView);
			Bitmap bitmapDrawable = new BitmapDrawable(getApplicationContext().getResources() , BitmapFactory.decodeFile(profilePicturePath)).getBitmap();
			int dstHeight = (int) ( bitmapDrawable.getHeight() * (512.0 / bitmapDrawable.getWidth()) );
			Bitmap scaled = Bitmap.createScaledBitmap(bitmapDrawable, 512, dstHeight, true);
      imageView.setImageBitmap(scaled);

		}
	}

}
