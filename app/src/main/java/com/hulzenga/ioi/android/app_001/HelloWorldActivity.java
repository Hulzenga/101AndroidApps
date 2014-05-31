package com.hulzenga.ioi.android.app_001;

import android.os.Bundle;

import com.hulzenga.ioi.android.AppActivity;
import com.hulzenga.ioi.android.R;

public class HelloWorldActivity extends AppActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_001_activity_hello_world);
  }
}
