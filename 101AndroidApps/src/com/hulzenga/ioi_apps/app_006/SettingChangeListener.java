package com.hulzenga.ioi_apps.app_006;

import android.hardware.Camera;

public interface SettingChangeListener {
    
    public static final int CAMERA = 0;
    public static final int FLASH = 1;
    public static final int COLOR_EFFECT = 2;
    public static final int EXPOSURE = 3;
        
        
    public void changeSetting(int type, Object newSetting);
}
