package com.jpqr.checklist;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class EditChecklist extends Activity {
	public static final String EXTRA_PATH = "PATH";

	private ArrayAdapter<String> mAdapter;
	private Checklist mChecklist;
	private String mPath;

	private EditText mChecklistNameField;

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

		ImageView addButton = (ImageView) findViewById(R.id.add_button);
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText addItemField = (EditText) findViewById(R.id.add_item_field);
				String itemToAdd = addItemField.getText().toString();
				if (!itemToAdd.equals("")) {
					mChecklist.add(itemToAdd);
					addItemField.setText("");
					mAdapter.notifyDataSetChanged();
				}
			}
		});

		mChecklistNameField = (EditText) findViewById(R.id.checklist_title);
		mChecklistNameField.setText(mChecklist.getTitle());

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
				mChecklist.setTitle(mChecklistNameField.getText().toString());
				mChecklist.toFile();
				Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(this, "Problem saving file.", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
