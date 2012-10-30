package com.jpqr.listpad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jpqr.listpad.R;
import com.jpqr.listpad.models.Checklist;

public final class ChecklistAdapter extends ArrayAdapter<String> {
		private Checklist mChecklist;
		
		public ChecklistAdapter(Context context, Checklist checklist, int textViewResourceId) {
			super(context, textViewResourceId, checklist);
			mChecklist = checklist;
		}



		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.checklist_edit_item, null);
			}
			TextView itemName = (TextView) view.findViewById(R.id.item_name);
			ImageView removeButton = (ImageView) view.findViewById(R.id.remove_button);
			OnClickListener onClickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					mChecklist.remove(position);
					notifyDataSetChanged();
				}
			};

			itemName.setText(mChecklist.get(position));
			removeButton.setOnClickListener(onClickListener);

			return view;
		}
	}