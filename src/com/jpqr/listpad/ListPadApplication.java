package com.jpqr.listpad;

import com.jpqr.listpad.managers.SharedPreferencesManager;

import android.app.Application;

public class ListPadApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferencesManager.init(getApplicationContext());
	}
}
