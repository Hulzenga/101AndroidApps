package com.hulzenga.ioi.android;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * DemoActivity provides a common set of behaviours for all demo apps. These
 * are:
 * <p/>
 * 1. show back navigation in the actionbar
 * <p/>
 * 2. override the default animation to a slide in/out
 *
 * @author Jouke Hulzenga
 */
public abstract class DemoActivity extends ActionBarActivity {

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(com.hulzenga.ioi.android.R.anim.left_slide_in, com.hulzenga.ioi.android.R.anim.right_slide_out);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // setup the action bar to use up navigation
    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {

      NavUtils.navigateUpFromSameTask(this);
      overridePendingTransition(com.hulzenga.ioi.android.R.anim.left_slide_in, com.hulzenga.ioi.android.R.anim.right_slide_out);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

}
