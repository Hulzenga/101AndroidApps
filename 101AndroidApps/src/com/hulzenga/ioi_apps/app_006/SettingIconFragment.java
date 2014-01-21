package com.hulzenga.ioi_apps.app_006;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.hulzenga.ioi_apps.R;

public class SettingIconFragment extends Fragment {

    private static final String               TAG             = "PARAMETER_ICON_FRAGMENT";

    private GridView                          mGridView;
    private ListAdapter                       mAdapter;
    private SettingChangeListener             mSettingChangeListener;

    public static final List<Object> CAMERA_ORDER = new ArrayList<Object>();
    {
       CAMERA_ORDER.add(String.valueOf(CameraInfo.CAMERA_FACING_FRONT));
       CAMERA_ORDER.add(String.valueOf(CameraInfo.CAMERA_FACING_BACK));
    }
    
    public static final Map<Object, Integer> CAMERA_ICON_MAP = new HashMap<Object, Integer>();
    {
        CAMERA_ICON_MAP.put(CameraInfo.CAMERA_FACING_FRONT, R.drawable.app_006_camera_facing_front);
        CAMERA_ICON_MAP.put(CameraInfo.CAMERA_FACING_BACK,  R.drawable.app_006_camera_facing_back);
    }
    
    public static final List<Object>          FLASH_ORDER     = new ArrayList<Object>();
    {
        FLASH_ORDER.add(Parameters.FLASH_MODE_OFF);
        FLASH_ORDER.add(Parameters.FLASH_MODE_ON);
        FLASH_ORDER.add(Parameters.FLASH_MODE_AUTO);
        FLASH_ORDER.add(Parameters.FLASH_MODE_RED_EYE);
        FLASH_ORDER.add(Parameters.FLASH_MODE_TORCH);
    }

    private static final Map<Object, Integer> FLASH_ICON_MAP  = new HashMap<Object, Integer>();
    {
        FLASH_ICON_MAP.put(Parameters.FLASH_MODE_OFF, R.drawable.app_005_water_element);
        FLASH_ICON_MAP.put(Parameters.FLASH_MODE_ON, R.drawable.app_005_fire_element);
        FLASH_ICON_MAP.put(Parameters.FLASH_MODE_AUTO, R.drawable.app_005_fire_element);
        FLASH_ICON_MAP.put(Parameters.FLASH_MODE_RED_EYE, R.drawable.app_005_fire_element);
        FLASH_ICON_MAP.put(Parameters.FLASH_MODE_TORCH, R.drawable.app_005_fire_element);
    }

    public static final List<Object>          EFFECT_ORDER    = new ArrayList<Object>();
    {
        EFFECT_ORDER.add(Parameters.EFFECT_NONE);
        EFFECT_ORDER.add(Parameters.EFFECT_MONO);
        EFFECT_ORDER.add(Parameters.EFFECT_NEGATIVE);
        EFFECT_ORDER.add(Parameters.EFFECT_SEPIA);
        EFFECT_ORDER.add(Parameters.EFFECT_SOLARIZE);
        EFFECT_ORDER.add(Parameters.EFFECT_POSTERIZE);
        EFFECT_ORDER.add(Parameters.EFFECT_AQUA);
        EFFECT_ORDER.add(Parameters.EFFECT_BLACKBOARD);
        EFFECT_ORDER.add(Parameters.EFFECT_WHITEBOARD);
    }

    private static final Map<Object, Integer> EFFECT_ICON_MAP = new HashMap<Object, Integer>();
    {
        EFFECT_ICON_MAP.put(Parameters.EFFECT_NONE, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_MONO, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_NEGATIVE, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_SEPIA, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_SOLARIZE, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_POSTERIZE, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_AQUA, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_BLACKBOARD, R.drawable.app_005_earth_element);
        EFFECT_ICON_MAP.put(Parameters.EFFECT_WHITEBOARD, R.drawable.app_005_earth_element);
    }

    public enum ParameterGroup {
        CAMERA(SettingChangeListener.CAMERA, CAMERA_ORDER, CAMERA_ICON_MAP),
        FLASH(SettingChangeListener.FLASH, FLASH_ORDER, FLASH_ICON_MAP),
        EFFECTS(SettingChangeListener.COLOR_EFFECT, EFFECT_ORDER, EFFECT_ICON_MAP);

        public List<Object>         order;
        public int                  type;
        public Map<Object, Integer> iconMap;

        ParameterGroup(int type, List<Object> order, Map<Object, Integer> iconMap) {
            this.order = order;
            this.type = type;
            this.iconMap = iconMap;
        }
    }

    private List<Map<String, Object>> mGridViewData      = new ArrayList<Map<String, Object>>();
    private List<Object>              mAvailableSettings = new ArrayList<Object>();
    private ParameterGroup            mGroup;
    private static final String       EFFECT_ICON        = "EFFECT_ICON";
    private static final String       EFFECT_DESCRIPTION = "EFFECT_DESCRIPTION";

    public static SettingIconFragment newInstance(SettingChangeListener settingChangeListener,
            List<String> availableSettings, ParameterGroup group) {
        
        SettingIconFragment fragment = new SettingIconFragment();
        
        Bundle bundle = new Bundle();
        fragment.mSettingChangeListener = settingChangeListener;
        fragment.mGroup = group;

        fragment.mGridViewData = new ArrayList<Map<String, Object>>();
        fragment.mAvailableSettings = new ArrayList<Object>();

        // if no available settings return empty fragment
        if (availableSettings == null) {
            return fragment;
        }

        // Sort the effects to achieve desired display order
        for (Object setting : group.order) {
            if (availableSettings.contains(setting)) {
                fragment.mAvailableSettings.add(setting);
            }
        }

        // put the available effects into the Hashmap List for display
        for (Object effect : fragment.mAvailableSettings) {
            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put(EFFECT_ICON, group.iconMap.get(effect));
            map.put(EFFECT_DESCRIPTION, effect);
            fragment.mGridViewData.add(map);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGridView = new GridView(getActivity());
        mGridView.setNumColumns(mGridViewData.size());

        mAdapter = new SimpleAdapter(getActivity(), mGridViewData, R.layout.app_006_item_icon_description,
                new String[] { EFFECT_ICON, EFFECT_DESCRIPTION }, new int[] { R.id.app_006_icon,
                        R.id.app_006_iconDescription });
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSettingChangeListener.changeSetting(mGroup.type, mAvailableSettings.get(position));
            }
        });
        return mGridView;
    }

}