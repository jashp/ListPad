package com.jpqr.checklist;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jpqr.checklist.R;
import com.jpqr.dragdrop.TouchInterceptor;
import com.jpqr.dragdrop.TouchInterceptor.DropListener;

public class PowerWidgetOrder extends ListActivity
{
    private static final String TAG = "PowerWidgetOrderActivity";

    private ListView mButtonList;
    private ButtonAdapter mButtonAdapter;
    View mContentView = null;
    Context mContext;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.edit_checklist_activity);
        
        mButtonList = getListView();
        ((TouchInterceptor) mButtonList).setDropListener(mDropListener);
        mButtonAdapter = new ButtonAdapter(mContext);
        setListAdapter(mButtonAdapter);
        

    }

    @Override
    public void onDestroy() {
        ((TouchInterceptor) mButtonList).setDropListener(null);
        setListAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        // reload our buttons and invalidate the views for redraw
        mButtonAdapter.reloadButtons();
        mButtonList.invalidateViews();
    }

    private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
        public void drop(int from, int to) {
            // get the current button list
            ArrayList<String> buttons = new ArrayList<String>();
            // move the button
            if (from < buttons.size()) {
                String button = buttons.remove(from);

                if (to <= buttons.size()) {
                    buttons.add(to, button);

                    // tell our adapter/listview to reload
                    mButtonAdapter.reloadButtons();
                    mButtonList.invalidateViews();
                }
            }
        }
    };

    private class ButtonAdapter extends BaseAdapter {
        private Context mContext;
        private Resources mSystemUIResources = null;
        private LayoutInflater mInflater;
        private ArrayList<String> mButtons;

        public ButtonAdapter(Context c) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);
            reloadButtons();
        }

        public void reloadButtons() {
            mButtons = new ArrayList<String>();
            mButtons.add("1");
            mButtons.add("2");
            mButtons.add("3");
        }

        public int getCount() {
            return mButtons.size();
        }

        public Object getItem(int position) {
            return mButtons.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View view, ViewGroup parent) {
            final View v;
            if (view == null) {
                v = mInflater.inflate(R.layout.checklist_edit_item, null);
            } else {
                v = view;
            }

            final TextView name = (TextView) v.findViewById(R.id.item_name);

            name.setText(mButtons.get(position));

            return v;
        }
    }
}