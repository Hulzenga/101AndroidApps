package com.hulzenga.ioi.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * The FragmentActivity companion to the DemoActivity class. All functionality added there
 * should be found here as well
 * Created by jouke on 20-4-14.
 */
public class DemoFragmentActivity extends FragmentActivity {

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(com.hulzenga.ioi.android.R.anim.left_slide_in, com.hulzenga.ioi.android.R.anim.right_slide_out);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // setup the action bar to use up navigation
    getActionBar().setDisplayHomeAsUpEnabled(true);
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
