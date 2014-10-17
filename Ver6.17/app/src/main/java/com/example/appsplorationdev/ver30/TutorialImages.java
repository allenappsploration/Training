package com.example.appsplorationdev.ver30;

import java.util.ArrayList;
import java.util.Random;

public class TutorialImages {

	private Random randNo;
	private ArrayList<Integer> imageId;

	public TutorialImages() {
		imageId = new ArrayList<Integer>();
		imageId.add(R.drawable.page1);
		imageId.add(R.drawable.page2);
		imageId.add(R.drawable.page3);
		imageId.add(R.drawable.page4);
	}

	public int getImageId() {
		randNo = new Random();
		return imageId.get(randNo.nextInt(imageId.size()));
	}

	public ArrayList<Integer> getImageItem() {
		return imageId;
	}
}
