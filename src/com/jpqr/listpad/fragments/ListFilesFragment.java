package com.jpqr.listpad.fragments;

import java.util.HashSet;
import java.util.Set;

import com.jpqr.listpad.R;
import com.jpqr.listpad.R.id;
import com.jpqr.listpad.R.layout;
import com.jpqr.listpad.managers.SharedPreferencesManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFilesFragment extends Fragment {
	public static final String ARG_TYPE = "type";
	public static final int FAVOURITES = 0;
	public static final int RECENT = 1;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_files_fragment, container, false);
		ListView listView = (ListView) view.findViewById(R.id.files_list);
		
		int argType = getArguments().getInt(ARG_TYPE);
		Set<String> files = null;
		switch (argType) {
			case FAVOURITES:
				files = SharedPreferencesManager.getInstance().getFavouriteFiles();
				files.add("fav1");
				files.add("fav2");
				files.add("fav3");
			break;
			case RECENT:
				files = SharedPreferencesManager.getInstance().getRecentFiles();
				files.add("recent1");
				files.add("recent2");
				files.add("recent3");
			break;
			default:
				throw new IllegalStateException("Invalid arguement for ListFilesFragment");
		}
	
		listView.setAdapter(new ArrayAdapter<Object>(getActivity(), android.R.layout.simple_list_item_1, files.toArray()));

		return view;
	}
}
