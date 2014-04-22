package com.hulzenga.ioi_apps.app_008;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.hulzenga.ioi_apps.DemoFragmentActivity;

public class OverheidActivity extends DemoFragmentActivity {

  OverheidPagerAdapter mAdapter;
  ViewPager            mPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mPager = new ViewPager(this);
    mPager.setId(23192);
    mAdapter = new OverheidPagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mAdapter);

    final ActionBar actionBar = getActionBar();

    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
      @Override
      public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mPager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

      }

      @Override
      public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

      }
    };

    for (OverheidFeed feed : OverheidFeed.values()) {
      actionBar.addTab(actionBar.newTab().setText(feed.getName()).setTabListener(tabListener));
    }
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });


    setContentView(mPager);
  }
}
