package com.jpqr.checklist;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AllChecklists extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_checklists);
        
        File dir = new File(Checklist.DIRECTORY_PATH);
        File[] files = dir.listFiles();
       
        final String[] fileNames = new String[files.length];
        for(int i = 0; i < files.length; i++) {
        	fileNames[i] = files[i].getName();
        }
        
		ListView listView = (ListView) findViewById(R.id.all_checklist_list);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames);
		listView.setAdapter(arrayAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EditChecklist.newInstance(AllChecklists.this, fileNames[position]);
			}
		});
        
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
            	startActivity(new Intent(this, EditChecklist.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    
    
}
