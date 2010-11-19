package com.example.notifyservice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Activity which starts the service when it called.
 * 
 * @author kevingalligan
 */
public class MyActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.startService).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				GetStuffService.setAlarm(MyActivity.this, 20);
			}
		});
	}
}
