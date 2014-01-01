package com.hulzenga.ioi_apps.app_003;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_003.database.MonsterProvider;
import com.hulzenga.ioi_apps.app_003.database.MonsterContract;

public class MonsterDatabaseActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;
	private ListView monsterListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_003_activity_crud);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		fillData();
		monsterListView = (ListView) findViewById(R.id.monsterList);
		monsterListView.setAdapter(adapter);
	}

	public void fillData() {
		String[] from = new String[] {MonsterContract.COLUMN_MONSTER_NAME};
		int[]    to	  = new int[]    {R.id.monsterItemName};
		
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.app_003_item_monster_list, null, from, to);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	public void addMonster(View view) {
		ContentValues values = new ContentValues();
		values.put(MonsterContract.COLUMN_MONSTER_NAME, "tester");
		getContentResolver().insert(MonsterProvider.CONTENT_URI, values);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crud, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = {MonsterContract.COLUMN_MONSTER_ID, MonsterContract.COLUMN_MONSTER_NAME};
		CursorLoader cursorLoader = new CursorLoader(this, MonsterProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		adapter.swapCursor(null);		
	}

}
