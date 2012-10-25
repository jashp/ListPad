package com.jpqr.listpad.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuInflater;
import com.jpqr.listpad.R;
import com.jpqr.listpad.activities.EditChecklist;
import com.jpqr.listpad.db.FilesDataSource;

public class ListFilesFragment extends SherlockFragment {
	public static final String ARG_TYPE = "type";
	private ListView mListView;
	private ArrayList<String> mFiles;
	private int mType;
	private FilesDataSource mDataSource;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_files_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.files_list);
		mDataSource = new FilesDataSource(getActivity());
		mDataSource.open();

		mType = getArguments().getInt(ARG_TYPE);
		mFiles = mDataSource.getAll(mType);

		mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFiles));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EditChecklist.newInstance(getActivity(), mFiles.get(position));
			}

		});		
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		mFiles.clear();
		mFiles.addAll(mDataSource.getAll(mType));
		mListView.invalidateViews();

	}

}
