package com.jpqr.checklist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AllChecklists extends ListActivity {
	private ArrayList<File> mFiles = new ArrayList<File>();
	private FileListAdapter mAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAdapter = new FileListAdapter();
        
		ListView listView = getListView();
		listView.setAdapter(mAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EditChecklist.newInstance(AllChecklists.this, mFiles.get(position).getPath());
			}
		});

    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	refreshChecklists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_checklist:
            	EditChecklist.newInstance(this, null);
            	refreshChecklists();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void refreshChecklists() {
    	File dir = new File(Checklist.DEFAULT_DIRECTORY);
    	
    	if (!dir.isDirectory()) {
    		boolean bool = dir.mkdirs();
    		
    		try {
				Checklist sample = new Checklist();
				sample.setTitle("groceries.txt");
				sample.add("milk");
				sample.add("eggs");
				sample.add("bread");
				sample.add("ice cream");
				sample.add("apples");
				sample.add("bananas");
				sample.makeFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} 
    	
    	mFiles.clear();
    	File[] files = dir.listFiles();
        mFiles.addAll(Arrays.asList(files));
    	mAdapter.notifyDataSetChanged();
 
    }
    
    public boolean checkExternalStorage() {
    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    return true;
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    return false;
    	} else {
    	    return false;
    	}
    }
    
    public class FileListAdapter extends ArrayAdapter<File> {
    	private final static int RESOURCE_ID = android.R.layout.simple_list_item_1;
    	
    	public FileListAdapter() {
    		super(AllChecklists.this, RESOURCE_ID, mFiles);
    	}
    	
    	@Override
    	public View getView(final int position, View view, ViewGroup parent) {
    		if (view == null) {
    			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			view = inflater.inflate(RESOURCE_ID, null);
    		}
    		TextView itemName = (TextView) view.findViewById(android.R.id.text1);
    		itemName.setText(mFiles.get(position).getName());
    		    		
    		return view;
    	}
    }
    
    
}
