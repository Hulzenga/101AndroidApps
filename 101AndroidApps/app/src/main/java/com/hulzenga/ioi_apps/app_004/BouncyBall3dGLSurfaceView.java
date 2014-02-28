package com.hulzenga.ioi_apps.app_004;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class BouncyBall3dGLSurfaceView extends GLSurfaceView {

  private BouncyBall3dRenderer mRenderer;

  public BouncyBall3dGLSurfaceView(Context context) {
    super(context);

    mRenderer = new BouncyBall3dRenderer(context);

    setEGLContextClientVersion(2);

    setRenderer(mRenderer);
  }

  private float mPreviousX;
  private float mPreviousY;

  @Override
  public boolean onTouchEvent(MotionEvent e) {

    if (e != null) {
      float x = e.getX();
      float y = e.getY();

      switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:

          final float dx = x - mPreviousX;
          final float dy = y - mPreviousY;

          mRenderer.touchMove(dx, dy);
      }

      mPreviousX = x;
      mPreviousY = y;
      return true;

    }
    return false;
  }
}
