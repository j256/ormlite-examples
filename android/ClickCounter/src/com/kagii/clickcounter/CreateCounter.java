package com.kagii.clickcounter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.kagii.clickcounter.R;
import com.kagii.clickcounter.data.ClickCount;
import com.kagii.clickcounter.data.ClickGroup;
import com.kagii.clickcounter.data.DatabaseHelper;

/**
 * Screen on which you edit the details about a counter that is being created.
 * 
 * @author kevingalligan
 */
public class CreateCounter extends OrmLiteBaseActivity<DatabaseHelper> {

	private static final String CLICK_COUNT_ID = "clickCountId";

	private EditText clickName;
	private EditText clickDescription;
	private Spinner clickGroup;
	private Button addGroup;
	private Button createCounter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_counter);

		clickName = (EditText) findViewById(R.id.clickName);
		clickDescription = (EditText) findViewById(R.id.clickDescription);
		clickGroup = (Spinner) findViewById(R.id.clickGroup);
		addGroup = (Button) findViewById(R.id.addGroup);

		addGroup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showDialog(123);
			}
		});

		createCounter = (Button) findViewById(R.id.createCounter);

		createCounter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					ClickCount clickCount = saveToObj();
					Dao<ClickCount, Integer> dao = getHelper().getClickDao();
					boolean alreadyCreated = false;
					if (clickCount.getId() != null) {
						ClickCount dbCount = dao.queryForId(clickCount.getId());
						if (dbCount != null) {
							clickCount.changeValue(dbCount.getValue());
							dao.update(clickCount);
							alreadyCreated = true;
						}
					}

					if (alreadyCreated) {
						finish();
					} else {
						dao.create(clickCount);
						CounterScreen.callMe(CreateCounter.this, clickCount.getId());
					}
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});
		reInit(savedInstanceState);
	}

	public static void callMe(Context c) {
		c.startActivity(new Intent(c, CreateCounter.class));
	}

	public static void callMe(Context c, Integer clickCountId) {

		Intent intent = new Intent(c, CreateCounter.class);
		intent.putExtra(CLICK_COUNT_ID, clickCountId);
		c.startActivity(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText input = new EditText(CreateCounter.this);

		builder.setView(input);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				input.setText("");
				ClickGroup group = new ClickGroup();
				group.setName(value);
				try {
					Dao<ClickGroup, Integer> dao = getHelper().getGroupDao();
					dao.create(group);
					refreshGroupSpinnerEntries(dao);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}

				selectSpinnerGroup(group.getId());
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		return alert;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ClickCount c = saveToObj();
		outState.putSerializable("CLICK_COUNT", c);
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			Dao<ClickCount, Integer> countDao = getHelper().getClickDao();
			Dao<ClickGroup, Integer> groupDao = getHelper().getGroupDao();

			refreshGroupSpinnerEntries(groupDao);

			if (savedInstanceState != null) {
				loadFromObj((ClickCount) savedInstanceState.get(CLICK_COUNT_ID));
			} else {
				int clickCountId = getClickCountId();

				if (clickCountId > -1) {
					ClickCount clickCount = countDao.queryForId(clickCountId);
					if (clickCount != null) {
						loadFromObj(clickCount);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void refreshGroupSpinnerEntries(Dao<ClickGroup, Integer> groupDao) throws SQLException {

		List<ClickGroup> clickGroups = new ArrayList<ClickGroup>();
		clickGroups.add(new ClickGroup());
		clickGroups.addAll(groupDao.queryForAll());
		ArrayAdapter<ClickGroup> adapter =
				new ArrayAdapter<ClickGroup>(this, android.R.layout.simple_spinner_item, clickGroups);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clickGroup.setAdapter(adapter);
	}

	private ClickCount saveToObj() {
		ClickCount c = new ClickCount();

		int clickCountId = getClickCountId();
		if (clickCountId > -1) {
			c.setId(clickCountId);
		}
		if (clickGroup.getSelectedItem() != null) {
			ClickGroup group = (ClickGroup) clickGroup.getSelectedItem();
			if (group.getId() != null) {
				c.setGroup(group);
			}
		}

		c.setDescription(clickDescription.getText().toString());
		c.setName(clickName.getText().toString());
		return c;
	}

	private int getClickCountId() {
		return getIntent().getIntExtra(CLICK_COUNT_ID, -1);
	}

	private void loadFromObj(ClickCount c) throws SQLException {
		if (c.getGroup().getId() != null) {
			getHelper().getGroupDao().refresh(c.getGroup());
			selectSpinnerGroup(c.getGroup().getId());
		}
		clickDescription.setText(c.getDescription());
		clickName.setText(c.getName());
	}

	private void selectSpinnerGroup(Integer clickGroupId) {
		SpinnerAdapter ad = clickGroup.getAdapter();
		int count = ad.getCount();
		for (int i = 0; i < count; i++) {
			ClickGroup group = (ClickGroup) ad.getItem(i);

			if (group.getId() != null && group.getId().equals(clickGroupId)) {
				clickGroup.setSelection(i);
				break;
			}
		}
	}
}
