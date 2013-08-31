
package com.logim.main.utils;

import android.support.v4.view.ViewPager.OnPageChangeListener;

public class PageChangeListener implements OnPageChangeListener {

	int currentIndex = 0;
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		currentIndex = arg0;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}
}
