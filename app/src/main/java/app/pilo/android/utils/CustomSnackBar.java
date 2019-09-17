package app.pilo.android.utils;

import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import android.view.View;

public class CustomSnackBar{

    public static void make(View view, CharSequence text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
        snackbar.show();
    }

    public static void make(View view, CharSequence text, int length) {
        Snackbar snackbar = Snackbar.make(view, text, length);
        ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
        snackbar.show();
    }
}
