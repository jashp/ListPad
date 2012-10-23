package com.jpqr.listpad.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jpqr.dragdrop.TouchInterceptor;
import com.jpqr.listpad.R;
import com.jpqr.listpad.R.id;
import com.jpqr.listpad.R.layout;
import com.jpqr.listpad.R.menu;
import com.jpqr.listpad.managers.SharedPreferencesManager;
import com.jpqr.listpad.models.Checklist;

public class EditChecklist extends SherlockActivity {
	public static final String EXTRA_PATH = "PATH";
	private ChecklistAdapter mAdapter;
	private Checklist mChecklist;
	private ListView mListView;
	private Context mContext;
	private EditText mChecklistNameField;
	private boolean mListModeActive = true;
	private boolean mIsFavourite;
	private EditText mChecklistTextField;
	private String mPath;

	private EditText mAddItemField;

	public static void newInstance(Context context, String path) {
		Intent intent = new Intent(context, EditChecklist.class);
		intent.putExtra(EXTRA_PATH, path);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		Uri pathUri = getIntent().getData();
		String pathString = getIntent().getStringExtra(EXTRA_PATH);
		URI pathURI;
		try {
			if (pathUri != null) {
				pathURI = new URI(pathUri.toString());
			} else if (pathString != null) {
				pathURI = new URI(pathString);
			} else {
				throw new URISyntaxException("null", "EditChecklist Activity must be opened with a path in the intent or the instance");
			}
			
			mChecklist = new Checklist(pathURI);
			
			SharedPreferencesManager preferences = SharedPreferencesManager.getInstance();
			mPath = pathURI.toString();
			preferences.addRecentFile(mPath);
			mIsFavourite = preferences.isFavouriteFile(mPath);
		} catch (FileNotFoundException e) {
			Toast.makeText(mContext, "File not found.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(mContext, "Input/output problem.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Toast.makeText(mContext, "Problem with file path.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}


		setContentView(R.layout.edit_checklist_activity);
		mListView = (ListView) findViewById(R.id.list_field);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		((TouchInterceptor) mListView).setDropListener(mDropListener);

		View addItemView = inflater.inflate(R.layout.checklist_new_item, null);
		mListView.addFooterView(addItemView);

		mChecklistNameField = (EditText) findViewById(R.id.checklist_title);
		mChecklistNameField.setText(mChecklist.getTitle());

		mChecklistTextField = (EditText) findViewById(R.id.text_field);

		mAdapter = new ChecklistAdapter(mContext, R.layout.checklist_edit_item);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(mContext, "Yo", Toast.LENGTH_SHORT).show();
			}
		});

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
	}

	@Override
	protected void onPause() {
		super.onPause();
		// save();
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
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.edit_file_menu, menu);
		MenuItem favouriteItem = menu.findItem(R.id.favourite);
		favouriteItem.setIcon(mIsFavourite ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				save();
				return true;
			case R.id.delete:
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								if (mChecklist.delete()) {
									finish();
									Toast.makeText(mContext, "File deleted.", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(mContext, "Problem deleting file.", Toast.LENGTH_LONG).show();
								}
							break;

							case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage("Are you sure you want to delete this checklist?");
				builder.setPositiveButton("Yes", dialogClickListener);
				builder.setNegativeButton("No", dialogClickListener);
				builder.show();

				return true;
			case R.id.switch_mode:
				if (mListModeActive) {
					mChecklistTextField.setText(mChecklist.toString());
					mChecklistTextField.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
					mListModeActive = false;
				} else {
					mChecklist.fromString(mChecklistTextField.getText().toString());
					mChecklistTextField.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					mAdapter.notifyDataSetChanged();
					mListModeActive = true;
				}

				return true;
			case R.id.favourite:
				if (mIsFavourite) {
					item.setIcon(android.R.drawable.star_big_off);
					SharedPreferencesManager.getInstance().removeFavouriteFile(mPath);
					mIsFavourite = false;
					Toast.makeText(mContext, "Unfavorited.", Toast.LENGTH_SHORT).show();
				} else {
					item.setIcon(android.R.drawable.star_big_on);
					SharedPreferencesManager.getInstance().addFavouriteFile(mPath);
					mIsFavourite = true;
					Toast.makeText(mContext, "Favorited.", Toast.LENGTH_SHORT).show();
				}
				return true;
				
			case R.id.close:
				if (mIsFavourite) {
					item.setIcon(android.R.drawable.star_big_off);
					SharedPreferencesManager.getInstance().removeFavouriteFile(mPath);
					mIsFavourite = false;
					Toast.makeText(mContext, "Unfavorited.", Toast.LENGTH_SHORT).show();
				} else {
					item.setIcon(android.R.drawable.star_big_on);
					SharedPreferencesManager.getInstance().addFavouriteFile(mPath);
					mIsFavourite = true;
					Toast.makeText(mContext, "Favorited.", Toast.LENGTH_SHORT).show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private boolean save() {
		String fileName = mChecklistNameField.getText().toString().trim();

		if (!isFilenameValid(fileName)) {
			Toast.makeText(mContext, "The list name is not valid.", Toast.LENGTH_LONG).show();
			return false;
		}

		mChecklist.setTitle(fileName);
		try {
			mChecklist.toFile();
			Toast.makeText(mContext, "List saved.", Toast.LENGTH_SHORT).show();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isFilenameValid(String fileName) {
		// TODO check filename
		return true;
	}

	private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
		public void drop(int from, int to) {
			ArrayList<String> list = mChecklist;
			int size = list.size();
			if (from < size) {
				if (to >= size) {
					to = size - 1;
				}
				String item = list.remove(from);
				list.add(to, item);
				mListView.invalidateViews();
			}
		}
	};

	public final class ChecklistAdapter extends ArrayAdapter<String> {

		public ChecklistAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId, mChecklist);
		}

		public int getCount() {
			return mChecklist.size();
		}

		public String getItem(int position) {
			return mChecklist.get(position);
		}

		public long getItemId(int position) {
			return position;
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
