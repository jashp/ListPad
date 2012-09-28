package com.jpqr.checklist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ChecklistAdapter extends ArrayAdapter<String> {
	List<String> mItems;
	public ChecklistAdapter(Context context, int textViewResourceId, List<String> items) {
		super(context, textViewResourceId, items);
		mItems = items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.checklist_edit_item, null);
		}
		TextView itemName = (TextView) view.findViewById(R.id.item_name);
		itemName.setText(mItems.get(position));
		
		Button removeButton = (Button) view.findViewById(R.id.remove_button);
	
		return view;
	}
}
