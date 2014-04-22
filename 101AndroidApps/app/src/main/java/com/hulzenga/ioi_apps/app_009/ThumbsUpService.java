package com.hulzenga.ioi_apps.app_009;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;

import com.hulzenga.ioi_apps.R;

import java.io.IOException;

/**
 * Created by jouke on 21-4-14.
 */
public class ThumbsUpService extends Service {

  private static final String TAG             = "ThumbsUpService";
  private static final int    NOTIFICATION_ID = 938423783;

  private Binder mBinder = new ThumbsUpBinder();
  private ThumbsUpServer mServer;
  private String mServerAddress;
  private boolean mServerRunning = false;


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  public boolean startServer() {
    try {
      mServer = new ThumbsUpServer(getAssets(), "www/app_009");
      mServer.start();
      mServerRunning = true;

      WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
      int ip = wifiManager.getConnectionInfo().getIpAddress();

      Notification.Builder builder = new Notification.Builder(getApplicationContext());
      builder.setSmallIcon(R.drawable.app_009_small_icon);
      builder.setContentTitle(getResources().getString(R.string.app_009_title));

      mServerAddress = Formatter.formatIpAddress(ip) + ":" + mServer.getPort();
      builder.setContentText(getResources().getString(R.string.app_009_server_running_message) +
          " " + mServerAddress);

      Intent intent = new Intent(getApplicationContext(), ThumbsUpActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
      builder.setContentIntent(pendingIntent);

      startForeground(NOTIFICATION_ID, builder.getNotification());

    } catch (IOException e) {
      Log.e(TAG, "Failed to start server");
    }

    return mServerRunning;
  }

  public String getServerAddress() {
    return mServerAddress;
  }

  public void stopServer() {
    if (mServer != null) {
      mServer.stop();
      mServerRunning = false;

      stopForeground(true);
    }
  }

  public boolean isServerRunning() {
    return mServerRunning;
  }

  public class ThumbsUpBinder extends Binder {
    public ThumbsUpService getService() {
      return ThumbsUpService.this;
    }
  }
}
