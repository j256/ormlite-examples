package com.kagii.clickcounter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.kagii.clickcounter.R;
import com.kagii.clickcounter.data.ClickCount;
import com.kagii.clickcounter.data.ClickGroup;
import com.kagii.clickcounter.data.DatabaseHelper;

/**
 * Screen which shows the counter data and allows the counter button to be clicked.
 * 
 * @author kevingalligan
 */
public class CounterScreen extends OrmLiteBaseActivity<DatabaseHelper> {

	private static final String CLICK_COUNTER_ID = "clickCounterId";
	private final AtomicInteger countValue = new AtomicInteger();
	private final DateFormat df = new SimpleDateFormat("M/dd/yyyy hh:mm a");

	private int clickCounterid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counter_screen);

		clickCounterid = getIntent().getIntExtra(CLICK_COUNTER_ID, -1);

		try {
			Dao<ClickCount, Integer> dao = getHelper().getClickDao();
			ClickCount clickCount = dao.queryForId(clickCounterid);
			fillText(R.id.clickName, clickCount.getName());
			fillText(R.id.clickDescription, clickCount.getDescription());
			Date date = clickCount.getLastClickDate();
			if (date != null) {
				fillText(R.id.clickDate, df.format(date));
			}
			ClickGroup group = clickCount.getGroup();
			if (group.getId() == null) {
				fillText(R.id.clickGroup, "<None>");
			} else {
				getHelper().getGroupDao().refresh(group);
				fillText(R.id.clickGroup, group.getName());
			}
			countValue.set(clickCount.getValue());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		findViewById(R.id.clickButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				click();
			}
		});

		updateScreenValue();
	}

	public static void callMe(Context c, Integer clickCounterId) {
		Intent intent = new Intent(c, CounterScreen.class);
		intent.putExtra(CLICK_COUNTER_ID, clickCounterId);
		c.startActivity(intent);
	}

	private void fillText(int resId, String text) {
		TextView textView = (TextView) findViewById(resId);
		textView.setText(text);
	}

	private void click() {
		int value = countValue.incrementAndGet();

		AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {

			@Override
			protected Void doInBackground(Integer... integers) {
				Integer countId = integers[0];
				Integer countValue = integers[1];

				try {
					Dao<ClickCount, Integer> dao = getHelper().getClickDao();
					ClickCount clickCount = dao.queryForId(countId);
					if (clickCount.getValue() < countValue) {
						clickCount.changeValue(countValue);
						dao.update(clickCount);
					}
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				updateScreenValue();
				MediaPlayer mp = MediaPlayer.create(CounterScreen.this, R.raw.click);
				mp.start();
			}
		};

		asyncTask.execute(clickCounterid, value);
	}

	private void updateScreenValue() {
		((TextView) findViewById(R.id.countValue)).setText(Integer.toString(countValue.get()));
	}
}
