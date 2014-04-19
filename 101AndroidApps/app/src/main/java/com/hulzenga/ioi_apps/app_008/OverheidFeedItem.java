package com.hulzenga.ioi_apps.app_008;

public class OverheidFeedItem {

  private String mTitle;
  private String mLink;
  private String mPubDate;
  private String mDescription;

  public OverheidFeedItem(String title, String link, String pubDate, String description) {
    mTitle = title;
    mLink = link;
    mPubDate = pubDate;
    mDescription = description;
  }

  public String getTitle() {
    return mTitle;
  }

  public String getLink() {
    return mLink;
  }

  public String getPubDate() {
    return mPubDate;
  }

  public String getDescription() {
    return mDescription;
  }
}
