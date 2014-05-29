package com.hulzenga.ioi.android.app_008;

class OverheidFeedItem {

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
