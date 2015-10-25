package com.example.projectqwerty001;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AddNewTimetableEntry extends Activity implements OnClickListener,
		OnItemSelectedListener {

	Button changeStartTime, changeEndTime, save;
	TextView startTime, endTime;
	Spinner selectSubject;

	public static final int ID_DIALOG_START_TIME = 0;
	public static final int ID_DIALOG_END_TIME = 1;
	public static final int START_HOUR = 9;
	public static final int START_MIN = 0;

	ApplicationDatabase adb;

	SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");

	Date dStartTime = null, dEndTime = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_timetable_entry);
		init();
		// getting subjects name fromApplicationDatabase to String Array

		// ttdb.open();
		// String subjectNamesArray[] = new String[(int) ttdb
		// .getNumberOfEntries(TimetableDatabase
		// .getTableName(NewTimetableEntry.currentlyOpenedDay))];
		// ttdb.close();
		// st a

		ArrayList<String> subjectNameArrayList = new ArrayList<String>();
		adb.open();
		subjectNameArrayList = adb.getSubjects();
		adb.close();

		// ArrayList<TimetableEntryInformation> tiList = new
		// ArrayList<TimetableEntryInformation>();
		// ttdb.open();
		// tiList = ttdb.getSubjects(TimetableDatabase
		// .getTableName(NewTimetableEntry.currentlyOpenedDay));
		// ttdb.close();
		// for (int i = 0; i < tiList.size(); i++) {
		// subjectNamesArray[i] = tiList.get(i).getSubjectName();
		// }

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				AddNewTimetableEntry.this,
				android.R.layout.simple_spinner_item, subjectNameArrayList);
		selectSubject.setAdapter(adapter);
		Toast.makeText(getApplicationContext(), "Adapter Set",
				Toast.LENGTH_LONG).show();

		// ///
	}

	public void init() {
		changeStartTime = (Button) findViewById(R.id.bSetStartTime);
		changeStartTime.setOnClickListener(this);

		changeEndTime = (Button) findViewById(R.id.bSetEndTime);
		changeEndTime.setOnClickListener(this);

		selectSubject = (Spinner) findViewById(R.id.spSelectSubject);
		selectSubject.setOnItemSelectedListener(this);

		save = (Button) findViewById(R.id.bSave_newTTEntry);
		save.setOnClickListener(this);

		startTime = (TextView) findViewById(R.id.tvStartTime_newTTEntry);

		endTime = (TextView) findViewById(R.id.tvEndTime_newTTEntry);

		adb = new ApplicationDatabase(this);

	}

	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bSetStartTime:
			showDialog(ID_DIALOG_START_TIME);
			break;
		case R.id.bSetEndTime:
			showDialog(ID_DIALOG_END_TIME);
			break;
		case R.id.bSave_newTTEntry:
			// This one's final for the Save Button
			String n = (String) selectSubject.getSelectedItem();
			Toast.makeText(getApplicationContext(), n, Toast.LENGTH_SHORT)
					.show();
			// String a = TimeHandler.convertTo24Hr("01:30 am");
			// Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG)
			// .show();

			String subjectName = (String) selectSubject.getSelectedItem();
			// String sStartTime = TimeHandler.convertTo24Hr(startTime.getText()
			// .toString());
			// String sEndTime = TimeHandler.convertTo24Hr(endTime.getText()
			// .toString());

			if (dStartTime == null) {
				Toast.makeText(getApplicationContext(),
						"Enter a proper Start Time", Toast.LENGTH_SHORT).show();
				break;
			}

			if (dEndTime == null) {
				Toast.makeText(getApplicationContext(),
						"Enter a proper End Time", Toast.LENGTH_SHORT).show();
			}

			if (dStartTime.equals(dEndTime)) {
				Toast.makeText(getApplicationContext(),
						"Start and End times are same!", Toast.LENGTH_SHORT)
						.show();
				break;
			}

			if (dStartTime.after(dEndTime)) {
				Toast.makeText(getApplicationContext(),
						"End time is before Start time", Toast.LENGTH_SHORT)
						.show();
				break;

			}

			// int compareResult = sStartTime.compareTo(sEndTime);
			// if (compareResult > 0) {
			// Toast.makeText(getApplicationContext(),
			// "End time is before Start time", Toast.LENGTH_SHORT)
			// .show();
			// break;
			// }
			// Toast.makeText(getApplicationContext(),
			// sStartTime + "  " + sEndTime, Toast.LENGTH_SHORT).show();

			TimeRange currentTimeRange = new TimeRange(dStartTime, dEndTime);

			ArrayList<TimeRange> trList = new ArrayList<TimeRange>();

			TimetableDatabase ttdb1 = new TimetableDatabase(this);
			ttdb1.open();
			try {
				trList = ttdb1.getTimeRanges(TimetableDatabase
						.getTableName(NewTimetableEntry.currentlyOpenedDay));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ttdb1.close();

			boolean res = true;

			for (int i = 0; i < trList.size(); i++) {
				boolean result = TimeHandler.isConflict(currentTimeRange,
						trList.get(i));
				if (result == true) {
					Toast.makeText(getApplicationContext(),
							"Time Ranges Overlap", Toast.LENGTH_SHORT).show();
					res = false;
					break;
				}
			}

			if (res == false) {
				break;
			}

			// finally, after all checks, adding the stuff to database. huh,
			// SIGHS!
			TimetableDatabase ttdb = new TimetableDatabase(this);
			try {
				ttdb.open();
				long result = ttdb.createEntry(TimetableDatabase
						.getTableName(NewTimetableEntry.currentlyOpenedDay),
						dStartTime, dEndTime, (String) selectSubject
								.getSelectedItem());
				ttdb.close();
				if (result == -1) {
					Toast.makeText(getApplicationContext(),
							"Another Subject Starts OR Ends at Same Time",
							Toast.LENGTH_SHORT).show();
					break;
				}
			} catch (SQLiteConstraintException e) {
				Toast.makeText(getApplicationContext(),
						"Another Subject Starts OR Ends at Same Time",
						Toast.LENGTH_SHORT).show();
				break;
			} catch (SQLException se) {
				Toast.makeText(getApplicationContext(),
						"Something went Wrong : " + se, Toast.LENGTH_LONG)
						.show();
			}
			finish();
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch (id) {
		case ID_DIALOG_START_TIME:
			return new TimePickerDialog(this, startTimeSetListener, START_HOUR,
					START_MIN, false);
		case ID_DIALOG_END_TIME:
			return new TimePickerDialog(this, endTimeSetListener, START_HOUR,
					START_MIN, false);
		}
		return super.onCreateDialog(id, args);
	}

	private OnTimeSetListener startTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			String sHourOfDay = String.valueOf(hourOfDay);
			if (sHourOfDay.length() == 1) {
				sHourOfDay = "0" + sHourOfDay;
			}

			String sMinute = String.valueOf(minute);
			if (sMinute.length() == 1) {
				sMinute = "0" + sMinute;
			}

			String time = sHourOfDay + ":" + sMinute;

			try {
				dStartTime = parseFormat.parse(time);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			startTime.setText(displayFormat.format(dStartTime));
			// //
			// String ampm = "am";
			// if (hourOfDay >= 12) {
			// hourOfDay = hourOfDay - 12;
			// ampm = "pm";
			// }
			//
			// String sHourOfDay = String.valueOf(hourOfDay);
			// if (sHourOfDay.length() == 1) {
			// sHourOfDay = "0" + sHourOfDay;
			// }
			//
			// String sMinute = String.valueOf(minute);
			// if (sMinute.length() == 1) {
			// sMinute = "0" + sMinute;
			// }
			//
			// startTime.setText("" + sHourOfDay + ":" + sMinute + " " + ampm);

			changeStartTime.setText("Change");

			Toast.makeText(getApplicationContext(),
					displayFormat.format(dStartTime), Toast.LENGTH_LONG).show();

		}
	};

	private OnTimeSetListener endTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub

			String sHourOfDay = String.valueOf(hourOfDay);
			if (sHourOfDay.length() == 1) {
				sHourOfDay = "0" + sHourOfDay;
			}

			String sMinute = String.valueOf(minute);
			if (sMinute.length() == 1) {
				sMinute = "0" + sMinute;
			}

			String time = sHourOfDay + ":" + sMinute;

			try {
				dEndTime = parseFormat.parse(time);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// String ampm = "am";
			// if (hourOfDay >= 12) {
			// hourOfDay = hourOfDay - 12;
			// ampm = "pm";
			// }

			// String sHourOfDay = String.valueOf(hourOfDay);
			// if (sHourOfDay.length() == 1) {
			// sHourOfDay = "0" + sHourOfDay;
			// }
			//
			// String sMinute = String.valueOf(minute);
			// if (sMinute.length() == 1) {
			// sMinute = "0" + sMinute;
			// }

			endTime.setText(displayFormat.format(dEndTime));

			changeEndTime.setText("Change");

			Toast.makeText(getApplicationContext(),
					displayFormat.format(dEndTime), Toast.LENGTH_LONG).show();

		}
	};

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		adb.open();
		@SuppressWarnings("unused")
		String subName = adb.getSubjectName(position);
		adb.close();
		// Toast.makeText(getApplicationContext(), subName, Toast.LENGTH_SHORT)
		// .show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
