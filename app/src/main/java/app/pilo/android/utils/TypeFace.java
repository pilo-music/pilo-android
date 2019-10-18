package app.pilo.android.utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFace {
    public static Typeface font(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/font.ttf");
    }
}
