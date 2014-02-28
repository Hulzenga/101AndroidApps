package com.hulzenga.ioi_apps.app_002;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;

public class BouncyBallsView extends View implements OnTouchListener {

	/*
   * TODO: figure out bug which causes all balls to disappear. Encountered when
	 * screen is full balls and one of the balls is forcefully pulled through the balls 
	 * (might be a divide by almost zero thing in collision handling) 
	 */

  private static final String TAG = "BouncyBallView";

  //drawing frequency parameters
  private static final long  TIMESTEP         = 16;
  private static final float TIMESTEP_SECONDS = TIMESTEP / 1000.0f;

  //performance limit
  private static final int MAX_NUMBER_OF_BALLS = 90;

  //lock so drawing and touch events don't interlace
  private static final Object lock = new Object();

  private Paint mBallPaint = new Paint();

  //list of the balls and a possible selected ball
  private List<Ball> mBalls        = new ArrayList<Ball>();
  private Ball       mSelectedBall = null;

  //state booleans
  private          boolean mBallSelected = false;
  private volatile boolean mRunning      = false;

  public BouncyBallsView(Context context) {
    super(context);
    this.setOnTouchListener(this);

    //Initialise paint
    mBallPaint.setColor(Color.GREEN);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    synchronized (lock) {
      for (Ball ball : mBalls) {
        drawBall(canvas, ball);
      }
      if (mBallSelected) {
        drawBall(canvas, mSelectedBall);
      }
    }
  }

  private void drawBall(Canvas canvas, Ball ball) {
    final int speed = Math.min(Math.max((int) Math.sqrt(ball.vX * ball.vX + ball.vY * ball.vY), 0), 255);

    mBallPaint.setColor(Color.rgb(speed, 255 - speed, 0));
    canvas.drawCircle(ball.x, ball.y, ball.r, mBallPaint);
  }

  public boolean onTouch(View view, MotionEvent event) {

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:

        //Check if selecting an existing ball
        for (int i = 0; i < mBalls.size(); i++) {
          if (mBalls.get(i).isBallHere(event.getX(), event.getY())) {
            mSelectedBall = mBalls.get(i);
            mSelectedBall.select();
            mBallSelected = true;
            synchronized (lock) {
              mBalls.remove(i);
            }
            break;
          }
        }

        //If not selecting make a new ball and select it
        if (!mBallSelected && mBalls.size() < MAX_NUMBER_OF_BALLS) {
          mSelectedBall = new Ball(event.getX(), event.getY());
          mBallSelected = true;
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (mBallSelected) {
          mSelectedBall.dragBallTo(event.getX(), event.getY());
        }
        break;
      case MotionEvent.ACTION_UP:
        if (mBallSelected) {
          //return selected ball to the list
          synchronized (lock) {
            mBalls.add(mSelectedBall);
          }
          mBallSelected = false;
        }
        break;
    }

    return true;
  }

  public void start() {
    new Thread(new Runnable() {

      @Override
      public void run() {

        long time = System.currentTimeMillis();

        mRunning = true;

        while (mRunning) {

          synchronized (lock) {

            for (int i = 0; i < mBalls.size(); i++) {
              mBalls.get(i).updatePosition(TIMESTEP_SECONDS);

              for (int j = i + 1; j < mBalls.size(); j++) {
                mBalls.get(i).handleCollision(mBalls.get(j));
              }
              if (mBallSelected) {
                mBalls.get(i).handleCollision(mSelectedBall);
              }

            }
          }

          try {
            Thread.sleep(TIMESTEP - (System.currentTimeMillis() - time)); //tries to sync
          } catch (InterruptedException e) {
            e.printStackTrace();
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.d(TAG, "Error: the numerical simulation per step takes longer than the desired timestep");
          }

          time = System.currentTimeMillis();
          postInvalidate();
        }

      }
    }).start();
  }

  public void stop() {
    mRunning = false;
  }


  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    Ball.setBounds(w, h);
  }


}
