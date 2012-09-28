package com.jpqr.checklist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class EditChecklist extends Activity {
	ArrayAdapter<String> mAdapter;
	ArrayList<String> mItems;
	EditText mAddItemField;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_checklist);

		mItems = new ArrayList<String>();
		mItems.add("One");
		mItems.add("Two");
		mItems.add("Three");

		mAdapter = new ChecklistAdapter(this, R.layout.checklist_edit_item, mItems);
		

		ListView listView = (ListView) findViewById(R.id.checklist_items);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.checklist_new_item, null);
		listView.addFooterView(view);
		mAddItemField = (EditText) findViewById(R.id.add_item_field);

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
				mItems.add(itemToAdd);
				mAddItemField.setText("");
				mAdapter.notifyDataSetChanged();
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
