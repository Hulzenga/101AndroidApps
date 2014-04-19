package com.hulzenga.ioi_apps.app_008;

public enum OverheidFeed {
  NIEUWSBERICHTEN ("Nieuwsberichten", "http://feeds.rijksoverheid.nl/nieuws.rss"),
  DOCUMENTEN("Documenten en publicaties", "http://feeds.rijksoverheid.nl/regering/documenten-en-publicaties.rss"),
  BESLUIITEN("Besluiten", "http://feeds.rijksoverheid.nl/documenten-en-publicaties/besluiten.rss"),
  KAMERSTUKKEN("Kamerstukken", "http://feeds.rijksoverheid.nl/kamerstukken.rss"),
  TOESPRAKEN("Toespraken", "http://feeds.rijksoverheid.nl/toespraken.rss"),
  REISADVIEZEN("Reisadviezen", "http://feeds.rijksoverheid.nl/onderwerpen/reisadviezen.rss");

  private String feedName;
  private String feedAddress;

  OverheidFeed(String feedName, String feedAddress) {
    this.feedName = feedName;
    this.feedAddress = feedAddress;
  }

  public String getName() {
    return feedName;
  }

  public String getAddress() {
    return feedAddress;
  }
}
