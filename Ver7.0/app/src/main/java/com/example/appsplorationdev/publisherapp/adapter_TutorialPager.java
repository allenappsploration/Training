package com.example.appsplorationdev.publisherapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class adapter_TutorialPager extends FragmentStatePagerAdapter {

	private ArrayList<Integer> itemData;

	public adapter_TutorialPager(FragmentManager fm,
                                 ArrayList<Integer> itemData) {
		super(fm);
		this.itemData = itemData;
	}

	@Override
	public int getCount() {
		return itemData.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	@Override
	public Fragment getItem(int position) {
		TutorialImageView f = TutorialImageView.newInstance();
		f.setImageList(itemData.get(position));
		return f;
	}
}