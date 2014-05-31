package com.hulzenga.ioi.android;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableOfContentsActivity extends AppActivity {

  private static final String TAG = "TableOfContentsActivity";

  private static final String ACTIVITY_NAME        = "app_name";
  private static final String ACTIVITY_DESCRIPTION = "app_description";
  private static final String ACTIVITY_ICON        = "app_icon";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    useUpNavigation(false);
    setContentView(com.hulzenga.ioi.android.R.layout.root_activity_table_of_contents);

    ListView appList = (ListView) findViewById(com.hulzenga.ioi.android.R.id.appList);

    final List<Map<String, Object>> data = new ArrayList<>();
    final List<Class<?>> demos = new ArrayList<>();
    Resources res = getResources();

    for (int i = 1; i < App.values().length; i++) {
      HashMap<String, Object> item = new HashMap<>();
      item.put(ACTIVITY_NAME, App.values()[i].getTitleString(res));
      item.put(ACTIVITY_DESCRIPTION, App.values()[i].getShortDescriptionString(res));
      item.put(ACTIVITY_ICON, App.values()[i].getIcon());
      data.add(item);

      demos.add(App.values()[i].getStartActivity());
    }

    SimpleAdapter adapter = new SimpleAdapter(this, data, com.hulzenga.ioi.android.R.layout.root_item_table_of_contents, new String[]{
        ACTIVITY_NAME, ACTIVITY_DESCRIPTION, ACTIVITY_ICON}, new int[]{com.hulzenga.ioi.android.R.id.app_name, com.hulzenga.ioi.android.R.id.app_description,
        com.hulzenga.ioi.android.R.id.app_icon});
    appList.setAdapter(adapter);


    appList.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Class startThisActivity = demos.get(position);

        if (startThisActivity != null) {
          final Intent appIntent = new Intent(TableOfContentsActivity.this, startThisActivity);
          startActivity(appIntent);
          overridePendingTransition(com.hulzenga.ioi.android.R.anim.right_slide_in, com.hulzenga.ioi.android.R.anim.left_slide_out);
        }
      }
    });

    appList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        AppDetailsDialog dialog = AppDetailsDialog.newInstance(App.values()[position+1]);
        dialog.show(trans, null);
        return true;
      }
    });
  }
}
