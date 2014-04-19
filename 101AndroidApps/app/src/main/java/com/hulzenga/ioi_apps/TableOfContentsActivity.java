package com.hulzenga.ioi_apps;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableOfContentsActivity extends Activity {

  private static final String TAG = "TABLE_OF_CONTENTS";

  private static final String ACTIVITY_NAME        = "app_name";
  private static final String ACTIVITY_DESCRIPTION = "app_description";
  private static final String ACTIVITY_ICON        = "app_icon";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.root_activity_table_of_contents);

    ListView appList = (ListView) findViewById(R.id.appList);

    final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    final List<Class<?>> demos = new ArrayList<Class<?>>();
    Resources res = getResources();

    for (DemoApp app : DemoApp.values()) {
      HashMap<String, Object> item = new HashMap<String, Object>();
      item.put(ACTIVITY_NAME, app.getTitleString(res));
      item.put(ACTIVITY_DESCRIPTION, app.getShortDescriptionString(res));
      item.put(ACTIVITY_ICON, app.getIcon());
      data.add(item);

      demos.add(app.getActivity());
    }

    SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.root_item_table_of_contents, new String[]{
        ACTIVITY_NAME, ACTIVITY_DESCRIPTION, ACTIVITY_ICON}, new int[]{R.id.app_name, R.id.app_description,
        R.id.app_icon});
    appList.setAdapter(adapter);

    appList.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Class startThisActivity = demos.get(position);

        if (startThisActivity != null) {
          final Intent appIntent = new Intent(TableOfContentsActivity.this, startThisActivity);
          startActivity(appIntent);
          overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
        }
      }
    });
  }
}
