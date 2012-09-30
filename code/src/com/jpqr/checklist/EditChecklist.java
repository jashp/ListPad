package com.jpqr.checklist;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class EditChecklist extends Activity {
	public static final String EXTRA_PATH = "PATH";

	private ArrayAdapter<String> mAdapter;
	private Checklist mChecklist;
	private String mPath;
	private boolean mChanged = false;

	private EditText mChecklistNameField;

	private EditText mAddItemField;

	public static void newInstance(Context context, String path) {
		Intent intent = new Intent(context, EditChecklist.class);
		intent.putExtra(EXTRA_PATH, path);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_checklist);
		mPath = getIntent().getStringExtra(EXTRA_PATH);

		if (mPath == null) {
			mChecklist = new Checklist();
		} else {
			try {
				mChecklist = new Checklist(mPath);
			} catch (FileNotFoundException e) {
				Toast.makeText(this, "File not found.", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(this, "Input/output problem.", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}

		ListView listView = (ListView) findViewById(R.id.checklist_items);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View addItemView = inflater.inflate(R.layout.checklist_new_item, null);
		listView.addFooterView(addItemView);

		mAdapter = new ChecklistAdapter(this, R.layout.checklist_edit_item, mChecklist);
		listView.setAdapter(mAdapter);

		mAddItemField = (EditText) findViewById(R.id.add_item_field);
		mAddItemField.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					addToChecklist();
			        return true;    
			    }
				return false;
			}
		});		
		ImageView addButton = (ImageView) findViewById(R.id.add_button);
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addToChecklist();
			}
		});
		

		mChecklistNameField = (EditText) findViewById(R.id.checklist_title);
		mChecklistNameField.setText(mChecklist.getTitle());

	}

	private void addToChecklist() {
		String itemToAdd = mAddItemField.getText().toString();
		if (!itemToAdd.equals("")) {
			mChecklist.add(itemToAdd);
			mAddItemField.setText("");
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_checklist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save:
			try {
				String fileName = mChecklistNameField.getText().toString();
				if (isFilenameValid(fileName)) {
					mChecklist.setTitle(fileName);
					mChecklist.toFile();
					Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "The checklist name is not valid.", Toast.LENGTH_LONG).show();
				}
				
			} catch (IOException e) {
				Toast.makeText(this, "Problem saving file.", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			finish();
			return true;
		case R.id.delete:
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						mChecklist.delete();
						finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete this checklist?");
			builder.setPositiveButton("Yes", dialogClickListener);
			builder.setNegativeButton("No", dialogClickListener).show();

			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public static boolean isFilenameValid(String fileName) {
		//TODO implement logic
		return true;
	}

}
