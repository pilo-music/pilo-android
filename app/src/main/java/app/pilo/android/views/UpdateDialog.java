package app.pilo.android.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.pilo.android.R;
import app.pilo.android.activities.SplashScreenActivity;

public class UpdateDialog {

    private Activity activity;
    private String title;
    private String description;
    private String link;
    private int version;
    private int minVersion;

    private TextView tvTitle;
    private TextView tvDescription;
    private Button btnCancel;
    private Button btnUpdate;

    public UpdateDialog(Activity activity, String title, String description, String link, int version, int minVersion) {
        this.activity = activity;
        this.title = title;
        this.description = description;
        this.link = link;
        this.version = version;
        this.minVersion = minVersion;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(activity, R.style.DialogTheme);
        if (version < minVersion) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }else{
            dialog.setCanceledOnTouchOutside(true);
        }


        dialog.setContentView(R.layout.update_dialog);

        tvTitle = dialog.findViewById(R.id.tv_title);
        tvDescription = dialog.findViewById(R.id.tv_description);


        tvTitle.setText(title);
        tvDescription.setText(description);

        if (version < minVersion) {
            btnCancel.setVisibility(View.GONE);
        }

        btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnUpdate = dialog.findViewById(R.id.btn_update);


        btnUpdate.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            activity.startActivity(browserIntent);
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            ((SplashScreenActivity) activity).checkForLogin();
        });

        dialog.show();

    }
}
