package com.hulzenga.ioi_apps.app_010;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jouke on 24-4-14.
 */
public class CompassView extends View {


  private static final float COMPASS_SCREEN_FRACTION = 0.8f;
  private Paint mLinePaint;
  private Paint mNorthPaint;
  private float mRadius;
  private float[] mMagneticFieldStrength = {1.0f, 0.0f, 0.0f};
  private int mWidth, mHeight;
  private RectF   mCompassRect;
  private boolean mLandscape;

  public CompassView(Context context) {
    this(context, null);
  }

  public CompassView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CompassView(Context context, AttributeSet attrs, int style) {
    super(context, attrs, style);

    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setColor(Color.WHITE);
    mLinePaint.setStrokeWidth(4.0f);
    mLinePaint.setStyle(Paint.Style.STROKE);

    mNorthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mNorthPaint.setColor(Color.RED);

  }

  @Override
  protected void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    mLandscape = Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
    mHeight = h;

    mRadius = ((w > h) ? h : w) * COMPASS_SCREEN_FRACTION * 0.5f;
    mCompassRect = new RectF(w / 2 - mRadius, h / 2 - mRadius, w / 2 + mRadius, h / 2 + mRadius);

  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);



    float fieldStrength = (float) Math.sqrt(
        mMagneticFieldStrength[0] * mMagneticFieldStrength[0] +
            mMagneticFieldStrength[1] * mMagneticFieldStrength[1] +
            mMagneticFieldStrength[2] * mMagneticFieldStrength[2]
    );

    float inPlaneFieldStrength = (float) Math.sqrt(
        mMagneticFieldStrength[0] * mMagneticFieldStrength[0] +
            mMagneticFieldStrength[1] * mMagneticFieldStrength[1]
    );

    canvas.translate(mWidth / 2.0f, mHeight / 2.0f);
    canvas.scale(1.0f, 1.0f - (mMagneticFieldStrength[2]/fieldStrength));

    canvas.drawCircle(0.0f, 0.0f, mRadius, mLinePaint);

    for (int i = 0; i < 360; i += 10) {
      float x = (float) Math.cos(Math.PI / 180.0 * (double) i);
      float y = (float) Math.sin(Math.PI / 180.0 * (double) i);

      float frac = 0.9f;
      canvas.drawLine(+x * mRadius, -y * mRadius, +x * mRadius * frac, -y * mRadius * frac, mLinePaint);
    }




    canvas.drawCircle(
        mRadius * mMagneticFieldStrength[0] / inPlaneFieldStrength,
        mRadius * mMagneticFieldStrength[1] / inPlaneFieldStrength,
        10.0f, mNorthPaint);

  }

  public void updateOrientation(float[] magneticFieldStrength) {
    mMagneticFieldStrength = magneticFieldStrength;
    invalidate();
  }
}
