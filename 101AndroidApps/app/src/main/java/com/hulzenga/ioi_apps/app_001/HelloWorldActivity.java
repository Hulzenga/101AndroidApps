package com.hulzenga.ioi_apps.app_001;

import android.os.Bundle;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class HelloWorldActivity extends DemoActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_001_activity_hello_world);
  }
}
