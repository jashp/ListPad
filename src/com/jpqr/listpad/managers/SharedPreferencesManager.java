package com.jpqr.listpad.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jpqr.listpad.models.Checklist;

public class SharedPreferencesManager {

	private static final class SharedPrefsKeys {
		private static final String LAST_LOCATION = "last_location";
		private static final String FIRST_RUN = "FIRST_RUN";
	}

	private static final String PREFERENCES_NAME = "listpad_preferences";

	private static SharedPreferencesManager mSharedPreferencesManager;

	private SharedPreferences mSharedPreferences;
	private Context mContext;

	private SharedPreferencesManager(Context context) {
		mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		mContext = context;
	}

	public static SharedPreferencesManager getInstance() {
		return mSharedPreferencesManager;
	}

	public static synchronized void init(Context context) {
		if (mSharedPreferencesManager == null) {
			mSharedPreferencesManager = new SharedPreferencesManager(context);
		}
	}

	public boolean isFirstRun() {
		return mSharedPreferences.getBoolean(SharedPrefsKeys.FIRST_RUN, true);
	}

	public void setFirstRun(boolean isFirstRun) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(SharedPrefsKeys.FIRST_RUN, isFirstRun);
		editor.commit();
	}

	public void setLastLocation(String path) {
		Editor editor = mSharedPreferences.edit();
		editor.putString(SharedPrefsKeys.LAST_LOCATION, path);
		editor.commit();
	}

	public String getLastLocation() {
		return mSharedPreferences.getString(SharedPrefsKeys.LAST_LOCATION, Checklist.DEFAULT_DIRECTORY);
	}

}
