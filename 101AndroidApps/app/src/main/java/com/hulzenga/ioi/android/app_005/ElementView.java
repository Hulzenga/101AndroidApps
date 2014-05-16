package com.hulzenga.ioi.android.app_005;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.hulzenga.ioi.android.R;

public class ElementView extends ImageView {

  private static Bitmap sEarth;
  private static Bitmap sAir;
  private static Bitmap sFire;
  private static Bitmap sWater;
  private        int    mPosition;

  public ElementView(Context context, int position) {
    super(context);

    mPosition = position;

    ensureBitmapsLoaded(context);
  }

  private static void ensureBitmapsLoaded(Context context) {
    if (sEarth == null) {
      sEarth = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_005_earth);
      sAir = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_005_air);
      sFire = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_005_fire);
      sWater = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_005_water);
    }
  }

  public int getPosition() {
    return mPosition;
  }

  public void setPosition(int position) {
    mPosition = position;
  }

  public static class DragShadowBuilder extends View.DragShadowBuilder {

    private BitmapDrawable shadow;

    public DragShadowBuilder(ElementView view, Element.ClassicalElement element) {


      switch (element) {
        case EARTH:
          shadow = new BitmapDrawable(view.getContext().getResources(), sEarth);
          break;
        case AIR:
          shadow = new BitmapDrawable(view.getContext().getResources(), sAir);
          break;
        case FIRE:
          shadow = new BitmapDrawable(view.getContext().getResources(), sFire);
          break;
        case WATER:
          shadow = new BitmapDrawable(view.getContext().getResources(), sWater);
          break;
      }

      shadow.setBounds(0, 0, shadow.getBitmap().getWidth(), shadow.getBitmap().getHeight());

    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
      shadowSize.set(shadow.getBounds().right, shadow.getBounds().bottom);
      shadowTouchPoint.set(shadow.getBounds().right, shadow.getBounds().bottom);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
      shadow.draw(canvas);
    }

  }

}
