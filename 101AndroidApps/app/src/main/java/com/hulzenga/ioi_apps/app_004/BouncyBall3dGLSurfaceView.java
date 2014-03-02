package com.hulzenga.ioi_apps.app_004;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.hulzenga.ioi_apps.util.open_gl.ColorFunction;
import com.hulzenga.ioi_apps.util.open_gl.ColorFunctionFactory;
import com.hulzenga.ioi_apps.util.open_gl.engine.SceneGraph;
import com.hulzenga.ioi_apps.util.open_gl.engine.SceneNode;
import com.hulzenga.ioi_apps.util.open_gl.geometry.Box;
import com.hulzenga.ioi_apps.util.open_gl.geometry.Sphere;

public class BouncyBall3dGLSurfaceView extends GLSurfaceView {

  private BouncyBall3dRenderer mRenderer;
  private BouncyBallSimulator  mBallSimulator;

  private float                mPreviousX;
  private float                mPreviousY;

  public BouncyBall3dGLSurfaceView(Context context) {
    super(context);

    SceneGraph mSceneGraph = new SceneGraph();

    ColorFunction uniformRed = ColorFunctionFactory.createUniform(1.0f, 0.0f, 0.0f, 1.0f);
    SceneNode floor = new SceneNode(new Box(10.0f, 0.0f, 10.0f), uniformRed);
    mSceneGraph.addNode(floor);

    SceneNode block = new SceneNode(new Box(0.5f, 1.5f, 0.5f), uniformRed);
    block.setTranslation(1.0f, 0.5f, 0.0f);
    mSceneGraph.addNode(block);

    SceneNode ball = new SceneNode(new Sphere(0.5f, 16, 16), ColorFunctionFactory.createRandom());
    ball.setTranslation(0.0f, 2.0f, 0.0f);
    mSceneGraph.addNode(ball);

    mBallSimulator = new BouncyBallSimulator(0.5f, 2.0f, 0.0f, 30.0f);

    ball.subscribe(mBallSimulator);

    mRenderer = new BouncyBall3dRenderer(context, mSceneGraph);

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
    return false;
  }

  @Override
  public void onResume() {
    super.onResume();
    mBallSimulator.start(getContext());
  }

  @Override
  public void onPause() {
    super.onPause();
    mBallSimulator.stop();
  }
}
