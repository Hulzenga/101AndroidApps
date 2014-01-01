package com.hulzenga.ioi_apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_001.HelloWorldActivity;
import com.hulzenga.ioi_apps.app_002.BouncyBallsActivity;
import com.hulzenga.ioi_apps.app_003.MonsterDatabaseActivity;

public class TableOfContents extends Activity {

	private final String ACTIVITY_NAME = "app_name";
	private final String ACTIVITY_DESCRIPTION = "app_description";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_activity_table_of_contents);
		
		ListView appList = (ListView) findViewById(R.id.appList);
		
		final List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		final SparseArray<Class<? extends Activity>> activityMapping = new SparseArray<Class<? extends Activity>>();
		
		int i = 0;
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ACTIVITY_NAME, "Hello World");
			item.put(ACTIVITY_DESCRIPTION, "displays the world famous hello world string");
			data.add(item);
			activityMapping.put(i++, HelloWorldActivity.class);
		}
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ACTIVITY_NAME, "Bouncy Ball");
			item.put(ACTIVITY_DESCRIPTION, "click to make bouncy balls");
			data.add(item);
			activityMapping.put(i++, BouncyBallsActivity.class);
		}
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ACTIVITY_NAME, "CRUD");
			item.put(ACTIVITY_DESCRIPTION, "simple create, read, update and destroy app");
			data.add(item);
			activityMapping.put(i++, MonsterDatabaseActivity.class);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.root_item_table_of_contents,
				new String[] {ACTIVITY_NAME, ACTIVITY_DESCRIPTION}, new int[] {R.id.app_name, R.id.app_description});		
		appList.setAdapter(adapter);
		
		appList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Class<? extends Activity> startThisActivity = activityMapping.get(position);
				
				if (startThisActivity != null) {
					final Intent appIntent = new Intent(TableOfContents.this, startThisActivity);
					startActivity(appIntent);
					overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
				}
			}
			
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.table_of_contents, menu);
		return true;
	}	

}
