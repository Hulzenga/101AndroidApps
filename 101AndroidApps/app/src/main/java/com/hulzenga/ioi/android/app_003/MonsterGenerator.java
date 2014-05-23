package com.hulzenga.ioi.android.app_003;

import java.util.Random;

/**
 * Simple static class spitting out random monster names
 */
class MonsterGenerator {

  // random inputs for the monster names
  private static final String[] MONSTER_PREFIX  = {"", "Hell", "Infernal", "Vorpal", "Murderous", "Razor", "Foul",
      "Oozing", ""};
  private static final String[] MONSTER_TYPE    = {"Lizard", "Saurus", "Bunny", "Gerbil", "Raven", "Skinpecker",
      "Night Terror"};
  private static final String[] MONSTER_POSTFIX = {"", "of the Crannerbog", "from the Eastern Wilds",
      "of the Great Spire", "from the Abyss", "stuck betwixt worlds", "of the Nether"};

  private static Random mRandom = new Random();

  // private constructor cannot be instantiated
  private MonsterGenerator() {
  }

  /**
   * Creates a new random monster name based on a random mix of hardcoded pre-
   * and postfixes combined with a basic monster type
   *
   * @return random monster name
   */
  public static String randomMonster() {
    return (pickRandom(MONSTER_PREFIX) + " " + pickRandom(MONSTER_TYPE) + " " + pickRandom(MONSTER_POSTFIX)).trim();
  }

  private static String pickRandom(String[] wordList) {
    return wordList[mRandom.nextInt(wordList.length)];
  }

}
