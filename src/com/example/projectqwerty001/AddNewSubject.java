package com.example.projectqwerty001;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewSubject extends Activity implements OnClickListener {

	EditText subjectName, subjectDescription;
	Button save, cancel, removeSubject, manageNotes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		makeFullScreenActivity();
		setContentView(R.layout.add_new_subject);
		init();

	}

	protected void init() {
		subjectName = (EditText) findViewById(R.id.etSubjectName);
		subjectDescription = (EditText) findViewById(R.id.etSubjectDesc);

		save = (Button) findViewById(R.id.bSaveSubject);
		save.setOnClickListener(this);

		cancel = (Button) findViewById(R.id.bCancelSubject);
		cancel.setOnClickListener(this);

		removeSubject = (Button) findViewById(R.id.bRemoveSubject);
		removeSubject.setOnClickListener(this);
		removeSubject.setClickable(false);

		manageNotes = (Button) findViewById(R.id.bManageNotes);
		manageNotes.setOnClickListener(this);
	}

	void makeFullScreenActivity() {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bCancelSubject:
			setResult(RESULT_CANCELED);
			finish();
			break;

		case R.id.bSaveSubject:

			ApplicationDatabase ad1 = new ApplicationDatabase(this);
			String subName = subjectName.getText().toString();
			String subDesc = subjectDescription.getText().toString();
			subName = subName.trim();

			// Checking subName
			if (subName.isEmpty()) {
				Toast.makeText(getApplicationContext(),
						"Please enter Subject Name!", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			if (subName.contains("\"")) {
				Toast.makeText(getApplicationContext(),
						"Subject Name contains invalid characters",
						Toast.LENGTH_SHORT).show();
				break;
			}

			// Checking subDesc
			subDesc = subDesc.trim();
			if (subDesc.contains("\"")) {
				Toast.makeText(getApplicationContext(),
						"Description contains invalid characters",
						Toast.LENGTH_SHORT).show();
				break;
			}

			ad1.open();
			long result = ad1.createEntry(subName, subDesc);
			ad1.close();
			if (result == -1) {
				Toast.makeText(getApplicationContext(),
						"Subject Name Already Exists!", Toast.LENGTH_SHORT)
						.show();
				break;
			}

			// Now making directory for Notes
			File f = new File(SplashActivity.APPLICATION_PATH + "/" + subName);
			boolean res = f.mkdir();
			if (res == false) {
				Toast.makeText(getApplicationContext(),
						"Failed to create Directory", Toast.LENGTH_SHORT)
						.show();
				Log.i("DIR", "Directory Creation Failed (Add Subject)");
			}

			ad1.open();
			int a = (int) ad1.getNumberOfEntries();
			ad1.close();
			Toast.makeText(getApplicationContext(), "Entries : " + a,
					Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.bManageNotes:
			try {
				Intent i = getPackageManager().getLaunchIntentForPackage(
						"com.dropbox.android");
				startActivity(i);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Dropbox Application Not Installed", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		default:
			Toast.makeText(getApplicationContext(), "Not yet configured",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setResult(RESULT_CANCELED);
		// finish(); //no need of finish() for now
	}
}