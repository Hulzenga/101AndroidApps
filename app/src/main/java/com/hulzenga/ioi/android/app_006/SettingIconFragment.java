package com.hulzenga.ioi.android.app_006;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hulzenga.ioi.android.R;
import com.hulzenga.ioi.android.util.Constrain;

import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingIconFragment extends Fragment {

  private static final String TAG = "SettingIconFragment";
  private static final String SETTING_GROUP_KEY = "SettingGroupKey";

  private Setting.IconGroup mIconGroup;
  private SettingChangeListener mSettingChangeListener;

  private             int          mSelectedPosition = -1;

  public static SettingIconFragment newInstance(Setting.IconGroup group) {

    SettingIconFragment fragment = new SettingIconFragment();
    Bundle args = new Bundle();
    args.putSerializable(SETTING_GROUP_KEY, group);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      mSettingChangeListener = (SettingChangeListener) activity;
    } catch (ClassCastException e) {
      Log.e(TAG, activity.toString() + " must implement SettingChangeListener");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mIconGroup = (Setting.IconGroup) getArguments().get(SETTING_GROUP_KEY);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    GridView gridView = new GridView(getActivity());


    // setup gridview so that each column has at least 90dip of width to work with
    int availableIconSpaces =
        (int) Math.floor(container.getWidth() / (getResources().getDisplayMetrics().density * 90.0));

    gridView.setNumColumns(
        Constrain.upperBound(mIconGroup.getSettings().length, availableIconSpaces));

    ListAdapter adapter = new ArrayAdapter<Setting>(
        getActivity(), R.layout.app_006_item_icon_description, mIconGroup.getSettings()) {
      @Nullable
      @Override
      public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
          holder = (ViewHolder) view.getTag();
        } else {
          view = inflater.inflate(R.layout.app_006_item_icon_description, parent, false);
          holder = new ViewHolder(view);
          view.setTag(holder);
        }

        holder.icon.setImageDrawable(getResources().getDrawable(getItem(position).getIcon()));
        holder.iconDescription.setText(getItem(position).getParam());
        return view;
      }

    };
    gridView.setAdapter(adapter);

//    gridView.setOnItemClickListener(new OnItemClickListener() {
//
//      @Override
//      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        mSelectedPosition = position;
//
//        // The camera selection requires an integer, so for that
//        // specific
//        // case different behaviour is required
//        if (mGroup == ParameterGroup.CAMERA) {
//          mSettingChangeListener.changeSetting(mGroup.type, position);
//        } else {
//          mSettingChangeListener.changeSetting(mGroup.type, mAvailableSettings.get(position));
//        }
//
//      }
//    });
    return gridView;
  }

  public static class ViewHolder {
    @InjectView(R.id.app_006_icon)            ImageView icon;
    @InjectView(R.id.app_006_iconDescription) TextView iconDescription;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}