package com.jpqr.checklist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import android.os.Environment;

public class Checklist extends ArrayList<String> {

	private static final long serialVersionUID = -4776403069516340972L;
	public static final String DEFAULT_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/Checklists/";
	private String mTitle;
	private File mFile;

	public Checklist() {
		this.mTitle = "";
		this.mFile = new File(DEFAULT_DIRECTORY, "untitled.txt");
	}

	public Checklist(String path) throws IOException, FileNotFoundException {
		this.mFile = new File(path);
		this.mTitle = mFile.getName();

		BufferedReader reader = new BufferedReader(new FileReader(mFile));
		String line;
		while ((line = reader.readLine()) != null) {
			add(line);
		}
		reader.close();
	}

	public Checklist(URI path) throws IOException, FileNotFoundException {
		this.mFile = new File(path);
		this.mTitle = mFile.getName();

		BufferedReader reader = new BufferedReader(new FileReader(mFile));
		String line;
		while ((line = reader.readLine()) != null) {
			add(line);
		}
		reader.close();
	}
	
	public void toFile() throws IOException {
		if (!mTitle.equals(mFile.getName())) {
			File newFile = new File(mFile.getParentFile().getPath(), mTitle);
			mFile.renameTo(newFile);
			mFile = newFile;
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(mFile));
		ArrayList<String> list = getList();
		for (String item : list) {
			writer.write(item + "\r\n");
		}
		writer.close();
	}

	public ArrayList<String> getList() {
		return (ArrayList<String>) this;
	}

	public void fromString(String items) {
		String[] itemsArray = items.split("\r\n");
		ArrayList<String> list = getList();
		for (String item : itemsArray) {
			list.add(item);
		}
	}
	
	public String toString() {
		ArrayList<String> list = getList();
		StringBuilder stringBuilder = new StringBuilder();
		for (String item : list) {
			stringBuilder.append(item + "\r\n");
		}
		
		return stringBuilder.substring(0, stringBuilder.length()-2).toString();
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public boolean delete() {
		return mFile.delete();
	}
}
