package com.hulzenga.ioi.android;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.hulzenga.ioi.android.app_008.OverheidActivity;
import com.hulzenga.ioi.android.app_009.ThumbsUpActivity;
import com.hulzenga.ioi.android.app_010.CompassActivity;

public enum DemoApp {

  APP_001(com.hulzenga.ioi.android.app_001.HelloWorldActivity.class, com.hulzenga.ioi.android.R.string.app_001_title,
      com.hulzenga.ioi.android.R.drawable.app_001_icon, com.hulzenga.ioi.android.R.string.app_001_shortDescription, 0),
  APP_002(com.hulzenga.ioi.android.app_002.BouncyBallsActivity.class, com.hulzenga.ioi.android.R.string.app_002_title,
      com.hulzenga.ioi.android.R.drawable.app_002_icon, com.hulzenga.ioi.android.R.string.app_002_shortDescription, 0),
  APP_003(com.hulzenga.ioi.android.app_003.MonsterDatabaseActivity.class, com.hulzenga.ioi.android.R.string.app_003_title,
      com.hulzenga.ioi.android.R.drawable.app_003_icon, com.hulzenga.ioi.android.R.string.app_003_shortDescription, 0),
  APP_004(com.hulzenga.ioi.android.app_004.BouncyBall3dActivity.class, com.hulzenga.ioi.android.R.string.app_004_title,
      com.hulzenga.ioi.android.R.drawable.app_004_icon, com.hulzenga.ioi.android.R.string.app_004_shortDescription, 0),
  APP_005(com.hulzenga.ioi.android.app_005.ElementActivity.class, com.hulzenga.ioi.android.R.string.app_005_title,
      com.hulzenga.ioi.android.R.drawable.app_005_icon, com.hulzenga.ioi.android.R.string.app_005_shortDescription, 0),
  APP_006(com.hulzenga.ioi.android.app_006.SimpleCameraActivity.class, com.hulzenga.ioi.android.R.string.app_006_title,
      com.hulzenga.ioi.android.R.drawable.app_006_icon, com.hulzenga.ioi.android.R.string.app_006_shortDescription, 0),
  APP_007(com.hulzenga.ioi.android.app_007.Menu.class, com.hulzenga.ioi.android.R.string.app_007_title,
      com.hulzenga.ioi.android.R.drawable.app_007_icon, com.hulzenga.ioi.android.R.string.app_007_shortDescription, 0),
  APP_008(OverheidActivity.class, com.hulzenga.ioi.android.R.string.app_008_title,
      com.hulzenga.ioi.android.R.drawable.app_008_icon, com.hulzenga.ioi.android.R.string.app_008_shortDescription, 0),
  APP_009(ThumbsUpActivity.class, com.hulzenga.ioi.android.R.string.app_009_title,
      com.hulzenga.ioi.android.R.drawable.app_009_icon, com.hulzenga.ioi.android.R.string.app_009_shortDescription, 0),
  APP_010(CompassActivity.class, com.hulzenga.ioi.android.R.string.app_010_title,
      com.hulzenga.ioi.android.R.drawable.app_010_icon, com.hulzenga.ioi.android.R.string.app_010_shortDescription, 0);

  private int   title;
  private Class activity;
  private int   icon;
  private int   shortDescription;
  private int   longDescription;

  private DemoApp(Class activity, int title, int icon, int shortDescription, int longDescription) {
    this.title = title;
    this.activity = activity;
    this.icon = icon;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
  }

  public int getTitle() {
    return title;
  }

  public String getTitleString(Resources res) {
    return res.getString(title);
  }

  public Class getActivity() {
    return activity;
  }

  public int getShortDescription() {
    return shortDescription;
  }

  public String getShortDescriptionString(Resources res) {
    return res.getString(shortDescription);
  }

  public int getIcon() {
    return icon;
  }

  public Drawable getIconDrawable(Resources res) {
    return res.getDrawable(icon);
  }
}