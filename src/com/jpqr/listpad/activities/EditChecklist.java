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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.jpqr.adapters.ChecklistAdapter;
import com.jpqr.dragdrop.TouchInterceptor;
import com.jpqr.listpad.R;
import com.jpqr.listpad.db.FilesDataSource;
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
	private DialogInterface.OnClickListener mDeleteDialog;
	private DialogInterface.OnClickListener mCloseDialog;
	private FilesDataSource mDataSource;

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
		mDataSource = new FilesDataSource(mContext);
		mDataSource.open();
		
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
			mPath = pathURI.toString();
			mDataSource.addFile(mPath, FilesDataSource.Type.RECENT);
			mIsFavourite = mDataSource.isFavourite(mPath);
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

		mAdapter = new ChecklistAdapter(mContext, mChecklist, R.layout.checklist_edit_item);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				AlertDialog.Builder editPrompt = new AlertDialog.Builder(mContext);
				final TextView itemName = (TextView) view.findViewById(R.id.item_name);
				final EditText editText = new EditText(mContext);
				editText.setText(itemName.getText());
				editPrompt.setView(editText);

				editPrompt.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = editText.getText().toString();
					itemName.setText(value);
					mChecklist.set(position, value);
				  }
				});

				editPrompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				  }
				});
				
				editPrompt.show();
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
				delete();
				return true;
			case R.id.switch_mode:
				switchMode();
				return true;
			case R.id.favourite:
				favourite(item);
				return true;
			case R.id.close:
				close();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void delete() {
		if (mDeleteDialog == null || mDeleteDialogBuilder == null) {
			mDeleteDialog = new DialogInterface.OnClickListener() {
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
			mDeleteDialogBuilder = new AlertDialog.Builder(mContext);
			mDeleteDialogBuilder.setMessage("Are you sure you want to delete this file?");
			mDeleteDialogBuilder.setPositiveButton("Yes", mDeleteDialog);
			mDeleteDialogBuilder.setNegativeButton("No", mDeleteDialog);
		}
		mDeleteDialogBuilder.show();
	}

	private void switchMode() {
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
	}

	private void favourite(MenuItem item) {
		if (mIsFavourite) {
			item.setIcon(android.R.drawable.star_big_off);
			mDataSource.deleteFile(mPath, FilesDataSource.Type.FAVOURITE);
			mIsFavourite = false;
			Toast.makeText(mContext, "Unfavorited.", Toast.LENGTH_SHORT).show();
		} else {
			item.setIcon(android.R.drawable.star_big_on);
			mDataSource.addFile(mPath, FilesDataSource.Type.FAVOURITE);
			mIsFavourite = true;
			Toast.makeText(mContext, "Favorited.", Toast.LENGTH_SHORT).show();
		}
	}

	private void close() {
		if (mChecklist.isModified()) {
			if (mCloseDialog == null || mCloseDialogBuilder == null) {
				mCloseDialog = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								if (save()) {
									finish();
								}
							break;
							case DialogInterface.BUTTON_NEGATIVE:
								finish();
							break;
							case DialogInterface.BUTTON_NEUTRAL:
							break;
						}
					}
				};
				mCloseDialogBuilder = new AlertDialog.Builder(mContext);
				mCloseDialogBuilder.setMessage("Do you want to save this file before closing?");
				mCloseDialogBuilder.setPositiveButton("Yes", mCloseDialog);
				mCloseDialogBuilder.setNegativeButton("No", mCloseDialog);
				mCloseDialogBuilder.setNeutralButton("Cancel", mCloseDialog);
			}
			mCloseDialogBuilder.show();
		} else {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				close();
			break;
		}
		return super.onKeyDown(keyCode, event);
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
			Toast.makeText(mContext, "Problem saving file.", Toast.LENGTH_SHORT).show();
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
	private AlertDialog.Builder mCloseDialogBuilder;
	private AlertDialog.Builder mDeleteDialogBuilder;
}
