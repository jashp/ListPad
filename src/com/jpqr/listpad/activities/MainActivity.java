package com.jpqr.listpad.activities;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jpqr.listpad.R;
import com.jpqr.listpad.adapters.TabsAdapter;
import com.jpqr.listpad.database.FilesDataSource;
import com.jpqr.listpad.fragments.FileExplorerFragment;
import com.jpqr.listpad.fragments.ListFilesFragment;
import com.jpqr.listpad.managers.SharedPreferencesManager;
import com.jpqr.listpad.models.Checklist;

public class MainActivity extends SherlockFragmentActivity {
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	FileExplorerFragment mFileExplorer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (SharedPreferencesManager.getInstance().isFirstRun()) {
			firstRun();
		}
		setContentView(R.layout.fragment_tabs_pager);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		mTabsAdapter.addTab(mTabHost.newTabSpec("browse").setIndicator("Browse"), FileExplorerFragment.class, new Bundle());

		Bundle favourite = new Bundle();
		favourite.putInt(ListFilesFragment.ARG_TYPE, FilesDataSource.Type.FAVOURITE);
		mTabsAdapter.addTab(mTabHost.newTabSpec("favourite").setIndicator("Favorites"), ListFilesFragment.class, favourite);

		Bundle recent = new Bundle();
		recent.putInt(ListFilesFragment.ARG_TYPE, FilesDataSource.Type.RECENT);
		mTabsAdapter.addTab(mTabHost.newTabSpec("recent").setIndicator("Recent"), ListFilesFragment.class, recent);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		mFileExplorer = (FileExplorerFragment) mTabsAdapter.getItem(0);
	}

	private void firstRun() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						String path = Checklist.DEFAULT_DIRECTORY + "/ListPad/";
						File sampleDir = new File(path);
						if (!sampleDir.isDirectory()) {
							if (sampleDir.mkdirs()) {
								try {
									Checklist sampleFile = new Checklist(path);
									sampleFile.setTitle("groceries.txt");
									sampleFile.add("milk");
									sampleFile.add("eggs");
									sampleFile.add("bread");
									sampleFile.add("ice cream");
									sampleFile.add("apples");
									sampleFile.add("bananas");
									sampleFile.saveFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						mFileExplorer.open(sampleDir);
					break;
				}
			}

		};
		builder.setPositiveButton("Yes", dialog);
		builder.setNegativeButton("No", dialog);
		builder.setTitle("Welcome to ListPad!");
		builder.setMessage("Would you like to create a ListPad folder on your SD card with a sample list? (You can delete it after)");
		builder.show();
		SharedPreferencesManager.getInstance().setFirstRun(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mTabHost.getCurrentTab() == 0) {
			return mFileExplorer.back();
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
