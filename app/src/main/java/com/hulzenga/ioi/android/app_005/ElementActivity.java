package com.hulzenga.ioi.android.app_005;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hulzenga.ioi.android.AppActivity;
import com.hulzenga.ioi.android.R;
import com.hulzenga.ioi.android.app_005.ElementSnakeView.ElementAnimationCallback;

import java.util.ArrayList;
import java.util.List;

public class ElementActivity extends AppActivity implements ElementAnimationCallback {

  private ElementSnakeView mElementsSnakeView;
  private Button           mAddElementButton;
  private Button           mAddMultipleElementsButton;

  private ElementAdapter mAdapter;

  private int mAddNMoreElements = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.app_005_activity_elements);

    // link up member views
    mElementsSnakeView = (ElementSnakeView) findViewById(R.id.app_005_ElementsGridView);
    mAddElementButton = (Button) findViewById(R.id.app_005_addElementButton);
    mAddMultipleElementsButton = (Button) findViewById(R.id.app_005_addMultipleElementsButton);


    List<Element> elementalList = new ArrayList<Element>();
    mAdapter = new ElementAdapter(this, R.layout.app_005_item_element, elementalList);

    mElementsSnakeView.setAdapter(mAdapter);
    mElementsSnakeView.registerAnimationCallback(this);
  }


  public void addElement(View v) {
    mAdapter.add(Element.getRandomElement());
  }

  // the code for this should ideally have been implemented inside the
  // snakeview
  public void addMultipleElements(View v) {
    mAddNMoreElements = 4;
    addElement(v);
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
    if (mAddNMoreElements > 0) {
      addElement(null);
      mAddNMoreElements--;
    } else {
      enableButtons();
    }
  }

}
