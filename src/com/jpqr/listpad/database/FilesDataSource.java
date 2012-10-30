package com.jpqr.listpad.database;

import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FilesDataSource {

	public class Type {
		public static final int FAVOURITE = 0;
		public static final int RECENT = 1;
	}

	// Database fields
	private SQLiteDatabase database;
	private FilesDatabaseHelper dbHelper;

	public FilesDataSource(Context context) {
		dbHelper = new FilesDatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long addFile(String uri, int type) {
		ContentValues values = new ContentValues();
		values.put(FilesDatabaseHelper.COLUMN_URI, uri);
		switch (type) {
			case Type.RECENT:
				return database.insert(FilesDatabaseHelper.TABLE_RECENT_FILES, null, values);
			case Type.FAVOURITE:
				return database.insert(FilesDatabaseHelper.TABLE_FAV_FILES, null, values);
		}
		return -1;
	}

	public int deleteFile(String uri, int type) {
		String[] whereClause = new String[] { uri };
		switch (type) {
			case Type.RECENT:
				return database.delete(FilesDatabaseHelper.TABLE_RECENT_FILES, FilesDatabaseHelper.COLUMN_URI + " = ?", whereClause);
			case Type.FAVOURITE:
				return database.delete(FilesDatabaseHelper.TABLE_FAV_FILES, FilesDatabaseHelper.COLUMN_URI + " = ?", whereClause);
		}
		return -1;
	}

	public boolean isFavourite(String uri) {
		Cursor cursor = database.query(FilesDatabaseHelper.TABLE_FAV_FILES, new String[] { FilesDatabaseHelper.COLUMN_ID, FilesDatabaseHelper.COLUMN_URI }, FilesDatabaseHelper.COLUMN_URI + " = ?", new String[] { uri },
				null, null, null);
		boolean isFavourite = cursor.getCount() > 0;
		cursor.close();
		return isFavourite;
	}


	public ArrayList<File> getAllFiles(int type) {
		String table = "";
		switch (type) {
			case Type.RECENT:
				table = FilesDatabaseHelper.TABLE_RECENT_FILES;
			break;
			case Type.FAVOURITE:
				table = FilesDatabaseHelper.TABLE_FAV_FILES;
			break;
			default:
				throw new InputMismatchException("Invalid type of file reference");
		}

		Cursor cursor = database.query(table, new String[] { FilesDatabaseHelper.COLUMN_ID, FilesDatabaseHelper.COLUMN_URI }, null, null, null, null, FilesDatabaseHelper.COLUMN_ID + " DESC", "20");
		ArrayList<File> files = new ArrayList<File>();
		if (cursor.moveToFirst()) {
			int index = cursor.getColumnIndex(FilesDatabaseHelper.COLUMN_URI);
			do {
				File file = new File(cursor.getString(index));
				if (file.exists()) {
					files.add(file);
				} else {
					//TODO remove file path from database
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		return files;
	}

}