package com.hulzenga.ioi_apps.app_002;

import android.os.Bundle;

import com.hulzenga.ioi_apps.DemoActivity;

public class BouncyBallsActivity extends DemoActivity {

  private BouncyBallsView bouncyBallView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    bouncyBallView = new BouncyBallsView(this);
    setContentView(bouncyBallView);
  }

  @Override
  protected void onPause() {
    bouncyBallView.stop();
    super.onPause();
  }

  @Override
  protected void onStart() {
    bouncyBallView.start();
    super.onStart();
  }

}
