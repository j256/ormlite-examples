package com.kagii.clickcounter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kagii.clickcounter.data.ClickCount;
import com.kagii.clickcounter.data.ClickGroup;
import com.kagii.clickcounter.data.DatabaseHelper;

/**
 * Lists the counters and allows you to select one for usage or create a new one.
 * 
 * @author kevingalligan
 */
public class ClickConfig extends OrmLiteBaseActivity<DatabaseHelper> {

	private final DateFormat df = new SimpleDateFormat("M/dd/yy HH:mm");

	private ListView listView;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.createCounter).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				CreateCounter.callMe(ClickConfig.this);
			}
		});

		listView = (ListView) findViewById(R.id.clickList);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				ClickCount count = (ClickCount) listView.getAdapter().getItem(i);
				CounterScreen.callMe(ClickConfig.this, count.getId());
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				ClickCount count = (ClickCount) listView.getAdapter().getItem(i);
				CreateCounter.callMe(ClickConfig.this, count.getId());
				return true;
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			fillList();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void fillList() throws SQLException {
		Log.i(ClickConfig.class.getName(), "Show list again");
		Dao<ClickCount, Integer> dao = getHelper().getClickDao();
		QueryBuilder<ClickCount, Integer> builder = dao.queryBuilder();
		builder.orderBy(ClickCount.DATE_FIELD_NAME, false).limit(30);
		List<ClickCount> list = dao.query(builder.prepare());
		ArrayAdapter<ClickCount> arrayAdapter = new CountsAdapter(this, R.layout.count_row, list);
		listView.setAdapter(arrayAdapter);
	}

	private class CountsAdapter extends ArrayAdapter<ClickCount> {

		public CountsAdapter(Context context, int textViewResourceId, List<ClickCount> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.count_row, null);
			}
			ClickCount count = getItem(position);

			ClickGroup group = count.getGroup();
			if (group == null) {
				fillText(v, R.id.clickGroup, "");
			} else {
				try {
					getHelper().getGroupDao().refresh(group);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				fillText(v, R.id.clickGroup, group.getName());
			}
			fillText(v, R.id.clickName, count.getName());
			if (count.getLastClickDate() == null) {
				fillText(v, R.id.clickDate, "");
			} else {
				fillText(v, R.id.clickDate, df.format(count.getLastClickDate()));
			}
			fillText(v, R.id.clickCount, Integer.toString(count.getValue()));

			return v;
		}

		private void fillText(View v, int id, String text) {
			TextView textView = (TextView) v.findViewById(id);
			textView.setText(text == null ? "" : text);
		}
	}
}
