package com.jpqr.listpad.fragments;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.jpqr.listpad.R;
import com.jpqr.listpad.activities.EditActivity;
import com.jpqr.listpad.adapters.FileListAdapter;
import com.jpqr.listpad.managers.SharedPreferencesManager;
import com.jpqr.listpad.models.Checklist;

public class FileExplorerFragment extends SherlockFragment {

	private File mFile;
	private ArrayList<File> mFiles;
	private TextView mLabel;
	private ListView mListView;
	private Context mContext;
	private ArrayAdapter<File> mAdapter;
	private Stack<File> mForwardStack;
	private View mEmptyText;
	private AlertDialog mNewFileDialog;
	private EditText mNewFileName;

	public static void newInstance(Context context) {
		Intent intent = new Intent(context, FileExplorerFragment.class);
		context.startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mFile = new File(SharedPreferencesManager.getInstance().getLastLocation());
		mFiles = new ArrayList<File>();
		mAdapter = new FileListAdapter(mContext, mFiles);
		mForwardStack = new Stack<File>();

		View view = inflater.inflate(R.layout.file_picker, container, false);
		View newFileView = inflater.inflate(R.layout.new_file_dialog, container, false);
		createNewFileDialog(newFileView);
		
		
		mLabel = (TextView) view.findViewById(R.id.current_dir_label);
		mEmptyText = view.findViewById(R.id.empty_text);

		mListView = (ListView) view.findViewById(R.id.list_files);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				open(mFiles.get(position));

			}
		});

		registerForContextMenu(mListView);

		mListView.setAdapter(mAdapter);

		view.findViewById(R.id.folder_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});

		view.findViewById(R.id.folder_forward).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forward();
			}
		});

		view.findViewById(R.id.file_add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newFile();
			}
		});

		updateDir();

		return view;
	}

	@Override
	public void onResume() {
		updateDir();
		super.onResume();
	}

	@Override
	public void onPause() {
		SharedPreferencesManager.getInstance().setLastLocation(mFile.getAbsolutePath());
		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.list_files) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(mFiles.get(info.position).getName());
			String[] menuItems = new String[] { "Open", "Delete" };
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		File file = mFiles.get(info.position);
		switch (item.getItemId()) {
			case 0:
				open(file);
			break;
			case 1:
				file.delete();
				updateDir();
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void updateEmptyText() {
		if (mFiles.size() == 0) {
			mEmptyText.setVisibility(View.VISIBLE);
		} else {
			mEmptyText.setVisibility(View.GONE);
		}
	}

	private void updateDir() {
		mLabel.setText(mFile.getAbsolutePath().replaceFirst(".*" + Checklist.DEFAULT_DIRECTORY, "/sdcard"));
		mFiles.clear();

		File[] dirs = mFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		if (dirs != null) {
			mFiles.addAll(Arrays.asList(dirs));
		}

		File[] files = mFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		if (files != null) {
			mFiles.addAll(Arrays.asList(files));
		}

		mAdapter.notifyDataSetChanged();
		updateEmptyText();
	}
	
	private void createNewFileDialog(View newFileView) {
		AlertDialog.Builder mNewFileDialogBuilder = new AlertDialog.Builder(mContext);
		mNewFileDialogBuilder.setView(newFileView);
		mNewFileName = (EditText) newFileView.findViewById(R.id.file_name);
		final Spinner spinner = (Spinner) newFileView.findViewById(R.id.spinner_file_folder);
		mNewFileDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = mNewFileName.getText().toString();
				switch (spinner.getSelectedItemPosition()) {
					case 0:
						if (Checklist.isFileNameValid(name)) {
							if (!name.contains(".")) {
								name += ".txt";
							}
							File newFile = new File(mFile, name);
							try {
								newFile.createNewFile();
								open(newFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(mContext, "Invalid file name.", Toast.LENGTH_SHORT).show();
						}
					break;
					case 1:
						if (Checklist.isFolderNameValid(name)) {
							File newFile = new File(mFile, name);
							newFile.mkdirs();
							open(newFile);
						} else {
							Toast.makeText(mContext, "Invalid folder name.", Toast.LENGTH_SHORT).show();
						}
					break;
				}
			}
		});

		mNewFileDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		mNewFileDialog = mNewFileDialogBuilder.create();
	}

	private void newFile() {
		mNewFileName.setText("");
		mNewFileDialog.show();
	}

	public boolean back() {
		File parent = mFile.getParentFile();
		if (parent != null && !mFile.getAbsolutePath().equals(Checklist.DEFAULT_DIRECTORY)) {
			mForwardStack.push(mFile);
			mFile = parent;
			updateDir();
			return true;
		} else {
			return false;
		}
	}

	private void forward() {
		if (!mForwardStack.isEmpty()) {
			mFile = mForwardStack.pop();
			updateDir();
		}
	}

	public void open(File file) {
		if (file.isFile()) {
			EditActivity.newInstance(mContext, file.getAbsolutePath());
		} else if (file.isDirectory()) {
			mForwardStack.clear();
			mFile = file;
			updateDir();
		}
	}

}