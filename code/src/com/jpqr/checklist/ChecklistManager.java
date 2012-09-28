package com.jpqr.checklist;

import java.util.ArrayList;

public class ChecklistManager {
	public static ArrayList<Checklist> mChecklists;

	public static ArrayList<Checklist> getChecklists() {
		return mChecklists;
	}

	public static void resetChecklists() {
		mChecklists = new ArrayList<Checklist>();
	}

	public static int numChecklists() {
		return mChecklists.size();
	}

	public static Checklist getChecklist(int index) {
		return mChecklists.get(index);
	}

	public static void removeChecklist(int index) {
		mChecklists.remove(index);
	}

}
