package com.hulzenga.ioi.android.app_006;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.hulzenga.ioi.android.R;
import com.hulzenga.ioi.android.app_006.SettingChangeListener.ChangeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingMenuFragment extends Fragment {

  private SettingChangeListener mSettingChangeListener;

  private static final String IMAGE_SIZE    = "Image size";
  private static final String FOCUS         = "Focus";
  private static final String SCENE_MODE    = "SceneGraph mode";
  private static final String ISO           = "ISO";
  private static final String WHITE_BALANCE = "White balance";
  //private static final String TIMER = "Timer";

  public static final List<String> SETTINGS = new ArrayList<String>();

  static {
    SETTINGS.add(IMAGE_SIZE);
    SETTINGS.add(FOCUS);
    SETTINGS.add(SCENE_MODE);
    SETTINGS.add(ISO);
    SETTINGS.add("White balance");
    //SETTINGS.add("Timer");
  }

  private boolean[] availableSettings = new boolean[SETTINGS.size()];

  public static final Map<Integer, ChangeType> SETTING_CHANGE_MAP = new HashMap<Integer, ChangeType>();

  static {
    SETTING_CHANGE_MAP.put(SETTINGS.indexOf(IMAGE_SIZE), ChangeType.IMAGE_SIZE);
    SETTING_CHANGE_MAP.put(SETTINGS.indexOf(FOCUS), ChangeType.FOCUS);
    SETTING_CHANGE_MAP.put(SETTINGS.indexOf(SCENE_MODE), ChangeType.SCENE_MODE);
    SETTING_CHANGE_MAP.put(SETTINGS.indexOf(ISO), ChangeType.ISO);
    SETTING_CHANGE_MAP.put(SETTINGS.indexOf(WHITE_BALANCE), ChangeType.WHITE_BALANCE);
    //SETTING_CHANGE_MAP.put(SETTINGS.indexOf(TIMER), ChangeType.TIMER);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mSettingChangeListener = (SettingChangeListener) activity;
  }

  public static SettingMenuFragment newInstance(Parameters params) {
    SettingMenuFragment fragment = new SettingMenuFragment();

    //go through the camera parameters to see which menu options should be enabled

    fragment.availableSettings[SETTINGS.indexOf(IMAGE_SIZE)] = true;

    if (params.getSupportedFocusModes() != null) {
      fragment.availableSettings[SETTINGS.indexOf(FOCUS)] = true;
    }
    if (params.getSupportedSceneModes() != null) {
      fragment.availableSettings[SETTINGS.indexOf(SCENE_MODE)] = true;
    }

    fragment.availableSettings[SETTINGS.indexOf(ISO)] = true;

    if (params.getSupportedWhiteBalance() != null) {
      fragment.availableSettings[SETTINGS.indexOf(WHITE_BALANCE)] = true;
    }

    //fragment.availableSettings[SETTINGS.indexOf(TIMER)] = true;

    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ListView view = new ListView(getActivity());
    view.setBackgroundResource(R.drawable.app_006_item_background);
    view.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

    ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, SETTINGS) {

      @Override
      public boolean areAllItemsEnabled() {
        return false;
      }

      @Override
      public boolean isEnabled(int position) {
        return availableSettings[position];
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        if (!isEnabled(position)) {
          view.setEnabled(false);
        }
        return view;
      }
    };

    view.setAdapter(adapter);
    view.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSettingChangeListener.open2ndLevelSetting(SETTING_CHANGE_MAP.get(position));
      }
    });

    return view;
  }
}
