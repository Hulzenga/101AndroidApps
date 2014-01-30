package com.hulzenga.ioi_apps;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;

public class AppRepository {

    public enum AppId {

        APP_001(com.hulzenga.ioi_apps.app_001.HelloWorldActivity.class, R.string.app_001_title,
                R.drawable.app_001_icon, R.string.app_001_shortDescription),
        APP_002(com.hulzenga.ioi_apps.app_002.BouncyBallsActivity.class, R.string.app_002_title,
                R.drawable.app_002_icon, R.string.app_002_shortDescription),
        APP_003(com.hulzenga.ioi_apps.app_003.MonsterDatabaseActivity.class, R.string.app_003_title,
                R.drawable.ic_launcher, R.string.app_003_shortDescription),
        APP_004(com.hulzenga.ioi_apps.app_004.BouncyBall3dActivity.class, R.string.app_004_title,
                R.drawable.app_004_icon, R.string.app_004_shortDescription),
        APP_005(com.hulzenga.ioi_apps.app_005.ElementActivity.class, R.string.app_005_title,
                R.drawable.app_005_icon, R.string.app_005_shortDescription),
        APP_006(com.hulzenga.ioi_apps.app_006.SimpleCameraActivity.class, R.string.app_006_title,
                R.drawable.ic_launcher, R.string.app_006_shortDescription),
        APP_007(com.hulzenga.ioi_apps.app_007.WikipediaGame.class, R.string.app_007_title,
                R.drawable.ic_launcher, R.string.app_007_shortDescription), ;
        /*
         * APP_002, APP_003, APP_004, APP_005, APP_006, APP_007,;
         */

        private int   title;
        private Class activity;
        private int   icon;
        private int   shortDescription;

        private AppId(Class activity, int title, int icon, int shortDescription) {
            this.title = title;
            this.activity = activity;
            this.icon = icon;
            this.shortDescription = shortDescription;
        }
    }

    private static List<AppSummary> appSummaries;

    private AppRepository() {
    }

    public App getApp(Resources resources, AppId appId) {
        return new App(resources, appId);
    }

    public static List<AppSummary> getAppSummaries(Resources resources) {
        if (appSummaries == null) {
            appSummaries = new ArrayList<AppSummary>();
            for (AppId appId : AppId.values()) {
                appSummaries.add(new AppSummary(resources, appId));
            }
        }
        return appSummaries;
    }

    public static class AppSummary {

        private String title;
        private Class  activity;
        private String shortDescription;
        private int    icon;

        public AppSummary(Resources resources, AppId appId) {
            title = resources.getString(appId.title);
            activity = appId.activity;
            shortDescription = resources.getString(appId.shortDescription);
            icon = appId.icon;
        }

        public String getTitle() {
            return title;
        }

        public Class getActivity() {
            return activity;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public int getIcon() {
            return icon;
        }
    }

    public static class App extends AppSummary {

        public App(Resources resources, AppId appId) {
            super(resources, appId);
        }
    }

}
