package com.jpqr.checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class AllChecklists extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChecklistManager.initChecklists();
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
            	EditChecklist.newInstance(this, ChecklistManager.numChecklists());
            	startActivity(new Intent(this, EditChecklist.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
}
