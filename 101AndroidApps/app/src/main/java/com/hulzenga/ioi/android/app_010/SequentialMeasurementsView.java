package com.hulzenga.ioi.android.app_010;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jouke on 1-5-14.
 */
public class SequentialMeasurementsView extends View {

  private static final float EPS           = Float.MIN_VALUE;
  private static final int   BUFFER_LENGTH = 101;
  public static final String MEASUREMNTS_KEY = "MEASUREMENTS";
  private float[] mMeasurements   = new float[BUFFER_LENGTH];
  private float   mMinMeasurement = 0.0f;
  private float   mMaxMeasurement = EPS;

  private Paint mLinePaint;
  private int   mHeight;
  private int   mWidth;
  private int   mHalfHeight;
  private float   mStepWidth;
  private Paint mAxisPaint;

  public SequentialMeasurementsView(Context context) {
    this(context, null);
  }

  public SequentialMeasurementsView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SequentialMeasurementsView(Context context, AttributeSet attrs, int style) {
    super(context, attrs, style);

    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setColor(Color.RED);
    mLinePaint.setStrokeWidth(4.0f);

    mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mAxisPaint.setColor(Color.WHITE);
    mAxisPaint.setStrokeWidth(2.0f);
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

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    mWidth = w;
    mStepWidth = mWidth / (float)(BUFFER_LENGTH-1);
    mHeight = h;
    mHalfHeight = mHeight / 2;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;
      float[] savedMeasurements = bundle.getFloatArray(MEASUREMNTS_KEY);
      if (savedMeasurements != null) {
        mMeasurements = savedMeasurements;
      }
    }
    super.onRestoreInstanceState(state);
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    super.onSaveInstanceState();
    Bundle bundle = new Bundle();
    bundle.putFloatArray(MEASUREMNTS_KEY, mMeasurements);
    return bundle;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    //draw axis
    canvas.drawLine(0.0f, 0.0f, 0.0f, mHeight, mAxisPaint);

    float axisHeight;
    if (mMaxMeasurement < 0.0f) {
      axisHeight = 0.0f;
    } else if (mMinMeasurement < 0.0f) {
      axisHeight = (mMaxMeasurement/(mMaxMeasurement-mMinMeasurement))*mHeight;
    } else {
      axisHeight = mHeight;
    }
    canvas.drawLine(0.0f, axisHeight, mWidth, axisHeight, mAxisPaint);

    float yScale = mHeight/(mMaxMeasurement-mMinMeasurement+EPS);
    for (int i = 0; i < BUFFER_LENGTH - 1; i++) {
      canvas.drawLine(
          i * mStepWidth, mHeight - yScale*(mMeasurements[i]-mMinMeasurement),
          (i + 1) * mStepWidth, mHeight - yScale*(mMeasurements[i+1]-mMinMeasurement),
          mLinePaint);
    }
  }

  private static class SavedState extends BaseSavedState {


    public SavedState(Parcel source) {
      super(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
    }
  }
}
