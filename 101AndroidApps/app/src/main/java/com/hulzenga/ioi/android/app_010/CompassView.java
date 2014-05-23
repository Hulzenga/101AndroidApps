package com.hulzenga.ioi.android.app_010;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.util.Arrays;

/**
 * Created by jouke on 24-4-14.
 */
class CompassView extends View {


  private static final float COMPASS_SCREEN_FRACTION = 0.9f;
  private static final float TILT_SCALE              = 0.8f;
  private static final float MAX_SCALE               = 100.0f; //
  private static final float NEEDLE_SCALE_WIDTH      = 16.0f;
  private static final float NEEDLE_SCALE_LENGTH     = 180.0f;
  private static final float PIN_SCALE               = 8.0f;
  private static final float PIN_HIGLIGHT_SCALE      = 10.0f;
  private static final float THICK_LINE              = 3.0f;
  private static final float MEDIUM_LINE             = 2.0f;
  private static final float THIN_LINE               = 1.0f;
  private static final float FINE_LINE               = 1.0f;
  private static final  float TILT_CUTOFF             = 45.0f;

  private final Paint mPinHighlightPaint;
  private final Paint mTextPaint;
  private final Paint mLinePaint;
  private final Paint mNorthPaint;
  private final Paint mSouthPaint;
  private final Paint mPinPaint;
  private       Path  mHalfNeedlePath;
  private       float mRadius;
  private       int   mWidth;
  private       int   mHeight;

  private Camera mCamera;
  private float[] mEulerAngles = {0.0f, 0.0f, 1.0f};
  private Matrix  mMatrix      = new Matrix();
  private float   mScale       = 1.0f;
  private int mDisplayRotation;

  public CompassView(Context context) {
    this(context, null);
  }

  public CompassView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CompassView(Context context, AttributeSet attrs, int style) {
    super(context, attrs, style);

    mCamera = new Camera();

    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setColor(Color.WHITE);
    mLinePaint.setStrokeWidth(4.0f);
    mLinePaint.setStyle(Paint.Style.STROKE);

    mNorthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mNorthPaint.setColor(Color.RED);

    mSouthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mSouthPaint.setColor(Color.WHITE);

    mPinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPinPaint.setColor(Color.GRAY);

    mPinHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPinHighlightPaint.setColor(Color.LTGRAY);

    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
    mTextPaint.setColor(Color.WHITE);
    mTextPaint.setTextAlign(Paint.Align.CENTER);

  }



  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);

    if (width > height) {
      setMeasuredDimension(height, height);
    } else {
      setMeasuredDimension(width, width);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
    mHeight = h;

    mRadius = ((w > h) ? h : w) * COMPASS_SCREEN_FRACTION * 0.5f;
    mScale = mRadius / MAX_SCALE;

    mHalfNeedlePath = new Path();
    mHalfNeedlePath.moveTo(0.0f, 0.0f);
    mHalfNeedlePath.lineTo(0.5f * NEEDLE_SCALE_WIDTH * mScale, 0.0f);
    mHalfNeedlePath.lineTo(0.0f, -0.5f * NEEDLE_SCALE_LENGTH * mScale);
    mHalfNeedlePath.lineTo(-0.5f * NEEDLE_SCALE_WIDTH * mScale, 0.0f);
    mHalfNeedlePath.setFillType(Path.FillType.WINDING);

    WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    mDisplayRotation = windowManager.getDefaultDisplay().getRotation();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.translate(mWidth / 2.0f, mHeight / 2.0f);

    float xRotation = 0.0f, yRotation = 0.0f, zRotation = 0.0f;
    switch (mDisplayRotation) {
      case Surface.ROTATION_0:
        xRotation = 180.0f*(mEulerAngles[1] / (float) Math.PI);
        yRotation = 180.0f*(mEulerAngles[2] / (float) Math.PI);
        zRotation = 180.0f*(mEulerAngles[0] / (float) Math.PI);
        break;
      case Surface.ROTATION_90:
        xRotation = 180.0f*(mEulerAngles[2] / (float) Math.PI);
        yRotation = -180.0f*(mEulerAngles[1] / (float) Math.PI);
        zRotation = 180.0f*((mEulerAngles[0] + (float)Math.PI*0.5f) / (float) Math.PI);
        break;
      case Surface.ROTATION_180:
        xRotation = -180.0f*(mEulerAngles[1] / (float) Math.PI);
        yRotation = -180.0f*(mEulerAngles[2] / (float) Math.PI);
        zRotation = 180.0f*((mEulerAngles[0] + (float)Math.PI) / (float) Math.PI);
        break;
      case Surface.ROTATION_270:
        xRotation = -180.0f*(mEulerAngles[2] / (float) Math.PI);
        yRotation = +180.0f*(mEulerAngles[1] / (float) Math.PI);
        zRotation = 180.0f*((mEulerAngles[0] + (float)Math.PI*1.5f) / (float) Math.PI);
        break;
    }
    final boolean tilted = Math.abs(xRotation) > TILT_CUTOFF || Math.abs(yRotation) > TILT_CUTOFF;

    if (tilted) {
      canvas.drawText("Heading: " + String.format("%.0f",zRotation) + " deg", 0.0f, -70.0f * mScale, mTextPaint);
    }

    mCamera.save();
    mCamera.setLocation(0.0f, 0.0f, 40.0f);
    mCamera.rotate(
        0.0f,
        +TILT_SCALE * yRotation,
        0.0f);
    mCamera.rotate(
        +TILT_SCALE * xRotation,
        0.0f,
        0.0f
    );
    mCamera.getMatrix(mMatrix);
    mCamera.restore();
    canvas.concat(mMatrix);

    mLinePaint.setStrokeWidth(MEDIUM_LINE * mScale);
    canvas.drawCircle(0.0f, 0.0f, mRadius, mLinePaint);

    for (int i = 0; i < 360; i += 5) {
      float x = (float) Math.sin(Math.PI / 180.0 * (double) i);
      float y = (float) -Math.cos(Math.PI / 180.0 * (double) i);

      float frac = 1.0f;

      if (i % 90 == 0) {
        mLinePaint.setStrokeWidth(THICK_LINE * mScale);
        frac = 0.88f;
      } else if (i % 30 == 0) {
        mLinePaint.setStrokeWidth(MEDIUM_LINE * mScale);
        frac = 0.90f;
      } else if (i % 10 == 0) {
        mLinePaint.setStrokeWidth(THIN_LINE * mScale);
        frac = 0.92f;
      } else {
        mLinePaint.setStrokeWidth(FINE_LINE * mScale);
        frac = 0.94f;
      }
      canvas.drawLine(+x * mRadius, -y * mRadius, +x * mRadius * frac, -y * mRadius * frac, mLinePaint);
    }


    canvas.save();
    canvas.rotate(-zRotation);
    canvas.drawPath(mHalfNeedlePath, mNorthPaint);
    canvas.rotate(+180.0f);
    canvas.drawPath(mHalfNeedlePath, mSouthPaint);
    canvas.restore();

    canvas.drawCircle(0.0f, 0.0f, 0.5f * PIN_HIGLIGHT_SCALE * mScale, mPinPaint);
    canvas.drawCircle(0.0f, 0.0f, 0.5f * PIN_SCALE * mScale, mPinHighlightPaint);
    mTextPaint.setTextSize(15.0f * mScale);


//    if (mHeadingTextView != null) {
//      mHeadingTextView.setText("Heading: " + String.format("%.0f",zRotation) + " deg");
//    }

  }

  public void updateOrientation(float[] eulerAngles) {
    mEulerAngles = Arrays.copyOf(eulerAngles, eulerAngles.length);

    postInvalidate();
  }
}
