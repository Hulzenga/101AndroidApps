package com.hulzenga.ioi_apps.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageButton;

/**
 * A utility class containing methods to simplify development but which do not
 * necessarily belong in a deployed app
 */
public class DeveloperTools {

    private DeveloperTools() {
    }

    public enum Statefulness {
        greyWhenDisabled
    }

    public static void makeImageButtonStateful(ImageButton imageButton, Statefulness type) {
        switch (type) {
        case greyWhenDisabled:
            Bitmap original = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
            Bitmap clone = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Config.ARGB_8888);

            Canvas c = new Canvas(clone);
            Paint p = new Paint();
            p.setColorFilter(new PorterDuffColorFilter(Color.GRAY, Mode.SRC_IN));
            c.drawBitmap(original, 0, 0, p);

            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, new BitmapDrawable(clone));
            stateListDrawable.addState(new int[] {}, imageButton.getDrawable());            

            imageButton.setImageDrawable(stateListDrawable);

            break;
        default:
            return;
        }
    }
}
