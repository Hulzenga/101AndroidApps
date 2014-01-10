package com.hulzenga.ioi_apps.app_005;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_005.ElementsView.ElementsViewObserver;

public class ElementsActivity extends DemoActivity implements ElementsViewObserver {

    private static final String[] ELEMENTS            = { "Earth", "Air", "Fire", "Water" };

    private List<Element>         mGridElements       = new ArrayList<Element>();
    private List<Element>         mCachedElementViews = new ArrayList<Element>();

    private ElementsView          mElementsGridView;
    private Button                mAddElementButton;
    private Button                mAddMultipleElementsButton;

    private ElementAdapter        mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_005_activity_elements);

        // link up member views
        mElementsGridView = (ElementsView) findViewById(R.id.app_005_ElementsGridView);
        mAddElementButton = (Button) findViewById(R.id.app_005_addElementButton);
        mAddMultipleElementsButton = (Button) findViewById(R.id.app_005_addMultipleElementsButton);

        mAdapter = new ElementAdapter(this, R.layout.app_005_item_element, mGridElements);
        mAdapter.add(new Element());
        mAdapter.add(new Element());

        mElementsGridView.setAdapter(mAdapter);
        mElementsGridView.registerObserver(this);
    }

    public void addElement(View v) {
        mAdapter.add(new Element());
    }

    public void addMultipleElements(View v) {        
        addElement(null);
        addElement(null);
        addElement(null);
        addElement(null);
    }

    private void enableButtons() {
        mAddElementButton.setEnabled(true);
        mAddElementButton.setClickable(true);
        mAddMultipleElementsButton.setEnabled(true);
        mAddMultipleElementsButton.setClickable(true);
    }

    private void disableButtons() {
        mAddElementButton.setEnabled(false);
        mAddElementButton.setClickable(false);
        mAddMultipleElementsButton.setEnabled(false);
        mAddMultipleElementsButton.setClickable(false);
    }

    @Override
    public void onAnimationStart() {
        disableButtons();
    }

    @Override
    public void onAnimationEnd() {
        enableButtons();
    }

}
