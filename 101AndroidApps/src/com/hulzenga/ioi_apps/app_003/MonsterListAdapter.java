package com.hulzenga.ioi_apps.app_003;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;

import com.hulzenga.ioi_apps.R;

public class MonsterListAdapter extends SimpleCursorAdapter {

    public MonsterListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        
        ImageButton editButton = (ImageButton) view.findViewById(R.id.app_003_editButton);
        editButton.setTag(cursor.getLong(0));
        
        ImageButton removeButton = (ImageButton) view.findViewById(R.id.app_003_removeButton);
        removeButton.setTag(cursor.getLong(0));
    }
}
