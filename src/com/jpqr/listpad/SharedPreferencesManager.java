package com.jpqr.listpad;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SharedPreferencesManager {

	private static final class SharedPrefsKeys {
		private static final String RECENT_FILES = "recent_files";
		private static final String FAVOURITE_FILES = "favourite_files";
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

	public void setRecentFiles(HashSet<String> recentFiles) {
		Editor editor = mSharedPreferences.edit();
		editor.putStringSet(SharedPrefsKeys.RECENT_FILES, recentFiles);
		editor.commit();
	}

	public Set<String> getRecentFiles() {
		return mSharedPreferences.getStringSet(SharedPrefsKeys.RECENT_FILES, new HashSet<String>());
	}
	
	public void setFavouriteFiles(HashSet<String> recentFiles) {
		Editor editor = mSharedPreferences.edit();
		editor.putStringSet(SharedPrefsKeys.FAVOURITE_FILES, recentFiles);
		editor.commit();
	}

	public Set<String> getFavouriteFiles() {
		return mSharedPreferences.getStringSet(SharedPrefsKeys.FAVOURITE_FILES, new HashSet<String>());
	}


	private void clearSharedPreferences() {
		Editor editor = mSharedPreferences.edit();
		editor.putStringSet(SharedPrefsKeys.RECENT_FILES, null);
		editor.commit();
	}

}
