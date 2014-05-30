package com.hulzenga.ioi.android.app_005;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hulzenga.ioi.android.R;

import java.util.Random;

public enum Element {

  EARTH(R.drawable.app_005_earth_element, R.drawable.app_005_earth),
  AIR(R.drawable.app_005_air_element, R.drawable.app_005_air),
  FIRE(R.drawable.app_005_fire_element, R.drawable.app_005_fire),
  WATER(R.drawable.app_005_water_element, R.drawable.app_005_water);

  private static boolean mBitmapsLoaded = false;
  private static Random mRandom = new Random();
  private static Bitmap mEmptyIcon;

  private int mIconId;
  private int mShadowId;
  private Bitmap mIcon;
  private Bitmap mShadow;

  private Element(int iconId, int shadowId) {
    mIconId = iconId;
    mShadowId = shadowId;
  }

  public static Element getRandomElement() {
    switch (mRandom.nextInt(4)) {
      case 0:
        return Element.EARTH;
      case 1:
        return Element.AIR;
      case 2:
        return Element.FIRE;
      case 3:
        return Element.WATER;
      default:
        throw new AssertionError("mRandom.nextInt(4) returned something other than 0,1,2 or 3. " +
            "Check device for radiation damage");
    }
  }

  public static void loadBitmaps(Context context) {
    for (Element element: Element.values()) {
      element.mIcon = BitmapFactory.decodeResource(context.getResources(), element.mIconId);
      element.mShadow = BitmapFactory.decodeResource(context.getResources(), element.mShadowId);
    }

    mEmptyIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_005_empty_element);
  }

  public static Bitmap getmEmptyIcon() {
    return mEmptyIcon;
  }

  public Bitmap getIcon() {
    if (mIcon != null) {
      return mIcon;
    } else {
      throw new IllegalStateException("Element.loadBitmaps(context) needs to be called before getIcon()!");
    }
  }

  public Bitmap getShadow() {
    if (mShadow != null) {
      return mShadow;
    } else {
      throw new IllegalStateException("Element.loadBitmaps(context) needs to be called before getIcon()!");
    }


  }
}
