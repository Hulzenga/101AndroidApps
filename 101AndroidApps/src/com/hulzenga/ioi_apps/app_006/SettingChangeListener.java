package com.hulzenga.ioi_apps.app_006;

public interface SettingChangeListener {
    
    public enum SettingType {
        COLOR_EFFECT
    }
    
    public void selectCamera(int camera);
    public void changeSetting(SettingType type, String newSetting);
}
