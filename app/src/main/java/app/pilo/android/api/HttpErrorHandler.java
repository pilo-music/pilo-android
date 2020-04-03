package app.pilo.android.api;

import android.app.Activity;

import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.utils.Utils;

public class HttpErrorHandler {
    private Activity activity;
    private String message = null;

    public HttpErrorHandler(Activity activity) {
        this.activity = activity;
        handle();
    }

    public HttpErrorHandler(Activity activity, String message) {
        this.activity = activity;
        this.message = message;
        handle();
    }

    private void handle() {
        //todo check network connection

        CharSequence text;
        if (message != null)
            text = message;
        else
            text = activity.getString(R.string.server_connection_error);


        Alerter.create(activity)
                .setTitle(text)
                .setTextTypeface(Utils.font(activity))
                .setTitleTypeface(Utils.font(activity))
                .setButtonTypeface(Utils.font(activity))
                .setText(R.string.server_connection_message)
                .setBackgroundColorRes(R.color.colorError)
                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                .show();
    }
}