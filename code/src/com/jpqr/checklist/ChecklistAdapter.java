package com.jpqr.checklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChecklistAdapter extends ArrayAdapter<String> {
	private Checklist mChecklist;
	public ChecklistAdapter(Context context, int textViewResourceId, Checklist checklist) {
		super(context, textViewResourceId, checklist.getItems());
		mChecklist = checklist;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.checklist_edit_item, null);
		}
		TextView itemName = (TextView) view.findViewById(R.id.item_name);
		itemName.setText(mChecklist.get(position));

		ImageView removeButton = (ImageView) view.findViewById(R.id.remove_button);
		removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mChecklist.remove(position);
				notifyDataSetChanged();
			}
		});
		
		return view;
	}
}
