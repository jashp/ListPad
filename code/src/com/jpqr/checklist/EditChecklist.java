package com.jpqr.checklist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jpqr.dragdrop.DragDropListView;
import com.jpqr.dragdrop.DragDropListView.DragListener;
import com.jpqr.dragdrop.DragDropListView.DropListener;
import com.jpqr.dragdrop.DragDropListView.RemoveListener;

public class EditChecklist extends ListActivity {
	public static final String EXTRA_PATH = "PATH";
	private final int NUM_FOOTER = 1;
	private ChecklistAdapter mAdapter;
	private Checklist mChecklist;
	private String mPath;

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

		setContentView(R.layout.edit_checklist_activity);
		ListView listView = getListView();

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View addItemView = inflater.inflate(R.layout.checklist_new_item, null);
		listView.addFooterView(addItemView);

		mChecklistNameField = (EditText) findViewById(R.id.checklist_title);
		mChecklistNameField.setText(mChecklist.getTitle());

		mAdapter = new ChecklistAdapter(this, R.layout.checklist_edit_item);
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

		if (listView instanceof DragDropListView) {
			((DragDropListView) listView).setDropListener(mDropListener);
			((DragDropListView) listView).setRemoveListener(mRemoveListener);
			((DragDropListView) listView).setDragListener(mDragListener);
		}
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
		// TODO implement logic
		return true;
	}

	private DropListener mDropListener = new DropListener() {
		public void onDrop(int from, int to) {
			if (to > 0 && to < mChecklist.size() - NUM_FOOTER) {
				ArrayList<String> list = mChecklist.getList();
				String temp = list.get(from);
				list.remove(from);
				list.add(to, temp);
				getListView().invalidateViews();
			}
		}
	};

	private RemoveListener mRemoveListener = new RemoveListener() {
		public void onRemove(int which) {
			if (which > 0 && which < mChecklist.size() - NUM_FOOTER) {
				mChecklist.remove(which);
				getListView().invalidateViews();
			}
		}
	};

	private DragListener mDragListener = new DragListener() {

		int backgroundColor = 0xe0103010;
		int defaultBackgroundColor;

		public void onDrag(int x, int y, ListView listView) {
			// TODO Auto-generated method stub
		}

		public void onStartDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
			itemView.setBackgroundColor(backgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
			if (iv != null)
				iv.setVisibility(View.INVISIBLE);
		}

		public void onStopDrag(View itemView) {
			itemView.setVisibility(View.VISIBLE);
			itemView.setBackgroundColor(defaultBackgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
			if (iv != null)
				iv.setVisibility(View.VISIBLE);
		}

	};

	public final class ChecklistAdapter extends ArrayAdapter<String> {

		public ChecklistAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId, mChecklist.getList());
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.checklist_edit_item, null);
			}
			TextView itemName = (TextView) view.findViewById(R.id.item_name);
			ImageView removeButton = (ImageView) view.findViewById(R.id.remove_button);
			OnClickListener onClickListener = new OnClickListener() {
				public void onClick(View v) {
					mChecklist.remove(position);
					notifyDataSetChanged();
				}
			};

			itemName.setText(mChecklist.get(position));
			removeButton.setOnClickListener(onClickListener);

			return view;
		}
	}

}
