package com.hulzenga.ioi.android.app_010;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by jouke on 1-5-14.
 */
class CompassDetailsView extends View {

  private static final float   EPS             = Float.MIN_VALUE;
  private static final int     BUFFER_LENGTH   = 101;
  private static final  String  MEASUREMNTS_KEY = "MEASUREMENTS";
  private static final  String  STATE_KEY       = "STATE";
  private              float[] mMeasurements   = new float[BUFFER_LENGTH];
  private              float   mMinMeasurement = 0.0f;
  private              float   mMaxMeasurement = EPS;

  private Paint mLinePaint;
  private int   mHeight;
  private int   mWidth;
  private int   mHalfHeight;
  private float mStepWidth;
  private Paint mAxisPaint;
  private float mEstimatedFieldStrength;
  private Paint mTextPaint;
  private float mHeading = 0.0f;
  private float mTilt    = 0.0f;
  private int mDisplayRotation;

  public CompassDetailsView(Context context) {
    this(context, null);
  }

  public CompassDetailsView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CompassDetailsView(Context context, AttributeSet attrs, int style) {
    super(context, attrs, style);

    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setColor(Color.RED);
    mLinePaint.setStrokeWidth(4.0f);

    mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mAxisPaint.setColor(Color.WHITE);
    mAxisPaint.setStrokeWidth(2.0f);

    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setTextAlign(Paint.Align.LEFT);
    mTextPaint.setTextSize(20.0f);
    mTextPaint.setColor(Color.WHITE);
  }

  public void addMeasurement(float measurement) {

    float min = mMeasurements[1], max = mMeasurements[1];

    for (int i = 0; i < BUFFER_LENGTH - 1; i++) {
      mMeasurements[i] = mMeasurements[i + 1];

      if (mMeasurements[i] < min) {
        min = mMeasurements[i];
      } else if (mMeasurements[i] > max) {
        max = mMeasurements[i];
      }
    }

    mMeasurements[BUFFER_LENGTH - 1] = measurement;
    mMinMeasurement = min;
    mMaxMeasurement = max;

    invalidate();
  }


  public void updateOrientation(float[] eulerAngles) {

    if (Math.abs(eulerAngles[1]) > Math.abs(eulerAngles[2])) {
      mTilt = Math.abs(180.0f * (eulerAngles[1] / (float) Math.PI));
    } else {
      mTilt = Math.abs(180.0f * (eulerAngles[2] / (float) Math.PI));
    }

    switch (mDisplayRotation) {
      case Surface.ROTATION_0:
        mHeading = 180.0f * (eulerAngles[0] / (float) Math.PI);
        break;
      case Surface.ROTATION_90:
        mHeading = 180.0f * ((eulerAngles[0] + (float) Math.PI * 0.5f) / (float) Math.PI);
        break;
      case Surface.ROTATION_180:
        mHeading = 180.0f * ((eulerAngles[0] + (float) Math.PI) / (float) Math.PI);
        break;
      case Surface.ROTATION_270:
        mHeading = 180.0f * ((eulerAngles[0] + (float) Math.PI * 1.5f) / (float) Math.PI);
        break;
    }

    invalidate();
  }

  public void setEstimatedFieldStrength(float estimatedFieldStrength) {
    mEstimatedFieldStrength = estimatedFieldStrength;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    mWidth = w - 2 * (int) MARGIN;
    mStepWidth = mWidth / (float) (BUFFER_LENGTH - 1);
    mHeight = h - 2 * (int) MARGIN;
    mHalfHeight = mHeight / 2;

    WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    mDisplayRotation = windowManager.getDefaultDisplay().getRotation();
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;
      mMeasurements = bundle.getFloatArray(MEASUREMNTS_KEY);
      state = bundle.getParcelable(STATE_KEY);
    }
    super.onRestoreInstanceState(state);
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();
    bundle.putFloatArray(MEASUREMNTS_KEY, mMeasurements);
    bundle.putParcelable(STATE_KEY, super.onSaveInstanceState());
    return bundle;
  }

  private static final float MARGIN = 3f;

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.translate(MARGIN, MARGIN);


//    canvas.scale(1.0f, -1.0f);
//    canvas.translate(MARGIN, -mHeight-MARGIN);

    canvas.drawLine(0.0f, mHeight, mWidth, mHeight, mAxisPaint);
    canvas.drawLine(0.0f, mHeight, 0.0f, mHalfHeight, mAxisPaint);

    float yScale = mHalfHeight / 100.0f;
//    //draw axis
//    canvas.drawLine(MARGIN, MARGIN, MARGIN, mHeight-MARGIN, mAxisPaint);
//    canvas.drawLine(MARGIN, )
//    float axisHeight;
//    if (mMaxMeasurement < 0.0f) {
//      axisHeight = 0.0f;
//    } else if (mMinMeasurement < 0.0f) {
//      axisHeight = (mMaxMeasurement / (mMaxMeasurement - mMinMeasurement)) * mHeight;
//    } else {
//      axisHeight = mHeight;
//    }
//    canvas.drawLine(0.0f, axisHeight, mWidth, axisHeight, mAxisPaint);
//
//    float yScale = mHeight / (mMaxMeasurement - mMinMeasurement - EPS);
    for (int i = 0; i < BUFFER_LENGTH - 1; i++) {
      canvas.drawLine(
          i * mStepWidth, mHeight - yScale * mMeasurements[i], (i + 1) * mStepWidth, mHeight - yScale * (mMeasurements[i + 1]),
          mLinePaint);
    }
    if (mEstimatedFieldStrength != 0.0f) {
      canvas.drawLine(0.0f, mHeight - yScale * mEstimatedFieldStrength, mWidth, mHeight - yScale * mEstimatedFieldStrength, mLinePaint);
    }

    canvas.drawText(String.format("Heading: %3.0f Tilt: %3.0f", mHeading, mTilt), 0.0f, mHalfHeight, mTextPaint);
//
//    if (mEstimatedFieldStrength != 0.0f) {
//      canvas.drawLine(0.0f, yScale * mEstimatedFieldStrength, 0.0f, yScale * mEstimatedFieldStrength, mLinePaint);
//    }
  }
}
