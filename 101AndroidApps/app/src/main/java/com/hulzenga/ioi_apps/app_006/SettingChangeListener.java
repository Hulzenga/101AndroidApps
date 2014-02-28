package com.hulzenga.ioi_apps.app_006;


public interface SettingChangeListener {

  public enum ChangeType {
    CAMERA, FLASH, COLOR_EFFECT, EXPOSURE, IMAGE_SIZE, FOCUS, SCENE_MODE, ISO, WHITE_BALANCE, TIMER
  }

  public void changeSetting(ChangeType type, Object newSetting);

  public void open2ndLevelSetting(ChangeType type);

}
