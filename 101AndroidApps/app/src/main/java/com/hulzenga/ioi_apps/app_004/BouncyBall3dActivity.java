package com.hulzenga.ioi_apps.app_004;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class BouncyBall3dActivity extends DemoActivity {

  private BouncyBall3dGLSurfaceView mGLSurfaceView;
  private BouncyBallGame mGame;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
    final boolean supportsOpenGlEs2 = configurationInfo.reqGlEsVersion >= 2;

    if (!supportsOpenGlEs2) {
      Toast.makeText(this, "This device does not support OpenGL ES 2.0+", Toast.LENGTH_LONG).show();
      finish();
    }

    // hide action bar
    getActionBar().hide();

    // hide the status bar
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.app_004_activity_bouncy_ball);

    mGLSurfaceView = (BouncyBall3dGLSurfaceView) findViewById(R.id.app_004_bouncyBall3dView);
    Button bounceButton = (Button) findViewById(R.id.app_004_bounceButton);
    bounceButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mGame.moveUp();
      }
    });

    mGame = new BouncyBallGame(mGLSurfaceView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mGLSurfaceView.onResume();
    mGame.start(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mGLSurfaceView.onPause();
    mGame.stop();
  }


}
