package com.hulzenga.ioi_apps.app_007;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.util.ConstraintEnforcer;
import com.hulzenga.ioi_apps.util.ScreenMetrics;

public class StatusFragment extends Fragment {

  private static final String TAG = "STATUS FRAGMENT";

  private TextView mTimerText;
  private TextView mDifficultyText;
  private TextView mScoreDeltaText;
  private TextView mScoreText;

  private int mScore = 0;
  private int mTime  = 60;

  private Thread mTimerThread;
  private volatile boolean mPaused = false;
  private volatile boolean mFinish = false;

  private TimeOutListener mTimeOutListener;

  private int   mAnimationLengthMedium;
  private float mAnimationDistance;

  public interface TimeOutListener {
    public void onTimeOut(int score);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    mAnimationLengthMedium = activity.getResources().getInteger(R.integer.animation_medium);
    mAnimationDistance = ScreenMetrics.dpToPix(60.0f, activity);

    try {
      mTimeOutListener = (TimeOutListener) activity;
    } catch (ClassCastException e) {
      Log.e(TAG, activity.toString() + " must implement StatusFragment.TimeOutListener");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    return inflater.inflate(R.layout.app_007_fragment_status, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);


    mTimerText = (TextView) getView().findViewById(R.id.app_007_timerTextView);
    mDifficultyText = (TextView) getView().findViewById(R.id.app_007_difficultyTextView);
    mScoreText = (TextView) getView().findViewById(R.id.app_007_scoreTextView);
    mScoreDeltaText = (TextView) getView().findViewById(R.id.app_007_scoreDeltaView);
    showScore();

  }

  public void resetScore() {
    mScore = 0;
  }

  public void addPoint() {
    mScore++;

    mScoreDeltaText.setText("+1");
    mScoreDeltaText.setTextColor(Color.GREEN);
    AnimatorSet set = new AnimatorSet();
    Animator scoreDelta = ObjectAnimator.ofFloat(mScoreDeltaText, View.TRANSLATION_Y, -mAnimationDistance);
    set.play(scoreDelta);
    set.setDuration(mAnimationLengthMedium);
    set.addListener(new AnimatorListener() {

      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mScoreDeltaText.setText("");
        mScoreDeltaText.setTranslationY(0.0f);
        showScore();
      }

      @Override
      public void onAnimationCancel(Animator animation) {
      }
    });
    set.start();
  }

  public void penaltyPoints(int points) {
    // only do the animation and update if there are actual pennalty points
    if (points > 0) {
      mScore = ConstraintEnforcer.lowerBound(0, mScore - points);
      mScoreDeltaText.setText("-" + points);
      mScoreDeltaText.setTextColor(Color.RED);
      AnimatorSet set = new AnimatorSet();
      Animator scoreDelta = ObjectAnimator.ofFloat(mScoreDeltaText, View.TRANSLATION_Y, +mAnimationDistance);
      set.play(scoreDelta);
      set.setDuration(mAnimationLengthMedium);
      set.addListener(new AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mScoreDeltaText.setText("");
          mScoreDeltaText.setTranslationY(0.0f);
          showScore();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
      });
      set.start();
    }

  }

  private void showScore() {
    mScoreText.setText(String.valueOf(mScore));
  }

  public void setTimer(int seconds) {

  }

  private boolean mRunning = false;

  public boolean isRunning() {
    return mRunning;
  }

  public void startTimer() {

    final Activity activity = getActivity();
    mRunning = true;

    mTimerThread = new Thread(new Runnable() {

      @Override
      public void run() {
        int i = 0;

        while (true) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            Log.d(TAG, "timer interrupted");
            break;
          }
          if (mFinish) {
            break;
          }
          if (!mPaused) {
            if (i++ % 10 == 0) {
              mTime--;
              activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                  mTimerText.setText(String.valueOf(mTime));
                }
              });

              if (mTime <= 0) {
                activity.runOnUiThread(new Runnable() {

                  @Override
                  public void run() {
                    mTimeOutListener.onTimeOut(mScore);
                  }
                });

                mFinish = true;
              }
            }
          }
        }
      }
    });
    mTimerThread.start();
  }

  public void pauseTimer() {
    mPaused = true;
  }

  public void resumeTimer() {
    mPaused = false;
  }

  public void stopTimer() {
    mFinish = true;
    mPaused = false;
  }
}
