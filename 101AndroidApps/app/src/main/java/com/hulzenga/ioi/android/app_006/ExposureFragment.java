package com.hulzenga.ioi.android.app_006;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.hulzenga.ioi.android.R;
import com.hulzenga.ioi.android.app_006.SettingChangeListener.ChangeType;

public class ExposureFragment extends Fragment {

  private SettingChangeListener mSettingChangeListener;

  private int mMinExposure;
  private int mCurrentExposure;
  private int mMaxExposure;

  public static ExposureFragment newInstance(SettingChangeListener settingChangeListener, int minExposure,
                                             int currentExposure, int maxExposure) {
    ExposureFragment fragment = new ExposureFragment();
    fragment.mSettingChangeListener = settingChangeListener;

    fragment.mMinExposure = minExposure;
    fragment.mCurrentExposure = currentExposure;
    fragment.mMaxExposure = maxExposure;

    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.app_006_fragment_exposure, container, false);
    SeekBar exposure = (SeekBar) view.findViewById(R.id.app_006_exposure_slider);
    exposure.setMax(mMaxExposure - mMinExposure);
    exposure.setProgress(mCurrentExposure - mMinExposure);

    exposure.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSettingChangeListener.changeSetting(ChangeType.EXPOSURE, progress + mMinExposure);
      }
    });
    return view;
  }
}
