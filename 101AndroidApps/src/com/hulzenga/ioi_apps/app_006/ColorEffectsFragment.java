package com.hulzenga.ioi_apps.app_006;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
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
import com.hulzenga.ioi_apps.app_006.SettingChangeListener.SettingType;

public class ColorEffectsFragment extends Fragment {

    private static final String               TAG                = "COLOR_EFFECTS_FRAGMENT";

    private GridView                          mColorEffectsGridView;
    private ListAdapter                       mAdapter;
    private SettingChangeListener             mSettingChangeListener;

    /**
     * A list containing all possible effects in display order
     */
    private static final List<String>         EFFECTS_IN_ORDER   = new ArrayList<String>();
    {
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_NONE);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_MONO);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_NEGATIVE);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_SEPIA);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_SOLARIZE);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_POSTERIZE);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_AQUA);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_BLACKBOARD);
        EFFECTS_IN_ORDER.add(Parameters.EFFECT_WHITEBOARD);
    }

    /**
     * A mapping of effects to their respective icons
     */
    private static final Map<String, Integer> mEffectIconMap     = new HashMap<String, Integer>();
    {
        mEffectIconMap.put(Parameters.EFFECT_AQUA, R.drawable.app_005_water_element);
        mEffectIconMap.put(Parameters.EFFECT_BLACKBOARD, R.drawable.app_005_fire_element);
    }

    // private List<Map<String, Integer>> mGridViewData = new
    // ArrayList<Map<String, Integer>>();

    private List<Map<String, Object>>         mGridViewData      = new ArrayList<Map<String, Object>>();
    private List<String>                      mAvailableEffects  = new ArrayList<String>();
    private static final String               EFFECT_ICON        = "EFFECT_ICON";
    private static final String               EFFECT_DESCRIPTION = "EFFECT_DESCRIPTION";

    public static ColorEffectsFragment newInstance(SettingChangeListener settingChangeListener,
            List<String> availableEffects) {

        ColorEffectsFragment fragment = new ColorEffectsFragment();
        fragment.mSettingChangeListener = settingChangeListener;

        // Sort the effects to achieve desired display order
        for (String effect : EFFECTS_IN_ORDER) {
            if (availableEffects.contains(effect)) {
                fragment.mAvailableEffects.add(effect);
            }
        }

        // put the available effects into the Hashmap List for display
        for (String effect : fragment.mAvailableEffects) {
            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put(EFFECT_ICON, mEffectIconMap.get(effect));
            map.put(EFFECT_DESCRIPTION, effect);
            fragment.mGridViewData.add(map);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mColorEffectsGridView = new GridView(getActivity());
        mColorEffectsGridView.setNumColumns(mGridViewData.size());

        mAdapter = new SimpleAdapter(getActivity(), mGridViewData, R.layout.app_006_item_icon_description,
                new String[] { EFFECT_ICON, EFFECT_DESCRIPTION }, new int[] { R.id.app_006_icon,
                        R.id.app_006_iconDescription });
        mColorEffectsGridView.setAdapter(mAdapter);

        mColorEffectsGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSettingChangeListener.changeSetting(SettingType.COLOR_EFFECT, mAvailableEffects.get(position));
            }
        });
        return mColorEffectsGridView;
    }

}
