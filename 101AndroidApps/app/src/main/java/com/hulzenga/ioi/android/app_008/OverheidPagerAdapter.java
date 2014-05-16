package com.hulzenga.ioi.android.app_008;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class OverheidPagerAdapter extends FragmentPagerAdapter {

  public OverheidPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
  }

  @Override
  public Fragment getItem(int position) {
    return OverheidFeedFragment.newInstance(OverheidFeed.values()[position]);
  }

  @Override
  public int getCount() {
    return  OverheidFeed.values().length;
  }
}
