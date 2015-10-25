package com.example.projectqwerty001;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class AddSubjectsActivity extends Activity implements OnClickListener {

//	AddSubjectsActivity addSubjectsActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		makeFullScreenActivity();
		// setContentView(R.layout.subjects_list); // This is the Activity,
		// where
		// all Added Subjects will be
		// displayed with ListView
		if (isSubjectAdded()) {
			Intent i = new Intent("com.example.projectqwerty001.SUBJECTLIST");
			startActivity(i);
		} else {
			// start other activity if No Subjects are added
			Intent i = new Intent(
					"com.example.projectqwerty001.NOSUBJECTSADDED");
			startActivity(i);
		}
		finish();
	}

	private boolean isSubjectAdded() {
		// TODO Auto-generated method stub
		ApplicationDatabase ad = new ApplicationDatabase(this);
		ad.open();
		long entries = ad.getNumberOfEntries();
		ad.close();
		if (entries > 0) {
			return true;
		}
		return false;
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

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	

}