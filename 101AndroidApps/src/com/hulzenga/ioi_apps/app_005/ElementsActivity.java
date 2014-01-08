package com.hulzenga.ioi_apps.app_005;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class ElementsActivity extends DemoActivity {

    private static final String[] ELEMENTS      = { "Earth", "Air", "Fire", "Water" };
    private Random                mRandom       = new Random();
    
    private List<String>          mGridElements = new ArrayList<String>();

    private ElementsGridView      mElementsGridView;

    private ArrayAdapter          mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_005_activity_elements);

        mElementsGridView = (ElementsGridView) findViewById(R.id.app_005_ElementsGridView);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mGridElements);

        mElementsGridView.setAdapter(mAdapter);
    }

    public void addElement(View v) {
        String randomElement = ELEMENTS[mRandom.nextInt(ELEMENTS.length)];
        mAdapter.add(randomElement);
    }
    
    public void addMultipleElements(View v) {
        addElement(null);
        addElement(null);
        addElement(null);
        addElement(null);
    }

}
