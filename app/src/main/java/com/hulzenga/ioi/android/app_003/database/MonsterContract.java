package com.hulzenga.ioi.android.app_003.database;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Contract definition for the Monster Database
 */
public class MonsterContract {

  //this class can not be instantiated
  private MonsterContract() {
  }

  //table name
  public static final String TABLE_MONSTERS = "Monsters";

  //table fields
  public static final String COLUMN_MONSTER_ID          = "_id";
  public static final String COLUMN_MONSTER_NAME        = "MonsterName";
  public static final String COLUMN_MONSTER_DESCRIPTION = "Description";

  //set of all table fields
  public static final HashSet<String> VALID_COLUMNS = new HashSet<String>(
      Arrays.asList(new String[]{
          COLUMN_MONSTER_ID,
          COLUMN_MONSTER_NAME,
          COLUMN_MONSTER_DESCRIPTION})
  );

}
