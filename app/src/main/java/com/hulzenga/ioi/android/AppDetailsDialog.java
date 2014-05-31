package com.hulzenga.ioi.android;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
    setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View detailsDialog = inflater.inflate(R.layout.app_detail_dialog, container, false);
    TextView titleView = (TextView) detailsDialog.findViewById(R.id.app_details_titleView);
    TextView shortView = (TextView) detailsDialog.findViewById(R.id.app_details_shortDescriptionView);
    ImageView imageView = (ImageView) detailsDialog.findViewById(R.id.app_details_imageView);
    TextView longView = (TextView) detailsDialog.findViewById(R.id.app_details_longDescriptionView);

    titleView.setText(app.getTitle());
    shortView.setText(app.getShortDescription());
    imageView.setImageResource(app.getIcon());
    longView.setText(app.getLongDescription());
    return detailsDialog;
  }
}
