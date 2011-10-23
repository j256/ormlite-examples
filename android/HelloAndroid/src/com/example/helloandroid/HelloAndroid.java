package com.example.helloandroid;

import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * Sample Android UI activity which displays a text window when it is run.
 */
public class HelloAndroid extends OrmLiteBaseActivity<DatabaseHelper> {

	private final String LOG_TAG = getClass().getSimpleName();

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

	/**
	 * Do our sample database stuff.
	 */
	private void doSampleDatabaseStuff(String action, TextView tv) {
		// get our dao
		RuntimeExceptionDao<SimpleData, Integer> simpleDao = getHelper().getSimpleDataDao();
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
	}
}