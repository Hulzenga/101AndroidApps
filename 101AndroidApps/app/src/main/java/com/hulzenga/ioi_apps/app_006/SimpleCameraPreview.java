package com.hulzenga.ioi_apps.app_006;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class SimpleCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

  private static final String TAG = "CAMERA_PREVIEW";

  private SurfaceHolder mHolder;
  private Camera        mCamera;

  public SimpleCameraPreview(Context context) {
    super(context);
    setupSurfaceHolder();
  }

  public SimpleCameraPreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    setupSurfaceHolder();
  }

  private void setupSurfaceHolder() {
    mHolder = getHolder();
    mHolder.addCallback(this);
  }

  public void loadCamera(Camera camera) {
    mCamera = camera;
    startPreview();
  }

  public void startPreview() {
    if (mCamera != null) {
      try {
        mCamera.setPreviewDisplay(mHolder);
        mCamera.startPreview();
      } catch (IOException e) {
        Log.e(TAG, "Error setting camera preview: " + e.getMessage());
      } catch (RuntimeException e) {
        Log.e(TAG, "Runtime exception");
      }
    } else {
      Log.d(TAG, "No camera loaded");
    }
  }

  public void stopPreview() {
    if (mCamera != null) {
      mCamera.stopPreview();
    } else {
      Log.d(TAG, "No camera to stop");
    }
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {

  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    // TODO Auto-generated method stub

  }

}
