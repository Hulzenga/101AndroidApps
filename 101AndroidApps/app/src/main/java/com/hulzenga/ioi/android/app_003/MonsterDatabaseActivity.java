package com.hulzenga.ioi.android.app_003;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hulzenga.ioi.android.DemoActivity;
import com.hulzenga.ioi.android.R;
import com.hulzenga.ioi.android.app_003.MonsterEditDialog.EditDialogListener;
import com.hulzenga.ioi.android.app_003.database.MonsterContract;
import com.hulzenga.ioi.android.app_003.database.MonsterProvider;

/**
 * A simple CRUD app which keeps track of a list of monsters.
 * <p/>
 * A good bit of the code for this app is based on/ispired by Lars Vogel's
 * excellent "Android SQLite database and content provider" available at <a
 * href=
 * "http://www.vogella.com/articles/AndroidSQLite/article.html">http://www.
 * vogella.com/articles/AndroidSQLite/article.html</a >
 */
public class MonsterDatabaseActivity extends DemoActivity implements LoaderManager.LoaderCallbacks<Cursor>,
                                                                     EditDialogListener {

  // private state keeping
  private MonsterListAdapter mAdapter;
  private ListView           mMonsterListView;
  private EditText           mMonsterEditText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_003_activity_monster_database);

    // setup the monsterName EditText such that it runs addMonster when
    // pressing done
    mMonsterEditText = (EditText) findViewById(R.id.app_003_monsterEditText);
    mMonsterEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          addMonster(null);
          handled = true;
        }
        return handled;
      }
    });

    // start the LoaderManager and ListView adapter
    fillData();

    // bind the adapter to the monsterList
    mMonsterListView = (ListView) findViewById(R.id.app_003_monsterList);
    mMonsterListView.setAdapter(mAdapter);
  }

  /**
   * This method starts a MonsterEditDialog to change the monster name of a
   * given element in the monster list
   *
   * @param v view which called the method
   */
  public void editMonsterName(View v) {

    // this finds the layout element of the row, which contains the required
    // tags
    final View parent = (View) v.getParent();

    // populate the argument bundle with monster id and name
    Bundle arguments = new Bundle();
    arguments.putLong(MonsterEditDialog.ARGUMENT_ID, (Long) parent.getTag(R.id.app_003_item_id));
    arguments.putString(MonsterEditDialog.ARGUMENT_NAME, (String) parent.getTag(R.id.app_003_item_name));

    // create and launch the dialog
    MonsterEditDialog edit = new MonsterEditDialog();
    edit.setArguments(arguments);
    edit.show(getFragmentManager(), null);
  }

  /**
   * This method removes the monster positioned at the same row as "View v"
   * from the list
   *
   * @param v view which called the method
   */
  public void removeMonster(View v) {
    final View parent = (View) v.getParent();
    Uri uri = Uri.parse(MonsterProvider.CONTENT_URI + "/" + parent.getTag(R.id.app_003_item_id));
    getContentResolver().delete(uri, null, null);
  }

  /**
   * This method sets the input monster name to a random monster name
   *
   * @param v
   */
  public void setRandomMonster(View v) {
    mMonsterEditText.setText(MonsterGenerator.randomMonster());
  }

  /**
   * Sets up the LoaderManager and the adapter
   */
  private void fillData() {
    String[] from = new String[]{MonsterContract.COLUMN_MONSTER_NAME};
    int[] to = new int[]{R.id.app_003_monsterItemName};

    getLoaderManager().initLoader(0, null, this);
    mAdapter = new MonsterListAdapter(this, R.layout.app_003_item_monster_list, null, from, to, 0);
  }

  /**
   * Inserts the monster name currently in the mMonsterEditText into the
   * underlying ContentProvider
   *
   * @param view
   */
  public void addMonster(View view) {
    ContentValues values = new ContentValues();
    String monsterName = mMonsterEditText.getText().toString();

    // if there is input add the monster, if not show toast
    if (!TextUtils.isEmpty(monsterName)) {
      values.put(MonsterContract.COLUMN_MONSTER_NAME, monsterName);
      getContentResolver().insert(MonsterProvider.CONTENT_URI, values);
      mMonsterEditText.setText("");
    } else {
      Toast.makeText(this, getResources().getString(R.string.app_003_noNameWarning), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    String[] projection = {MonsterContract.COLUMN_MONSTER_ID, MonsterContract.COLUMN_MONSTER_NAME};
    return new CursorLoader(this, MonsterProvider.CONTENT_URI, projection, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
    mAdapter.swapCursor(cursor);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursor) {
    mAdapter.swapCursor(null);
  }

  @Override
  public void onEditDialogPositiveClick(Bundle arguments) {
    String monsterName = arguments.getString(MonsterEditDialog.ARGUMENT_NAME);
    long id = arguments.getLong(MonsterEditDialog.ARGUMENT_ID);
    ContentValues values = new ContentValues();
    values.put(MonsterContract.COLUMN_MONSTER_NAME, monsterName);

    Uri uri = Uri.parse(MonsterProvider.CONTENT_URI + "/" + id);
    getContentResolver().update(uri, values, null, null);
  }

}
