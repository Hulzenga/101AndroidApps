package com.hulzenga.ioi.android.app_004;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hulzenga.ioi.android.util.open_gl.engine.SceneGraph;

public class BouncyBall3dGLSurfaceView extends GLSurfaceView {

  private BouncyBall3dRenderer mRenderer;

  private float mPreviousX;
  private float mPreviousY;

  public BouncyBall3dGLSurfaceView(Context context) {
    this(context, null);
  }

  public BouncyBall3dGLSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setScene(SceneGraph sceneGraph) {
    mRenderer = new BouncyBall3dRenderer(getContext(), sceneGraph);

    setEGLContextClientVersion(2);

    setRenderer(mRenderer);
  }

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
    return super.onTouchEvent(e);
  }
}
