package com.hulzenga.ioi_apps.app_005;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.view.View;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class ElementsActivity extends DemoActivity {

    private static final String[] ELEMENTS      = { "Earth", "Air", "Fire", "Water" };
    private Random                mRandom       = new Random();

    private List<Element>         mGridElements = new ArrayList<Element>();

    private ElementsGridView      mElementsGridView;

    private ElementAdapter        mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_005_activity_elements);

        mElementsGridView = (ElementsGridView) findViewById(R.id.app_005_ElementsGridView);

        mAdapter = new ElementAdapter(this, R.layout.app_005_item_element, mGridElements);
        mAdapter.add(new Element());
        mAdapter.add(new Element());

        mElementsGridView.setAdapter(mAdapter);
    }

    public void addElement(View v) {
        String randomElement = ELEMENTS[mRandom.nextInt(ELEMENTS.length)];
        mAdapter.add(new Element());
        mElementsGridView.requestLayout();
    }

    public void addMultipleElements(View v) {
        addElement(null);
        addElement(null);
        addElement(null);
        addElement(null);
    }

}
