package com.jpqr.checklist;

import java.util.ArrayList;

public class Checklist {
	private String mTitle;
	private ArrayList<String> mItems;
	
	public Checklist(String title, ArrayList<String> items) {
		this.mItems = items;
		this.mTitle = title;
	}
	
	public ArrayList<String> getItems() {
		return mItems;
	}
	public void setItems(ArrayList<String> mItems) {
		this.mItems = mItems;
	}
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

}
