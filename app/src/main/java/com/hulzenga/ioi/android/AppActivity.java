package com.hulzenga.ioi.android;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * AppActivity provides a common set of behaviours for all demo apps. These
 * are:
 * <p/>
 * 1. show back navigation in the actionbar
 * <p/>
 * 2. override the default animation to a slide in/out
 *
 * @author Jouke Hulzenga
 */
public abstract class AppActivity extends ActionBarActivity {

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(com.hulzenga.ioi.android.R.anim.left_slide_in, com.hulzenga.ioi.android.R.anim.right_slide_out);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    useUpNavigation(true);
  }

  public void useUpNavigation(boolean useIt) {
    // setup the action bar to use up navigation
    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(useIt);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.app_base_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(com.hulzenga.ioi.android.R.anim.left_slide_in, com.hulzenga.ioi.android.R.anim.right_slide_out);
        return true;
      case R.id.app_menu_info:
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        final App app = App.getAppFromActivity(this);
        AppDetailsDialog dialog = AppDetailsDialog.newInstance(app);
        dialog.show(trans, null);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
