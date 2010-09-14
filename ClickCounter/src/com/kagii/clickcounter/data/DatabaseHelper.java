package com.kagii.clickcounter.data;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteAndroidDatabaseType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper which creates and upgrades the database and provides the DAOs for the app.
 * 
 * @author kevingalligan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	/************************************************
	 * Suggested Copy/Paste code. Everything from here to the done block.
	 ************************************************/

	private static final String DATABASE_NAME = "click.db";
	private static final int DATABASE_VERSION = 3;

	private DatabaseType databaseType = new SqliteAndroidDatabaseType();
	private Dao<ClickGroup, Integer> groupDao;
	private Dao<ClickCount, Integer> clickDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/************************************************
	 * Suggested Copy/Paste Done
	 ************************************************/

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(databaseType, connectionSource, ClickGroup.class);
			TableUtils.createTable(databaseType, connectionSource, ClickCount.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(databaseType, connectionSource, ClickGroup.class, true);
			TableUtils.dropTable(databaseType, connectionSource, ClickCount.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
					+ newVer, e);
		}
	}

	public Dao<ClickGroup, Integer> getGroupDao() throws SQLException {
		if (groupDao == null) {
			groupDao = BaseDaoImpl.createDao(databaseType, getConnectionSource(), ClickGroup.class);
		}
		return groupDao;
	}

	public Dao<ClickCount, Integer> getClickDao() throws SQLException {
		if (clickDao == null) {
			clickDao = BaseDaoImpl.createDao(databaseType, getConnectionSource(), ClickCount.class);
		}
		return clickDao;
	}
}
