package com.hulzenga.ioi.android;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jouke on 5/30/14.
 */
public class AppDetailsDialog extends DialogFragment {

  private static final String APP_KEY = "appKey";
  private App app;

  public static AppDetailsDialog newInstance(App app) {
    AppDetailsDialog dialog = new AppDetailsDialog();
    Bundle args = new Bundle();
    args.putSerializable(APP_KEY, app);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = (App) getArguments().getSerializable(APP_KEY);
    setStyle(DialogFragment.STYLE_NORMAL, getTheme());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().setTitle(app.getTitle());
    View contentView = inflater.inflate(R.layout.app_detail_dialog, container, false);
    TextView shortView = (TextView) contentView.findViewById(R.id.app_details_shortDescriptionView);
    ImageView imageView = (ImageView) contentView.findViewById(R.id.app_details_imageView);
    TextView longView = (TextView) contentView.findViewById(R.id.app_details_longDescriptionView);

    shortView.setText(app.getShortDescription());
    imageView.setImageResource(app.getIcon());
    longView.setText(getResources().getString(app.getLongDescription()));
    return contentView;
  }
}
