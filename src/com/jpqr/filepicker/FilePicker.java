package com.jpqr.filepicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jpqr.adapters.FileListAdapter;
import com.jpqr.listpad.R;
import com.jpqr.listpad.activities.EditChecklist;
import com.jpqr.listpad.models.Checklist;

public class FilePicker extends SherlockActivity {

	private File mFile;
	private ArrayList<File> mFiles;
	private TextView mLabel;
	private ListView mListView;
	private Context mContext;
	private ArrayAdapter<File> mAdapter;
	private Stack<File> mForwardStack;
	
	public static void newInstance(Context context) {
		Intent intent = new Intent(context, FilePicker.class);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mFile = new File(Checklist.DEFAULT_DIRECTORY);
		mFiles = new ArrayList<File>();
		mAdapter = new FileListAdapter(this, mFiles);
		mForwardStack = new Stack<File>();
		
		setContentView(R.layout.file_picker);
		mLabel = (TextView) findViewById(R.id.current_dir_label);
		mListView = (ListView) findViewById(R.id.list_files);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				File file = mFiles.get(position);
				if (file.isDirectory()) {
					mFile = file;
					updateDir();
					mForwardStack.clear();
				} else {
					EditChecklist.newInstance(mContext, file.getAbsolutePath());
				}
			}
		});
		mListView.setAdapter(mAdapter);
		updateDir();
	}
	
	@Override
	protected void onResume() {
		updateDir();
		super.onResume();
	}

	private void updateDir() {
		mLabel.setText(mFile.getPath());
		mFiles.clear();
		mFiles.addAll(Arrays.asList(mFile.listFiles()));
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.file_picker_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.back:
				back();
				return true;
			case R.id.forward:
				forward();
				return true;
			case R.id.new_file:
				newFile();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void newFile() {
		AlertDialog.Builder editPrompt = new AlertDialog.Builder(mContext);
		final EditText editText = new EditText(mContext);
		editText.setHint("File name");
		editPrompt.setView(editText);

		editPrompt.setPositiveButton("Create", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			String fileName = editText.getText().toString();
			if (!fileName.contains(".")) {
				fileName += ".txt";
			}
			File newFile = new File(mFile, fileName);
			try {
				newFile.createNewFile();
				EditChecklist.newInstance(mContext, newFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		});

		editPrompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  @Override
		public void onClick(DialogInterface dialog, int whichButton) {
		  }
		});
		
		editPrompt.show();
	}
	

	private void back() {
		File parent = mFile.getParentFile();
		if (parent != null) {
			mForwardStack.push(mFile);
			mFile = parent;
			updateDir();
		} else {
			finish();
		}
	}

	private void forward() {
		if (!mForwardStack.isEmpty()) {
			mFile = mForwardStack.pop();
			updateDir();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				back();
			break;
		}
		return true;
	}

}