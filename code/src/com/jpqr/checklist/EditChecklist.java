package com.jpqr.checklist;

import java.io.File;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class EditChecklist extends Activity {
	public static final String EXTRA_NAME = "NAME";
	
	private ArrayAdapter<String> mAdapter;
	private EditText mAddItemField;
	private Checklist mChecklist;
	
	public static void newInstance(Context context, String name) {
		Intent intent = new Intent(context, EditChecklist.class);
		intent.putExtra(EXTRA_NAME, name);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_checklist);
		String fileName = getIntent().getStringExtra(EXTRA_NAME);
		
		File file = new File(Checklist.DIRECTORY_PATH + fileName);
		
		try {
			mChecklist = new Checklist(file);
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "Input/output problem", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		ListView listView = (ListView) findViewById(R.id.checklist_items);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.checklist_new_item, null);
		listView.addFooterView(view);
		
		mAddItemField = (EditText) findViewById(R.id.add_item_field);
		
		Button saveButton = (Button) findViewById(R.id.save_checklist_edit);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ChecklistManager.editChecklist(mChecklist);
				finish();
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.save_checklist_edit);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		mAdapter = new ChecklistAdapter(this, R.layout.checklist_edit_item, mChecklist);
		listView.setAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_checklist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_item:
			Toast.makeText(this, "Creating a new checklist", Toast.LENGTH_SHORT).show();
			String itemToAdd = mAddItemField.getText().toString();
			if (!itemToAdd.equals("")) {
				mChecklist.add(itemToAdd);
				mAddItemField.setText("");
				mAdapter.notifyDataSetChanged();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
