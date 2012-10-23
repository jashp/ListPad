package com.jpqr.listpad;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jpqr.listpad.R;

public class RecentChecklists extends Activity {
	private ArrayList<String> mFiles = new ArrayList<String>();
	private ArrayAdapter<String> mAdapter;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		
		mAdapter = new ArrayAdapter<String>(mContext, android.R.id.text1,  mFiles);

		ListView listView = (ListView) findViewById(R.id.checklist_list);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EditChecklist.newInstance(mContext, mFiles.get(position));
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshChecklists();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.create_checklist:
				EditChecklist.newInstance(this, null);
				refreshChecklists();
				return true;
			case R.id.refresh:
				refreshChecklists();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void refreshChecklists() {
		// TODO Auto-generated method stub
		
	}

	public boolean checkExternalStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return false;
		} else {
			return false;
		}
	}



}
