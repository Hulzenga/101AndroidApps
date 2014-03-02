package com.hulzenga.ioi_apps.app_004;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;

import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.util.open_gl.engine.NodeController;
import com.hulzenga.ioi_apps.util.open_gl.engine.SceneNode;

/**
 * Simulates a ball of unit mass falling from a given start Height and starting speed
 * bouncing elastically on a floor
 */
public class BouncyBallSimulator implements NodeController {

  private static final long SIMULATION_STEP = 10;

  private static final float BOUNCE_FACTOR = 0.95f;
  private static final float BOUNCE_STOP_SPEED = 0.05f;

  private float mFloorHeight;
  private volatile float mHeight;
  private volatile float mSpeed;
  private float mGravity;
  private Thread mSimulationThread;
  private boolean mRunning = false;

  public BouncyBallSimulator(float floorHeight, float startHeight, float startSpeed, float gravity) {
    mFloorHeight = floorHeight;
    mHeight = startHeight;
    mSpeed = startSpeed;
    mGravity = gravity;


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

              while (mRunning) {

                if (!restingOnFloor) {
                  float delta = SIMULATION_STEP / 1000.0f;
                  float newHeight = mHeight + delta * mSpeed;

                  if (newHeight < mFloorHeight) {
                    mHeight = mFloorHeight;
                    mSpeed = -mSpeed-BOUNCE_STOP_SPEED;
                    ballSoundPool.play(bounceSound, 1.0f, 1.0f, 1, 0, 1.0f);
                  } else {
                    mHeight = newHeight;
                    mSpeed -= delta * mGravity;
                  }

                  if (mSpeed < BOUNCE_STOP_SPEED && mHeight == mFloorHeight) {
                    restingOnFloor = true;
                    mHeight = mFloorHeight;
                    mSpeed = 0.0f;
                  }
                } else {
                  if (mHeight != mFloorHeight || mSpeed > BOUNCE_STOP_SPEED) {
                    restingOnFloor = false;
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

  @Override
  public void update(SceneNode node) {
    node.setTranslation(0.0f, mHeight, 0.0f);
  }
}
