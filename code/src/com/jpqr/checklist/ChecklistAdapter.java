package com.jpqr.checklist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChecklistAdapter extends ArrayAdapter<String> {
	List<String> mItems;
	public ChecklistAdapter(Context context, int textViewResourceId, List<String> items) {
		super(context, textViewResourceId, items);
		mItems = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.checklist_edit_item, null);
		}
		TextView itemName = (TextView) view.findViewById(R.id.item_name);
		itemName.setText(mItems.get(position));
	
		return view;
	}
}
