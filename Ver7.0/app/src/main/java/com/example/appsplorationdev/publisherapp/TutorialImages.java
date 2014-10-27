package com.example.appsplorationdev.publisherapp;

import java.util.ArrayList;

public class TutorialImages {

	private ArrayList<Integer> imageId;

	public TutorialImages() {
		imageId = new ArrayList<Integer>();
		imageId.add(R.drawable.page1);
		imageId.add(R.drawable.page2);
		imageId.add(R.drawable.page3);
		imageId.add(R.drawable.page4);
	}

	public ArrayList<Integer> getImageItem() {
		return imageId;
	}
}
