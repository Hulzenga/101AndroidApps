package com.hulzenga.ioi_apps;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * DemoActivity provides a common set of behaviours for all demo apps. These
 * are:
 * 
 * 1. show back navigation in the actionbar
 * 
 * 2. override the default animation to a slide in/out
 * 
 * @author Jouke Hulzenga
 * 
 */
public abstract class DemoActivity extends Activity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
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
            overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
