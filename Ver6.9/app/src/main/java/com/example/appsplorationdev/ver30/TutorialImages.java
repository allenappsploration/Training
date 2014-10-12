package com.example.appsplorationdev.ver30;

import java.util.ArrayList;
import java.util.Random;

public class TutorialImages {

	private Random randNo;
	private ArrayList<Integer> imageId;

	public TutorialImages() {
		imageId = new ArrayList<Integer>();
		imageId.add(R.drawable.a1);
		imageId.add(R.drawable.a2);
		imageId.add(R.drawable.a3);
		imageId.add(R.drawable.a4);
		imageId.add(R.drawable.a5);
	}

	public int getImageId() {
		randNo = new Random();
		return imageId.get(randNo.nextInt(imageId.size()));
	}

	public ArrayList<Integer> getImageItem() {
		return imageId;
	}
}
