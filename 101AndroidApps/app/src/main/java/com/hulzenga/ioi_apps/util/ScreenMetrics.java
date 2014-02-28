package com.hulzenga.ioi_apps.util;

import android.content.Context;

public class ScreenMetrics {

  public static float pixToDp(float pix, Context context) {
    float density = context.getResources().getDisplayMetrics().density;
    return pix / density;
  }

  public static float dpToPix(float dp, Context context) {
    float density = context.getResources().getDisplayMetrics().density;
    return density * dp;
  }
}
