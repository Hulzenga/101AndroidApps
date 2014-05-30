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

class ElementView extends ImageView {


  private int mPosition;

  public ElementView(Context context) {
    this(context, 0);
  }

  public ElementView(Context context, int position) {
    super(context);

    mPosition = position;
  }

  public int getPosition() {
    return mPosition;
  }

  public void setPosition(int position) {
    mPosition = position;
  }

  public static class DragShadowBuilder extends View.DragShadowBuilder {

    private BitmapDrawable shadow;

    public DragShadowBuilder(ElementView view, Element element) {

      shadow = new BitmapDrawable(view.getContext().getResources(), element.getShadow());
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
