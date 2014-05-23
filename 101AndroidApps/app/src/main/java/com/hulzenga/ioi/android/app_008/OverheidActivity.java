package com.hulzenga.ioi.android.app_008;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.Toast;

import com.hulzenga.ioi.android.DemoActivity;
import com.hulzenga.ioi.android.R;

public class OverheidActivity extends DemoActivity {

  private final String TAG = "OverheidActivity";

  private OverheidPagerAdapter mAdapter;
  private ViewPager            mPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mPager = new ViewPager(this);
    mPager.setId(R.id.app_008_pagerId);
    mAdapter = new OverheidPagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mAdapter);

    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      ActionBar.TabListener tabListener = new ActionBar.TabListener() {

        @Override
        public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
          mPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

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
          getSupportActionBar().setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
      });
      setContentView(mPager);
    } else {
      Log.e(TAG, "ActionBar is null");
      Toast.makeText(this, R.string.app_008_noActionBarError, Toast.LENGTH_LONG).show();
      finish();
    }

  }
}
