package com.example.helloworld;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	final static String TAG = "MainActivity";

	
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
	}

	
	protected void onPause() {
		Log.i(TAG, "onPause");
	}

	
	protected void onRestart() {
		Log.i(TAG, "onRestart");
	}

	
	protected void onResume() {
		Log.i(TAG, "onResume");
	}

	
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

	
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
