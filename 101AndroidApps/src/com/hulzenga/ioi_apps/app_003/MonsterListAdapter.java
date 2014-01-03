package com.hulzenga.ioi_apps.app_003;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

import com.hulzenga.ioi_apps.R;

/**
 * A custom implementation of the SimpleCursorAdapter. This custom adapter
 * stores the name and id values of monsters in the tag of layout element of
 * each row
 */
public class MonsterListAdapter extends SimpleCursorAdapter {

    public MonsterListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        // add a tag to the LinearLayout of each view containing the id and name
        // of the element
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.app_003_linearItemLayout);
        layout.setTag(R.id.app_003_item_id, cursor.getLong(0));
        layout.setTag(R.id.app_003_item_name, cursor.getString(1));
    }
}
