package com.hulzenga.ioi.android.app_004;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;

import com.hulzenga.ioi.android.R;
import com.hulzenga.ioi.android.util.open_gl.ColorFunction;
import com.hulzenga.ioi.android.util.open_gl.ColorFunctionFactory;
import com.hulzenga.ioi.android.util.open_gl.engine.NodeController;
import com.hulzenga.ioi.android.util.open_gl.engine.SceneGraph;
import com.hulzenga.ioi.android.util.open_gl.engine.SceneNode;
import com.hulzenga.ioi.android.util.open_gl.geometry.Box;
import com.hulzenga.ioi.android.util.open_gl.geometry.Cylinder;
import com.hulzenga.ioi.android.util.open_gl.geometry.Geometry;
import com.hulzenga.ioi.android.util.open_gl.geometry.Grid;
import com.hulzenga.ioi.android.util.open_gl.geometry.Sphere;
import com.hulzenga.ioi.android.util.open_gl.geometry.Transform;

/**
 * Simulates a ball of unit mass falling from a given start Height and starting speed
 * bouncing elastically on a floor
 */
public class BouncyBallGame implements NodeController {

  private static final long SIMULATION_STEP = 10;

  private static final float GRAVITY = 15.0f;

  private static final float BOUNCE_FACTOR     = 0.90f;
  private static final float BOUNCE_STOP_SPEED = 0.15f;
  private static final float EPSILON           = 0.01f;

  private static final float          BALL_RADIUS       = 0.5f;
  private static final float          BALL_START_HEIGHT = 4.0f;
  private volatile     float          mBallHeight       = BALL_START_HEIGHT;
  private static final float          BALL_START_SPEED  = 0.0f;
  private              float          mBallSpeed        = BALL_START_SPEED;
  private static final float          FLOOR_UP          = 2.3f;
  private static final float          FLOOR_DOWN        = 2.0f;
  private volatile     float          mFloorHeight      = FLOOR_DOWN;
  private static final float          FLOOR_SPEED       = 10.0f;
  private              FLOOR_MOVEMENT mFloorMovement    = FLOOR_MOVEMENT.STOPPED;
  private Thread mSimulationThread;
  private boolean mRunning = false;

  public BouncyBallGame(BouncyBall3dGLSurfaceView bouncyBall3dGLSurfaceView) {
    bouncyBall3dGLSurfaceView.setScene(createScene());
  }

  SceneGraph createScene() {
    SceneGraph sceneGraph = new SceneGraph();

    ColorFunction uniformRed = ColorFunctionFactory.createUniform(1.0f, 0.0f, 0.0f, 1.0f);
    ColorFunction uniformGray = ColorFunctionFactory.createUniform(0.6f, 0.6f, 0.6f, 1.0f);
    ColorFunction dirt = ColorFunctionFactory.createRandomBound(0.35f, 0.45f, 0.35f, 0.45f, 0.1f, 0.15f);

    SceneNode cylinder = new SceneNode(new Cylinder(1.0f, 2 * FLOOR_DOWN, 16), uniformRed);
    cylinder.subscribe(new NodeController() {
      @Override
      public void update(SceneNode node) {node.setTranslation(0.0f, mFloorHeight - FLOOR_DOWN, 0.0f);}
    });
    sceneGraph.addNode(cylinder);

    sceneGraph.addNode(new SceneNode(new Cylinder(1.5f, 2 * FLOOR_DOWN - 0.5f, 16), uniformGray));
    sceneGraph.addNode(new SceneNode(new Cylinder(1.6f, 2 * FLOOR_DOWN - 1.0f, 16), uniformGray));

    Geometry box1Geometry = new Box(4.0f, 2.0f, 0.8f);
    Geometry box2Geometry = new Box(5.0f, 1.6f, 0.6f);
    Geometry box3Geometry = new Box(7.0f, 1.2f, 0.4f);

    for (int i = 0; i < 4; i++) {
      SceneNode box1 = new SceneNode(box1Geometry, uniformGray);
      box1.setRotation(0.0f, i * 45.0f, 0.0f);
      sceneGraph.addNode(box1);

      SceneNode box2 = new SceneNode(box2Geometry, uniformGray);
      box2.setRotation(0.0f, i * 45.0f, 0.0f);
      sceneGraph.addNode(box2);

      SceneNode box3 = new SceneNode(box3Geometry, uniformGray);
      box3.setRotation(0.0f, i * 45.0f, 0.0f);
      sceneGraph.addNode(box3);
    }

    Geometry baseSphere = new Sphere(3.0f, 16, 16);
//    baseSphere.discardSelectedVertices(new Geometry.VertexSelector() {
//      @Override
//      public boolean isSelected(Vec3 vertex) {
//        return vertex.y < 1.0f;
//      }
//    });
    SceneNode base = new SceneNode(baseSphere, uniformGray);
    base.setTranslation(0.0f, -2.0f, 0.0f);
    sceneGraph.addNode(base);

    Geometry floorGeometry = new Grid(60.0f, 60.0f, 30, 30);
    Transform.jiggle(floorGeometry, 0.8f, 0.3f, 0.8f);
    floorGeometry.makeFaceted();
    SceneNode floor = new SceneNode(floorGeometry, dirt);
    sceneGraph.addNode(floor);

    SceneNode ball = new SceneNode(new Sphere(BALL_RADIUS, 16, 16), ColorFunctionFactory.createRandom());
    ball.setTranslation(0.0f, BALL_START_HEIGHT, 0.0f);
    ball.subscribe(new NodeController() {
      @Override
      public void update(SceneNode node) {
        node.setTranslation(0.0f, mBallHeight, 0.0f);
      }
    });
    sceneGraph.addNode(ball);

    sceneGraph.setLookAt(0.0f, FLOOR_DOWN, 0.0f);

    return sceneGraph;
  }

  public void start(Context context) {

    if (!mRunning) {

      final SoundPool ballSoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
      final int bounceSound = ballSoundPool.load(context, R.raw.app_004_bounce, 1);

      ballSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
          mSimulationThread = new Thread(new Runnable() {
            @Override
            public void run() {
              boolean restingOnFloor = false;
              float delta;

              while (mRunning) {
                delta = SIMULATION_STEP / 1000.0f;

                switch (mFloorMovement) {
                  case DOWN:
                    mFloorHeight -= FLOOR_SPEED * delta;
                    if (mFloorHeight < FLOOR_DOWN) {
                      mFloorHeight = FLOOR_DOWN;
                      mFloorMovement = FLOOR_MOVEMENT.STOPPED;
                    }
                    break;
                  case UP:
                    mFloorHeight += FLOOR_SPEED * delta;
                    if (mFloorHeight > FLOOR_UP) {
                      mFloorHeight = FLOOR_UP;
                      mFloorMovement = FLOOR_MOVEMENT.DOWN;
                    }
                    break;
                  case STOPPED:
                    break;
                }

                if (!restingOnFloor) {

                  float newHeight = mBallHeight + delta * mBallSpeed - 0.5f * GRAVITY * delta * delta;

                  if (newHeight < mFloorHeight + BALL_RADIUS) {
                    mBallHeight = mFloorHeight + BALL_RADIUS;

                    if (mFloorMovement == FLOOR_MOVEMENT.UP && Math.abs(mBallSpeed) < FLOOR_SPEED) {
                      mBallSpeed = FLOOR_SPEED + Math.abs(0.3f * mBallSpeed);
                    } else {
                      mBallSpeed = -BOUNCE_FACTOR * mBallSpeed;
                    }

                    ballSoundPool.play(bounceSound, 1.0f, 1.0f, 1, 0, 1.0f);
                  } else {
                    mBallHeight = newHeight;
                    mBallSpeed -= delta * GRAVITY;
                  }

                  if (Math.abs(mBallSpeed) < BOUNCE_STOP_SPEED && mBallHeight < mFloorHeight + BALL_RADIUS + EPSILON) {
                    restingOnFloor = true;
                    mBallHeight = mFloorHeight + BALL_RADIUS;
                    mBallSpeed = 0.0f;
                  }
                } else {
                  if (mFloorMovement != FLOOR_MOVEMENT.STOPPED) {
                    restingOnFloor = false;
                    if (mFloorMovement == FLOOR_MOVEMENT.UP) {
                      mBallSpeed = FLOOR_SPEED + EPSILON;
                    }
                  }
                }

                SystemClock.sleep(SIMULATION_STEP);
              }
              ballSoundPool.release();
            }
          });
          mRunning = true;
          mSimulationThread.start();
        }
      });

    }
  }

  public void stop() {
    mRunning = false;
    try {
      mSimulationThread.join();
    } catch (InterruptedException e) {
      //shouldn't happen but not important
    }
  }

  public void moveUp() {
    mFloorMovement = FLOOR_MOVEMENT.UP;
  }

  private enum FLOOR_MOVEMENT {
    UP, DOWN, STOPPED
  }

  @Override
  public void update(SceneNode node) {
    node.setTranslation(0.0f, mBallHeight, 0.0f);
  }
}
