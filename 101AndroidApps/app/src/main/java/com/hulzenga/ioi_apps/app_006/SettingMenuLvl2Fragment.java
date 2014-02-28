package com.hulzenga.ioi_apps.app_006;

import android.app.Activity;
import android.app.Fragment;
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

import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_006.SettingChangeListener.ChangeType;

import java.util.ArrayList;
import java.util.List;

public class SettingMenuLvl2Fragment extends Fragment {

  private SettingChangeListener mSettingChangeListener;

  private ChangeType mChangeType;
  private String     mSelected;
  private List<String> mAvailableSettings = new ArrayList<String>();

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mSettingChangeListener = (SettingChangeListener) activity;
  }

  public static SettingMenuLvl2Fragment newInstance(ChangeType type, String selectedSetting,
                                                    List<String> availableSettings) {
    SettingMenuLvl2Fragment fragment = new SettingMenuLvl2Fragment();
    fragment.mChangeType = type;
    fragment.mSelected = selectedSetting;
    fragment.mAvailableSettings = availableSettings;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ListView listView = new ListView(getActivity());
    listView.setBackgroundResource(R.drawable.app_006_item_background);
    listView.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked,
        mAvailableSettings);
    listView.setAdapter(adapter);

    listView.setItemChecked(mAvailableSettings.indexOf(mSelected), true);

    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = (ListView) parent;
        listView.setItemChecked(mAvailableSettings.indexOf(mSelected), false);

        mSelected = mAvailableSettings.get(position);
        listView.setItemChecked(position, true);

        mSettingChangeListener.changeSetting(mChangeType, mAvailableSettings.get(position));

      }
    });
    return listView;
  }

}
