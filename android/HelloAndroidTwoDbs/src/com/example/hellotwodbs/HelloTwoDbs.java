package com.example.hellotwodbs;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

/**
 * Sample Android UI activity which displays a text window when it is run.
 * 
 * <p>
 * <b>NOTE:</b> This does <i>not</i> extend the {@link OrmLiteBaseActivity} but instead manages the helper itself
 * locally using the {@link #databaseHelper1} field, the {@link #getHelper1()} private method, and the call to
 * {@link OpenHelperManager#releaseHelper()} inside of the {@link #onDestroy()} method.
 * </p>
 */
public class HelloTwoDbs extends Activity {

	private final String LOG_TAG = getClass().getSimpleName();
	/**
	 * You'll need this in your class to cache the helper in the class.
	 */
	private DatabaseHelper1 databaseHelper1 = null;
	private DatabaseHelper2 databaseHelper2 = null;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
		TextView tv = new TextView(this);
		doSampleDatabaseStuff("onCreate", tv);
		setContentView(tv);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		/*
		 * You'll need this in your class to release the helper when done.
		 */
		if (databaseHelper1 != null) {
			databaseHelper1.close();
			databaseHelper1 = null;
		}
		if (databaseHelper2 != null) {
			databaseHelper2.close();
			databaseHelper2 = null;
		}
	}

	/**
	 * You'll need this in your class to get the helper from the manager once per class.
	 */
	private DatabaseHelper1 getHelper1() {
		if (databaseHelper1 == null) {
			databaseHelper1 = DatabaseHelper1.getHelper(this);
		}
		return databaseHelper1;
	}

	/**
	 * You'll need this in your class to get the helper from the manager once per class.
	 */
	private DatabaseHelper2 getHelper2() {
		if (databaseHelper2 == null) {
			databaseHelper2 = DatabaseHelper2.getHelper(this);
		}
		return databaseHelper2;
	}

	/**
	 * Do our sample database stuff as an example.
	 */
	private void doSampleDatabaseStuff(String action, TextView tv) {
		try {
			// our string builder for building the content-view
			StringBuilder sb = new StringBuilder();
			doSimpleDatabaseStuff(action, sb);
			sb.append("------------------------------------------\n");
			doComplexDatabaseStuff(action, sb);
			tv.setText(sb.toString());
			Log.i(LOG_TAG, "Done with page at " + System.currentTimeMillis());
		} catch (SQLException e) {
			Log.e(LOG_TAG, "Database exception", e);
			tv.setText("Database exeption: " + e);
			return;
		}
	}

	private StringBuilder doSimpleDatabaseStuff(String action, StringBuilder sb) throws SQLException {
		// get our dao
		Dao<SimpleData, Integer> simpleDao = getHelper1().getSimpleDataDao();
		// query for all of the data objects in the database
		List<SimpleData> list = simpleDao.queryForAll();
		sb.append("got ").append(list.size()).append(" SimpleData entries in ").append(action).append("\n");
		sb.append("------------------------------------------\n");

		// if we already have items in the database
		int objC = 0;
		for (SimpleData simple : list) {
			sb.append("[").append(objC).append("] = ").append(simple).append("\n");
			objC++;
		}
		sb.append("------------------------------------------\n");
		for (SimpleData simple : list) {
			simpleDao.delete(simple);
			sb.append("deleted SimpleData id ").append(simple.id).append("\n");
			Log.i(LOG_TAG, "deleting SimpleData(" + simple.id + ")");
		}

		int createNum;
		do {
			createNum = new Random().nextInt(2) + 1;
		} while (createNum == list.size());
		for (int i = 0; i < createNum; i++) {
			// create a new simple object
			long millis = System.currentTimeMillis();
			SimpleData simple = new SimpleData(millis);
			// store it in the database
			simpleDao.create(simple);
			Log.i(LOG_TAG, "created SimpleData(" + millis + ")");
			// output it
			sb.append("------------------------------------------\n");
			sb.append("created SimpleData entry #").append(i + 1).append(":\n");
			sb.append(simple).append("\n");
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		return sb;
	}

	private StringBuilder doComplexDatabaseStuff(String action, StringBuilder sb) throws SQLException {
		// get our dao
		Dao<ComplexData, Integer> complexDao = getHelper2().getComplexDataDao();
		// query for all of the data objects in the database
		List<ComplexData> list = complexDao.queryForAll();
		sb.append("got ").append(list.size()).append(" ComplexData entries in ").append(action).append("\n");
		sb.append("------------------------------------------\n");

		// if we already have items in the database
		int objC = 0;
		for (ComplexData simple : list) {
			sb.append("[").append(objC).append("] = ").append(simple).append("\n");
			objC++;
		}
		sb.append("------------------------------------------\n");
		for (ComplexData simple : list) {
			complexDao.delete(simple);
			sb.append("deleted ComplexData id ").append(simple.id).append("\n");
			Log.i(LOG_TAG, "deleting ComplexData simple(" + simple.id + ")");
		}

		int createNum;
		do {
			createNum = new Random().nextInt(2) + 1;
		} while (createNum == list.size());
		for (int i = 0; i < createNum; i++) {
			// create a new simple object
			long millis = System.currentTimeMillis();
			ComplexData complex = new ComplexData(millis);
			// store it in the database
			complexDao.create(complex);
			Log.i(LOG_TAG, "created ComplexData(" + millis + ")");
			// output it
			sb.append("------------------------------------------\n");
			sb.append("created ComplexData entry #").append(i + 1).append(":\n");
			sb.append(complex).append("\n");
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		return sb;
	}
}