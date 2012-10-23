package com.jpqr.listpad.managers;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;


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
	private boolean useStringSet() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}
	

	private String setToString(Set<String> set) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : set) {
			stringBuilder.append(s + "|");
		}
		return stringBuilder.toString();
	}
	
	private Set<String> stringToSet(String string) {
		String[] strings = string.split("|");
		Set<String> set = new HashSet<String>();
		for (String s : strings) {
			set.add(s);
		}
		return set;
		
	}
	
	public Set<String> getFavouriteFiles() {
		return getFiles(SharedPrefsKeys.FAVOURITE_FILES);
	}
	
	
	public Set<String> getRecentFiles() {
		return getFiles(SharedPrefsKeys.RECENT_FILES);
	}
	
	private Set<String> getFiles(String key) {
		if (useStringSet()) {
			return mSharedPreferences.getStringSet(key, new HashSet<String>());
		} else {
			return stringToSet(mSharedPreferences.getString(key, ""));
		}
	}

	public void setRecentFiles(Set<String> files) {
		setFiles(files, SharedPrefsKeys.RECENT_FILES);
	}
	
	public void setFavouriteFiles(Set<String> files) {
		setFiles(files, SharedPrefsKeys.FAVOURITE_FILES);
	}
	
	private void setFiles(Set<String> files, String key) {
		Editor editor = mSharedPreferences.edit();
		if (useStringSet()) {
			editor.putStringSet(key, files);
		} else {
			editor.putString(key, setToString(files));

		}
		editor.commit();
	}

	public void addRecentFile(String filePath) {
		Set<String> files = getRecentFiles();
		files.add(filePath);
		setRecentFiles(files);
	}
	
	public void addFavouriteFile(String filePath) {
		Set<String> files = getFavouriteFiles();
		files.add(filePath);
		setFavouriteFiles(files);
	}
	
	public void removeRecentFile(String filePath) {
		Set<String> files = getRecentFiles();
		files.remove(filePath);
		setRecentFiles(files);
	}
	
	public void removeFavouriteFile(String filePath) {
		Set<String> files = getFavouriteFiles();
		files.remove(filePath);
		setFavouriteFiles(files);
	}
	
	public boolean isFavouriteFile(String filePath) {
		Set<String> files = getFavouriteFiles();
		return files.contains(filePath);
	}

}
