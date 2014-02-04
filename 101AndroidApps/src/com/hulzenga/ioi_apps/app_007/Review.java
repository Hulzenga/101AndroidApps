package com.hulzenga.ioi_apps.app_007;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class Review extends DemoActivity {

    private ListView mWikiListView;
    private Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_007_activity_review);        
    }
    
    private class WikiAdapter extends ArrayAdapter<Wiki> {

        public WikiAdapter(Context context, int resource, int textViewResourceId, List<Wiki> objects) {
            super(context, resource, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }
        
    }

}
