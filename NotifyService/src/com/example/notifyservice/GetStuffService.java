package com.example.notifyservice;

import java.sql.SQLException;
import java.util.Random;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.notifyservice.data.DatabaseHelper;
import com.example.notifyservice.data.Thing;
import com.j256.ormlite.android.apptools.OrmLiteBaseService;

/**
 * Service which creates a new Thing entry in the database whenever it is started. An alarm calls it every 20 seconds.
 * 
 * @author kevingalligan
 */
public class GetStuffService extends OrmLiteBaseService<DatabaseHelper> {

	private static PendingIntent pi;
	private final static String LOG_NAME = GetStuffService.class.getName();

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Thing thing = new Thing();
		Random random = new Random();
		thing.setDescription("My thing " + random.nextInt(1000));
		try {
			getHelper().getThingDao().create(thing);
		} catch (SQLException e) {
			throw new RuntimeException("Could not create a new Thing in the database", e);
		}

		Intent intent = new Intent(this, ThingActivity.class);
		intent.putExtra(ThingActivity.KEY_THING_ID, thing.getId());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// The ticker text, this uses a formatted string so our message could be localized
		String tickerText = "New Thing: " + thing.getDescription();

		createNewNotif(this, tickerText, "NotifyService", "Test app for ORMLite", contentIntent, 1234, R.drawable.icon);

		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void setAlarm(Context c, int seconds) {
		AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

		if (pi != null) {
			try {
				mgr.cancel(pi);
			} catch (Exception e) {
				// Heyo
				Log.e(LOG_NAME, "unable to cancel pending intent");
			}
		}

		Intent intent = new Intent(c, GetStuffService.class);
		pi = PendingIntent.getService(c, 0, intent, 0);

		mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), seconds * 1000, pi);
	}

	private void createNewNotif(Context context, String tickerText, String title, String content, PendingIntent i,
			int notifId, int icon) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// construct the Notification object.
		Notification notif = new Notification(icon, tickerText, System.currentTimeMillis());

		// Set the info for the views that show in the notification panel.
		notif.setLatestEventInfo(context, title, content, i);

		notif.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
		notif.defaults = Notification.DEFAULT_VIBRATE;

		/*
		 * Note that we use R.layout.incoming_message_panel as the ID for the notification. It could be any integer you
		 * want, but we use the convention of using a resource id for a string related to the notification. It will
		 * always be a unique number within your application.
		 */
		nm.notify(notifId, notif);
	}
}
