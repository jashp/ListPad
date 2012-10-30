package com.jpqr.listpad.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FilesDatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_RECENT_FILES = "files_recent";
	public static final String TABLE_FAV_FILES = "files_favourite";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_URI = "uri";

	private static final String DATABASE_NAME = "files.db";
	private static final int DATABASE_VERSION = 7;

	// Database creation sql statement
	private static final String DATABASE_CREATE_RECENT = "CREATE TABLE " + TABLE_RECENT_FILES + "("
												+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+ COLUMN_URI + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE " 
												+ ");";
	private static final String DATABASE_CREATE_FAV = "CREATE TABLE " + TABLE_FAV_FILES + "("
												+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+ COLUMN_URI + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE " 
												+ ");";
	

	public FilesDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_RECENT);
		database.execSQL(DATABASE_CREATE_FAV);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(FilesDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_FILES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV_FILES);
		onCreate(db);
	}

}
