package com.example.notifyservice.data;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Helper class which creates/updates our database and provides the DAOs.
 * 
 * @author kevingalligan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "notify.db";
	private static final int DATABASE_VERSION = 4;
	private final String LOG_NAME = getClass().getName();

	private Dao<Thing, Integer> thingDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Thing.class);
		} catch (SQLException e) {
			Log.e(LOG_NAME, "Could not create new table for Thing", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Thing.class, true);
			onCreate(sqLiteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(LOG_NAME, "Could not upgrade the table for Thing", e);
		}
	}

	public Dao<Thing, Integer> getThingDao() throws SQLException {
		if (thingDao == null) {
			thingDao = getDao(Thing.class);
		}
		return thingDao;
	}
}
