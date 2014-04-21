package com.hulzenga.ioi_apps;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.hulzenga.ioi_apps.app_008.OverheidActivity;
import com.hulzenga.ioi_apps.app_009.ThumbsUpActivity;

public enum DemoApp {

  APP_001(com.hulzenga.ioi_apps.app_001.HelloWorldActivity.class, R.string.app_001_title,
      R.drawable.app_001_icon, R.string.app_001_shortDescription, 0),
  APP_002(com.hulzenga.ioi_apps.app_002.BouncyBallsActivity.class, R.string.app_002_title,
      R.drawable.app_002_icon, R.string.app_002_shortDescription, 0),
  APP_003(com.hulzenga.ioi_apps.app_003.MonsterDatabaseActivity.class, R.string.app_003_title,
      R.drawable.ic_launcher, R.string.app_003_shortDescription, 0),
  APP_004(com.hulzenga.ioi_apps.app_004.BouncyBall3dActivity.class, R.string.app_004_title,
      R.drawable.app_004_icon, R.string.app_004_shortDescription, 0),
  APP_005(com.hulzenga.ioi_apps.app_005.ElementActivity.class, R.string.app_005_title,
      R.drawable.app_005_icon, R.string.app_005_shortDescription, 0),
  APP_006(com.hulzenga.ioi_apps.app_006.SimpleCameraActivity.class, R.string.app_006_title,
      R.drawable.app_006_icon, R.string.app_006_shortDescription, 0),
  APP_007(com.hulzenga.ioi_apps.app_007.Menu.class, R.string.app_007_title,
      R.drawable.app_007_icon, R.string.app_007_shortDescription, 0),
  APP_008(OverheidActivity.class, R.string.app_008_title,
      R.drawable.ic_launcher, R.string.app_008_shortDescription, 0),
  APP_009(ThumbsUpActivity.class, R.string.app_009_title,
      R.drawable.app_009_icon, R.string.app_009_shortDescription, 0);

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
