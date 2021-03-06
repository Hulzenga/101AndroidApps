package com.hulzenga.ioi.android.app_007;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Score {

  private int    mPoints;
  private String mTimestamp;

  public Score(WikiGameActivity.Difficulty difficulty, int points) {
    this.mPoints = points;
    mTimestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm").format(new Date());
  }

  public int getPoints() {
    return mPoints;
  }

  public String getTimeStamp() {
    return mTimestamp;
  }

  private void saveScore() {

  }

  public static Map<WikiGameActivity.Difficulty, List<Score>> getHighScores(WikiGameActivity.Difficulty difficulty) {

    return new HashMap<WikiGameActivity.Difficulty, List<Score>>();
  }

  /**
   * Post the score, check if it is a high score and if so save it.
   *
   * @param score the score to post
   * @return true if the score is a new high score
   */
  public static boolean postScore(Score score) {
    return false;
  }
}
