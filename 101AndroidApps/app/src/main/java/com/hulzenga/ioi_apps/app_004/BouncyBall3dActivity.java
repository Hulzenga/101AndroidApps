package com.hulzenga.ioi_apps.app_004;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.hulzenga.ioi_apps.DemoActivity;

public class BouncyBall3dActivity extends DemoActivity {

  private BouncyBall3dGLSurfaceView mGLSurfaceView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
    final boolean supportsOpenGlEs2 = configurationInfo.reqGlEsVersion >= 2;

    if (supportsOpenGlEs2) {
      mGLSurfaceView = new BouncyBall3dGLSurfaceView(this);
    } else {
      Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
      finish();
    }
    setContentView(mGLSurfaceView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mGLSurfaceView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mGLSurfaceView.onPause();
  }


}
