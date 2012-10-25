package com.jpqr.adapters;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class FileListAdapter extends ArrayAdapter<File> {
	private final static int RESOURCE_ID = android.R.layout.simple_list_item_1;
	private boolean mOnlyFiles;
	private ArrayList<File> mFiles;
	
	public FileListAdapter(Context context, ArrayList<File> files, boolean onlyFiles) {
		super(context, RESOURCE_ID, files);
		mOnlyFiles = onlyFiles;
		mFiles = files;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(RESOURCE_ID, null);
		}
		TextView itemName = (TextView) view.findViewById(android.R.id.text1);
		itemName.setText(mFiles.get(position).getName());

		return view;
	}
}