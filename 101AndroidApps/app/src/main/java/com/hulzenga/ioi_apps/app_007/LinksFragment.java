package com.hulzenga.ioi_apps.app_007;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hulzenga.ioi_apps.R;

public class LinksFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.app_007_fragment_links, container, false);
  }
}