package com.hulzenga.ioi_apps.app_006;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class EmptyFragment extends Fragment {

  public static EmptyFragment newInstance() {
    return new EmptyFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    TextView emptyView = new TextView(getActivity());
    emptyView.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

    return emptyView;
  }
}
