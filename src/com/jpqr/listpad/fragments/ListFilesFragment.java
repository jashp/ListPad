package com.jpqr.listpad.fragments;

import java.util.ArrayList;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jpqr.listpad.R;
import com.jpqr.listpad.activities.EditChecklist;
import com.jpqr.listpad.managers.SharedPreferencesManager;

public class ListFilesFragment extends Fragment {
	public static final String ARG_TYPE = "type";
	public static final int FAVOURITES = 0;
	public static final int RECENT = 1;
	private ListView mListView;
	private ArrayList<String> mFiles;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_files_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.files_list);

		int argType = getArguments().getInt(ARG_TYPE);
		Set<String> filesSet = null;
		switch (argType) {
			case FAVOURITES:
				filesSet = SharedPreferencesManager.getInstance().getFavouriteFiles();
			break;
			case RECENT:
				filesSet = SharedPreferencesManager.getInstance().getRecentFiles();
			break;
			default:
				throw new IllegalStateException("Invalid arguement for ListFilesFragment");
		}
		mFiles = new ArrayList<String>(filesSet);
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
		mListView.invalidateViews();
	}
}
