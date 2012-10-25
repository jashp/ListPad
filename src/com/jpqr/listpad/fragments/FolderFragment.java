package com.jpqr.listpad.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.jpqr.adapters.FileListAdapter;
import com.jpqr.listpad.R;
import com.jpqr.listpad.activities.EditChecklist;
import com.jpqr.listpad.models.Checklist;


public class FolderFragment extends SherlockFragment {
	private ArrayList<File> mFiles = new ArrayList<File>();
	private ListView mListView;
	private ArrayAdapter<File> mAdapter;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_files_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.files_list);
		mAdapter = new FileListAdapter(getActivity(), mFiles, true);
		
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EditChecklist.newInstance(getActivity(), mFiles.get(position).toURI().toString());
			}
		});		
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		refreshChecklists();
	}



	private void refreshChecklists() {
		File dir = new File(Checklist.DEFAULT_DIRECTORY);

		if (!dir.isDirectory()) {
			boolean bool = dir.mkdirs();

			try {
				Checklist sample = new Checklist();
				sample.setTitle("groceries sample.txt");
				sample.add("milk");
				sample.add("eggs");
				sample.add("bread");
				sample.add("ice cream");
				sample.add("apples");
				sample.add("bananas");
				sample.toFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mFiles.clear();
		File[] files = dir.listFiles();
		mFiles.addAll(Arrays.asList(files));
		mAdapter.notifyDataSetChanged();

	}

	public boolean checkExternalStorage() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}



}
