package com.jpqr.checklist;

import java.util.ArrayList;
import java.util.Arrays;

public class Checklist {
	private String mTitle;
	private ArrayList<String> mItems;
	
	public Checklist(String title, ArrayList<String> items) {
		this.mTitle = title;
		this.mItems = items;
	}
	
	public Checklist(String title, String items) {
		this.mTitle = title;
		this.mItems = new ArrayList<String>(Arrays.asList(items.split("\r\n")));
	}
	
	public ArrayList<String> getItems() {
		return mItems;
	}
	public void setItems(ArrayList<String> items) {
		this.mItems = items;
	}	
	public void setItems(String items) {
		this.mItems = new ArrayList<String>(Arrays.asList(items.split("\r\n")));
	}	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	
	public void add(String item) {
		mItems.add(item);
	}
	
	public String get(int index) {
		return mItems.get(index);
	}
	
	public void remove(int index) {
		mItems.remove(index);
	}
	
	public boolean remove(String item) {
		return mItems.remove(item);
	}
	
	public String toText() {
		String text = "";
		for (String item : mItems) {
			text+=item+"\r\n";
		}
		return text;
	}
}
