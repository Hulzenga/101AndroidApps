package com.hulzenga.ioi.android.app_006;

import com.hulzenga.ioi.android.R;

import android.hardware.Camera.Parameters;

/**
 * Created by jouke on 6/3/14.
 */
public enum Setting {


  CAMERA_FACING_FRONT( "front", R.drawable.app_006_camera_facing_front),
  CAMERA_FACING_BACK( "back", R.drawable.app_006_camera_facing_back),

  FLASH_MODE_OFF(Parameters.FLASH_MODE_OFF, R.drawable.app_006_flash_no),
  FLASH_MODE_ON(Parameters.FLASH_MODE_ON, R.drawable.app_006_flash),
  FLASH_MODE_AUTO(Parameters.FLASH_MODE_AUTO, R.drawable.app_006_flash_automatic),
  FLASH_MODE_RED_EYE(Parameters.FLASH_MODE_RED_EYE, R.drawable.app_006_flash_red_eye),
  FLASH_MODE_TORCH(Parameters.FLASH_MODE_TORCH, R.drawable.app_006_flash_torch),

  EFFECT_NONE(Parameters.EFFECT_NONE, R.drawable.app_006_color_effect_none),
  EFFECT_MONO(Parameters.EFFECT_MONO, R.drawable.app_006_color_effect_mono),
  EFFECT_NEGATIVE(Parameters.EFFECT_NEGATIVE, R.drawable.app_006_color_effect_negative),
  EFFECT_SEPIA(Parameters.EFFECT_SEPIA, R.drawable.app_006_color_effect_sepia),
  EFFECT_SOLARIZE(Parameters.EFFECT_SOLARIZE, R.drawable.app_006_color_effect_solarize),
  EFFECT_POSTERIZE(Parameters.EFFECT_POSTERIZE, R.drawable.app_006_color_effect_posterize),
  EFFECT_AQUA(Parameters.EFFECT_AQUA, R.drawable.app_006_color_effect_aqua),
  EFFECT_BLACKBOARD(Parameters.EFFECT_BLACKBOARD, R.drawable.app_006_color_effect_blackboard),
  EFFECT_WHITEBOARD(Parameters.EFFECT_WHITEBOARD, R.drawable.app_006_color_effect_whiteboard),


  EXPOSURE,
  IMAGE_SIZE,
  FOCUS,
  SCENE_MODE,
  ISO,
  WHITE_BALANCE,
  TIMER;


  public enum IconGroup {
    CAMERA(
        CAMERA_FACING_FRONT, CAMERA_FACING_BACK
    ),
    FLASH(
        FLASH_MODE_OFF, FLASH_MODE_ON, FLASH_MODE_AUTO, FLASH_MODE_RED_EYE, FLASH_MODE_TORCH
    ),
    COLOR_EFFECT(
        EFFECT_NONE,EFFECT_MONO,EFFECT_NEGATIVE,EFFECT_SEPIA,EFFECT_SOLARIZE,
        EFFECT_POSTERIZE,EFFECT_AQUA,EFFECT_BLACKBOARD,EFFECT_WHITEBOARD
    );

    private Setting[] mSettings;

    private IconGroup(Setting... subSettings) {
      mSettings = subSettings;
    }

    public Setting[] getSettings() {
      return mSettings;
    }
  }

  public enum WordGroup {

    OTHER_SETTINGS(
        IMAGE_SIZE, FOCUS, SCENE_MODE, ISO, WHITE_BALANCE, TIMER
    );

    private Setting[] mSettings;

    private WordGroup(Setting... subSettings) {
      mSettings = subSettings;
    }

    public Setting[] getSettings() {
      return mSettings;
    }
  }

  private String mParam;
  private int mIcon;
  private boolean mAvailable = true;

  private Setting(){};
  private Setting(String param, int icon) {
    mParam = param;
    mIcon = icon;
  }

  public String getParam() {
    return mParam;
  }

  public int getIcon() {
    return mIcon;
  }
}
