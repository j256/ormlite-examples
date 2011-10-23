package com.example.hellonobase;

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
 * locally using the {@link #databaseHelper} field, the {@link #getHelper()} private method, and the call to
 * {@link OpenHelperManager#releaseHelper()} inside of the {@link #onDestroy()} method.
 * </p>
 */
public class HelloNoBase extends Activity {

	private final String LOG_TAG = getClass().getSimpleName();
	/**
	 * You'll need this in your class to cache the helper in the class.
	 */
	private DatabaseHelper databaseHelper = null;

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
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	/**
	 * You'll need this in your class to get the helper from the manager once per class.
	 */
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return databaseHelper;
	}

	/**
	 * Do our sample database stuff as an example.
	 */
	private void doSampleDatabaseStuff(String action, TextView tv) {
		try {
			// get our dao
			Dao<SimpleData, Integer> simpleDao = getHelper().getSimpleDataDao();
			// query for all of the data objects in the database
			List<SimpleData> list = simpleDao.queryForAll();
			// our string builder for building the content-view
			StringBuilder sb = new StringBuilder();
			sb.append("got ").append(list.size()).append(" entries in ").append(action).append("\n");

			// if we already have items in the database
			int simpleC = 0;
			for (SimpleData simple : list) {
				sb.append("------------------------------------------\n");
				sb.append("[").append(simpleC).append("] = ").append(simple).append("\n");
				simpleC++;
			}
			sb.append("------------------------------------------\n");
			for (SimpleData simple : list) {
				simpleDao.delete(simple);
				sb.append("deleted id ").append(simple.id).append("\n");
				Log.i(LOG_TAG, "deleting simple(" + simple.id + ")");
				simpleC++;
			}

			int createNum;
			do {
				createNum = new Random().nextInt(3) + 1;
			} while (createNum == list.size());
			for (int i = 0; i < createNum; i++) {
				// create a new simple object
				long millis = System.currentTimeMillis();
				SimpleData simple = new SimpleData(millis);
				// store it in the database
				simpleDao.create(simple);
				Log.i(LOG_TAG, "created simple(" + millis + ")");
				// output it
				sb.append("------------------------------------------\n");
				sb.append("created new entry #").append(i + 1).append(":\n");
				sb.append(simple).append("\n");
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// ignore
				}
			}

			tv.setText(sb.toString());
			Log.i(LOG_TAG, "Done with page at " + System.currentTimeMillis());
		} catch (SQLException e) {
			Log.e(LOG_TAG, "Database exception", e);
			tv.setText("Database exeption: " + e);
			return;
		}
	}
}