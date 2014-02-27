package com.hulzenga.ioi_apps.app_007;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class HighScores extends DemoActivity {

    private static final String DIFFICULTY = "difficulty";
    ListView mHighScoreList;
    ListAdapter  mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put(DIFFICULTY, "Hard");
            data.add(item);
        }
        {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put(DIFFICULTY, "Medium");
            data.add(item);
        }
        {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put(DIFFICULTY, "Easy");
            data.add(item);
        }

        mAdapter = new SimpleAdapter(this, data, R.layout.app_007_item_high_score, new String[] { DIFFICULTY },
                new int[] { R.id.app_007_highScoresDifficulty });
        mHighScoreList = new ListView(this);
        mHighScoreList.setAdapter(mAdapter);
        mHighScoreList.setEnabled(false);

        setContentView(mHighScoreList);
    }    
}
