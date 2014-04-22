package com.hulzenga.ioi_apps.app_009;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

/**
 * Created by jouke on 20-4-14.
 */
public class ThumbsUpActivity extends DemoActivity {

  private static final String TAG = "ThumbsUpActivity";

  private ImageButton     mThumbsUpButton;
  private TextView        mServerStatusTextView;
  private ThumbsUpService mThumbsUpService;
  private ServiceConnection mServiceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      ThumbsUpService.ThumbsUpBinder binder = (ThumbsUpService.ThumbsUpBinder) service;
      mThumbsUpService = binder.getService();
      setBound(true);

      if (mThumbsUpService.isServerRunning()) {
        setServerRunning(true);
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      setBound(false);
    }
  };
  private boolean mBound;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.app_009_activity_thumbs_up);
    mThumbsUpButton = (ImageButton) findViewById(R.id.app_009_thumbsUpButton);
    mServerStatusTextView = (TextView) findViewById(R.id.app_009_serverStatusTextView);
    mServerStatusTextView.setMovementMethod(LinkMovementMethod.getInstance());

    setBound(false);
    setServerRunning(false);

    mThumbsUpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        //mBound should be true if button is enabled, but it can't hurt to check
        if (mBound) {
          if (!mThumbsUpService.isServerRunning()) {
            if (mThumbsUpService.startServer()) {
              setServerRunning(true);
            } else {
              Toast.makeText(ThumbsUpActivity.this, R.string.app_009_server_start_failure, Toast.LENGTH_SHORT).show();
            }
          } else {

            mThumbsUpService.stopServer();
            setServerRunning(false);
          }
        } else {
          Log.w(TAG, "ThumbsUpButton clicked but the service is not bound !");
        }
      }
    });
  }

  private void setBound(boolean bound) {
    if (bound) {
      mBound = true;
      mThumbsUpButton.setEnabled(true);
    } else {
      mBound = false;
      mThumbsUpButton.setEnabled(false);
    }
  }

  private void setServerRunning(boolean running) {
    if (running) {
      mServerStatusTextView.setText(Html.fromHtml(getResources().getString(R.string.app_009_server_running_message)
          + " <a href=\"http://"+mThumbsUpService.getServerAddress()+"\">"+mThumbsUpService.getServerAddress()+"</a>"));
      mThumbsUpButton.setImageDrawable(getResources().getDrawable(R.drawable.app_009_thumbs_up));
    } else {
      mServerStatusTextView.setText(R.string.app_009_start_server_instruction);
      mThumbsUpButton.setImageDrawable(getResources().getDrawable(R.drawable.app_009_thumbs_down));
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    Intent intent = new Intent(this, ThumbsUpService.class);
    startService(intent);
    bindService(intent, mServiceConnection, Context.BIND_ABOVE_CLIENT);
  }

  @Override
  protected void onStop() {
    super.onStop();

    unbindService(mServiceConnection);

    if (mThumbsUpService != null && !mThumbsUpService.isServerRunning()) {
      Intent intent = new Intent(this, ThumbsUpService.class);
      stopService(intent);
    }

  }
}
