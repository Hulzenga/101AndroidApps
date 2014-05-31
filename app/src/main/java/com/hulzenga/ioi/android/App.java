package com.hulzenga.ioi.android;

import android.app.Activity;
import android.content.res.Resources;

import com.hulzenga.ioi.android.app_001.HelloWorldActivity;
import com.hulzenga.ioi.android.app_002.BouncyBallsActivity;
import com.hulzenga.ioi.android.app_003.MonsterDatabaseActivity;
import com.hulzenga.ioi.android.app_004.BouncyBall3dActivity;
import com.hulzenga.ioi.android.app_005.ElementActivity;
import com.hulzenga.ioi.android.app_006.SimpleCameraActivity;
import com.hulzenga.ioi.android.app_007.HighScores;
import com.hulzenga.ioi.android.app_007.Menu;
import com.hulzenga.ioi.android.app_007.Review;
import com.hulzenga.ioi.android.app_007.WikiGameActivity;
import com.hulzenga.ioi.android.app_008.OverheidActivity;
import com.hulzenga.ioi.android.app_009.ThumbsUpActivity;
import com.hulzenga.ioi.android.app_010.CompassActivity;

import java.util.HashMap;
import java.util.Map;

public enum App{

  APP_000(
      R.string.app_000_title,
      R.drawable.app_icon,
      R.string.app_000_shortDescription,
      R.string.app_000_longDescription,
      TableOfContentsActivity.class
  ),
  APP_001(
      R.string.app_001_title,
      R.drawable.app_001_icon,
      R.string.app_001_shortDescription,
      R.string.app_001_longDescription,
      HelloWorldActivity.class
  ),
  APP_002(
      R.string.app_002_title,
      R.drawable.app_002_icon,
      R.string.app_002_shortDescription,
      R.string.app_002_longDescription,
      BouncyBallsActivity.class
   ),
  APP_003(
      R.string.app_003_title,
      R.drawable.app_003_icon,
      R.string.app_003_shortDescription,
      R.string.app_003_longDescription,
      MonsterDatabaseActivity.class
  ),
  APP_004(
      R.string.app_004_title,
      R.drawable.app_004_icon,
      R.string.app_004_shortDescription,
      R.string.app_004_longDescription,
      BouncyBall3dActivity.class
  ),
  APP_005(
      R.string.app_005_title,
      R.drawable.app_005_icon,
      R.string.app_005_shortDescription,
      R.string.app_005_longDescription,
      ElementActivity.class
  ),
  APP_006(
      R.string.app_006_title,
      R.drawable.app_006_icon,
      R.string.app_006_shortDescription,
      R.string.app_006_longDescription,
      SimpleCameraActivity.class
  ),
  APP_007(
      R.string.app_007_title,
      R.drawable.app_007_icon,
      R.string.app_007_shortDescription,
      R.string.app_007_longDescription,
      Menu.class,
      HighScores.class,
      Review.class,
      WikiGameActivity.class
  ),
  APP_008(
      R.string.app_008_title,
      R.drawable.app_008_icon,
      R.string.app_008_shortDescription,
      R.string.app_008_longDescription,
      OverheidActivity.class
  ),
  APP_009(
      R.string.app_009_title,
      R.drawable.app_009_icon,
      R.string.app_009_shortDescription,
      R.string.app_009_longDescription,
      ThumbsUpActivity.class
  ),
  APP_010(
      R.string.app_010_title,
      R.drawable.app_009_icon,
      R.string.app_010_shortDescription,
      R.string.app_010_longDescription,
      CompassActivity.class);

  private static Map<Class, App> mActivityAppMap = new HashMap<>();
  static {
    for (App app: App.values()) {
      for (Class activity: app.mActivities) {
        mActivityAppMap.put(activity, app);
      }
    }
  }

  private Class mStartActivity;
  private Class[] mActivities;
  private int mTitle;
  private int mIcon;
  private int mShortDescription;
  private int mLongDescription;

  private App(int title, int icon, int shortDescription, int longDescription, Class... activities) {
    this.mTitle = title;
    this.mStartActivity = activities[0];
    this.mActivities = activities;
    this.mIcon = icon;
    this.mShortDescription = shortDescription;
    this.mLongDescription = longDescription;
  }

  public int getTitle() {
    return mTitle;
  }

  public String getTitleString(Resources res) {
    return res.getString(mTitle);
  }

  public Class getStartActivity() {
    return mStartActivity;
  }

  public int getShortDescription() {
    return mShortDescription;
  }

  public String getShortDescriptionString(Resources res) {
    return res.getString(mShortDescription);
  }

  public int getLongDescription() {
    return mLongDescription;
  }

  public int getIcon() {
    return mIcon;
  }

  public static App getAppFromActivity(Activity activity) {
    return mActivityAppMap.get(activity.getClass());
  }
}
