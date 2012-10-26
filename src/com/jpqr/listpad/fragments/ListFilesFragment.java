package com.jpqr.listpad.fragments;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.jpqr.adapters.FileListAdapter;
import com.jpqr.listpad.R;
import com.jpqr.listpad.activities.EditChecklist;
import com.jpqr.listpad.db.FilesDataSource;

public class ListFilesFragment extends SherlockFragment {
	public static final String ARG_TYPE = "type";
	private ListView mListView;
	private ArrayList<File> mFiles;
	private int mType;
	private FilesDataSource mDataSource;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_files_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.files_list);
		mDataSource = new FilesDataSource(getActivity());
		mDataSource.open();

		mType = getArguments().getInt(ARG_TYPE);
		mFiles = mDataSource.getAllFiles(mType);

		mListView.setAdapter(new FileListAdapter(getActivity(), mFiles));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EditChecklist.newInstance(getActivity(), mFiles.get(position).getAbsolutePath());
			}

		});
		mDataSource.close();
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		mDataSource.open();
		mFiles.clear();
		mFiles.addAll(mDataSource.getAllFiles(mType));
		mListView.invalidateViews();
		mDataSource.close();

	}

}
