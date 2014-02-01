package com.hulzenga.ioi_apps.app_007;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hulzenga.ioi_apps.R;

public class StatusFragment extends Fragment {

    private static final String TAG    = "STATUS FRAGMENT";

    private TextView            mTimerText;
    private TextView            mDifficultyText;
    private TextView            mScoreText;

    private int                 mScore = 0;
    private int                 mTime  = 10;
    private Thread              mTimerThread;

    private TimeOutListener     mTimeOutListener;

    public interface TimeOutListener {
        public void onTimeOut(int score);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View statusView = inflater.inflate(R.layout.app_007_fragment_status, container, false);

        mTimerText = (TextView) statusView.findViewById(R.id.app_007_timerTextView);
        mDifficultyText = (TextView) statusView.findViewById(R.id.app_007_difficultyTextView);
        mScoreText = (TextView) statusView.findViewById(R.id.app_007_scoreTextView);
        showScore();

        return statusView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mTimeOutListener = (TimeOutListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, activity.toString() + " must implement StatusFragment.TimeOutListener");
        }

    }

    public void resetScore() {
        mScore = 0;
    }

    public void addPoint() {
        mScore++;
        showScore();
    }

    public void penaltyPoints(int points) {
        mScore -= points;
        showScore();
    }

    private void showScore() {
        mScoreText.setText(String.valueOf(mScore));
    }

    public void setTimer(int seoncds) {

    }

    private volatile boolean mPaused = false;
    private volatile boolean mFinish = false;

    public void startTimer() {

        final Activity activity = getActivity();

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
