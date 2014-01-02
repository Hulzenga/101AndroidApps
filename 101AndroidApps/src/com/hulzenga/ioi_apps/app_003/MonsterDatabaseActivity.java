package com.hulzenga.ioi_apps.app_003;

import java.util.Random;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_003.database.MonsterContract;
import com.hulzenga.ioi_apps.app_003.database.MonsterProvider;

public class MonsterDatabaseActivity extends DemoActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private MonsterListAdapter    mAdapter;
    private ListView              mMonsterListView;
    private EditText              mMonsterNameText;

    // random inputs for the monster names
    private static final String[] MONSTER_PREFIX  = { "", "Hell", "Infernal", "Vorpal", "Murderous", "Razor", "Foul",
            "Oozing", ""                         };
    private static final String[] MONSTER_TYPE    = { "Lizard", "Saurus", "Bunny", "Gerbil", "Raven", "Skinpecker",
            "Night Terror"                       };
    private static final String[] MONSTER_POSTFIX = { "", "of the Crannerbog", "from the Eastern Wilds",
            "of the Great Spire", "from the Abyss", "stuck betwixt worlds", "of the Nether" };

    private Random                mRandom         = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_003_activity_crud);

        mMonsterNameText = (EditText) findViewById(R.id.app_003_monsterName);
        setInputToRandomMonsterName(null);

        fillData();
        mMonsterListView = (ListView) findViewById(R.id.app_003_monsterList);
        mMonsterListView.setAdapter(mAdapter);
    }

    public void editMonsterName(View v) {
    }
    
    public void removeMonster(View v) {
        Uri uri = Uri.parse(MonsterProvider.CONTENT_URI + "/" + v.getTag());
        getContentResolver().delete(uri, null, null);
    }

    public void setInputToRandomMonsterName(View v) {
        mMonsterNameText.setText(randomMonsterName());
    }

    private String randomMonsterName() {
        return (pickRandom(MONSTER_PREFIX) + " " + pickRandom(MONSTER_TYPE) + " " + pickRandom(MONSTER_POSTFIX)).trim();
    }

    private String pickRandom(String[] wordList) {
        return wordList[mRandom.nextInt(wordList.length)];
    }

    public void fillData() {
        String[] from = new String[] { MonsterContract.COLUMN_MONSTER_NAME };
        int[] to = new int[] { R.id.app_003_monsterItemName };

        getLoaderManager().initLoader(0, null, this);
        mAdapter = new MonsterListAdapter(this, R.layout.app_003_item_monster_list, null, from, to, 0);
    }

    public void addMonster(View view) {
        ContentValues values = new ContentValues();
        String monsterName = mMonsterNameText.getText().toString();

        // if there is input add the monster, if not show toast
        if (!TextUtils.isEmpty(monsterName)) {
            values.put(MonsterContract.COLUMN_MONSTER_NAME, monsterName);
            getContentResolver().insert(MonsterProvider.CONTENT_URI, values);
        } else {
            Toast.makeText(this, getResources().getString(R.string.app_003_noNameWarning), Toast.LENGTH_SHORT).show();
        }
        
        setInputToRandomMonsterName(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = { MonsterContract.COLUMN_MONSTER_ID, MonsterContract.COLUMN_MONSTER_NAME };
        CursorLoader cursorLoader = new CursorLoader(this, MonsterProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursor) {
        mAdapter.swapCursor(null);
    }

}
