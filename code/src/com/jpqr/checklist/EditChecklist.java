package com.jpqr.checklist;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EditChecklist extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_checklist);
        
        ArrayList<String> items = new ArrayList<String>();
        items.add("One");
        items.add("Two");
        items.add("Three");

        ListView listView = (ListView) findViewById(R.id.checklist_items);
        listView.setAdapter(new ChecklistAdapter(this , R.layout.checklist_edit_item, items));
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
}
