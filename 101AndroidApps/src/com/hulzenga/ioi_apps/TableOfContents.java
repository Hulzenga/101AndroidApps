package com.hulzenga.ioi_apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class TableOfContents extends Activity {

    private final String TAG                  = "TABLE_OF_CONTENTS";

    private final String ACTIVITY_NAME        = "app_name";
    private final String ACTIVITY_DESCRIPTION = "app_description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_activity_table_of_contents);

        ListView appList = (ListView) findViewById(R.id.appList);

        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        final SparseArray<Class> activityMapping = new SparseArray<Class>();

        XmlPullParser xpp = getResources().getXml(R.xml.app_list);

        try {
            int eventType = xpp.getEventType();
            Map<String, Object> item;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("app")) {
                        item = new HashMap<String, Object>();
                        item.put(ACTIVITY_NAME, xpp.getAttributeValue(null, "name"));
                        item.put(ACTIVITY_DESCRIPTION, xpp.getAttributeValue(null, "description"));
                        data.add(item);
                        
                        int index = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                        Class app = null;
                        
                        try { 
                            app = Class.forName(xpp.getAttributeValue(null, "class"));
                        } catch (ClassNotFoundException e) {
                            Log.e(TAG, "ClassNotFoundException: the class referenced to in the xml app list is incorrect");                            
                        }
                                
                        activityMapping.put(index, app);
                    }

                    break;
                case XmlPullParser.END_TAG:
                    break;
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, " XmlPullParserException: while parsing the xml app list");
        } catch (IOException e) {
            Log.e(TAG, " IOException: while parsing the xml app list");
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.root_item_table_of_contents, new String[] {
                ACTIVITY_NAME, ACTIVITY_DESCRIPTION }, new int[] { R.id.app_name, R.id.app_description });
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

}
