package com.hulzenga.ioi.android.app_003.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.hulzenga.ioi.android.app_003.database.MonsterContract.COLUMN_MONSTER_DESCRIPTION;
import static com.hulzenga.ioi.android.app_003.database.MonsterContract.COLUMN_MONSTER_ID;
import static com.hulzenga.ioi.android.app_003.database.MonsterContract.COLUMN_MONSTER_NAME;
import static com.hulzenga.ioi.android.app_003.database.MonsterContract.TABLE_MONSTERS;

/**
 * SQLiteOpenHelper to guarantee existance of an up to date database
 */
public class MonsterDatabaseHelper extends SQLiteOpenHelper {

  private static final String TAG = "MONSTER_DATABASE_HELPER";

  public static final int    DATABASE_VERSION = 1;
  public static final String DATABASE_NAME    = "Monsters.db";

  //SQL query which creates the database
  private static final String DATABASE_CREATE =
      "CREATE TABLE " + TABLE_MONSTERS + " ("
          + COLUMN_MONSTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + COLUMN_MONSTER_NAME + " TEXT NOT NULL, "
          + COLUMN_MONSTER_DESCRIPTION + " TEXT"
          + ")";

  private static final String DATABASE_DROP =
      "DROP TABLE IF EXISTS " + TABLE_MONSTERS;


  public MonsterDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(TAG, "Upgrading from version " + oldVersion + " to " + newVersion +
        ", this will destroy all old data");
    db.execSQL(DATABASE_DROP);
    onCreate(db);
  }

}
